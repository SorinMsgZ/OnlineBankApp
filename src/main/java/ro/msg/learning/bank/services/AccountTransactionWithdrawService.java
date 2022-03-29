package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.bank.dtos.AccountTransactionDTO;
import ro.msg.learning.bank.entities.*;
import ro.msg.learning.bank.exceptions.LimitException;
import ro.msg.learning.bank.exceptions.NotOperableException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountTransactionWithdrawService {
    private final AccountTransactionService accountTransactionService;
    private final ClientService clientService;
    private final AccountDetailService accountDetailService;
    private boolean isAutomaticJob;

    public void setAutomaticJob(boolean automaticJob) {
        isAutomaticJob = automaticJob;
    }

    public AccountTransactionDTO transactWithdraw(AccountTransactionDTO accountTransactionDTO) {

        OperationType actualOperationType = accountTransactionDTO.getOperation().getType();

        if (actualOperationType.equals(OperationType.WITHDRAW))
            withdrawFromCurrentOrSavingAccount(accountTransactionDTO);

        return accountTransactionService.create(accountTransactionDTO);
    }

    private void withdrawFromCurrentOrSavingAccount(AccountTransactionDTO accountTransactionDTO) {
        Account account = accountTransactionDTO.getAccount();
        if (AccountService.isClosedOrBlocked(account)) throw new NotOperableException();

        AccountType targetAccountType = account.getType();
        if (targetAccountType.equals(AccountType.CURRENT_ACCOUNT))
            checkWithdrawCurrentAccountLimits(accountTransactionDTO);
        if (targetAccountType.equals(AccountType.SAVING_ACCOUNT))
            checkWithdrawSavingAccountLimits(accountTransactionDTO);

        updateCashOfClientAndAmountOfAccountDetail(accountTransactionDTO);
    }

    private void updateCashOfClientAndAmountOfAccountDetail(AccountTransactionDTO accountTransactionDTO) {
        AccountTransactionSubject accountTransactionSubject = new AccountTransactionSubject();
        new ClientObserver(accountTransactionSubject, clientService);

        new AccountDetailUserObserver(accountTransactionSubject, accountDetailService,isAutomaticJob);
        accountTransactionSubject.setAccountTransactionDTO(accountTransactionDTO);

    }

    private void checkWithdrawCurrentAccountLimits(AccountTransactionDTO accountTransactionDTO) {
        if (!isCurrentAccountLimitMeet(accountTransactionDTO)) throw new LimitException();
    }

    private void checkWithdrawSavingAccountLimits(AccountTransactionDTO accountTransactionDTO) {

        if ((isFirstYearSinceAccountCreation(accountTransactionDTO)) &&
            (!isSavingAccountLimitMeet(accountTransactionDTO))) throw new LimitException();

    }

    private boolean isCurrentAccountLimitMeet(AccountTransactionDTO accountTransactionDTO) {

        return isSumOfTotalWithdrawInDayWithinLimit(accountTransactionDTO) &&
                isMinimumAmountPerTransactionWithinLimit(accountTransactionDTO);
    }

    private boolean isMinimumAmountPerTransactionWithinLimit(AccountTransactionDTO accountTransactionDTO) {
        return accountTransactionDTO.getAmount().compareTo(LimitAmount.WITHDRAW_MIN_PER_TRANSACTION
                .getLimit()) >= 0;
    }

    private boolean isSumOfTotalWithdrawInDayWithinLimit(AccountTransactionDTO accountTransactionDTO) {
        return (getSumOfTotalWithdrawInTransactionDay(accountTransactionDTO)).add(accountTransactionDTO.getAmount())
                .compareTo(LimitAmount.WITHDRAW_MAX_PER_DAY.getLimit()) <= 0;
    }

    private boolean isSavingAccountLimitMeet(AccountTransactionDTO accountTransactionDTO) {
        int timesOfWithdrawInMonth = (int) getTimesOfTotalWithdrawInTransactionMonth(accountTransactionDTO);
        BigDecimal transactionAmountInMonth = getSumOfTotalWithdrawInTransactionMonth(accountTransactionDTO);
        return isTimesOfWithdrawInMonthWithinLimit(timesOfWithdrawInMonth) &&
                isTransactionAmountInMonthWithinLimit(transactionAmountInMonth);
    }

    private boolean isTransactionAmountInMonthWithinLimit(BigDecimal transactionAmountInMonth) {
        return transactionAmountInMonth.compareTo(LimitAmount.WITHDRAW_MAX_PER_MONTH_ACCOUNT_SAVING
                .getLimit()) <= 0;
    }

    private boolean isTimesOfWithdrawInMonthWithinLimit(int timesOfWithdrawInMonth) {
        return timesOfWithdrawInMonth < LimitDuration.ACCOUNT_SAVING_WITHDRAW_IN_MONTH_VALID_TIMES.getLimitDuration();
    }

    private BigDecimal getSumOfTotalWithdrawInTransactionDay(AccountTransactionDTO accountTransactionDTO) {
        LocalDate dateOfTransaction = getDateOfTransaction(accountTransactionDTO);
        return accountTransactionService.readByOperationType(OperationType.WITHDRAW).stream()
                .filter(transactionDTO -> isTransactionFromSameDayOfActualTransaction(dateOfTransaction, transactionDTO)
                ).map(AccountTransactionDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getSumOfTotalWithdrawInTransactionMonth(AccountTransactionDTO accountTransactionDTO) {
        LocalDate dateOfTransaction = getDateOfTransaction(accountTransactionDTO);
        return getAccountTransactionDTOFromSameMonthStream(dateOfTransaction).map(AccountTransactionDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).add(accountTransactionDTO.getAmount());
    }

    private LocalDate getDateOfTransaction(AccountTransactionDTO accountTransactionDTO) {
        return accountTransactionDTO.getOperation().getTimeStamp().toLocalDate();
    }

    private boolean isTransactionFromSameDayOfActualTransaction(LocalDate dateOfTransaction,
                                                                AccountTransactionDTO transactionDTO) {
        return getDateOfTransaction(transactionDTO).compareTo(dateOfTransaction) == 0;
    }

    private long getTimesOfTotalWithdrawInTransactionMonth(AccountTransactionDTO accountTransactionDTO) {
        LocalDate dateOfTransaction = getDateOfTransaction(accountTransactionDTO);
        return getAccountTransactionDTOFromSameMonthStream(dateOfTransaction).count();
    }

    private Stream<AccountTransactionDTO> getAccountTransactionDTOFromSameMonthStream(LocalDate dateOfTransaction) {
        return accountTransactionService.readByOperationType(OperationType.WITHDRAW).stream()
                .filter(transactionDTO -> isTransactionFromSameMonthOfExpectedTransaction(dateOfTransaction, transactionDTO)
                );
    }

    private boolean isTransactionFromSameMonthOfExpectedTransaction(LocalDate dateOfTransaction,
                                                                    AccountTransactionDTO transactionDTO) {
        LocalDate transaction = getDateOfTransaction(transactionDTO);
        return (dateOfTransaction.getYear() == transaction.getYear()) &&
                (dateOfTransaction.getMonthValue() == transaction.getMonthValue());
    }

    private boolean isFirstYearSinceAccountCreation(AccountTransactionDTO accountTransactionDTO) {

        return getAgeOfAccount(accountTransactionDTO).getYears() <=
                LimitDuration.ACCOUNT_SAVING_WITHDRAW_MAX_VALID_YEARS.getLimitDuration();
    }

    private Period getAgeOfAccount(AccountTransactionDTO accountTransactionDTO) {
        LocalDate accountCreationDate = accountTransactionDTO.getAccount().getCreatingDate().toLocalDate();
        LocalDate transactionDate = getDateOfTransaction(accountTransactionDTO);
        return Period.between(accountCreationDate, transactionDate);
    }


}