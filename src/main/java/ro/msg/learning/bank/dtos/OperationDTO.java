package ro.msg.learning.bank.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.entities.AppUser;
import ro.msg.learning.bank.entities.Client;
import ro.msg.learning.bank.entities.Operation;
import ro.msg.learning.bank.entities.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class OperationDTO {

    private OperationType type;
    private String username;
    private String clientFirstname;
    private String clientLastname;
    private BigDecimal clientCash;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeStamp;


    public OperationDTO(OperationType type, String username, String clientFirstname, String clientLastname,
                        BigDecimal clientCash, LocalDateTime timeStamp) {
        this.type = type;
        this.username = username;
        this.clientFirstname = clientFirstname;
        this.clientLastname = clientLastname;
        this.clientCash = clientCash;
        this.timeStamp = timeStamp;
    }

    public Operation toEntity() {
        Operation result = new Operation();
        this.copyToEntity(result);
        return result;
    }

    public void copyToEntity(Operation operation) {
        Client client = new Client();
        client.setFirstname(clientFirstname);
        client.setLastname(clientLastname);
        client.setMoneyCash(clientCash);

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setClient(client);

        operation.setType(type);
        operation.setUserDetail(appUser);
        operation.setTimeStamp(timeStamp);

    }

    public static OperationDTO of(Operation entity) {
        return OperationDTO.builder()
                .type(entity.getType())
                .username(entity.getUserDetail().getUsername())
                .clientFirstname(entity.getUserDetail().getClient().getFirstname())
                .clientLastname(entity.getUserDetail().getClient().getLastname())
                .clientCash(entity.getUserDetail().getClient().getMoneyCash())
                .timeStamp(entity.getTimeStamp())
                .build();
    }

}
