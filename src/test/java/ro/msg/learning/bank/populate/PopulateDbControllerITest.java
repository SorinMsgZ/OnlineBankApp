package ro.msg.learning.bank.populate;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ro.msg.learning.bank.controllers.*;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class PopulateDbControllerITest {

    public static final int NUMBER_OF_NEW_ENTITY_CREATED = 1;
    @Autowired
    private BankController bankController;
    @Autowired
    private ClientController clientController;

    @Autowired
    private AppUserController appUserController;
    @Autowired
    private OperationController operationController;
    @Autowired
    private LoginController loginController;
    @Autowired
    private AccountController accountController;
    @Autowired
    private AccountMeasureController accountMeasureController;
    @Autowired
    private AccountTransactionController accountTransactionController;
    @Autowired
    private AccountDetailController accountDetailController;

    private BankDTO bankDTO;
    private ClientDTO clientDTO;
    private AppUserSecurityDTO appUserSecurityDTO;
    private OperationDTO operationDTO;
    private LoginDTO loginDTO;
    private AccountDTO accountDTO;
    private AccountMeasureDTO accountMeasureDTO;
    private AccountTransactionDTO accountTransactionDTO;
    private AccountDetailDTO accountDetailDTO;


    @Before
    public void setUp() throws Exception {
        tearDown();

        bankDTO = BankDTO.builder()
                .name(BankCompany.BTBANK)
                .build();

        clientDTO = ClientDTO.builder()
                .firstName("ClientFirstName")
                .lastName("ClientLastName")
                .moneyCash(new BigDecimal(100))
                .build();


        appUserSecurityDTO = AppUserSecurityDTO.builder()
                .username("volodimir")
                .password("parola")
                .client(clientDTO.toEntity())
                .build();

        LocalDateTime loginOperationTimeStamp=LocalDateTime.of(2022, 3,27, 5,22,22);

        operationDTO = OperationDTO.builder()
                .username(appUserSecurityDTO.getUsername())
                .type(OperationType.LOGIN)
                .clientFirstname(clientDTO.getFirstName())
                .clientLastname(clientDTO.getLastName())
                .clientCash(clientDTO.getMoneyCash())
                .timeStamp(loginOperationTimeStamp)
                .build();

        loginDTO = LoginDTO.builder()
                .operation(operationDTO.toEntity())
                .build();

        accountDTO = AccountDTO.builder()
                .iban(1)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(LocalDateTime.now().minusMonths(7))
                .isBlocked(false)
                .isClosed(false)
                .build();

        accountMeasureDTO = AccountMeasureDTO.builder()
                .operation(operationDTO.toEntity())
                .account(accountDTO.toEntity())
                .build();

        accountTransactionDTO = AccountTransactionDTO.builder()
                .operation(operationDTO.toEntity())
                .account(accountDTO.toEntity())
                .amount(new BigDecimal(20))
                .build();

        accountDetailDTO = AccountDetailDTO.builder()
                .appUser(appUserSecurityDTO.toEntity())
                .account(accountDTO.toEntity())
                .accountAmount(new BigDecimal(1000))
                .build();

    }

    @After
    public void tearDown()  {
        bankController.deleteAll();
        clientController.deleteAll();
        operationController.deleteAll();
        loginController.deleteAll();
        accountController.deleteAll();
        accountMeasureController.deleteAll();
        accountTransactionController.deleteAll();
        accountDetailController.deleteAll();
        appUserController.deleteAll();
    }

    @Test
    public void test_createEntities_returnAndSave1AdditionalofEachEntityToDB() {
        List<BankDTO> bankDTOListBefore = bankController.listAll();
        List<ClientDTO> clientDTOListBefore = clientController.listAll();
        List<AppUserDTO> appUserDTOListBefore = appUserController.listAll();
        List<OperationDTO> operationDTOListBefore = operationController.listAll();
        List<LoginDTO> loginDTOListBefore = loginController.listAll();
        List<AccountDTO> accountDTOListBefore = accountController.listAll();
        List<AccountMeasureDTO> accountMeasureDTOListBefore = accountMeasureController.listAll();
        List<AccountTransactionDTO> accountTransactionDTOListBefore = accountTransactionController.listAll();
        List<AccountDetailDTO> accountDetailDTOListBefore = accountDetailController.listAll();

        bankController.create(bankDTO);
        Assert.assertEquals(bankDTOListBefore.size() + NUMBER_OF_NEW_ENTITY_CREATED, bankController.listAll().size());

        clientController.create(clientDTO);
        Assert.assertEquals(clientDTOListBefore.size() + NUMBER_OF_NEW_ENTITY_CREATED, clientController.listAll().size());

        appUserController.create(appUserSecurityDTO);
        Assert.assertEquals(appUserDTOListBefore.size() + NUMBER_OF_NEW_ENTITY_CREATED, appUserController.listAll().size());

        operationController.create(operationDTO);
        Assert.assertEquals(operationDTOListBefore.size() + NUMBER_OF_NEW_ENTITY_CREATED, operationController.listAll().size());

        loginController.create(loginDTO);
        Assert.assertEquals(loginDTOListBefore.size() + NUMBER_OF_NEW_ENTITY_CREATED, loginController.listAll().size());

        accountController.create(accountDTO);
        Assert.assertEquals(accountDTOListBefore.size() + NUMBER_OF_NEW_ENTITY_CREATED, accountController.listAll().size());

        accountMeasureController.create(accountMeasureDTO);
        Assert.assertEquals(accountMeasureDTOListBefore.size() + NUMBER_OF_NEW_ENTITY_CREATED, accountMeasureController.listAll()
                .size());

        accountTransactionController.create(accountTransactionDTO);
        Assert.assertEquals(accountTransactionDTOListBefore.size() + NUMBER_OF_NEW_ENTITY_CREATED, accountTransactionController.listAll()
                .size());

        accountDetailController.create(accountDetailDTO);
        Assert.assertEquals(accountDetailDTOListBefore.size() + NUMBER_OF_NEW_ENTITY_CREATED, accountDetailController.listAll()
                .size());
    }

}
