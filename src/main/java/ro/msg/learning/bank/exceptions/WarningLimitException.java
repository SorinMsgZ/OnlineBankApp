package ro.msg.learning.bank.exceptions;


public class WarningLimitException extends RuntimeException {
    public WarningLimitException(double amount) {
        super("Withdraw or Transfer following Amount before closing the Account!: "+amount);
    }
}
