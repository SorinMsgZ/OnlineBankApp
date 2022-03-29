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
public class LoginScenarioITest {

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
    private LoginController loginController;
    @Autowired
    private OperationController operationController;

    private OperationDTO loginOperationDTO;
    private LoginDTO loginDTO;




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
        loginDTO = LoginDTO.builder()
                .operation(loginOperationDTO.toEntity())
                .build();


        bankController.deleteAll();
        bankController.create(bankDTO);
        clientController.deleteAll();
        clientController.create(clientDTO);
        appUserController.deleteAll();
        appUserController.create(appUserSecurityDTO);
        operationController.deleteAll();
        operationController.create(loginOperationDTO);
    }


    @Test
    public void test1_createLogin_returnAndSave1LoginToDB() {
        loginController.deleteAll();
        loginController.create(loginDTO);
        List<LoginDTO> loginDTOList = loginController.listAll();
        Assert.assertEquals(1, loginDTOList.size());
    }

    private OperationDTO getOperation(LocalDateTime loginOperationTimeStamp, OperationType login) {
        return OperationDTO.builder()
                .username(appUserSecurityDTO.getUsername())
                .type(login)
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
