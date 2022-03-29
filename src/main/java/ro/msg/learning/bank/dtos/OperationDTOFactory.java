package ro.msg.learning.bank.dtos;

import ro.msg.learning.bank.entities.OperationType;

public class OperationDTOFactory {
    public IOperationDTO getOperationDTO(String operationType) {
        if (operationType == null) return null;

        if (operationType.equalsIgnoreCase(OperationType.LOGIN.toString())) return new LoginDTO();
        if (operationType.equalsIgnoreCase(OperationType.DEPOSIT.toString())) return new AccountTransactionDTO();
        if (operationType.equalsIgnoreCase(OperationType.WITHDRAW.toString())) return new AccountTransactionDTO();
        if (operationType.equalsIgnoreCase(OperationType.TRANSFER.toString())) return new AccountTransactionDTO();
        if (operationType.equalsIgnoreCase(OperationType.CLOSEACCOUNT.toString())) return new AccountMeasureDTO();
        if (operationType.equalsIgnoreCase(OperationType.BLOCKACCOUNT.toString())) return new AccountMeasureDTO();
        if (operationType.equalsIgnoreCase(OperationType.UNBLOCKACCOUNT.toString())) return new AccountMeasureDTO();
        return null;

    }
}
