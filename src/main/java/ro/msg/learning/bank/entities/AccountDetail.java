package ro.msg.learning.bank.entities;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity(name="AccountTable")
@Table(name="account_detail")

public class AccountDetail {

    @EmbeddedId
    private AccountDetailId idUserAccount;
    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("userIdentification")
    @JoinColumn(name = "app_username")
    private AppUser appUser;
    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("accountIdentification")
    @JoinColumn(name = "account_iban")
    private Account account;
    private BigDecimal accountAmount;
}
