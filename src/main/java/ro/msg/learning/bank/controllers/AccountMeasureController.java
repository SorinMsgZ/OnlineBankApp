package ro.msg.learning.bank.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.bank.dtos.AccountMeasureDTO;
import ro.msg.learning.bank.services.AccountMeasureService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountMeasureController {
private final AccountMeasureService accountMeasureService;
    @PostMapping("/measures")
    public AccountMeasureDTO takeMeasure(@RequestBody AccountMeasureDTO accountMeasureDTO) {
        return accountMeasureService.takeMeasure(accountMeasureDTO);
    }

    @GetMapping("/measures")
    public List<AccountMeasureDTO> listAll() {
        return accountMeasureService.listAll();
    }

    @PostMapping("/manual/measures")
    public AccountMeasureDTO create(@RequestBody AccountMeasureDTO accountMeasureDTO) {
        return accountMeasureService.create(accountMeasureDTO);
    }

    @DeleteMapping("/manual/measures/{id}")
    public void deleteByFirstnameAndLastname(@PathVariable int id) {
        accountMeasureService.deleteById(id);
    }

    @DeleteMapping("/manual/measures")
    public void deleteAll() {
        accountMeasureService.deleteAll();
    }

    @PutMapping("/manual/measures/{id}")
    public AccountMeasureDTO updateByFirstnameAndLastname(@PathVariable int id, @RequestBody AccountMeasureDTO body) {
        return accountMeasureService.updateById(id, body);
    }
}

