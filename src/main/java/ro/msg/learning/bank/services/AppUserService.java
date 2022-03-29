package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.bank.dtos.AppUserDTO;
import ro.msg.learning.bank.dtos.AppUserSecurityDTO;
import ro.msg.learning.bank.entities.AppUser;
import ro.msg.learning.bank.exceptions.NotFoundException;
import ro.msg.learning.bank.repositories.AppUserRepository;
import ro.msg.learning.bank.repositories.ClientRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final ClientRepository clientRepository;

    public List<AppUserDTO> listAll() {
        return appUserRepository.findAll().stream()
                .map(AppUserDTO::of)
                .collect(Collectors.toList());
    }

    public AppUserDTO readByUserName(String userName) {
        return appUserRepository.findByUsername(userName)
                .map(AppUserDTO::of)
                .orElseThrow(NotFoundException::new);
    }

    public AppUserSecurityDTO create(AppUserSecurityDTO input) {
        AppUser appUser = input.toEntity();
        appUser.setUsername(input.getUsername());

        BCryptPasswordEncoder bCryptPasswordEncoder =new BCryptPasswordEncoder();
        appUser.setPassword(bCryptPasswordEncoder.encode(input.getPassword()));

        appUser.setClient(clientRepository.findByFirstnameAndLastname(input.getClient().getFirstname(),input.getClient().getLastname()).orElseThrow(NotFoundException::new));
        return AppUserSecurityDTO.of(appUserRepository.save(appUser));
    }

    public void deleteByUserName(String userName) {
        appUserRepository.deleteByUsername(userName);
    }

    public void deleteAll() {
        appUserRepository.deleteAll();
    }

    public AppUserDTO updateByUserName(String userName, AppUserDTO input) {
        AppUser appUser =
                appUserRepository.findByUsername(userName).orElseThrow(NotFoundException::new);
        input.copyToEntity(appUser);
        appUserRepository.save(appUser);
        return AppUserDTO.of(appUser);
    }
}
