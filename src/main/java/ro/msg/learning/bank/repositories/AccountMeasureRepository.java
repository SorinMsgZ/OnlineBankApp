package ro.msg.learning.bank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.bank.entities.AccountMeasure;

public interface AccountMeasureRepository extends JpaRepository<AccountMeasure,Integer> {
}
