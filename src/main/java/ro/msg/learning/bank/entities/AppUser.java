package ro.msg.learning.bank.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data

public class AppUser {
    @Id
    private String username;
    private String password;
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "client_id")
    private Client client;
}
