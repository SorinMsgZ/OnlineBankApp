package ro.msg.learning.bank.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ro.msg.learning.bank.dtos.AccountTransactionDTO;


import ro.msg.learning.bank.dtos.OperationDTO;
import ro.msg.learning.bank.entities.Account;
import ro.msg.learning.bank.entities.AccountTransaction;

import ro.msg.learning.bank.entities.Operation;
import ro.msg.learning.bank.entities.OperationType;
import ro.msg.learning.bank.exceptions.NotFoundException;
import ro.msg.learning.bank.repositories.AccountRepository;
import ro.msg.learning.bank.repositories.AccountTransactionRepository;
import ro.msg.learning.bank.repositories.OperationRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class AccountTransactionService {
    private final AccountTransactionRepository accountTransactionRepository;
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public List<AccountTransactionDTO> listAll() {
        return accountTransactionRepository.findAll().stream()
                .map(AccountTransactionDTO::of)
                .collect(Collectors.toList());
    }

    public List<AccountTransactionDTO> listAllByIban(int iban) {
        return accountTransactionRepository.findAll().stream()
                .filter(accountTransaction -> accountTransaction.getAccount().getIban() == iban)
                .map(AccountTransactionDTO::of)
                .collect(Collectors.toList());
    }


    public List<AccountTransactionDTO> readByOperationType(OperationType operationType) {
        return accountTransactionRepository.findByOperation_Type(operationType).stream()
                .map(AccountTransactionDTO::of).collect(Collectors.toList());
    }

    public AccountTransactionDTO create(AccountTransactionDTO input) {
        AccountTransaction accountTransaction = input.toEntity();

        Operation operation = createOperation(input, LocalDateTime.now());
        Account accountSender = getAccountByIban(input.getAccount().getIban());
        Account accountReceiver = getAccountByIban(input.getAccount().getIban());

        accountTransaction.setOperation(operation);
        accountTransaction.setAccount(accountSender);
        accountTransaction.setAccountReceiver(accountReceiver);
        return AccountTransactionDTO.of(accountTransactionRepository.save(accountTransaction));
    }

    private Operation createOperation(AccountTransactionDTO input, LocalDateTime timestampOfOperation) {
        OperationDTO operationDTO = OperationDTO.of(input.getOperation());
        operationDTO.setTimeStamp(timestampOfOperation);
        operationRepository.save(operationDTO.toEntity());
        int sizeOfOperationList=operationRepository.findAll().size();
        return operationRepository.findAll().get(sizeOfOperationList-1);
    }

    private Account getAccountByIban(int iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(NotFoundException::new);
    }

    public void deleteById(int id) {
        accountTransactionRepository.deleteById(id);
    }

    public void deleteAll() {
        accountTransactionRepository.deleteAll();
    }

    public AccountTransactionDTO updateById(int id, AccountTransactionDTO input) {
        AccountTransaction accountTransaction =
                accountTransactionRepository.findById(id).orElseThrow(NotFoundException::new);
        input.copyToEntity(accountTransaction);
        accountTransactionRepository.save(accountTransaction);
        return AccountTransactionDTO.of(accountTransaction);
    }
}
