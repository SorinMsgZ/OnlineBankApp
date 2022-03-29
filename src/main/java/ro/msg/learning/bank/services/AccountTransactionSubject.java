package ro.msg.learning.bank.services;

import ro.msg.learning.bank.dtos.AccountTransactionDTO;

import java.util.ArrayList;
import java.util.List;

public class AccountTransactionSubject {
    private List<ObserverObject> observers = new ArrayList<>();
    private AccountTransactionDTO accountTransactionDTO;

    public List<ObserverObject> getObservers() {
        return observers;
    }

    public void setObservers(List<ObserverObject> observers) {
        this.observers = observers;
    }

    public AccountTransactionDTO getAccountTransactionDTO() {
        return accountTransactionDTO;
    }

    public void setAccountTransactionDTO(AccountTransactionDTO accountTransactionDTO) {
        this.accountTransactionDTO = accountTransactionDTO;
        notifyAllObservers();
        detachAll();
    }


    public void attach(ObserverObject observer) {
        observers.add(observer);
    }


    public void notifyAllObservers() {
        for (ObserverObject observer : observers) {
            observer.update();
        }
    }


    public void detachAll() {
        List<ObserverObject> listOfActualObserver = observers;
        observers.removeAll(listOfActualObserver);
    }
}
