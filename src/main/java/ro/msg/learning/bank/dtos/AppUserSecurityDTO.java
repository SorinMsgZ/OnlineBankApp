package ro.msg.learning.bank.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.entities.AppUser;
import ro.msg.learning.bank.entities.Client;

@Data
@Builder
@NoArgsConstructor
public class AppUserSecurityDTO {
    private String username;
    private String password;
    private Client client;

    public AppUserSecurityDTO(String username, String password, Client client) {
        this.username = username;
        this.password = password;
        this.client = client;
    }

    public AppUser toEntity() {
        AppUser result = new AppUser();
        this.copyToEntity(result);
        return result;
    }

    public void copyToEntity(AppUser appUser) {
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setClient(client);
    }

    public static AppUserSecurityDTO of(AppUser entity) {
        return AppUserSecurityDTO.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .client(entity.getClient())
                .build();
    }
}
