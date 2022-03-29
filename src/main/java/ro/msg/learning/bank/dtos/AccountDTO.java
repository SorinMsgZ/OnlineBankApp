package ro.msg.learning.bank.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.entities.Account;
import ro.msg.learning.bank.entities.AccountType;
import ro.msg.learning.bank.entities.Bank;
import ro.msg.learning.bank.entities.BankCompany;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class AccountDTO {
    private int iban;
    private BankCompany bankCompany;
    private AccountType type;
    private LocalDateTime creatingDate;
    private LocalDateTime closingDate;
    private boolean isBlocked;
    private boolean isClosed;

    public AccountDTO(int iban, BankCompany bankCompany, AccountType type, LocalDateTime creatingDate,
                      LocalDateTime closingDate, boolean isBlocked, boolean isClosed) {
        this.iban = iban;
        this.bankCompany = bankCompany;
        this.type = type;
        this.creatingDate = creatingDate;
        this.closingDate = closingDate;
        this.isBlocked = isBlocked;
        this.isClosed = isClosed;
    }

    public Account toEntity() {
        Account result = new Account();
        this.copyToEntity(result);
        return result;
    }

    public void copyToEntity(Account account) {
        Bank bank = new Bank();
        bank.setName(bankCompany);

        account.setIban(iban);
        account.setBank(bank);
        account.setType(type);
        account.setCreatingDate(creatingDate);
        account.setClosingDate(closingDate);
        account.setBlocked(isBlocked);
        account.setClosed(isClosed);

    }

    public static AccountDTO of(Account entity) {
        return AccountDTO.builder()
                .iban(entity.getIban())
                .bankCompany(entity.getBank().getName())
                .type(entity.getType())
                .creatingDate(entity.getCreatingDate())
                .closingDate(entity.getClosingDate())
                .isBlocked(entity.isBlocked())
                .isClosed(entity.isClosed())
                .build();
    }
}
