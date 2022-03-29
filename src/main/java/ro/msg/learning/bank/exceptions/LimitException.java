package ro.msg.learning.bank.exceptions;

public class LimitException extends RuntimeException {
    public LimitException() {
        super("The actual limit is not Supported /or the actual Amount or Times of Withdraw does not meet the required Amount or Times Limit for this operation!");
    }
}
