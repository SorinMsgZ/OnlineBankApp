package ro.msg.learning.bank.services;

public abstract class ObserverObject {
    protected AccountTransactionSubject accountTransactionSubject;

    protected ObserverObject(AccountTransactionSubject accountTransactionSubject) {
        this.accountTransactionSubject = accountTransactionSubject;
    }

    public abstract void update();
}
