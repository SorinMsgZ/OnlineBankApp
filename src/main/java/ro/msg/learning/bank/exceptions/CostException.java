package ro.msg.learning.bank.exceptions;

public class CostException extends RuntimeException {
    public CostException() {
        super("cost is not supported");
    }
}
