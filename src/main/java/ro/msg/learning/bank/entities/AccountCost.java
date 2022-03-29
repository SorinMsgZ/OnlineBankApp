package ro.msg.learning.bank.entities;

import ro.msg.learning.bank.exceptions.CostException;

import java.math.BigDecimal;

public enum AccountCost {
    CLOSING_TAXES(new BigDecimal(5)),
    MAINTENANCE_COST(new BigDecimal(5));

    private BigDecimal cost;

    AccountCost(BigDecimal taxes) {
        this.cost = taxes;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public static AccountCost getCost(BigDecimal cost) {
        for (AccountCost accountCost : AccountCost.values()) {
            if (accountCost.getCost().compareTo(cost) == 0) return accountCost;
        }
        throw new CostException();
    }

}
