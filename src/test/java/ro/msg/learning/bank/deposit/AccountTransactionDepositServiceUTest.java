package ro.msg.learning.bank.deposit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.*;
import ro.msg.learning.bank.exceptions.InsufficientFundException;
import ro.msg.learning.bank.exceptions.LimitException;
import ro.msg.learning.bank.exceptions.NotOperableException;
import ro.msg.learning.bank.services.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
public class AccountTransactionDepositServiceUTest {
    public static final int IBAN = 123;
    public static final int YEAR_ACCOUNT_CREATED = 2000;
    public static final int MONTH_ACCOUNT_CREATED = 5;
    public static final int DAY_OF_MONTH_ACCOUNT_CREATED = 13;
    public static final int HOUR_ACCOUNT_CREATED = 12;
    public static final int MINUTE_ACCOUNT_CREATED = 20;
    public static final int SECOND_ACCOUNT_CREATED = 30;
    public static final LocalDateTimeDTO TIMESTAMP_ACCOUNT_CREATED = LocalDateTimeDTO.builder()
            .year(YEAR_ACCOUNT_CREATED)
            .month(MONTH_ACCOUNT_CREATED)
            .dayOfMonth(DAY_OF_MONTH_ACCOUNT_CREATED)
            .hour(HOUR_ACCOUNT_CREATED)
            .minute(MINUTE_ACCOUNT_CREATED)
            .second(SECOND_ACCOUNT_CREATED)
            .build();

    public static final BigDecimal MONEY_CASH_1 = new BigDecimal(50);
    public static final BigDecimal MONEY_CASH_2 = new BigDecimal(120);
    public static final BigDecimal MONEY_CASH_CLIENT = new BigDecimal(200);

    public static final String FIRSTNAME_0 = "firstname0";
    public static final String LASTNAME_0 = "lastname0";
    public static final ClientDTO CLIENT_OF_EXPECTED_TRANSACTION_DTO = ClientDTO.builder()
            .firstName(FIRSTNAME_0)
            .lastName(LASTNAME_0)
            .moneyCash(MONEY_CASH_CLIENT)
            .build();
    public static final String FIRSTNAME_1 = "firstname1";
    public static final String LASTNAME_1 = "lastname1";
    public static final ClientDTO CLIENT_OF_PAST_TRANSACTION_DTO_1 = ClientDTO.builder()
            .firstName(FIRSTNAME_1)
            .lastName(LASTNAME_1)
            .moneyCash(MONEY_CASH_1)
            .build();

    public static final String USERNAME_0 = "username0";
    public static final AppUserDTO APP_USER_OF_EXPECTED_TRANSACTION_DTO = AppUserDTO.builder()
            .username(USERNAME_0)
            .client(CLIENT_OF_EXPECTED_TRANSACTION_DTO.toEntity())
            .build();
    public static final String USERNAME_1 = "username1";
    public static final AppUserDTO APP_USER_OF_PAST_TRANSACTION_DTO_1 = AppUserDTO.builder()
            .username(USERNAME_1)
            .client(CLIENT_OF_PAST_TRANSACTION_DTO_1.toEntity())
            .build();
    public static final BigDecimal AMOUNT_BELOW_MIN_DEPOSIT_LIMIT = new BigDecimal(1);

    @Mock
    private AccountTransactionService accountTransactionService;
    @Mock
    private ClientService clientService;
    @Mock
    private AccountDetailService accountDetailService;


    @Test
    public void test1_deposit_ActiveOpen_Account_EnoughFundsAndActualOperationMeetsLimits_returnAccountTransaction() {

        String transaction = "DEPOSIT";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false);

        BigDecimal expectedDepositAmount = LimitAmount.DEPOSIT_MIN.getLimit();

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountDTO, expectedDepositAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();

        BigDecimal accountDetailAmount = expectedDepositAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedDepositAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionDepositService accountTransactionDepositService =
                new AccountTransactionDepositService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionDepositService.transactDeposit(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    private AccountTransactionDTO getAccountTransactionDTO(String transaction, AccountDTO accountDTO,
                                                           BigDecimal expectedDepositAmount) {
        return AccountTransactionDTO.builder()
                .operation(getOperationDTO(transaction, USERNAME_0, FIRSTNAME_0, LASTNAME_0, MONEY_CASH_CLIENT, TIMESTAMP_ACCOUNT_CREATED
                        .toEntity().plusYears(1)).toEntity())
                .account(accountDTO.toEntity())
                .amount(expectedDepositAmount)
                .build();
    }

    @Test(expected = InsufficientFundException.class)
    public void test2_deposit_ActiveOpen_Account_NotEnoughFundsAndActualOperationMeetsLimits_returnException() {

        String transaction = "DEPOSIT";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false);

        BigDecimal expectedDepositAmount = MONEY_CASH_CLIENT.add(new BigDecimal(1));

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountDTO, expectedDepositAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();

        BigDecimal accountDetailAmount = expectedDepositAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedDepositAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionDepositService accountTransactionDepositService =
                new AccountTransactionDepositService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionDepositService.transactDeposit(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = LimitException.class)
    public void test3_deposit_ActiveOpen_Account_EnoughFundsButMinimumDepositOutsideLimits_returnException() {

        String transaction = "DEPOSIT";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false);

        BigDecimal expectedDepositAmount = LimitAmount.DEPOSIT_MIN.getLimit().subtract(AMOUNT_BELOW_MIN_DEPOSIT_LIMIT);

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountDTO, expectedDepositAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();

        BigDecimal accountDetailAmount = expectedDepositAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedDepositAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionDepositService accountTransactionDepositService =
                new AccountTransactionDepositService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionDepositService.transactDeposit(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = NotOperableException.class)
    public void test4_deposit_InactiveOpen_Account_EnoughFundsAndActualOperationMeetsLimits_returnException() {

        String transaction = "DEPOSIT";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, true, false);

        BigDecimal expectedDepositAmount = LimitAmount.DEPOSIT_MIN.getLimit();

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountDTO, expectedDepositAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();

        BigDecimal accountDetailAmount = expectedDepositAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedDepositAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionDepositService accountTransactionDepositService =
                new AccountTransactionDepositService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionDepositService.transactDeposit(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = NotOperableException.class)
    public void test5_deposit_ActiveClose_Account_EnoughFundsAndActualOperationMeetsLimits_returnException() {

        String transaction = "DEPOSIT";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, true);

        BigDecimal expectedDepositAmount = LimitAmount.DEPOSIT_MIN.getLimit();

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountDTO, expectedDepositAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();

        BigDecimal accountDetailAmount = expectedDepositAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedDepositAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionDepositService accountTransactionDepositService =
                new AccountTransactionDepositService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionDepositService.transactDeposit(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }


    private void setUpMockReturnCalls(AccountDTO accountDTO, AccountDetailDTO accountDetailOfActualTransactionUserDTO,
                                      BigDecimal actualWithdrawAmount, BigDecimal accountDetailAmount,
                                      AccountTransactionDTO expectedAccountTransactionDTO,
                                      List<AccountTransactionDTO> accountTransactionFromPastDTOList) {
        getMockReturnCall_accountTransactionDTO(expectedAccountTransactionDTO);

        getMockReturnCall_returnAccountTransactionFromPastDTOList_ObjectiveIs_GetTotalWithdrawInDayUnderLimit(accountTransactionFromPastDTOList);

        ClientDTO updatedClientOfActualTransactionDTO = CLIENT_OF_EXPECTED_TRANSACTION_DTO;
        updatedClientOfActualTransactionDTO
                .setMoneyCash(MONEY_CASH_CLIENT.subtract(actualWithdrawAmount));
        getMockReturnCall_returnUpdatedClientDTOofExpectedTransaction_ObjectiveIs_updateCashByAddingAmountOfTransaction(updatedClientOfActualTransactionDTO);

        getMockReturnCall_returnClientDTOOfExpectedTransaction_ObjectiveIs_getCashOfExpectedClientDTOOfTransactionSearchedByFirstnameAndLastname();

        getMockReturnCall_returnAccountDetailDTOofExpectedTransactionUser_Objective_Is_getExpectedDetailDTOOfTransactionByUsernameAndIbanOfTransaction(accountDetailOfActualTransactionUserDTO);

        List<AccountDetailDTO> mockAccountDetailDTOList = new ArrayList<>();
        mockAccountDetailDTOList.add(accountDetailOfActualTransactionUserDTO);
        mockAccountDetailDTOList.add(getAccountDetailDTO_ClientOfPastTransaction(APP_USER_OF_PAST_TRANSACTION_DTO_1
                .toEntity(), accountDTO
                .toEntity(), accountDetailAmount));
        getMockReturnCall_returnAccountDetailDTOS_ObjectiveIs_getListOfAccountDetailDTOSearchedByIban(mockAccountDetailDTOList);
    }

    private OperationDTO getOperationDTO(String transaction, String username, String clientFirstname,
                                         String clientLastname, BigDecimal clientCash, LocalDateTime localDateTime) {
        return OperationDTO.builder()
                .type(OperationType.valueOf(transaction))
                .username(username)
                .clientFirstname(clientFirstname)
                .clientLastname(clientLastname)
                .clientCash(clientCash)
                .timeStamp(localDateTime)
                .build();
    }

    private AccountDTO getAccountDTO(LocalDateTime accountCreatedDate, AccountType accountType,
                                     boolean isBlocked,
                                     boolean isClosed) {
        return AccountDTO.builder()
                .iban(IBAN)
                .type(accountType)
                .creatingDate(accountCreatedDate)
                .isBlocked(isBlocked)
                .isClosed(isClosed)
                .build();
    }

    private AccountDetailDTO getAccountDetailDTO_ClientOfExpectedTransaction(AppUser appUser, Account account,
                                                                             BigDecimal accountAmount) {
        return getAccountDetailDTO(appUser, account, accountAmount);
    }

    private AccountDetailDTO getAccountDetailDTO(AppUser appUser, Account account, BigDecimal accountAmount) {
        return AccountDetailDTO.builder()
                .appUser(appUser)
                .account(account)
                .accountAmount(accountAmount)
                .build();
    }

    private AccountDetailDTO getAccountDetailDTO_ClientOfPastTransaction(AppUser appUser, Account account,
                                                                         BigDecimal accountAmount) {
        return getAccountDetailDTO(appUser, account, accountAmount);
    }


    private void getMockReturnCall_accountTransactionDTO(AccountTransactionDTO accountTransactionDTO) {
        when(accountTransactionService.create(any(AccountTransactionDTO.class))).thenReturn(accountTransactionDTO);
    }

    private void getMockReturnCall_returnAccountTransactionFromPastDTOList_ObjectiveIs_GetTotalWithdrawInDayUnderLimit(
            List<AccountTransactionDTO> mockAccountTransactionDTOList) {

        when(accountTransactionService.readByOperationType(OperationType.WITHDRAW))
                .thenReturn(mockAccountTransactionDTOList);
    }

    private void getMockReturnCall_returnUpdatedClientDTOofExpectedTransaction_ObjectiveIs_updateCashByAddingAmountOfTransaction(
            ClientDTO expectedClientDTO) {

        when(clientService.updateByFirstNameAndLastName(any(String.class), any(String.class), any(ClientDTO.class)))
                .thenReturn(expectedClientDTO);
    }

    private void getMockReturnCall_returnClientDTOOfExpectedTransaction_ObjectiveIs_getCashOfExpectedClientDTOOfTransactionSearchedByFirstnameAndLastname(
    ) {
        when(clientService
                .readByFirstNameAndLastName(any(String.class), any(String.class)))
                .thenReturn(AccountTransactionDepositServiceUTest.CLIENT_OF_EXPECTED_TRANSACTION_DTO);
    }

    private void getMockReturnCall_returnAccountDetailDTOofExpectedTransactionUser_Objective_Is_getExpectedDetailDTOOfTransactionByUsernameAndIbanOfTransaction(
            AccountDetailDTO accountDetailDTO) {

        when(accountDetailService.readByUsernameAndIban(any(String.class), anyInt())).thenReturn(accountDetailDTO);
    }

    private void getMockReturnCall_returnAccountDetailDTOS_ObjectiveIs_getListOfAccountDetailDTOSearchedByIban(
            List<AccountDetailDTO> accountDetailDTOS) {

        when(accountDetailService.readByAccountIban(anyInt())).thenReturn(accountDetailDTOS);

    }

}
