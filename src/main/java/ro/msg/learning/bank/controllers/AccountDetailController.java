package ro.msg.learning.bank.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.bank.dtos.AccountDetailDTO;
import ro.msg.learning.bank.services.AccountDetailService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountDetailController {
    private final AccountDetailService accountDetailService;

    @GetMapping("/accountdetails")
    public List<AccountDetailDTO> listAll() {
        return accountDetailService.listAll();
    }

    @GetMapping("/accountdetails/{username}/{iban}")
    public AccountDetailDTO readByUsernameAndIban(@PathVariable String username, @PathVariable int iban) {
        return accountDetailService.readByUsernameAndIban(username,iban);
    }

    @PostMapping("/accountdetails")
    public AccountDetailDTO create(@RequestBody AccountDetailDTO accountDetailDTO) {
        return accountDetailService.create(accountDetailDTO);
    }


    @DeleteMapping("/accountdetails")
    public void deleteAll() {
        accountDetailService.deleteAll();
    }

    @PutMapping("/accountdetails/{username}&{iban}")
    public AccountDetailDTO updateByUsernameAndIban(@PathVariable String username, @PathVariable int iban, @RequestBody AccountDetailDTO body) {
        return accountDetailService.updateByUsernameAndIban(username, iban, body);
    }
}

