package ro.msg.learning.bank.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
public class Account implements Serializable {
    @Id

    private int iban;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="bank_id")
    private Bank bank;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "ENUM('CURRENT_ACCOUNT','SAVING_ACCOUNT')", nullable = false)
    private AccountType type;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime creatingDate;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime closingDate;
    private boolean isBlocked;
    private boolean isClosed;

}
