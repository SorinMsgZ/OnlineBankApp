package ro.msg.learning.bank.services;

import ro.msg.learning.bank.dtos.AccountDetailDTO;
import ro.msg.learning.bank.entities.OperationType;
import ro.msg.learning.bank.exceptions.InsufficientFundException;

import java.math.BigDecimal;
import java.util.List;

public class AccountDetailUserObserver extends ObserverObject {
    private AccountDetailService accountDetailService;
    private boolean isAutomaticJob;

    public AccountDetailUserObserver(AccountTransactionSubject subject,
                                     AccountDetailService accountDetailService,boolean isAutomaticJob) {
        super(subject);
        this.accountTransactionSubject.attach(this);
        this.accountDetailService = accountDetailService;
        this.isAutomaticJob=isAutomaticJob;
    }

    protected AccountDetailUserObserver(AccountTransactionSubject subject) {
        super(subject);
    }

    @Override
    public void update() {

        String usernameOfTransaction= getUsernameOfTransaction();
        int ibanOfTransaction = getIbanOfTransaction();
        AccountDetailDTO actualAccountDetailDTO =
                accountDetailService.readByUsernameAndIban(usernameOfTransaction, ibanOfTransaction);
        List<AccountDetailDTO> accountDetailDTOS=accountDetailService.readByAccountIban(ibanOfTransaction);
        BigDecimal actualAmountOfAccount = actualAccountDetailDTO.getAccountAmount();

        BigDecimal actualAmountOfTransaction = accountTransactionSubject.getAccountTransactionDTO().getAmount();
        OperationType actualOperationType = getOperationTypeOfTransaction();


        if (actualOperationType.equals(OperationType.WITHDRAW)) {

            boolean isNotEnoughFundsAndNotAutomaticJob = (actualAmountOfAccount.compareTo(actualAmountOfTransaction) < 0) && (!isAutomaticJob);
            if (isNotEnoughFundsAndNotAutomaticJob) throw new InsufficientFundException();

            BigDecimal newAmount = actualAmountOfAccount.subtract(actualAmountOfTransaction);
            updateAllAccountDetail(usernameOfTransaction, ibanOfTransaction, accountDetailDTOS, newAmount);

        }
        if (actualOperationType.equals(OperationType.DEPOSIT)) {
            BigDecimal newAmount = actualAmountOfAccount.add(actualAmountOfTransaction);
            updateAllAccountDetail(usernameOfTransaction, ibanOfTransaction, accountDetailDTOS, newAmount);
        }


    }

    private void updateAllAccountDetail(String usernameOfTransaction, int ibanOfTransaction,
                                        List<AccountDetailDTO> accountDetailDTOS, BigDecimal newAmount) {
        accountDetailDTOS.forEach(accountDetailDTO -> {

            accountDetailDTO.setAccountAmount(newAmount);
        });
        accountDetailDTOS.forEach(accountDetailDTO -> updateAccountDetail(usernameOfTransaction, ibanOfTransaction, accountDetailDTO));
    }

    private void updateAccountDetail(String usernameOfTransaction, int ibanOfTransaction,
                                     AccountDetailDTO actualAccountDetailDTO) {
        accountDetailService.updateByUsernameAndIban(usernameOfTransaction, ibanOfTransaction, actualAccountDetailDTO);
    }

    private int getIbanOfTransaction() {
        return accountTransactionSubject.getAccountTransactionDTO().getAccount().getIban();
    }

    private String getUsernameOfTransaction() {
        return accountTransactionSubject.getAccountTransactionDTO().getOperation().getUserDetail().getUsername();
    }

    private OperationType getOperationTypeOfTransaction() {
        return accountTransactionSubject.getAccountTransactionDTO().getOperation().getType();
    }
}
