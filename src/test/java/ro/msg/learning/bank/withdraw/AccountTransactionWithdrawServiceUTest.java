package ro.msg.learning.bank.withdraw;

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
import ro.msg.learning.bank.services.AccountDetailService;
import ro.msg.learning.bank.services.AccountTransactionService;
import ro.msg.learning.bank.services.ClientService;
import ro.msg.learning.bank.services.AccountTransactionWithdrawService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
public class AccountTransactionWithdrawServiceUTest {
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
    public static final BigDecimal MONEY_CASH_CLIENT_OF_EXPECTED_TRANSACTION = new BigDecimal(200);

    public static final String FIRSTNAME_0 = "firstname0";
    public static final String LASTNAME_0 = "lastname0";
    public static final ClientDTO CLIENT_OF_EXPECTED_TRANSACTION_DTO = ClientDTO.builder()
            .firstName(FIRSTNAME_0)
            .lastName(LASTNAME_0)
            .moneyCash(MONEY_CASH_CLIENT_OF_EXPECTED_TRANSACTION)
            .build();
    public static final String FIRSTNAME_1 = "firstname1";
    public static final String LASTNAME_1 = "lastname1";
    public static final ClientDTO CLIENT_OF_PAST_TRANSACTION_DTO_1 = ClientDTO.builder()
            .firstName(FIRSTNAME_1)
            .lastName(LASTNAME_1)
            .moneyCash(MONEY_CASH_1)
            .build();
    public static final String FIRSTNAME_2 = "firstname2";
    public static final String LASTNAME_2 = "lastname2";

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
    public static final String USERNAME_2 = "username2";

    public static final BigDecimal TRANSACTION_AMOUNT_FROM_PAST_1 = new BigDecimal(100);
    public static final BigDecimal TRANSACTION_AMOUNT_FROM_PAST_2 = new BigDecimal(100);

    public static final BigDecimal AMOUNT_BELOW_WITHDRAW_VALUE = new BigDecimal(1);
    public static final BigDecimal AMOUNT_BELOW_MIN_PER_TRANSACTION_LIMIT = new BigDecimal(1);
    public static final BigDecimal AMOUNT_ABOVE_IN_DAY_MAX_LIMIT = new BigDecimal(1);
    public static final BigDecimal AMOUNT_ABOVE_MAX_MONTH_LIMIT = new BigDecimal(1);

    @Mock
    private AccountTransactionService accountTransactionService;
    @Mock
    private ClientService clientService;
    @Mock
    private AccountDetailService accountDetailService;


    @Test
    public void test1_withdraw_ActiveOpen_CurrentAccount_EnoughFundsAndActualOperationMeetsLimits_returnAccountTransaction() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MAX_PER_DAY.getLimit().subtract(TRANSACTION_AMOUNT_FROM_PAST_1);

        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 1);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        BigDecimal accountDetailAmount = expectedWithdrawAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }


    @Test(expected = NotOperableException.class)
    public void test2_withdraw_InactiveOpen_CurrentAccount_EnoughFundsAndActualOperationMeetsLimits_returnException() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, true, false);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MAX_PER_DAY.getLimit().subtract(TRANSACTION_AMOUNT_FROM_PAST_1);

        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 1);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        BigDecimal accountDetailAmount = expectedWithdrawAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = NotOperableException.class)
    public void test3_withdraw_ActiveClosed_CurrentAccount_EnoughFundsAndActualOperationMeetsLimits_returnException() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, true);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MAX_PER_DAY.getLimit().subtract(TRANSACTION_AMOUNT_FROM_PAST_1);

        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 1);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        BigDecimal accountDetailAmount = expectedWithdrawAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = InsufficientFundException.class)
    public void test4_withdraw_ActiveOpen_CurrentAccount_NotEnoughFundsAndActualOperationMeetsLimits_returnException() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MAX_PER_DAY.getLimit().subtract(TRANSACTION_AMOUNT_FROM_PAST_1);

        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 1);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        BigDecimal accountDetailAmount = expectedWithdrawAmount.subtract(AMOUNT_BELOW_WITHDRAW_VALUE);
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = LimitException.class)
    public void test5_withdraw_ActiveOpen_CurrentAccount_EnoughFundsButWithdrawAmountBelowMinLimit_returnException() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MIN_PER_TRANSACTION.getLimit().subtract(AMOUNT_BELOW_MIN_PER_TRANSACTION_LIMIT);

        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 1);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        BigDecimal accountDetailAmount = expectedWithdrawAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = LimitException.class)
    public void test6_withdraw_ActiveOpen_CurrentAccount_EnoughFundsButTotalWithdrawAmountInDayAboveMaxLimit_returnException() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MAX_PER_DAY.getLimit().subtract(TRANSACTION_AMOUNT_FROM_PAST_1)
                        .add(AMOUNT_ABOVE_IN_DAY_MAX_LIMIT);

        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 1);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        BigDecimal accountDetailAmount = expectedWithdrawAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }


    @Test
    public void test7_withdraw_ActiveOpen_SavingAccount_AccountAge1Year_EnoughFundsAndActualOperationMeetsLimits_returnAccountTransaction() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.SAVING_ACCOUNT, false, false);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MAX_PER_DAY.getLimit().subtract(TRANSACTION_AMOUNT_FROM_PAST_1);

        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 1);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        BigDecimal accountDetailAmount = expectedWithdrawAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = LimitException.class)
    public void test8_withdraw_ActiveOpen_SavingAccount_AccountAge1Year_EnoughFundsButWithdrawAmountAboveMaxLimitInMonth_returnException() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.SAVING_ACCOUNT, false, false);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MAX_PER_MONTH_ACCOUNT_SAVING.getLimit().subtract(TRANSACTION_AMOUNT_FROM_PAST_1)
                        .add(AMOUNT_ABOVE_MAX_MONTH_LIMIT);

        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 1);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        BigDecimal accountDetailAmount = expectedWithdrawAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test
    public void test9_withdraw_ActiveOpen_SavingAccount_AccountAge2Year_EnoughFundsAndWithdrawAmountAboveMaxLimitInMonth_returnAccountTransaction() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.SAVING_ACCOUNT, false, false);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MAX_PER_MONTH_ACCOUNT_SAVING.getLimit().subtract(TRANSACTION_AMOUNT_FROM_PAST_1)
                        .add(AMOUNT_ABOVE_MAX_MONTH_LIMIT);

        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 2);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        BigDecimal accountDetailAmount = expectedWithdrawAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = LimitException.class)
    public void test10_withdraw_ActiveOpen_SavingAccount_AccountAge1Year_EnoughFundsButWithdrawTimesAboveMaxLimitInMonth_returnException() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.SAVING_ACCOUNT, false, false);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MAX_PER_MONTH_ACCOUNT_SAVING.getLimit().subtract(TRANSACTION_AMOUNT_FROM_PAST_1);

        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 1);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        AccountTransactionDTO accountTransactionFromPastDTO2 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_2, FIRSTNAME_2, LASTNAME_2, MONEY_CASH_2, 2, TRANSACTION_AMOUNT_FROM_PAST_2);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO2);

        BigDecimal accountDetailAmount = expectedWithdrawAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test
    public void test11_withdraw_ActiveOpen_SavingAccount_AccountAge2Year_EnoughFundsAndWithdrawTimesAboveMaxLimitInMonth_returnException() {

        String transaction = "WITHDRAW";
        AccountDTO accountDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.SAVING_ACCOUNT, false, false);

        BigDecimal expectedWithdrawAmount =
                LimitAmount.WITHDRAW_MAX_PER_MONTH_ACCOUNT_SAVING.getLimit().subtract(TRANSACTION_AMOUNT_FROM_PAST_1)
                        .add(AMOUNT_ABOVE_MAX_MONTH_LIMIT);


        AccountTransactionDTO expectedAccountTransactionDTO =
                gettransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, expectedWithdrawAmount, 2);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, 1, TRANSACTION_AMOUNT_FROM_PAST_1);
        AccountTransactionDTO accountTransactionFromPastDTO2 =
                getSimilarTransactionDTOWhenAccountReachCustomAge(transaction, accountDTO, USERNAME_2, FIRSTNAME_2, LASTNAME_2, MONEY_CASH_2, 2, TRANSACTION_AMOUNT_FROM_PAST_2);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO2);

        BigDecimal accountDetailAmount = expectedWithdrawAmount;
        AccountDetailDTO accountDetailOfActualTransactionUserDTO =
                getAccountDetailDTO_ClientOfExpectedTransaction(APP_USER_OF_EXPECTED_TRANSACTION_DTO
                        .toEntity(), accountDTO
                        .toEntity(), accountDetailAmount);

        setUpMockReturnCalls(accountDTO, accountDetailOfActualTransactionUserDTO, expectedWithdrawAmount, accountDetailAmount, expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionWithdrawService accountTransactionWithdrawService =
                new AccountTransactionWithdrawService(accountTransactionService, clientService, accountDetailService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionWithdrawService.transactWithdraw(expectedAccountTransactionDTO);

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
                .setMoneyCash(MONEY_CASH_CLIENT_OF_EXPECTED_TRANSACTION.subtract(actualWithdrawAmount));
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

    private AccountTransactionDTO getSimilarTransactionDTOWhenAccountReachCustomAge(String transaction,
                                                                                    AccountDTO accountDTO,
                                                                                    String username1,
                                                                                    String firstname1,
                                                                                    String lastname1,
                                                                                    BigDecimal moneyCash1,
                                                                                    int hoursBeforeAgeAccountReach1Year,
                                                                                    BigDecimal transactionAmountFromPast1) {
        return AccountTransactionDTO.builder()
                .operation(getOperationDTO(transaction, username1, firstname1, lastname1, moneyCash1, TIMESTAMP_ACCOUNT_CREATED
                        .toEntity()
                        .plusYears(1).minusHours(hoursBeforeAgeAccountReach1Year)).toEntity())
                .account(accountDTO.toEntity())
                .amount(transactionAmountFromPast1)
                .build();
    }

    private AccountTransactionDTO gettransactionDTOWhenAccountReachCustomAge(String transaction,
                                                                             AccountDTO accountDTO,
                                                                             BigDecimal expectedWithdrawAmount,
                                                                             int accountAgeInYears) {
        return AccountTransactionDTO.builder()
                .operation(getOperationDTO(transaction, USERNAME_0, FIRSTNAME_0, LASTNAME_0, MONEY_CASH_CLIENT_OF_EXPECTED_TRANSACTION, TIMESTAMP_ACCOUNT_CREATED
                        .toEntity().plusYears(accountAgeInYears)).toEntity())
                .account(accountDTO.toEntity())
                .amount(expectedWithdrawAmount)
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
                .thenReturn(AccountTransactionWithdrawServiceUTest.CLIENT_OF_EXPECTED_TRANSACTION_DTO);
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
