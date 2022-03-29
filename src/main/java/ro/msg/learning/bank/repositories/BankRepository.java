package ro.msg.learning.bank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.bank.entities.Bank;
import ro.msg.learning.bank.entities.BankCompany;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank,Integer> {
    Optional<Bank> findByName(BankCompany bankCompany);
}
