package ro.msg.learning.bank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.bank.entities.Login;

public interface LoginRepository extends JpaRepository<Login,Integer> {

}
