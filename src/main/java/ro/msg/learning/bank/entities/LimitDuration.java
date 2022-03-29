package ro.msg.learning.bank.entities;

import ro.msg.learning.bank.exceptions.DurationException;

public enum LimitDuration {ACCOUNT_SAVING_WITHDRAW_MAX_VALID_YEARS(1), ACCOUNT_SAVING_WITHDRAW_IN_MONTH_VALID_TIMES(2);

private int limitDuration;

    LimitDuration(int limitDuration) {
        this.limitDuration = limitDuration;
    }

    public int getLimitDuration() {
        return limitDuration;
    }

    public static LimitDuration getDuration(int duration){
        for (LimitDuration limitDuration:LimitDuration.values()){
            if (limitDuration.getLimitDuration()==duration) return limitDuration;
        }
        throw new DurationException();
    }
}
