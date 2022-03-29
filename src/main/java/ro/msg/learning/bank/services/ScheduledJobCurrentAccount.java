package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Component
@RequiredArgsConstructor
@EnableAsync
public class ScheduledJobCurrentAccount {

    public static final BigDecimal MAINTENANCE_COST = AccountCost.MAINTENANCE_COST.getCost();
    public static final OperationDTO AUTOMATIC_MAINTENANCE_COST_WITHDRAW_OPERATION = OperationDTO.builder()
            .type(OperationType.WITHDRAW)
            .timeStamp(LocalDateTime.now())
            .username(BankCompany.BTBANK.toString())
            .clientLastname(BankCompany.BTBANK.toString())
            .build();
    private final AccountTransactionWithdrawService accountTransactionWithdrawService;
    private final AccountDetailService accountDetailService;

    @Async
    @Scheduled(cron = "${cron.expression.daily}", zone = "${cron.expression.zone}")
    public void retainMonthlyMaintenanceCostFromCurrentAccount() {

        List<AccountDetailDTO> accountDetails =
                accountDetailService.readByAccountTypeAndIsClosedStatus(AccountType.CURRENT_ACCOUNT, false);

        for (AccountDetailDTO accountDetail : accountDetails
        ) {

            if (isEndOfMonthReferencedToAccountCreatedDate(accountDetail)) {

                AccountTransactionDTO bankAccountTransactionDTO = AccountTransactionDTO.builder()
                        .operation(AUTOMATIC_MAINTENANCE_COST_WITHDRAW_OPERATION.toEntity())
                        .account(accountDetail.getAccount())
                        .amount(MAINTENANCE_COST)
                        .build();

                accountTransactionWithdrawService.setAutomaticJob(true);
                accountTransactionWithdrawService.transactWithdraw(bankAccountTransactionDTO);
            }
        }

    }

    private boolean isEndOfMonthReferencedToAccountCreatedDate(AccountDetailDTO accountDetail) {

        LocalDate dateOfMonthAccountCreated = accountDetail.getAccount().getCreatingDate().toLocalDate();
        LocalDate currentDate = LocalDateTime.now().toLocalDate();

        int differenceInDays = Period.between(dateOfMonthAccountCreated, currentDate).getDays();
        return (differenceInDays == 0);
    }
}
