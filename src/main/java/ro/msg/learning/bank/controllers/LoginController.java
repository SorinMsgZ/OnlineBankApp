package ro.msg.learning.bank.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.bank.dtos.LoginDTO;
import ro.msg.learning.bank.services.LoginService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {


    private final LoginService loginService;

    @GetMapping("/logsessions")
    public List<LoginDTO> listAll() {
        return loginService.listAll();
    }

    @PostMapping("/logsessions")
    public LoginDTO create(@RequestBody LoginDTO login) {
        return loginService.create(login);
    }


    @DeleteMapping("/logsessions/{id}")
    public void deleteById(@PathVariable int id) {
        loginService.deleteById(id);
    }

    @DeleteMapping("/logsessions")
    public void deleteAll() {
        loginService.deleteAll();
    }

    @PutMapping("/logsessions/{id}")
    public LoginDTO updateById(@PathVariable int id, @RequestBody LoginDTO body) {
        return loginService.updateById(id, body);
    }
}

