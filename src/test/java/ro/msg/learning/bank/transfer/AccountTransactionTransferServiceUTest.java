package ro.msg.learning.bank.transfer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.*;
import ro.msg.learning.bank.exceptions.LimitException;
import ro.msg.learning.bank.exceptions.NotOperableException;
import ro.msg.learning.bank.services.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
public class AccountTransactionTransferServiceUTest {
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

    public static final String FIRSTNAME_1 = "firstname1";
    public static final String LASTNAME_1 = "lastname1";

    public static final String FIRSTNAME_2 = "firstname2";
    public static final String LASTNAME_2 = "lastname2";

    public static final String USERNAME_0 = "username0";

    public static final String USERNAME_1 = "username1";

    public static final String USERNAME_2 = "username2";

    public static final BigDecimal TRANSACTION_AMOUNT_FROM_PAST_1 = new BigDecimal(1500);
    public static final BigDecimal TRANSACTION_AMOUNT_FROM_PAST_2 = new BigDecimal(1500);

    public static final BigDecimal TRANSFER_MAX_PER_DAY_LIMIT =
            LimitAmount.TRANSFER_MAX_PER_DAY.getLimit();
    public static final BigDecimal TRANSFER_MAX_PER_TRANSFER_LIMIT =
            LimitAmount.TRANSFER_MAX_PER_TRANSACTION.getLimit();

    public static final BigDecimal AMOUNT_OUTSIDE_DAY_LIMIT = new BigDecimal(1);
    public static final BigDecimal AMOUNT_OUTSIDE_1TIME_TRANSFER_LIMIT = new BigDecimal(1);
    public static final BigDecimal AMOUNT_BELOW_MIN_LIMIT_PER_TRANSFER = new BigDecimal(0);

    @Mock
    private AccountTransactionService accountTransactionService;

    @Mock
    private AccountTransactionWithdrawService accountTransactionWithdrawService;
    @Mock
    private AccountTransactionDepositService accountTransactionDepositService;


    @Test
    public void test1_transfer_FromActiveOpen_CurrentAccount_ToActiveOpen_CurrentAccount_EnoughFundsAndActualOperationMeetsLimits_returnAccountTransaction() {

        String transaction = "TRANSFER";
        AccountDTO accountSenderDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        AccountDTO accountReceiverDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        BigDecimal expectedTransferAmount = TRANSFER_MAX_PER_TRANSFER_LIMIT;

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountSenderDTO, accountReceiverDTO, expectedTransferAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, TRANSACTION_AMOUNT_FROM_PAST_1);
        AccountTransactionDTO accountTransactionFromPastDTO2 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_2, FIRSTNAME_2, LASTNAME_2, MONEY_CASH_2, TRANSACTION_AMOUNT_FROM_PAST_2);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO2);

        setUpMockReturnCalls(expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionTransferService accountTransactionTransferService =
                new AccountTransactionTransferService(accountTransactionService, accountTransactionWithdrawService, accountTransactionDepositService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionTransferService.transactTransfer(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    private AccountTransactionDTO getAccountTransactionDTO(String transaction, AccountDTO accountDTO,
                                                           AccountDTO accountReceiverDTO,
                                                           BigDecimal expectedTransferAmount) {
        return AccountTransactionDTO.builder()
                .operation(getOperationDTO(transaction, USERNAME_0, FIRSTNAME_0, LASTNAME_0, MONEY_CASH_CLIENT_OF_EXPECTED_TRANSACTION, TIMESTAMP_ACCOUNT_CREATED
                        .toEntity().plusYears(1)).toEntity())
                .account(accountDTO.toEntity())
                .accountReceiver(accountReceiverDTO.toEntity())
                .amount(expectedTransferAmount)
                .build();
    }

    @Test
    public void test2_transfer_FromActiveOpen_SavingAccount_ToActiveOpen_CurrentAccount_EnoughFundsAndActualOperationMeetsLimits_returnAccountTransaction() {

        String transaction = "TRANSFER";
        AccountDTO accountSenderDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.SAVING_ACCOUNT, false, false, BankCompany.BTBANK);

        AccountDTO accountReceiverDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        BigDecimal expectedTransferAmount = TRANSFER_MAX_PER_TRANSFER_LIMIT;

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountSenderDTO, accountReceiverDTO, expectedTransferAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        setUpMockReturnCalls(expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionTransferService accountTransactionTransferService =
                new AccountTransactionTransferService(accountTransactionService, accountTransactionWithdrawService, accountTransactionDepositService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionTransferService.transactTransfer(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = NotOperableException.class)
    public void test3_transfer_FromInactiveOpen_CurrentAccount_ToActiveOpen_CurrentAccount_EnoughFundsAndActualOperationMeetsLimits_returnException() {

        String transaction = "TRANSFER";
        AccountDTO accountSenderDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, true, false, BankCompany.BTBANK);

        AccountDTO accountReceiverDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        BigDecimal expectedTransferAmount =TRANSFER_MAX_PER_TRANSFER_LIMIT;

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountSenderDTO, accountReceiverDTO, expectedTransferAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        setUpMockReturnCalls(expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionTransferService accountTransactionTransferService =
                new AccountTransactionTransferService(accountTransactionService, accountTransactionWithdrawService, accountTransactionDepositService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionTransferService.transactTransfer(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = NotOperableException.class)
    public void test4_transfer_FromActiveClosed_CurrentAccount_ToActiveOpen_CurrentAccount_EnoughFundsAndActualOperationMeetsLimits_returnException() {

        String transaction = "TRANSFER";
        AccountDTO accountSenderDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, true, BankCompany.BTBANK);

        AccountDTO accountReceiverDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        BigDecimal expectedTransferAmount =TRANSFER_MAX_PER_TRANSFER_LIMIT;

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountSenderDTO, accountReceiverDTO, expectedTransferAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        setUpMockReturnCalls(expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionTransferService accountTransactionTransferService =
                new AccountTransactionTransferService(accountTransactionService, accountTransactionWithdrawService, accountTransactionDepositService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionTransferService.transactTransfer(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = NotOperableException.class)
    public void test5_transfer_FromActiveOpen_CurrentAccount_ToInactiveOpen_CurrentAccount_EnoughFundsAndActualOperationMeetsLimits_returnException() {

        String transaction = "TRANSFER";
        AccountDTO accountSenderDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        AccountDTO accountReceiverDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, true, false, BankCompany.BTBANK);

        BigDecimal expectedTransferAmount =TRANSFER_MAX_PER_TRANSFER_LIMIT;

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountSenderDTO, accountReceiverDTO, expectedTransferAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        setUpMockReturnCalls(expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionTransferService accountTransactionTransferService =
                new AccountTransactionTransferService(accountTransactionService, accountTransactionWithdrawService, accountTransactionDepositService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionTransferService.transactTransfer(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = NotOperableException.class)
    public void test6_transfer_FromActiveOpen_CurrentAccount_ToActiveClosed_CurrentAccount_EnoughFundsAndActualOperationMeetsLimits_returnException() {

        String transaction = "TRANSFER";
        AccountDTO accountSenderDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        AccountDTO accountReceiverDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, true, BankCompany.BTBANK);

        BigDecimal expectedTransferAmount =TRANSFER_MAX_PER_TRANSFER_LIMIT;

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountSenderDTO, accountReceiverDTO, expectedTransferAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        setUpMockReturnCalls(expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionTransferService accountTransactionTransferService =
                new AccountTransactionTransferService(accountTransactionService, accountTransactionWithdrawService, accountTransactionDepositService);

        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionTransferService.transactTransfer(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = LimitException.class)
    public void test7_transfer_FromActiveOpen_CurrentAccount_ToActiveOpen_CurrentAccount_EnoughFundsButMaxAmountPerTransactionOutsideLimit_returnException() {

        String transaction = "TRANSFER";
        AccountDTO accountSenderDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        AccountDTO accountReceiverDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        BigDecimal expectedTransferAmount = TRANSFER_MAX_PER_TRANSFER_LIMIT.add(AMOUNT_OUTSIDE_1TIME_TRANSFER_LIMIT);

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountSenderDTO, accountReceiverDTO, expectedTransferAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, TRANSACTION_AMOUNT_FROM_PAST_1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);

        setUpMockReturnCalls(expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionTransferService accountTransactionTransferService =
                new AccountTransactionTransferService(accountTransactionService, accountTransactionWithdrawService, accountTransactionDepositService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionTransferService.transactTransfer(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = LimitException.class)
    public void test8_transfer_FromActiveOpen_CurrentAccount_ToActiveOpen_CurrentAccount_EnoughFundsButMaxAmountPerDayOutsideLimit_returnException() {

        String transaction = "TRANSFER";
        AccountDTO accountSenderDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        AccountDTO accountReceiverDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        BigDecimal expectedTransferAmount = TRANSFER_MAX_PER_DAY_LIMIT.subtract(TRANSACTION_AMOUNT_FROM_PAST_1).subtract(TRANSACTION_AMOUNT_FROM_PAST_2).add(AMOUNT_OUTSIDE_DAY_LIMIT);

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountSenderDTO, accountReceiverDTO, expectedTransferAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, TRANSACTION_AMOUNT_FROM_PAST_1);
        AccountTransactionDTO accountTransactionFromPastDTO2 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_2, FIRSTNAME_2, LASTNAME_2, MONEY_CASH_2, TRANSACTION_AMOUNT_FROM_PAST_2);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO2);

        setUpMockReturnCalls(expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionTransferService accountTransactionTransferService =
                new AccountTransactionTransferService(accountTransactionService, accountTransactionWithdrawService, accountTransactionDepositService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionTransferService.transactTransfer(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    @Test(expected = LimitException.class)
    public void test9_transfer_FromActiveOpen_CurrentAccount_ToActiveOpen_CurrentAccount_EnoughFundsButAmountBelowMinLimit_returnException() {

        String transaction = "TRANSFER";
        AccountDTO accountSenderDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        AccountDTO accountReceiverDTO = getAccountDTO(TIMESTAMP_ACCOUNT_CREATED
                .toEntity(), AccountType.CURRENT_ACCOUNT, false, false, BankCompany.BTBANK);

        BigDecimal expectedTransferAmount = TRANSFER_MAX_PER_DAY_LIMIT.subtract(AMOUNT_BELOW_MIN_LIMIT_PER_TRANSFER);

        AccountTransactionDTO expectedAccountTransactionDTO =
                getAccountTransactionDTO(transaction, accountSenderDTO, accountReceiverDTO, expectedTransferAmount);

        List<AccountTransactionDTO> similarAccountTransactionFromPastDTOList = new ArrayList<>();
        AccountTransactionDTO accountTransactionFromPastDTO1 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_1, FIRSTNAME_1, LASTNAME_1, MONEY_CASH_1, TRANSACTION_AMOUNT_FROM_PAST_1);
        AccountTransactionDTO accountTransactionFromPastDTO2 =
                getSimilarAccountTransactionDTO(transaction, accountSenderDTO, USERNAME_2, FIRSTNAME_2, LASTNAME_2, MONEY_CASH_2, TRANSACTION_AMOUNT_FROM_PAST_2);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO1);
        similarAccountTransactionFromPastDTOList.add(accountTransactionFromPastDTO2);

        setUpMockReturnCalls(expectedAccountTransactionDTO, similarAccountTransactionFromPastDTOList);

        AccountTransactionTransferService accountTransactionTransferService =
                new AccountTransactionTransferService(accountTransactionService, accountTransactionWithdrawService, accountTransactionDepositService);


        AccountTransactionDTO actualAccountTransactionDTO =
                accountTransactionTransferService.transactTransfer(expectedAccountTransactionDTO);

        Assert.assertEquals(expectedAccountTransactionDTO, actualAccountTransactionDTO);

    }

    private AccountTransactionDTO getSimilarAccountTransactionDTO(String transaction, AccountDTO accountDTO,
                                                                  String username1, String firstname1, String lastname1,
                                                                  BigDecimal moneyCash1,
                                                                  BigDecimal transactionAmountFromPast1) {
        return AccountTransactionDTO.builder()
                .operation(getOperationDTO(transaction, username1, firstname1, lastname1, moneyCash1, TIMESTAMP_ACCOUNT_CREATED
                        .toEntity()
                        .plusYears(1).minusHours(1)).toEntity())
                .account(accountDTO.toEntity())
                .amount(transactionAmountFromPast1)
                .build();
    }


    private void setUpMockReturnCalls(AccountTransactionDTO expectedAccountTransactionDTO,
                                      List<AccountTransactionDTO> accountTransactionFromPastDTOList) {
        getMockReturnCall_accountTransactionDTO(expectedAccountTransactionDTO);
        getMockReturnCall_returnAccountTransactionFromPastDTOList_ObjectiveIs_GetTotalWithdrawInDayUnderLimit(accountTransactionFromPastDTOList);
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
                                     boolean isClosed, BankCompany bankCompany) {
        return AccountDTO.builder()
                .iban(IBAN)
                .bankCompany(bankCompany)
                .type(accountType)
                .creatingDate(accountCreatedDate)
                .isBlocked(isBlocked)
                .isClosed(isClosed)
                .build();
    }

    private void getMockReturnCall_accountTransactionDTO(AccountTransactionDTO accountTransactionDTO) {
        when(accountTransactionService.create(any(AccountTransactionDTO.class))).thenReturn(accountTransactionDTO);
        when(accountTransactionWithdrawService.transactWithdraw(any(AccountTransactionDTO.class)))
                .thenReturn(accountTransactionDTO);
        when(accountTransactionDepositService.transactDeposit(any(AccountTransactionDTO.class)))
                .thenReturn(accountTransactionDTO);
    }

    private void getMockReturnCall_returnAccountTransactionFromPastDTOList_ObjectiveIs_GetTotalWithdrawInDayUnderLimit(
            List<AccountTransactionDTO> mockAccountTransactionDTOList) {

        when(accountTransactionService.readByOperationType(OperationType.WITHDRAW))
                .thenReturn(mockAccountTransactionDTOList);
    }


}
