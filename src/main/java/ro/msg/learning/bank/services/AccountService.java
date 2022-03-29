package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.bank.dtos.AccountDTO;
import ro.msg.learning.bank.entities.Account;

import ro.msg.learning.bank.entities.BankCompany;
import ro.msg.learning.bank.exceptions.NotFoundException;
import ro.msg.learning.bank.repositories.AccountRepository;
import ro.msg.learning.bank.repositories.BankRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;

    public List<AccountDTO> listAll() {
        return accountRepository.findAll().stream()
                .map(AccountDTO::of)
                .collect(Collectors.toList());
    }

    public AccountDTO findByIban(int iban) {
        return accountRepository.findByIban(iban)
                .map(AccountDTO::of)
                .orElseThrow(NotFoundException::new);
    }

    public AccountDTO create(AccountDTO input) {
        Account account = input.toEntity();

        account.setBank(bankRepository.findByName(BankCompany.BTBANK).orElseThrow(NotFoundException::new));

        return AccountDTO.of(accountRepository.save(account));
    }

    public void deleteByIban(int iban) {
        accountRepository.deleteById(iban);
    }

    public void deleteAll() {
        accountRepository.deleteAll();
    }

    public AccountDTO updateByIban(int iban, AccountDTO input) {
        Account account =
                accountRepository.findById(iban).orElseThrow(NotFoundException::new);
        input.copyToEntity(account);
        accountRepository.save(account);
        return AccountDTO.of(account);
    }

    public static boolean isClosedOrBlocked(Account account) {
        return account.isClosed() || account.isBlocked();

    }

    public static boolean isClosedOrUnblocked(Account account) {
        return account.isClosed() || !account.isBlocked();

    }

    public static boolean isClosed(Account account) {
        return account.isClosed();

    }

}
