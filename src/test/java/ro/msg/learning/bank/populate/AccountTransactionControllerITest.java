package ro.msg.learning.bank.populate;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ro.msg.learning.bank.controllers.*;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.AccountType;
import ro.msg.learning.bank.entities.BankCompany;
import ro.msg.learning.bank.entities.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountTransactionControllerITest {

    @Autowired
    private AccountTransactionController accountTransactionController;
    private AccountTransactionDTO accountTransactionDTO;
    @Autowired
    private OperationController operationController;
    @Autowired
    private ClientController clientController;
    @Autowired
    private AppUserController appUserController;
    @Autowired
    private AccountController accountController;
    private OperationDTO operationDTO;
    private AccountDTO accountDTO;
    private ClientDTO clientDTO;
    private AppUserSecurityDTO appUserSecurityDTO;
    @Autowired
    private BankController bankController;
    private BankDTO bankDTO;


    public void createEntities() throws Exception {
        deleteEntities();

        bankDTO = BankDTO.builder()
                .name(BankCompany.BTBANK)
                .build();
        bankController.create(bankDTO);

        clientDTO = ClientDTO.builder()
                .firstName("ClientFirstName")
                .lastName("ClientLastName")
                .moneyCash(new BigDecimal(100))
                .build();
        clientController.create(clientDTO);

        appUserSecurityDTO = AppUserSecurityDTO.builder()
                .username("volodimir")
                .password("parola")
                .client(clientDTO.toEntity())
                .build();
        appUserController.create(appUserSecurityDTO);

        LocalDateTime operationLocalDateTime = LocalDateTime.of(2022, 3, 28, 5, 22, 5);

        operationDTO = OperationDTO.builder()
                .username(appUserSecurityDTO.getUsername())
                .type(OperationType.LOGIN)
                .clientFirstname(clientDTO.getFirstName())
                .clientLastname(clientDTO.getLastName())
                .clientCash(clientDTO.getMoneyCash())
                .timeStamp(operationLocalDateTime)
                .build();
        operationController.create(operationDTO);

        accountDTO = AccountDTO.builder()
                .iban(1)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(operationLocalDateTime.minusMonths(7))
                .isBlocked(false)
                .isClosed(false)
                .build();
        accountController.create(accountDTO);

        accountTransactionDTO = AccountTransactionDTO.builder()
                .operation(operationDTO.toEntity())
                .account(accountDTO.toEntity())
                .accountReceiver(accountDTO.toEntity())
                .amount(new BigDecimal(20))
                .build();

    }


    public void deleteEntities()  {
        accountTransactionController.deleteAll();
        clientController.deleteAll();
        operationController.deleteAll();
        accountController.deleteAll();
        appUserController.deleteAll();
        bankController.deleteAll();
    }

    @Test
    public void test1_createAccountTransaction_returnAndSave1AccountTransactionToDB() throws Exception {
        createEntities();
        accountTransactionController.create(accountTransactionDTO);
        List<AccountTransactionDTO> accountTransactionDTOList = accountTransactionController.listAll();
        Assert.assertEquals(1, accountTransactionDTOList.size());

    }

}
