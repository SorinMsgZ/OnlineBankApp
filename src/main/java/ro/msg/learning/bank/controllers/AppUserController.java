package ro.msg.learning.bank.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.bank.dtos.AppUserDTO;
import ro.msg.learning.bank.dtos.AppUserSecurityDTO;
import ro.msg.learning.bank.services.AppUserService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("/appusers")
    public List<AppUserDTO> listAll() {
        return appUserService.listAll();
    }

    @PostMapping("/appusers")
    public AppUserSecurityDTO create(@RequestBody AppUserSecurityDTO appUserSecurityDTO) {
        return appUserService.create(appUserSecurityDTO);
    }

    @DeleteMapping("/appusers/{username}")
    public void deleteByFirstnameAndLastname(@PathVariable String username) {
        appUserService.deleteByUserName(username);
    }

    @DeleteMapping("/appusers")
    public void deleteAll() {
        appUserService.deleteAll();
    }

    @PutMapping("/appusers/{username}")
    public AppUserDTO updateByFirstnameAndLastname(@PathVariable String username, @RequestBody AppUserDTO body) {
        return appUserService.updateByUserName(username, body);
    }
}

