package ro.msg.learning.bank.entities;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
@Builder
public class AccountDetailId implements Serializable {
    @Column(name = "app_username")
    private String userIdentification;
    @Column(name = "account_iban")
    private int accountIdentification;

    public AccountDetailId() {
    }

    public AccountDetailId(String userIdentification, int accountIdentification) {
        this.userIdentification = userIdentification;
        this.accountIdentification = accountIdentification;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIdentification, accountIdentification);
    }
}
