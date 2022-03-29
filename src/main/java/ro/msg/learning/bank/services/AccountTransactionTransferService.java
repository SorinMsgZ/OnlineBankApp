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

@Service
@Transactional
@RequiredArgsConstructor
public class AccountTransactionTransferService {
    private final AccountTransactionService accountTransactionService;
    private final AccountTransactionWithdrawService accountTransactionWithdrawService;
    private final AccountTransactionDepositService accountTransactionDepositService;

    public AccountTransactionDTO transactTransfer(AccountTransactionDTO accountTransactionDTO) {

        OperationType actualOperationType = accountTransactionDTO.getOperation().getType();

        if (actualOperationType.equals(OperationType.TRANSFER))
            transferToCurrentOrSavingAccount(accountTransactionDTO);

        changeOperationType(accountTransactionDTO, OperationType.TRANSFER);
        return accountTransactionService.create(accountTransactionDTO);
    }

    private void transferToCurrentOrSavingAccount(AccountTransactionDTO accountTransactionDTO) {
        Account account = accountTransactionDTO.getAccount();
        Account accountReceiver = accountTransactionDTO.getAccountReceiver();
        if (AccountService.isClosedOrBlocked(account) || AccountService.isClosedOrBlocked(accountReceiver)) throw new NotOperableException();
        checkTransferCurrentAccountLimits(accountTransactionDTO);

        changeOperationType(accountTransactionDTO, OperationType.WITHDRAW);

        accountTransactionWithdrawService.transactWithdraw(accountTransactionDTO);
        changeOperationType(accountTransactionDTO, OperationType.DEPOSIT);
        depositIfReceiverIsClientOtherwiseRequestDeposit(accountTransactionDTO);
    }

    private void changeOperationType(AccountTransactionDTO accountTransactionDTO, OperationType operationType) {
        Operation transferOperation= accountTransactionDTO.getOperation();

        transferOperation.setType(operationType);
        accountTransactionDTO.setOperation(transferOperation);
    }

    private void depositIfReceiverIsClientOtherwiseRequestDeposit(AccountTransactionDTO accountTransactionDTO) {
        BankCompany bankSender = accountTransactionDTO.getOperation().getUserDetail().getClient().getDefaultBank();
        Account accountReceiver = accountTransactionDTO.getAccountReceiver();
        BankCompany bankReceiver = accountReceiver.getBank().getName();

        AccountTransaction depositAccountTransaction = accountTransactionDTO.toEntity();
        depositAccountTransaction.setAccount(accountReceiver);

        if (bankSender.compareTo(bankReceiver) == 0) {
            accountTransactionDepositService.transactDeposit(AccountTransactionDTO.of(depositAccountTransaction));
        }
        sendDepositRequestToForeignBank(bankReceiver, accountTransactionDTO);
    }

    private void sendDepositRequestToForeignBank(BankCompany bankReceiver,
                                                 AccountTransactionDTO accountTransactionDTO) {
    }


    private void checkTransferCurrentAccountLimits(AccountTransactionDTO accountTransactionDTO) {
        if (!isCurrentAccountLimitMeet(accountTransactionDTO)) throw new LimitException();
    }


    private boolean isCurrentAccountLimitMeet(AccountTransactionDTO accountTransactionDTO) {

        return isSumOfTotalTransferInDayWithinLimit(accountTransactionDTO) &&
                isMinimumAmountPerTransactionWithinLimit(accountTransactionDTO) &&
                isMaximumAmountPerTransactionWithinLimit(accountTransactionDTO);
    }

    private boolean isMinimumAmountPerTransactionWithinLimit(AccountTransactionDTO accountTransactionDTO) {
        return accountTransactionDTO.getAmount().compareTo(LimitAmount.TRANSFER_MIN_PER_TRANSACTION
                .getLimit()) >= 0;
    }

    private boolean isMaximumAmountPerTransactionWithinLimit(AccountTransactionDTO accountTransactionDTO) {
        return accountTransactionDTO.getAmount().compareTo(LimitAmount.TRANSFER_MAX_PER_TRANSACTION
                .getLimit()) <= 0;
    }

    private boolean isSumOfTotalTransferInDayWithinLimit(AccountTransactionDTO accountTransactionDTO) {
        return (getSumOfTotalTransferInTransactionDay(accountTransactionDTO)).add(accountTransactionDTO.getAmount())
                .compareTo(LimitAmount.TRANSFER_MAX_PER_DAY.getLimit()) <= 0;
    }

    private BigDecimal getSumOfTotalTransferInTransactionDay(AccountTransactionDTO accountTransactionDTO) {
        LocalDate dateOfTransaction = getDateOfTransaction(accountTransactionDTO);
        return accountTransactionService.readByOperationType(OperationType.WITHDRAW).stream()
                .filter(transactionDTO -> isTransactionFromSameDayOfActualTransaction(dateOfTransaction, transactionDTO)
                ).map(AccountTransactionDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LocalDate getDateOfTransaction(AccountTransactionDTO accountTransactionDTO) {
        return accountTransactionDTO.getOperation().getTimeStamp().toLocalDate();
    }

    private boolean isTransactionFromSameDayOfActualTransaction(LocalDate dateOfTransaction,
                                                                AccountTransactionDTO transactionDTO) {
        return getDateOfTransaction(transactionDTO).compareTo(dateOfTransaction) == 0;
    }
}