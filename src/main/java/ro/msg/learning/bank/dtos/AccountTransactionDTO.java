package ro.msg.learning.bank.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.entities.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
public class AccountTransactionDTO implements IOperationDTO {
    private Operation operation;
    private Account account;
    private Account accountReceiver;
    private BigDecimal amount;

    public AccountTransactionDTO(Operation operation, Account account,
                                 Account accountReceiver, BigDecimal amount) {
        this.operation = operation;
        this.account = account;
        this.accountReceiver = accountReceiver;
        this.amount = amount;
    }

    public AccountTransactionDTO(OperationType operationType) {
        this.operation.setType(operationType);
    }

    public AccountTransaction toEntity() {
        AccountTransaction result = new AccountTransaction();
        this.copyToEntity(result);
        return result;
    }

    public void copyToEntity(AccountTransaction accountTransaction) {

        accountTransaction.setOperation(operation);
        accountTransaction.setAccount(account);
        accountTransaction.setAccountReceiver(accountReceiver);
        accountTransaction.setAmount(amount);
    }

    public static AccountTransactionDTO of(AccountTransaction entity) {
        return AccountTransactionDTO.builder()
                .operation(entity.getOperation())
                .account(entity.getAccount())
                .accountReceiver(entity.getAccountReceiver())
                .amount(entity.getAmount())
                .build();
    }
}
