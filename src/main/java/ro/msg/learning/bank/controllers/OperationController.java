package ro.msg.learning.bank.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.bank.dtos.OperationDTO;
import ro.msg.learning.bank.services.OperationService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService operationService;

    @GetMapping("/operations")
    public List<OperationDTO> listAll() {
        return operationService.listAll();
    }

    @PostMapping("/operations")
    public OperationDTO create(@RequestBody OperationDTO operationDTO) {
        return operationService.create(operationDTO);
    }

    @DeleteMapping("/operations/{id}")
    public void deleteByFirstnameAndLastname(@PathVariable int id) {
        operationService.deleteById(id);
    }

    @DeleteMapping("/operations")
    public void deleteAll() {
        operationService.deleteAll();
    }

    @PutMapping("/operations/{id}")
    public OperationDTO updateById(@PathVariable int id, @RequestBody OperationDTO body) {
        return operationService.updateById(id, body);
    }
}

