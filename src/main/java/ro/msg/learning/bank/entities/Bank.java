package ro.msg.learning.bank.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "name", columnDefinition = "ENUM('BTBANK', 'BROMBNAK', 'CITYBANK', 'INGBANK', 'BRDBANK')", nullable = false)
    private BankCompany name;
}
