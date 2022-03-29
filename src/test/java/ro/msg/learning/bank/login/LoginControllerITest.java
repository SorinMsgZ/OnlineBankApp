package ro.msg.learning.bank.login;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ro.msg.learning.bank.controllers.ClientController;
import ro.msg.learning.bank.controllers.LoginController;
import ro.msg.learning.bank.controllers.OperationController;
import ro.msg.learning.bank.dtos.AppUserDTO;
import ro.msg.learning.bank.dtos.ClientDTO;
import ro.msg.learning.bank.dtos.LoginDTO;
import ro.msg.learning.bank.dtos.OperationDTO;
import ro.msg.learning.bank.entities.AppUser;

import ro.msg.learning.bank.entities.OperationType;
import ro.msg.learning.bank.repositories.AppUserRepository;

import java.math.BigDecimal;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerITest {

    @Autowired
    LoginController loginController;
    @Autowired
    ClientController clientController;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    OperationController operationController;

    @Before
    public void setUp()  {
        ClientDTO clientDTO = ClientDTO.builder()
                .firstName("volodimir")
                .lastName("ClientLastName")
                .moneyCash(new BigDecimal(100))
                .build();

        clientController.create(clientDTO);

        AppUserDTO appUserDTO= AppUserDTO.builder()
                .username("volodimir")
                .client(clientDTO.toEntity())
                .build();

        AppUser appUser=appUserDTO.toEntity();
        appUser.setPassword("$2a$12$ewr3FVyJy4/Sd6s2vvQhJ.Iclu80LbDWRv9Do6UZbKsXf835Vnw66");

        appUserRepository.save(appUser);

        OperationDTO operationDTO= OperationDTO.builder()
                .username(appUserDTO.getUsername())
                .type(OperationType.LOGIN)
                .build();
        operationController.create(operationDTO);

    }

    @After
    public void tearDown()  {
        loginController.deleteAll();
    }

    @Test
    public void test_CreateLogin_ReturnLoginOperation() {

        LoginDTO loginDTO = LoginDTO.builder()
                .operation(operationController.listAll().get(0).toEntity())
                .build();
        LoginDTO actualLoginOperation = loginController.create(loginDTO);

        Assert.assertNotEquals(null, actualLoginOperation);

    }
}
