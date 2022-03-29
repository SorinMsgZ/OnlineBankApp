package ro.msg.learning.bank.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    @JoinColumn(name="operation_id")
    private Operation operation;
}
