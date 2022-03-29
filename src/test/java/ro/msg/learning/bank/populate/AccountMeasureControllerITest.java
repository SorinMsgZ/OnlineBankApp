package ro.msg.learning.bank.populate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class AccountMeasureControllerITest {

    @Autowired
    private AccountMeasureController accountMeasureController;
    private AccountMeasureDTO accountMeasureDTO;
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

    @Before
    public void setUp() throws Exception {
        tearDown();

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

        LocalDateTime operationTimeStamp= LocalDateTime.of(2022,3,28,15,10,10);

        operationDTO = OperationDTO.builder()
                .username(appUserSecurityDTO.getUsername())
                .type(OperationType.LOGIN)
                .clientFirstname(clientDTO.getFirstName())
                .clientLastname(clientDTO.getLastName())
                .clientCash(clientDTO.getMoneyCash())
                .timeStamp(operationTimeStamp)
                .build();
        operationController.create(operationDTO);

        accountDTO = AccountDTO.builder()
                .iban(1)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(LocalDateTime.now().minusMonths(7))
                .isBlocked(false)
                .isClosed(false)
                .build();
        accountController.create(accountDTO);

        accountMeasureDTO = AccountMeasureDTO.builder()
                .operation(operationDTO.toEntity())
                .account(accountDTO.toEntity())
                .build();

    }

    @After
    public void tearDown()  {
        accountMeasureController.deleteAll();
        clientController.deleteAll();
        operationController.deleteAll();
        accountController.deleteAll();
        accountMeasureController.deleteAll();
        appUserController.deleteAll();
        bankController.deleteAll();

    }

    @Test
    public void test_createAccountMeasure_returnAndSave1AccountMeasureToDB() {
        accountMeasureController.create(accountMeasureDTO);
        List<AccountMeasureDTO> accountMeasureDTOList = accountMeasureController.listAll();
        Assert.assertEquals(1, accountMeasureDTOList.size());
    }
}
