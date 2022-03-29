package ro.msg.learning.bank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.bank.entities.AccountDetail;
import ro.msg.learning.bank.entities.AccountDetailId;
import ro.msg.learning.bank.entities.AccountType;

import java.util.Optional;

public interface AccountDetailRepository extends JpaRepository<AccountDetail, AccountDetailId> {
    Optional<AccountDetail> findByAppUser_Username(String username);
    Optional<AccountDetail> findByAccount_Iban(int iban);
    Optional<AccountDetail> findByAppUser_UsernameAndAccountIban_Iban(String username, int iban);
    Optional<AccountDetail> findByAccount_TypeAndAccountIban_isClosed(AccountType accountType, boolean isClosed);
    Optional<AccountDetail> findByAccount_isClosed(boolean isClosed);
}
