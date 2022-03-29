package ro.msg.learning.bank.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class AccountMeasure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="operation_id")
    private Operation operation;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="account_iban")
    private Account account;
}
