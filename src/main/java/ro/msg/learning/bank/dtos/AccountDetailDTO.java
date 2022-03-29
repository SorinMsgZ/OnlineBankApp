package ro.msg.learning.bank.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.entities.Account;
import ro.msg.learning.bank.entities.AccountDetail;
import ro.msg.learning.bank.entities.AccountDetailId;
import ro.msg.learning.bank.entities.AppUser;


import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
public class AccountDetailDTO {
    private AppUser appUser;
    private Account account;
    private BigDecimal accountAmount;

    public AccountDetailDTO(AppUser appUsername, Account account, BigDecimal accountAmount) {
        this.appUser = appUsername;
        this.account = account;
        this.accountAmount = accountAmount;
    }
    public AccountDetail toEntity() {
        AccountDetail result = new AccountDetail();
        this.copyToEntity(result);
        return result;
    }

    public void copyToEntity(AccountDetail accountDetail) {
        AccountDetailId accountDetailId= new AccountDetailId();
        accountDetailId.setUserIdentification(appUser.getUsername());
        accountDetailId.setAccountIdentification(account.getIban());
        accountDetail.setIdUserAccount(accountDetailId);

        accountDetail.setAppUser(appUser);
        accountDetail.setAccount(account);
        accountDetail.setAccountAmount(accountAmount);
    }

    public static AccountDetailDTO of(AccountDetail entity) {
        return AccountDetailDTO.builder()
                .appUser(entity.getAppUser())
                .account(entity.getAccount())
                .accountAmount(entity.getAccountAmount())
                .build();
    }
}
