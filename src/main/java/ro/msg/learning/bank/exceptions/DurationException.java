package ro.msg.learning.bank.exceptions;

public class DurationException extends RuntimeException {
    public DurationException() {
        super("limit duration is not supported");
    }
}
