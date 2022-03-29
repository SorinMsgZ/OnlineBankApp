package ro.msg.learning.bank.login;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ro.msg.learning.bank.controllers.LoginController;
import ro.msg.learning.bank.dtos.LoginDTO;
import ro.msg.learning.bank.dtos.OperationDTOFactory;
import ro.msg.learning.bank.services.LoginService;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
public class LoginControllerUTest {

    @Mock
    private LoginService loginService;

    @Test
    public void test_CreateLogin_ReturnLoginOperation() {
        LoginDTO loginDTO = getLoginDTO(new OperationDTOFactory());
        LoginController loginController = new LoginController(loginService);

        LoginDTO expectedLoginOperation = loginController.create(loginDTO);
        Assert.assertNotNull(expectedLoginOperation);
    }

    private LoginDTO getLoginDTO(OperationDTOFactory loginOperation) {
        LoginDTO loginDTO = (LoginDTO) loginOperation.getOperationDTO("LOGIN");
        when(loginService.create(loginDTO)).thenReturn(loginDTO);
        return loginDTO;
    }


}
