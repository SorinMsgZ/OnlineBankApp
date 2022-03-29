package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ro.msg.learning.bank.dtos.AccountDetailDTO;
import ro.msg.learning.bank.dtos.AccountMeasureDTO;
import ro.msg.learning.bank.dtos.AccountTransactionDTO;
import ro.msg.learning.bank.dtos.OperationDTO;
import ro.msg.learning.bank.entities.*;
import ro.msg.learning.bank.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@EnableAsync
public class ScheduledJobAutomaticCloseAccount {
    public static final long AUTOMATIC_CLOSE_LIMIT = 6;
    public static final OperationDTO AUTOMATIC_CLOSE_ACCOUNT_OPERATION = OperationDTO.builder()
            .type(OperationType.CLOSEACCOUNT)
            .timeStamp(LocalDateTime.now())
            .username(BankCompany.BTBANK.toString())
            .clientLastname(BankCompany.BTBANK.toString())
            .build();

    private final AccountDetailService accountDetailService;
    private final AccountMeasureService accountMeasureService;
    private final AccountTransactionService accountTransactionService;

    @Async
    @Scheduled(cron = "${cron.expression.daily}", zone = "${cron.expression.zone}")
    public void closeAutomaticallyAccount() {

        List<AccountDetailDTO> accountDetailsDTOListNotClosedWithNoAmount =
                accountDetailService.readByIsClosedStatusAndCustomAmount(false, BigDecimal.ZERO);

        for (AccountDetailDTO accountDetail : accountDetailsDTOListNotClosedWithNoAmount
        ) {
            Comparator<Operation> timestampComparator = Comparator
                    .comparing(Operation::getTimeStamp);

            Optional<Operation> latestOperationByIbanAvailable =
                    accountTransactionService.listAllByIban(accountDetail.getAccount().getIban()).stream()
                            .map(AccountTransactionDTO::getOperation).max(timestampComparator);
            boolean isLatestOperationByIbanAvailable = latestOperationByIbanAvailable.isPresent();
            if (!isLatestOperationByIbanAvailable) throw new NotFoundException();
           

            if (isLatestTransactionOlderThanCloseLimit(latestOperationByIbanAvailable.get())) {
                Account account = accountDetail.getAccount();
                account.setClosingDate(LocalDateTime.now());

                AccountMeasureDTO bankAccountMeasureDTO = AccountMeasureDTO.builder()
                        .operation(AUTOMATIC_CLOSE_ACCOUNT_OPERATION.toEntity())
                        .account(account)
                        .build();

                accountMeasureService.takeMeasure(bankAccountMeasureDTO);

            }

        }
    }

    private boolean isLatestTransactionOlderThanCloseLimit(Operation latestOperationByIban) {
        LocalDate dateCandidateToCloseAccount = LocalDate.now().minusMonths(AUTOMATIC_CLOSE_LIMIT);
        return latestOperationByIban.getTimeStamp().toLocalDate().compareTo(dateCandidateToCloseAccount) <= 0;
    }
}
