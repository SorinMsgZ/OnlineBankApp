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

import ro.msg.learning.bank.entities.BankCompany;
import ro.msg.learning.bank.entities.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class OperationControllerITest {


    @Autowired
    private OperationController operationController;
    @Autowired
    private ClientController clientController;
    @Autowired
    private AppUserController appUserController;
    @Autowired
    private AccountController accountController;
    private OperationDTO operationDTO;
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

        operationDTO = OperationDTO.builder()
                .username(appUserSecurityDTO.getUsername())
                .type(OperationType.LOGIN)
                .clientFirstname(clientDTO.getFirstName())
                .clientLastname(clientDTO.getLastName())
                .clientCash(clientDTO.getMoneyCash())
                .timeStamp(LocalDateTime.now())
                .build();

    }

    @After
    public void tearDown()  {
        clientController.deleteAll();
        operationController.deleteAll();
        appUserController.deleteAll();
        bankController.deleteAll();
    }

    @Test
    public void test_createOperation_returnAndSave1OperationToDB() {
        operationController.create(operationDTO);
        List<OperationDTO> operationDTOList=operationController.listAll();
        Assert.assertEquals(1, operationDTOList.size());
    }
}
