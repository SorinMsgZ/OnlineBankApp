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
public class Operation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "ENUM('LOGIN','DEPOSIT','WITHDRAW','TRANSFER','CLOSEACCOUNT','BLOCKACCOUNT','UNBLOCKACCOUNT')", nullable = false)
    private OperationType type;
    @OneToOne
    @JoinColumn(name = "user_detail")
    private AppUser userDetail;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeStamp;

}
