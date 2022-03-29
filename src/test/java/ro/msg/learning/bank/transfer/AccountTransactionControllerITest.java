package ro.msg.learning.bank.transfer;

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
import ro.msg.learning.bank.controllers.*;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
public class AccountTransactionControllerITest {
    public static final String ACCOUNT_TRANSACTION_URL = "/api/transfers";
    public static final String MOCK_USERNAME = "MockUsername";
    public static final String MOCK_PASSWORD = "MOCK_PASSWORD";
    public static final String MOCK_ROLE = "MOCK_ROLE";
    public static final String FIRST_NAME = "Volodimir";
    public static final String LAST_NAME = "ClientLastName";
    public static final BigDecimal MONEY_CASH = new BigDecimal(100);
    public static final String USERNAME_VALID = "volodimir";
    public static final String PASSWORD_VALID = "example123";

    @Autowired
    BankController bankController;
    @Autowired
    ClientController clientController;

    @Autowired
    AppUserController appUserController;

    @Autowired
    OperationController operationController;
    @Autowired
    AccountController accountController;
    @Autowired
    AccountDetailController accountDetailController;

    @Autowired
    private MockMvc mvc;

    private AccountTransactionDTO accountTransactionDTO;

    @Before
    public void setUp()  {
        tearDown();

        BankDTO bankDTO = BankDTO.builder()
                .name(BankCompany.BTBANK)
                .build();
        bankController.create(bankDTO);

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

        LocalDateTime timestampOfoperation = LocalDateTime.of(2022, 2, 15, 12, 12, 12);

        OperationDTO operationDTO = OperationDTO.builder()
                .username(appUserSecurityDTO.getUsername())
                .clientFirstname(FIRST_NAME)
                .clientLastname(LAST_NAME)
                .clientCash(MONEY_CASH)
                .type(OperationType.TRANSFER)
                .timeStamp(timestampOfoperation)
                .build();
        operationController.create(operationDTO);

        AccountDTO accountDTO = AccountDTO.builder()
                .iban(123)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(LocalDateTime.now().minusYears(1))
                .isBlocked(false)
                .isClosed(false)
                .build();
        accountController.create(accountDTO);
        AccountDTO accountReceiverDTO = AccountDTO.builder()
                .iban(245)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(LocalDateTime.now().minusYears(1))
                .isBlocked(false)
                .isClosed(false)
                .build();
        accountController.create(accountReceiverDTO);

        AccountDetailDTO accountDetailDTO = AccountDetailDTO.builder()
                .appUser(appUserSecurityDTO.toEntity())
                .account(accountDTO.toEntity())
                .accountAmount(new BigDecimal(1000))
                .build();
        accountDetailController.create(accountDetailDTO);

        AccountDetailDTO accountDetailReceiverDTO = AccountDetailDTO.builder()
                .appUser(appUserSecurityDTO.toEntity())
                .account(accountReceiverDTO.toEntity())
                .accountAmount(new BigDecimal(2000))
                .build();
        accountDetailController.create(accountDetailReceiverDTO);

        accountTransactionDTO = AccountTransactionDTO.builder()
                .operation(operationDTO.toEntity())
                .account(accountDTO.toEntity())
                .accountReceiver(accountReceiverDTO.toEntity())
                .amount(new BigDecimal(100))
                .build();

    }

    @After
    public void tearDown() {
        bankController.deleteAll();
        clientController.deleteAll();
        appUserController.deleteAll();
        operationController.deleteAll();
        accountController.deleteAll();
        accountDetailController.deleteAll();
    }

    @Test
    @WithMockUser(username = MOCK_USERNAME, password = MOCK_PASSWORD, roles = MOCK_ROLE)
    public void test_Operate_Transfer_AccountTransactionUsingWithMockUser_Return_StatusOK_NewAccountTransaction() throws Exception {

       MvcResult mvcResult = mvc.perform(post(ACCOUNT_TRANSACTION_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getAccountMeasureDTOAsString(accountTransactionDTO))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Assert.assertNotNull(getActualAccountTransactionDTO(mvcResult).get());

    }

    private String getAccountMeasureDTOAsString(
            AccountTransactionDTO accountTransactionDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.writeValueAsString(accountTransactionDTO);
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    private Optional<AccountTransactionDTO> getActualAccountTransactionDTO(
            MvcResult postResult) throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = getObjectMapper();
        return Optional.of(
                objectMapper.readValue(postResult.getResponse().getContentAsString(), AccountTransactionDTO.class));
    }

}
