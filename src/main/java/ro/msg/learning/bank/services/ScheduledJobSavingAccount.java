package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ro.msg.learning.bank.dtos.AccountDetailDTO;
import ro.msg.learning.bank.dtos.AccountTransactionDTO;
import ro.msg.learning.bank.dtos.OperationDTO;
import ro.msg.learning.bank.entities.AccountType;
import ro.msg.learning.bank.entities.BankCompany;
import ro.msg.learning.bank.entities.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Component
@RequiredArgsConstructor
@EnableAsync
public class ScheduledJobSavingAccount {
    public static final BigDecimal INTEREST_RATE = BigDecimal.valueOf(0.02);
    public static final OperationDTO AUTOMATIC_INTEREST_RATE_DEPOSIT_OPERATION = OperationDTO.builder()
            .type(OperationType.DEPOSIT)
            .timeStamp(LocalDateTime.now())
            .username(BankCompany.BTBANK.toString())
            .clientLastname(BankCompany.BTBANK.toString())
            .build();
    private final AccountTransactionDepositService accountTransactionDepositService;
    private final AccountDetailService accountDetailService;

    @Async
    @Scheduled(cron = "${cron.expression.daily}", zone = "${cron.expression.zone}")
    public void addMonthlyInterestRateToSavingAccount() {

        List<AccountDetailDTO> accountDetails =
                accountDetailService.readByAccountTypeAndIsClosedStatus(AccountType.SAVING_ACCOUNT, false);

        for (AccountDetailDTO accountDetail : accountDetails
        ) {

            if (isEndOfMonthReferencedToAccountCreatedDate(accountDetail)) {
                AccountTransactionDTO bankAccountTransactionDTO = AccountTransactionDTO.builder()
                        .operation(AUTOMATIC_INTEREST_RATE_DEPOSIT_OPERATION.toEntity())
                        .account(accountDetail.getAccount())
                        .amount(INTEREST_RATE.multiply(accountDetail.getAccountAmount()))
                        .build();

                accountTransactionDepositService.setAutomaticJob(true);
                accountTransactionDepositService.transactDeposit(bankAccountTransactionDTO);
            }
        }

    }

    private boolean isEndOfMonthReferencedToAccountCreatedDate(AccountDetailDTO accountDetail) {

        LocalDate dayOfMonthAccountCreated = accountDetail.getAccount().getCreatingDate().toLocalDate();
        LocalDate currentDay = LocalDateTime.now().toLocalDate();

        return (Period.between(dayOfMonthAccountCreated, currentDay).getDays() == 0);
    }
}
