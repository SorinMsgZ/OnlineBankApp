package ro.msg.learning.bank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.bank.entities.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser,String> {
    Optional<AppUser> findByUsername(String username);
    void deleteByUsername(String username);
}
