package ro.msg.learning.bank.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.entities.Bank;
import ro.msg.learning.bank.entities.BankCompany;


@Data
@Builder
@NoArgsConstructor
public class BankDTO {

    private BankCompany name;

    public BankDTO(BankCompany name) {
        this.name = name;
    }

    public Bank toEntity() {
        Bank result = new Bank();
        this.copyToEntity(result);
        return result;
    }

    public void copyToEntity(Bank bank) {
        bank.setName(name);

    }

    public static BankDTO of(Bank entity) {
        return BankDTO.builder()
                .name(entity.getName())
                .build();
    }
}

