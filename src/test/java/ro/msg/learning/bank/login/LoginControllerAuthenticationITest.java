package ro.msg.learning.bank.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import ro.msg.learning.bank.controllers.AppUserController;
import ro.msg.learning.bank.controllers.ClientController;
import ro.msg.learning.bank.controllers.LoginController;
import ro.msg.learning.bank.controllers.OperationController;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.Operation;
import ro.msg.learning.bank.entities.OperationType;
import ro.msg.learning.bank.repositories.AppUserRepository;

import java.io.UnsupportedEncodingException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
public class LoginControllerAuthenticationITest {
    public static final String SECURED_LOGIN_URL = "/api/login";
    public static final String CREATE_LOGIN_URL = "/api/logsessions";
    public static final String MOCK_USER_NAME = "mockUserName";
    public static final String MOCK_PASSWORD = "mockPassword";
    public static final String FIRST_NAME = "Volodimir";
    public static final String LAST_NAME = "ClientLastName";
    public static final BigDecimal MONEY_CASH = new BigDecimal(100);
    public static final String USERNAME_VALID = "volodimir";
    public static final String PASSWORD_VALID = "example123";
    public static final String MOCK_ROLE = "rol";
    @Autowired
    private MockMvc mvc;
    @Autowired
    ClientController clientController;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    AppUserController appUserController;
    @Autowired
    LoginController loginController;
    @Autowired
    OperationController operationController;

    @Before
    public void setUp()  {
        tearDown();

        ClientDTO clientDTO = ClientDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .moneyCash(MONEY_CASH)
                .build();

        clientController.create(clientDTO);

        AppUserSecurityDTO appUserSecurityDTO = AppUserSecurityDTO.builder()
                .username(USERNAME_VALID)
                .password(PASSWORD_VALID)
                .client(clientDTO.toEntity())
                .build();
        appUserController.create(appUserSecurityDTO);

        OperationDTO operationDTO = OperationDTO.builder()
                .username(appUserSecurityDTO.getUsername())
                .type(OperationType.LOGIN)
                .build();
        operationController.create(operationDTO);

        LoginDTO loginDTO = LoginDTO.builder()
                .operation(operationDTO.toEntity())
                .build();
        loginController.create(loginDTO);
    }

    @After
    public void tearDown() {
        loginController.deleteAll();
        appUserController.deleteAll();
        operationController.deleteAll();
        clientController.deleteAll();
    }

    @Test
    public void test1_OperateLoginUsingInvalidCredentials_Unauthenticated() throws Exception {

        String testUsername = "invalid";
        String testPassword = PASSWORD_VALID;
        ResultMatcher expectedAuthentication = unauthenticated();

        mvc.perform(formLogin(SECURED_LOGIN_URL).user("username", testUsername).password("password", testPassword))
                .andExpect(expectedAuthentication)
                .andDo(print())
                .andReturn();
    }

    @Test
    public void test2_OperateLoginUsingValidCredentials_Authenticated() throws Exception {

        ResultMatcher expectedAuthentication = authenticated().withUsername(USERNAME_VALID);

        mvc.perform(formLogin(SECURED_LOGIN_URL).user("username", USERNAME_VALID).password("password", PASSWORD_VALID))
                .andExpect(expectedAuthentication)
                .andDo(print())
                .andReturn();
    }

    @Test
    public void test3_OperateLoginUsingMockValidCredentials_AuthenticatedReturnLoginOperation() throws Exception {

        Operation operation = getOperation();

        MvcResult mvcResult =
                getMvcResult(operation);

        Assert.assertNotNull(getActualOperationDTO(mvcResult).get());

    }

    @Test
    @WithMockUser(username = MOCK_USER_NAME, password = MOCK_PASSWORD, roles = MOCK_ROLE)
    public void test4_OperateLoginUsingWithMockUser_AuthenticatedReturnLoginOperation() throws Exception {

        Operation operation = getOperation();

        MvcResult mvcResult = mvc.perform(post(CREATE_LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getOperationDTOAsString(operation))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Assert.assertNotNull(getActualOperationDTO(mvcResult).get());

    }

    private Operation getOperation() {
        return operationController.listAll().get(0).toEntity();
    }

    private MvcResult getMvcResult(Operation operation) throws Exception {
        return mvc.perform(post(LoginControllerAuthenticationITest.CREATE_LOGIN_URL)
                .with(user(LoginControllerAuthenticationITest.MOCK_USER_NAME)
                        .password(LoginControllerAuthenticationITest.MOCK_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getOperationDTOAsString(operation))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(LoginControllerAuthenticationITest.MOCK_USER_NAME))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    private Optional<LoginDTO> getActualOperationDTO(
            MvcResult postResult) throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = getObjectMapper();
        return Optional.of(
                objectMapper.readValue(postResult.getResponse().getContentAsString(), LoginDTO.class));
    }

    private String getOperationDTOAsString(Operation operation) throws JsonProcessingException {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.writeValueAsString(LoginDTO.builder()
                .operation(operation)
                .build());
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
