package ro.msg.learning.bank.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.entities.Login;
import ro.msg.learning.bank.entities.Operation;


@Data
@Builder
@NoArgsConstructor
public class LoginDTO implements IOperationDTO {

    private Operation operation;

    public LoginDTO(Operation operation) {
        this.operation = operation;
    }

    public Login toEntity() {
        Login result = new Login();
        this.copyToEntity(result);
        return result;
    }

    public void copyToEntity(Login login) {

        login.setOperation(operation);

    }

    public static LoginDTO of(Login entity) {
        return LoginDTO.builder()
                .operation(entity.getOperation())
                .build();
    }
}
