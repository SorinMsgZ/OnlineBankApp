package ro.msg.learning.bank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.bank.entities.Operation;
import ro.msg.learning.bank.entities.OperationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OperationRepository extends JpaRepository<Operation, Integer> {

    List<Operation> findByType(OperationType operationType);
    Optional<Operation>findByTimeStamp(LocalDateTime localDateTime);
}
