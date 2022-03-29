package ro.msg.learning.bank.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ro.msg.learning.bank.entities.AppUser;
import ro.msg.learning.bank.repositories.AppUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataBaseUserService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findById(username)
                .map(DataBaseUserService::mapUser)
                .orElseThrow(() -> new UsernameNotFoundException("The requested user was not found!"));
    }

    private static UserDetails mapUser(AppUser appUser) {
        return new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(), List
                .of(new SimpleGrantedAuthority(appUser.getClient() == null ? "ROLE_CLIENT" : "ROLE_NOCLIENT")));
    }

}
