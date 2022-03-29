package ro.msg.learning.bank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.bank.entities.AccountTransaction;
import ro.msg.learning.bank.entities.OperationType;

import java.util.Optional;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction,Integer> {
    Optional<AccountTransaction> findByOperation_Type(OperationType operationType);
}
