package ro.msg.learning.bank.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.bank.dtos.AccountTransactionDTO;
import ro.msg.learning.bank.services.AccountTransactionDepositService;
import ro.msg.learning.bank.services.AccountTransactionService;
import ro.msg.learning.bank.services.AccountTransactionTransferService;
import ro.msg.learning.bank.services.AccountTransactionWithdrawService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountTransactionController {
private final AccountTransactionWithdrawService accountTransactionWithdrawService;
private final AccountTransactionDepositService accountTransactionDepositService;
private final AccountTransactionTransferService accountTransactionTransferService;
private final AccountTransactionService accountTransactionService;
    @PostMapping("/withdraws")
    public AccountTransactionDTO withdraw(@RequestBody AccountTransactionDTO accountTransactionDTO) {
        return accountTransactionWithdrawService.transactWithdraw(accountTransactionDTO);
    }

    @PostMapping("/deposits")
    public AccountTransactionDTO deposit(@RequestBody AccountTransactionDTO accountTransactionDTO) {
        return accountTransactionDepositService.transactDeposit(accountTransactionDTO);
    }

    @PostMapping("/transfers")
    public AccountTransactionDTO transfer(@RequestBody AccountTransactionDTO accountTransactionDTO) {
        return accountTransactionTransferService.transactTransfer(accountTransactionDTO);
    }

    @GetMapping("/transactions")
    public List<AccountTransactionDTO> listAll() {
        return accountTransactionService.listAll();
    }

    @PostMapping("/transactions")
    public AccountTransactionDTO create(@RequestBody AccountTransactionDTO accountTransactionDTO) {
        return accountTransactionService.create(accountTransactionDTO);
    }


    @DeleteMapping("/transactions/{id}")
    public void deleteById(@PathVariable int id) {
        accountTransactionService.deleteById(id);
    }

    @DeleteMapping("/transactions")
    public void deleteAll() {
        accountTransactionService.deleteAll();
    }

    @PutMapping("/transactions/{id}")
    public AccountTransactionDTO updateById(@PathVariable int id, @RequestBody AccountTransactionDTO body) {
        return accountTransactionService.updateById(id, body);
    }

}
