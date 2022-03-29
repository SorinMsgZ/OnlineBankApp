package ro.msg.learning.bank.entities;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
public class AccountTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "operation_id")
    private Operation operation;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "account_iban")
    private Account account;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "account_iban", insertable=false, updatable=false)
    private Account accountReceiver;
    private BigDecimal amount;

}
