package ro.msg.learning.bank.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.bank.dtos.BankDTO;
import ro.msg.learning.bank.services.BankService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BankController {
private final BankService bankService;

    @GetMapping("/banks")
    public List<BankDTO> listAll() {
        return bankService.listAll();
    }

    @PostMapping("/banks")
    public BankDTO create(@RequestBody BankDTO bankDTO) {
        return bankService.create(bankDTO);
    }


    @DeleteMapping("/banks/{id}")
    public void deleteById(@PathVariable int id) {
        bankService.deleteById(id);
    }

    @DeleteMapping("/banks")
    public void deleteAll() {
        bankService.deleteAll();
    }

    @PutMapping("/banks/{id}")
    public BankDTO updateById(@PathVariable int id, @RequestBody BankDTO body) {
        return bankService.updateById(id, body);
    }
}

