package ro.msg.learning.bank.services;

import ro.msg.learning.bank.dtos.ClientDTO;
import ro.msg.learning.bank.entities.Client;
import ro.msg.learning.bank.entities.OperationType;
import ro.msg.learning.bank.exceptions.InsufficientFundException;

import java.math.BigDecimal;

public class ClientObserver extends ObserverObject {
    private ClientService clientService;

    public ClientObserver(AccountTransactionSubject subject, ClientService clientService) {
        super(subject);
        this.accountTransactionSubject.attach(this);
        this.clientService = clientService;
    }

    @Override
    public void update() {
        Client clientOfTransaction = getClientOfTransaction();
        BigDecimal actualCashOfClient = getMoneyCashOfClient(clientOfTransaction);
        BigDecimal actualAmountOfTransaction = accountTransactionSubject.getAccountTransactionDTO().getAmount();

        OperationType actualOperationType = getOperationTypeOfTransaction();


        if (actualOperationType.equals(OperationType.WITHDRAW)) {
            clientOfTransaction.setMoneyCash(actualCashOfClient.add(actualAmountOfTransaction));

        }
        if (actualOperationType.equals(OperationType.DEPOSIT)) {

            if (actualCashOfClient.compareTo(actualAmountOfTransaction) < 0) throw new InsufficientFundException();
            clientOfTransaction.setMoneyCash(actualCashOfClient.subtract(actualAmountOfTransaction));
        }
        clientService.updateByFirstNameAndLastName(clientOfTransaction.getFirstname(),clientOfTransaction.getLastname(), ClientDTO
                .of(clientOfTransaction));
    }

    private OperationType getOperationTypeOfTransaction() {
        return accountTransactionSubject.getAccountTransactionDTO().getOperation().getType();
    }

    private BigDecimal getMoneyCashOfClient(Client clientOfTransaction) {
        return clientService
                .readByFirstNameAndLastName(clientOfTransaction.getFirstname(), clientOfTransaction.getLastname())
                .getMoneyCash();
    }

    private Client getClientOfTransaction() {
        return accountTransactionSubject.getAccountTransactionDTO().getOperation().getUserDetail()
                .getClient();
    }
}
