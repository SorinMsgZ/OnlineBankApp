package ro.msg.learning.bank.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.bank.dtos.ClientDTO;
import ro.msg.learning.bank.services.ClientService;


import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/clients")
    public List<ClientDTO> listAll() {
        return clientService.listAll();
    }

    @GetMapping("/clients/{firstName}&{lastName}")
    public ClientDTO readByFirstNameAndLastName(@PathVariable String firstname, @PathVariable String lastname) {
        return clientService.readByFirstNameAndLastName(firstname, lastname);
    }

    @PostMapping("/clients")
    public ClientDTO create(@RequestBody ClientDTO clientDTO) {
        return clientService.create(clientDTO);
    }

    @DeleteMapping("/clients/{firstName}&{lastName}")
    public void deleteByFirstnameAndLastname(@PathVariable String firstname, @PathVariable String lastname) {
        clientService.deleteByFirstnameAndLastname(firstname, lastname);
    }

    @DeleteMapping("/clients")
    public void deleteAll() {
        clientService.deleteAll();
    }

    @PutMapping("/clients/{firstName}&{lastName}")
    public ClientDTO updateByFirstnameAndLastname(@PathVariable String firstname, @PathVariable String lastname,
                                                  @RequestBody ClientDTO body) {
        return clientService.updateByFirstNameAndLastName(firstname, lastname, body);
    }
}
