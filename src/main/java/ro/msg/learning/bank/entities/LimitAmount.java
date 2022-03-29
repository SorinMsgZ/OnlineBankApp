package ro.msg.learning.bank.entities;

import ro.msg.learning.bank.exceptions.LimitException;

import java.math.BigDecimal;

public enum LimitAmount {
    WITHDRAW_MAX_PER_DAY(new BigDecimal(5000)),
    WITHDRAW_MIN_PER_TRANSACTION(new BigDecimal(10)),
    WITHDRAW_MAX_PER_MONTH_ACCOUNT_SAVING(new BigDecimal(10000)),
    DEPOSIT_MIN(new BigDecimal(10)),
    TRANSFER_MAX_PER_TRANSACTION(new BigDecimal(2000)),
    TRANSFER_MIN_PER_TRANSACTION(new BigDecimal(10)),
    TRANSFER_MAX_PER_DAY(new BigDecimal(5000));

    private BigDecimal limit;

    LimitAmount(BigDecimal limit) {
        this.limit = limit;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public static LimitAmount getLimit(BigDecimal limit) {
        for (LimitAmount limitAmount : LimitAmount.values())
            if (limitAmount.getLimit().compareTo(limit) == 0) return limitAmount;
        throw new LimitException();
    }
}
