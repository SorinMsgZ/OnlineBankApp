package ro.msg.learning.bank.exceptions;

public class NotOperableException extends RuntimeException {
    public NotOperableException() {
        super("Wrong Operation or the Account is already closed or blocked");
    }

    public NotOperableException(String message) {
        super(message);
    }
}