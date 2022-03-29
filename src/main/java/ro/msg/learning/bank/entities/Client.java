package ro.msg.learning.bank.entities;


import lombok.Data;


import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data

public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "default_bank", columnDefinition = "ENUM('BTBANK', 'BROMBNAK', 'CITYBANK', 'INGBANK', 'BRDBANK')", nullable = false)
    private final BankCompany defaultBank=BankCompany.BTBANK;
    private BigDecimal moneyCash;
    private String firstname;
    private String lastname;
}
