package ro.msg.learning.bank.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.entities.Client;


import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
public class ClientDTO {

    private BigDecimal moneyCash;
    private String firstName;
    private String lastName;

    public ClientDTO(BigDecimal moneyCash, String firstName, String lastName) {
        this.moneyCash = moneyCash;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Client toEntity() {
        Client result = new Client();
        this.copyToEntity(result);
        return result;
    }

    public void copyToEntity(Client client) {
        client.setFirstname(firstName);
        client.setLastname(lastName);
        client.setMoneyCash(moneyCash);
    }

    public static ClientDTO of(Client entity) {
        return ClientDTO.builder()
                .moneyCash(entity.getMoneyCash())
                .firstName(entity.getFirstname())
                .lastName(entity.getLastname())
                .build();
    }
}
