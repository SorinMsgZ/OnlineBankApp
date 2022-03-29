package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.bank.dtos.AccountDTO;
import ro.msg.learning.bank.dtos.AccountMeasureDTO;

import ro.msg.learning.bank.dtos.OperationDTO;
import ro.msg.learning.bank.entities.*;
import ro.msg.learning.bank.exceptions.LimitException;

import ro.msg.learning.bank.exceptions.NotFoundException;
import ro.msg.learning.bank.exceptions.NotOperableException;
import ro.msg.learning.bank.exceptions.WarningLimitException;
import ro.msg.learning.bank.repositories.AccountMeasureRepository;
import ro.msg.learning.bank.repositories.AccountRepository;
import ro.msg.learning.bank.repositories.OperationRepository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountMeasureService {
    private final AccountMeasureRepository accountMeasureRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final AccountDetailService accountDetailService;
    private final OperationRepository operationRepository;


    public AccountMeasureDTO takeMeasure(AccountMeasureDTO accountMeasureDTO) {

        OperationType actualMeasure = accountMeasureDTO.getOperation().getType();

        if (actualMeasure.equals(OperationType.BLOCKACCOUNT))
            blockAccount(accountMeasureDTO);
        if (actualMeasure.equals(OperationType.UNBLOCKACCOUNT))
            unblockAccount(accountMeasureDTO);
        if (actualMeasure.equals(OperationType.CLOSEACCOUNT))
            closeAccountManually(accountMeasureDTO);

        Account account = getAccountByIban(accountMeasureDTO);
        accountService.updateByIban(account.getIban(), AccountDTO.of(account));
        return create(accountMeasureDTO);
    }

    private void blockAccount(AccountMeasureDTO accountMeasureDTO) {
        Account account = getAccountByIban(accountMeasureDTO);
        if (AccountService.isClosedOrBlocked(account)) throw new NotOperableException();
        if (!OperationService.isExpectedOperation(OperationType.BLOCKACCOUNT, accountMeasureDTO.getOperation()))
            throw new NotOperableException();

        account.setBlocked(true);
    }

    private void unblockAccount(AccountMeasureDTO accountMeasureDTO) {
        Account account = getAccountByIban(accountMeasureDTO);
        if (AccountService.isClosedOrUnblocked(account)) throw new NotOperableException();
        if (!OperationService.isExpectedOperation(OperationType.UNBLOCKACCOUNT, accountMeasureDTO.getOperation()))
            throw new NotOperableException();

        account.setBlocked(false);
    }

    private void closeAccountManually(AccountMeasureDTO accountMeasureDTO) {
        Account account = getAccountByIban(accountMeasureDTO);
        if (AccountService.isClosed(account)) throw new NotOperableException();

        BigDecimal actualAmount = accountDetailService.readByAccountIban(account.getIban()).get(0).getAccountAmount();
        BigDecimal limitAmount = AccountCost.CLOSING_TAXES.getCost();
        if (actualAmount.compareTo(limitAmount) < 0)
            throw new LimitException();
        if (actualAmount.compareTo(limitAmount) > 0)
            throw new WarningLimitException(actualAmount.doubleValue() - limitAmount.doubleValue());
        account.setClosed(true);
        account.setClosingDate(LocalDateTime.now());
    }

    private Account getAccountByIban(AccountMeasureDTO accountMeasureDTO) {
        return accountRepository.findByIban(accountMeasureDTO.getAccount().getIban())
                .orElseThrow(NotFoundException::new);
    }

    public AccountMeasureDTO create(AccountMeasureDTO input) {
        AccountMeasure accountMeasure = input.toEntity();

        Operation operation = createOperation(input, LocalDateTime.now());
        Account account = getAccountByIban(input);

        accountMeasure.setOperation(operation);
        accountMeasure.setAccount(account);

        return AccountMeasureDTO.of(accountMeasureRepository.save(accountMeasure));
    }
    private Operation createOperation(AccountMeasureDTO input, LocalDateTime timestampOfOperation) {
        OperationDTO operationDTO = OperationDTO.of(input.getOperation());
        operationDTO.setTimeStamp(timestampOfOperation);
        operationRepository.save(operationDTO.toEntity());
        int sizeOfOperationList=operationRepository.findAll().size();
        return operationRepository.findAll().get(sizeOfOperationList-1);
    }

    public List<AccountMeasureDTO> listAll() {
        return accountMeasureRepository.findAll().stream()
                .map(AccountMeasureDTO::of)
                .collect(Collectors.toList());
    }

    public AccountMeasureDTO readById(int id) {
        return accountMeasureRepository.findById(id)
                .map(AccountMeasureDTO::of)
                .orElseThrow(NotFoundException::new);
    }


    public void deleteById(int id) {
        accountMeasureRepository.deleteById(id);
    }

    public void deleteAll() {
        accountMeasureRepository.deleteAll();
    }

    public AccountMeasureDTO updateById(int id, AccountMeasureDTO input) {
        AccountMeasure accountMeasure =
                accountMeasureRepository.findById(id).orElseThrow(NotFoundException::new);
        input.copyToEntity(accountMeasure);
        accountMeasureRepository.save(accountMeasure);
        return AccountMeasureDTO.of(accountMeasure);
    }

}