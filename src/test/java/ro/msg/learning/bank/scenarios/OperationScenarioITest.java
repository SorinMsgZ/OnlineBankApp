package ro.msg.learning.bank.scenarios;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OperationScenarioITest {

    public static final BigDecimal MONEY_CASH_CLIENT = new BigDecimal(100);
    public static final String USERNAME = "volodimir";
    @Autowired
    private ClientController clientController;
    private ClientDTO clientDTO;
    @Autowired
    private AppUserController appUserController;
    private AppUserSecurityDTO appUserSecurityDTO;
    @Autowired
    private BankController bankController;
    private BankDTO bankDTO;

    @Autowired
    private OperationController operationController;
    private OperationDTO loginOperationDTO;



    @Before
    public void setUp()  {
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

        LocalDateTime loginOperationTimeStamp = getTimeStampOfOperation();

        loginOperationDTO = getOperation(loginOperationTimeStamp, OperationType.LOGIN);

    }



    @Test
    public void test1_createBank_returnAndSave1BankToDB() {
        bankController.deleteAll();

        bankController.create(bankDTO);
        Assert.assertEquals(1, bankController.listAll().size());
    }

    @Test
    public void test2_createClient_returnAndSave1ClientToDB() {
        clientController.deleteAll();

        clientController.create(clientDTO);
        List<ClientDTO> clientDTOList = clientController.listAll();
        Assert.assertEquals(1, clientDTOList.size());
    }

    @Test
    public void test3_createAppUser_returnAndSave1AppUserToDB() {
        appUserController.deleteAll();

        appUserController.create(appUserSecurityDTO);
        List<AppUserDTO> appUserDTOList = appUserController.listAll();
        Assert.assertEquals(1, appUserDTOList.size());
    }

    @Test
    public void test4_createLoginOperation_returnAndSave1OperationToDB() {
        operationController.deleteAll();

        operationController.create(loginOperationDTO);
        List<OperationDTO> operationDTOList = operationController.listAll();
        Assert.assertEquals(1, operationDTOList.size());
    }

    private OperationDTO getOperation(LocalDateTime loginOperationTimeStamp, OperationType operationType) {
        return OperationDTO.builder()
                .username(appUserSecurityDTO.getUsername())
                .type(operationType)
                .clientFirstname(clientDTO.getFirstName())
                .clientLastname(clientDTO.getLastName())
                .clientCash(clientDTO.getMoneyCash())
                .timeStamp(loginOperationTimeStamp)
                .build();
    }

    private LocalDateTime getTimeStampOfOperation() {
        return LocalDateTime.of(2022, 3, 27, 5, 22, 22);
    }

}
