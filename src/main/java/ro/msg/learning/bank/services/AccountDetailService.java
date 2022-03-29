package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.bank.dtos.AccountDetailDTO;
import ro.msg.learning.bank.entities.*;
import ro.msg.learning.bank.exceptions.NotFoundException;
import ro.msg.learning.bank.repositories.AccountDetailRepository;
import ro.msg.learning.bank.repositories.AccountRepository;
import ro.msg.learning.bank.repositories.AppUserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountDetailService {
    private final AccountDetailRepository accountDetailRepository;
    private final AccountRepository accountRepository;
    private final AppUserRepository appUserRepository;

    public List<AccountDetailDTO> listAll() {
        return accountDetailRepository.findAll().stream()
                .map(AccountDetailDTO::of)
                .collect(Collectors.toList());
    }

    public List<AccountDetailDTO> readByAccountIban(int iban) {
        return accountDetailRepository.findByAccount_Iban(iban).stream()
                .map(AccountDetailDTO::of).collect(Collectors.toList());
    }

    public List<AccountDetailDTO> readByAccountTypeAndIsClosedStatus(AccountType accountType, boolean isClosed) {
        return accountDetailRepository.findByAccount_TypeAndAccountIban_isClosed(accountType,isClosed).stream()
                .map(AccountDetailDTO::of).collect(Collectors.toList());
    }

    public List<AccountDetailDTO> readByIsClosedStatusAndCustomAmount(boolean isClosed, BigDecimal customAmount) {
        return accountDetailRepository.findByAccount_isClosed(isClosed).stream().filter(accountDetail -> accountDetail.getAccountAmount().compareTo(customAmount) == 0)
                .map(AccountDetailDTO::of).collect(Collectors.toList());
    }

    public AccountDetailDTO readByUsernameAndIban(String username,int iban) {
        return accountDetailRepository.findByAppUser_UsernameAndAccountIban_Iban(username,iban)
                .map(AccountDetailDTO::of)
                .orElseThrow(NotFoundException::new);
    }

    public AccountDetailDTO updateByUsernameAndIban(String username, int iban, AccountDetailDTO input) {
        AccountDetail accountDetail =
                accountDetailRepository.findByAppUser_UsernameAndAccountIban_Iban(username,iban).orElseThrow(NotFoundException::new);
        input.copyToEntity(accountDetail);
        accountDetailRepository.save(accountDetail);
        return AccountDetailDTO.of(accountDetail);
    }

    public void deleteAll() {
        accountDetailRepository.deleteAll();
    }

    public AccountDetailDTO create(AccountDetailDTO input) {
        AccountDetail accountDetail = input.toEntity();

        AppUser appUser = appUserRepository.findByUsername(input.getAppUser().getUsername()).orElseThrow(NotFoundException::new);
        Account account = accountRepository.findByIban(input.getAccount().getIban()).orElseThrow(NotFoundException::new);

        accountDetail.setAppUser(appUser);
        accountDetail.setAccount(account);


        accountDetail.setAccountAmount(input.getAccountAmount());
        return AccountDetailDTO.of(accountDetailRepository.save(accountDetail));
    }
}
