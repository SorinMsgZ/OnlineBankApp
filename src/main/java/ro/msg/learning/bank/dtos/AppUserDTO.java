package ro.msg.learning.bank.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.entities.AppUser;
import ro.msg.learning.bank.entities.Client;

@Data
@Builder
@NoArgsConstructor
public class AppUserDTO {
    private String username;
    private Client client;

    public AppUserDTO(String username, Client client) {
        this.username = username;
        this.client = client;
    }

    public AppUser toEntity() {
        AppUser result = new AppUser();
        this.copyToEntity(result);
        return result;
    }

    public void copyToEntity(AppUser appUser) {
        appUser.setUsername(username);
        appUser.setPassword("MockUser");
        appUser.setClient(client);
    }

    public static AppUserDTO of(AppUser entity) {
        return AppUserDTO.builder()
                .username(entity.getUsername())
                .client(entity.getClient())
                .build();
    }
}
