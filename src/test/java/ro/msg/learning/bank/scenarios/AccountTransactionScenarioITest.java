package ro.msg.learning.bank.scenarios;

import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ro.msg.learning.bank.controllers.*;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountTransactionScenarioITest {

    public static final BigDecimal MONEY_CASH_CLIENT = new BigDecimal(100);
    public static final String USERNAME = "volodimir";
    public static final BigDecimal WITHDRAW_AMOUNT = new BigDecimal(100);
    public static final BigDecimal ACCOUNT_AMOUNT = new BigDecimal(1000);
    public static final BigDecimal DEPOSIT_AMOUNT = new BigDecimal(50);
    public static final BigDecimal TRANSFER_AMOUNT = new BigDecimal(44);
    public static final BigDecimal ACCOUNT_AMOUNT_RECEIVER = new BigDecimal(500);
    @Autowired
    private ClientController clientController;
    @Autowired
    private AppUserController appUserController;
    private AppUserSecurityDTO appUserSecurityDTO;
    private ClientDTO clientDTO;
    private AppUserDTO appUserDTO;
    @Autowired
    private BankController bankController;
    private BankDTO bankDTO;


    @Autowired
    private OperationController operationController;
    @Autowired
    private AccountDetailController accountDetailController;
    private AccountDetailDTO activeAccountDetailDTO;
    private AccountDetailDTO activeAccountDetailReceiverDTO;
    AccountDetailDTO accountDetailWithCloseAmountWithinLimitDTO;
    @Autowired
    private AccountController accountController;
    private AccountDTO activeAccountDTO;
    private AccountDTO blockAccountDTO;
    private AccountDTO activeAccountReceiverDTO;


    private OperationDTO unblockOperationDTO;
    private OperationDTO closeOperationDTO;
    private OperationDTO withdrawOperationDTO;
    private OperationDTO depositOperationDTO;
    private OperationDTO transferOperationDTO;

    @Autowired
    private AccountTransactionController accountTransactionController;
    private AccountTransactionDTO withdrawAccountTransactionDTO;
    private AccountTransactionDTO depositAccountTransactionDTO;
    private AccountTransactionDTO transferAccountTransactionDTO;

    @Autowired
    private MockMvc mvc;
    public static final String SECURED_LOGIN_URL = "/api/login";


    public void setUpBasicDB() {
        bankDTO = BankDTO.builder()
                .name(BankCompany.BTBANK)
                .build();
        clientDTO = ClientDTO.builder()
                .firstName("ClientFirstName")
                .lastName("ClientLastName")
                .moneyCash(MONEY_CASH_CLIENT)
                .build();

        appUserSecurityDTO = AppUserSecurityDTO.builder()
                .username(USERNAME)
                .password("example123")
                .client(clientDTO.toEntity())
                .build();

        LocalDateTime operationTimeStamp = getTimeStampOfOperation();

        activeAccountDTO = AccountDTO.builder()
                .iban(1)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(operationTimeStamp.minusMonths(7))
                .isBlocked(false)
                .isClosed(false)
                .build();

        activeAccountDetailDTO = AccountDetailDTO.builder()
                .appUser(appUserSecurityDTO.toEntity())
                .account(activeAccountDTO.toEntity())
                .accountAmount(ACCOUNT_AMOUNT)
                .build();

        activeAccountReceiverDTO = AccountDTO.builder()
                .iban(2)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(operationTimeStamp.minusMonths(8))
                .isBlocked(false)
                .isClosed(false)
                .build();

        activeAccountDetailReceiverDTO = AccountDetailDTO.builder()
                .appUser(appUserSecurityDTO.toEntity())
                .account(activeAccountReceiverDTO.toEntity())
                .accountAmount(ACCOUNT_AMOUNT_RECEIVER)
                .build();



        withdrawOperationDTO = getOperation(operationTimeStamp, OperationType.WITHDRAW);

         withdrawAccountTransactionDTO = AccountTransactionDTO.builder()
                 .operation(withdrawOperationDTO.toEntity())
                 .account(activeAccountDTO.toEntity())
                 .amount(WITHDRAW_AMOUNT)
                 .build();

        depositOperationDTO = getOperation(operationTimeStamp, OperationType.DEPOSIT);

        depositAccountTransactionDTO = AccountTransactionDTO.builder()
                .operation(depositOperationDTO.toEntity())
                .account(activeAccountDTO.toEntity())
                .amount(DEPOSIT_AMOUNT)
                .build();

        transferOperationDTO = getOperation(operationTimeStamp, OperationType.TRANSFER);


        transferAccountTransactionDTO = AccountTransactionDTO.builder()
                .operation(transferOperationDTO.toEntity())
                .account(activeAccountDTO.toEntity())
                .accountReceiver(activeAccountReceiverDTO.toEntity())
                .amount(TRANSFER_AMOUNT)
                .build();


        AccountDetail accountDetailExpectedCloseAmount= activeAccountDetailDTO.toEntity();
        accountDetailWithCloseAmountWithinLimitDTO = AccountDetailDTO.of(accountDetailExpectedCloseAmount);
        accountDetailWithCloseAmountWithinLimitDTO.setAccountAmount(AccountCost.CLOSING_TAXES.getCost());

        tearDown();
        bankController.create(bankDTO);
        clientController.create(clientDTO);
        appUserController.create(appUserSecurityDTO);


    }

    @After
    public void tearDown() {
        bankController.deleteAll();
        clientController.deleteAll();
        appUserController.deleteAll();
        accountController.deleteAll();
        accountDetailController.deleteAll();
        operationController.deleteAll();
        accountTransactionController.deleteAll();
    }


    @Test
    public void test1_withdrawAccountTransaction_EnoughFunds_returnAndSave1AccountTransactionToDB_decreaseAccountAmount_increaseMoneyCashOfClient() {
        setUpBasicDB();
        accountController.create(activeAccountDTO);
        accountDetailController.create(activeAccountDetailDTO);
        operationController.create(withdrawOperationDTO);

        accountTransactionController.withdraw(withdrawAccountTransactionDTO);


        List<AccountTransactionDTO> accountMeasureDTOList = accountTransactionController.listAll();
        Assert.assertEquals(1, accountMeasureDTOList.size());

        BigDecimal actualAccountAmount = accountDetailController
                .readByUsernameAndIban(activeAccountDetailDTO.getAppUser().getUsername(), activeAccountDetailDTO.getAccount()
                        .getIban()).getAccountAmount();
        Assert.assertEquals(ACCOUNT_AMOUNT.subtract(WITHDRAW_AMOUNT), actualAccountAmount);

        BigDecimal actualClientCash= clientController.readByFirstNameAndLastName(withdrawOperationDTO.getClientFirstname(), withdrawOperationDTO
                .getClientLastname()).getMoneyCash();
        Assert.assertEquals(MONEY_CASH_CLIENT.add(WITHDRAW_AMOUNT), actualClientCash);

    }

    @Test
    public void test2_depositAccountTransaction_EnoughFunds_returnAndSave1AccountTransactionToDB_increaseAccountAmount_decreaseMoneyCashOfClient() {
        setUpBasicDB();
        accountController.create(activeAccountDTO);
        accountDetailController.create(activeAccountDetailDTO);
        operationController.create(depositOperationDTO);

        accountTransactionController.deposit(depositAccountTransactionDTO);


        List<AccountTransactionDTO> accountTransactionDTOList = accountTransactionController.listAll();
        Assert.assertEquals(1, accountTransactionDTOList.size());

        BigDecimal actualAccountAmount = accountDetailController
                .readByUsernameAndIban(activeAccountDetailDTO.getAppUser().getUsername(), activeAccountDetailDTO.getAccount()
                        .getIban()).getAccountAmount();
        Assert.assertEquals(ACCOUNT_AMOUNT.add(DEPOSIT_AMOUNT), actualAccountAmount);

        BigDecimal actualClientCash= clientController.readByFirstNameAndLastName(depositOperationDTO.getClientFirstname(), depositOperationDTO
                .getClientLastname()).getMoneyCash();
        Assert.assertEquals(MONEY_CASH_CLIENT.subtract(DEPOSIT_AMOUNT), actualClientCash);

    }

    @Test
    public void test3_transferAccountTransaction_SameBank_EnoughFunds_returnAndSave2AccountTransactionToDB_decreaseAccountAmountSender_increaseAccountAmountReceiver() {
        setUpBasicDB();
        accountController.create(activeAccountDTO);
        accountDetailController.create(activeAccountDetailDTO);
        accountController.create(activeAccountReceiverDTO);
        accountDetailController.create(activeAccountDetailReceiverDTO);
        operationController.create(transferOperationDTO);

        accountTransactionController.transfer(transferAccountTransactionDTO);


        List<AccountTransactionDTO> accountTransactionDTOList = accountTransactionController.listAll();
        Assert.assertEquals(3, accountTransactionDTOList.size());

        BigDecimal actualAccountAmountSender = accountDetailController
                .readByUsernameAndIban(activeAccountDetailDTO.getAppUser().getUsername(), activeAccountDetailDTO.getAccount()
                        .getIban()).getAccountAmount();
        BigDecimal actualAccountAmountReceiver = accountDetailController
                .readByUsernameAndIban(activeAccountDetailReceiverDTO.getAppUser().getUsername(), activeAccountDetailReceiverDTO.getAccount()
                        .getIban()).getAccountAmount();

        Assert.assertEquals(ACCOUNT_AMOUNT.subtract(TRANSFER_AMOUNT), actualAccountAmountSender);
        Assert.assertEquals(ACCOUNT_AMOUNT_RECEIVER.add(TRANSFER_AMOUNT), actualAccountAmountReceiver);

    }




    private OperationDTO getOperation(LocalDateTime OperationTimeStamp, OperationType operationType) {
        return OperationDTO.builder()
                .username(appUserSecurityDTO.getUsername())
                .type(operationType)
                .clientFirstname(clientDTO.getFirstName())
                .clientLastname(clientDTO.getLastName())
                .clientCash(clientDTO.getMoneyCash())
                .timeStamp(OperationTimeStamp)
                .build();

    }

    private LocalDateTime getTimeStampOfOperation() {
        return LocalDateTime.of(2022, 3, 27, 5, 22, 22);
    }
}
