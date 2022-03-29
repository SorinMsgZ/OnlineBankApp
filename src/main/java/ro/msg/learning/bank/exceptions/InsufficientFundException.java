package ro.msg.learning.bank.exceptions;

public class InsufficientFundException extends RuntimeException {
    public InsufficientFundException() {
        super("The Client or the Account has insufficient CASH/ FUND!");
    }
}
