package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.bank.dtos.AccountTransactionDTO;
import ro.msg.learning.bank.entities.*;
import ro.msg.learning.bank.exceptions.LimitException;
import ro.msg.learning.bank.exceptions.NotOperableException;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountTransactionDepositService {
    private final AccountTransactionService accountTransactionService;
    private final ClientService clientService;
    private final AccountDetailService accountDetailService;
    private  boolean  isAutomaticJob;

    public void setAutomaticJob(boolean automaticJob) {
        isAutomaticJob = automaticJob;
    }

    public AccountTransactionDTO transactDeposit(AccountTransactionDTO accountTransactionDTO) {

        OperationType actualOperationType = accountTransactionDTO.getOperation().getType();

        if (actualOperationType.equals(OperationType.DEPOSIT))
            depositToCurrentOrSavingAccount(accountTransactionDTO);

        return accountTransactionService.create(accountTransactionDTO);
    }

    private void depositToCurrentOrSavingAccount(AccountTransactionDTO accountTransactionDTO) {
        Account account = accountTransactionDTO.getAccount();
        if (AccountService.isClosedOrBlocked(account)) throw new NotOperableException();

        checkDepositMinimLimit(accountTransactionDTO);

        updateCashOfClientAndAmountOfAccountDetail(accountTransactionDTO);
    }

    private void updateCashOfClientAndAmountOfAccountDetail(AccountTransactionDTO accountTransactionDTO) {
        AccountTransactionSubject accountTransactionSubject = new AccountTransactionSubject();
        new ClientObserver(accountTransactionSubject, clientService);

        new AccountDetailUserObserver(accountTransactionSubject, accountDetailService,isAutomaticJob);
        accountTransactionSubject.setAccountTransactionDTO(accountTransactionDTO);

    }

    private void checkDepositMinimLimit(AccountTransactionDTO accountTransactionDTO) {
        if (!isMinimumAmountPerTransactionWithinLimit(accountTransactionDTO)) throw new LimitException();
    }

    private boolean isMinimumAmountPerTransactionWithinLimit(AccountTransactionDTO accountTransactionDTO) {
        return accountTransactionDTO.getAmount().compareTo(LimitAmount.DEPOSIT_MIN.getLimit()) >= 0;
    }

}