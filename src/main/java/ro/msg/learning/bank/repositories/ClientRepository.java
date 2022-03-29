package ro.msg.learning.bank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.bank.entities.Client;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    Optional<Client> findByFirstnameAndLastname(String firstname, String lastname);

    void deleteByFirstnameAndLastname(String firstname, String lastname);

}
