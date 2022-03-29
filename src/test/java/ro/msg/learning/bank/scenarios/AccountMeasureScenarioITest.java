package ro.msg.learning.bank.scenarios;

import org.junit.*;
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
public class AccountMeasureScenarioITest {

    public static final BigDecimal MONEY_CASH_CLIENT = new BigDecimal(100);
    public static final String USERNAME = "volodimir";
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
    private AccountDetailDTO accountDetailDTO;
    AccountDetailDTO accountDetailWithCloseAmountWithinLimitDTO;
    @Autowired
    private AccountController accountController;
    private AccountDTO activeAcountDTO;
    private AccountDTO blockAccountDTO;

    private OperationDTO blockOperationDTO;
    private OperationDTO unblockOperationDTO;
    private OperationDTO closeOperationDTO;

    @Autowired
    private AccountMeasureController accountMeasureController;
    private AccountMeasureDTO blockAccountMeasureDTO;
    private AccountMeasureDTO unblockAccountMeasureDTO;
    private AccountMeasureDTO closeAccountMeasureDTO;

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

        activeAcountDTO = AccountDTO.builder()
                .iban(1)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(operationTimeStamp.minusMonths(7))
                .isBlocked(false)
                .isClosed(false)
                .build();

        accountDetailDTO = AccountDetailDTO.builder()
                .appUser(appUserSecurityDTO.toEntity())
                .account(activeAcountDTO.toEntity())
                .accountAmount(new BigDecimal(1000))
                .build();

        LocalDateTime blockOperationTimeStamp = getTimeStampOfOperation();

        blockOperationDTO = getOperation(blockOperationTimeStamp, OperationType.BLOCKACCOUNT);

        blockAccountMeasureDTO = AccountMeasureDTO.builder()
                .operation(blockOperationDTO.toEntity())
                .account(activeAcountDTO.toEntity())
                .build();

        unblockOperationDTO = getOperation(blockOperationTimeStamp, OperationType.UNBLOCKACCOUNT);

        Account blockAccount = activeAcountDTO.toEntity();
        blockAccountDTO = AccountDTO.of(blockAccount);
        blockAccountDTO.setBlocked(true);

        unblockAccountMeasureDTO = AccountMeasureDTO.builder()
                .operation(unblockOperationDTO.toEntity())
                .account(blockAccountDTO.toEntity())
                .build();

        closeOperationDTO = getOperation(blockOperationTimeStamp, OperationType.CLOSEACCOUNT);
        closeAccountMeasureDTO = AccountMeasureDTO.builder()
                .operation(closeOperationDTO.toEntity())
                .account(activeAcountDTO.toEntity())
                .build();

        AccountDetail accountDetailExpectedCloseAmount=accountDetailDTO.toEntity();
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
        accountMeasureController.deleteAll();
    }


    @Test
    public void test1_takeBlockAccountMeasure_returnAndSave1AccountMeasureToDB_BlockAccount() {
        setUpBasicDB();
        accountController.create(activeAcountDTO);
        accountDetailController.create(accountDetailDTO);
        operationController.create(blockOperationDTO);

        accountMeasureController.takeMeasure(blockAccountMeasureDTO);

        List<AccountMeasureDTO> accountMeasureDTOList = accountMeasureController.listAll();
        Assert.assertEquals(1, accountMeasureDTOList.size());
        Assert.assertEquals(true, accountController.findByIban(blockAccountMeasureDTO.getAccount().getIban())
                .isBlocked());
    }


    @Test
    public void test2_takeUnblockAccountMeasure_returnAndSave1AccountMeasureToDB_UnblockAccount() {
        setUpBasicDB();
        accountController.create(blockAccountDTO);
        accountDetailController.create(accountDetailDTO);
        operationController.create(unblockOperationDTO);

        accountMeasureController.takeMeasure(unblockAccountMeasureDTO);

        List<AccountMeasureDTO> accountMeasureDTOList = accountMeasureController.listAll();
        Assert.assertEquals(1, accountMeasureDTOList.size());
        Assert.assertEquals(false, accountController.findByIban(unblockAccountMeasureDTO.getAccount().getIban())
                .isBlocked());
    }

    @Test
    public void test3_takeCloseAccountMeasureRequiredCloseLimit_returnAndSave1AccountMeasureToDB_CloseAccount() {
        setUpBasicDB();
        accountController.create(activeAcountDTO);
        accountDetailController.create(accountDetailWithCloseAmountWithinLimitDTO);
        operationController.create(closeOperationDTO);

        accountMeasureController.takeMeasure(closeAccountMeasureDTO);

        List<AccountMeasureDTO> accountMeasureDTOList = accountMeasureController.listAll();
        Assert.assertEquals(1, accountMeasureDTOList.size());
        Assert.assertEquals(true, accountController.findByIban(closeAccountMeasureDTO.getAccount().getIban())
                .isClosed());
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
