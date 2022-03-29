package ro.msg.learning.bank.scenarios;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ro.msg.learning.bank.controllers.AccountDetailController;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.AccountType;
import ro.msg.learning.bank.entities.BankCompany;
import ro.msg.learning.bank.entities.OperationType;
import ro.msg.learning.bank.security.DataBaseUserService;
import ro.msg.learning.bank.services.AccountDetailService;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AccountDetailController.class)
@TestPropertySource("classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AccountDetailControllerITest {

    public static final String ACCOUNT_DETAIL_URL = "/api/accountdetails/";
    public static final String MOCK_USERNAME = "MockUsername";
    public static final String MOCK_PASSWORD = "MOCK_PASSWORD";
    public static final String MOCK_ROLE = "MOCK_ROLE";
    public static final String FIRST_NAME = "Volodimir";
    public static final String LAST_NAME = "ClientLastName";
    public static final BigDecimal MONEY_CASH = new BigDecimal(100);
    public static final String USERNAME_VALID = "volodimir";
    public static final String PASSWORD_VALID = "example123";
    public static final int IBAN = 123;

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AccountDetailService accountDetailService;
    @MockBean
    private DataBaseUserService dataBaseUserService;
    private AccountDetailDTO accountDetailDTO;

    @Before
    public void setUp() {

        ClientDTO clientDTO = ClientDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .moneyCash(MONEY_CASH)
                .build();


        AppUserSecurityDTO appUserSecurityDTO = AppUserSecurityDTO.builder()
                .username(USERNAME_VALID)
                .password(PASSWORD_VALID)
                .client(clientDTO.toEntity())
                .build();

        LocalDateTime timestampOfoperation = LocalDateTime.of(2022, 2, 15, 12, 12, 12);

        OperationDTO operationDTO = OperationDTO.builder()
                .username(appUserSecurityDTO.getUsername())
                .clientFirstname(FIRST_NAME)
                .clientLastname(LAST_NAME)
                .clientCash(MONEY_CASH)
                .type(OperationType.TRANSFER)
                .timeStamp(timestampOfoperation)
                .build();

        AccountDTO accountDTO = AccountDTO.builder()
                .iban(IBAN)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(LocalDateTime.now().minusYears(1))
                .isBlocked(false)
                .isClosed(false)
                .build();


        accountDetailDTO = AccountDetailDTO.builder()
                .appUser(appUserSecurityDTO.toEntity())
                .account(accountDTO.toEntity())
                .accountAmount(new BigDecimal(1000))
                .build();

    }

    @Test
    @WithMockUser(username = MOCK_USERNAME, password = MOCK_PASSWORD, roles = MOCK_ROLE)
    public void test_readAccountDetailByUsernameAndIbanUsingWithMockUser_Return_StatusOK() throws Exception {

        when(accountDetailService.readByUsernameAndIban(any(String.class), anyInt())).thenReturn(accountDetailDTO);

        MvcResult mvcResult =
                mvc.perform(MockMvcRequestBuilders.get(ACCOUNT_DETAIL_URL + USERNAME_VALID + "/" + IBAN)
                        .accept(MediaType.APPLICATION_JSON))
                        .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());

    }

}