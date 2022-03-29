package ro.msg.learning.bank.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.bank.dtos.AccountDTO;

import ro.msg.learning.bank.services.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {
private final AccountService accountService;


    @GetMapping("/accounts")
    public List<AccountDTO> listAll() {
        return accountService.listAll();
    }

    @GetMapping("/accounts/{iban}")
    public AccountDTO findByIban(@PathVariable int iban) {
        return accountService.findByIban(iban);
    }

    @PostMapping("/accounts")
    public AccountDTO create(@RequestBody AccountDTO accountDTO) {
        return accountService.create(accountDTO);
    }


    @DeleteMapping("/accounts/{iban}")
    public void deleteById(@PathVariable int iban) {
        accountService.deleteByIban(iban);
    }

    @DeleteMapping("/accounts")
    public void deleteAll() {
        accountService.deleteAll();
    }

    @PutMapping("/accounts/{iban}")
    public AccountDTO updateByIban(@PathVariable int iban, @RequestBody AccountDTO body) {
        return accountService.updateByIban(iban, body);
    }
}
