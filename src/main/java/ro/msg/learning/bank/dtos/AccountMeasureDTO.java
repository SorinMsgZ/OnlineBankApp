package ro.msg.learning.bank.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.entities.Account;
import ro.msg.learning.bank.entities.AccountMeasure;
import ro.msg.learning.bank.entities.Operation;
import ro.msg.learning.bank.entities.OperationType;

@Data
@Builder
@NoArgsConstructor
public class AccountMeasureDTO implements IOperationDTO{
    private Operation operation;
    private Account account;

    public AccountMeasureDTO(Operation operation, Account account) {
        this.operation = operation;
        this.account = account;
    }
    public AccountMeasureDTO(OperationType operationType) {
        this.operation.setType(operationType);
    }

    public AccountMeasure toEntity() {
        AccountMeasure result = new AccountMeasure();
        this.copyToEntity(result);
        return result;
    }

    public void copyToEntity(AccountMeasure accountMeasure) {
        accountMeasure.setOperation(operation);
        accountMeasure.setAccount(account);
    }

    public static AccountMeasureDTO of(AccountMeasure entity) {
        return AccountMeasureDTO.builder()
                .operation(entity.getOperation())
                .account(entity.getAccount())
                .build();
    }

}

