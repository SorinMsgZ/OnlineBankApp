package ro.msg.learning.bank.schedules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ro.msg.learning.bank.dtos.AccountDTO;
import ro.msg.learning.bank.dtos.AccountDetailDTO;
import ro.msg.learning.bank.dtos.AccountTransactionDTO;
import ro.msg.learning.bank.entities.AccountType;
import ro.msg.learning.bank.services.AccountDetailService;
import ro.msg.learning.bank.services.AccountTransactionWithdrawService;
import ro.msg.learning.bank.services.ScheduledJobCurrentAccount;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
public class ScheduledJobCurrentAccountUTest {


    public static final int PERIOD_RETAINING_COST_IN_MONTHS = 1;
    @MockBean
    private AccountTransactionWithdrawService accountTransactionWithdrawService;
    @MockBean
    private AccountDetailService accountDetailService;

    @Test
    public void test_whenDaysUntilMaturityPeriodIs0_thenSendInformationToAccountTransactionWithdrawService() {


        ScheduledJobCurrentAccount scheduledJobCurrentAccount =
                new ScheduledJobCurrentAccount(accountTransactionWithdrawService, accountDetailService);

        LocalDateTime dayOfMonthAccountCreated = getDateWithCustom_DaysUntilMaturityPeriod(LocalDateTime.now(), 0);
        getMocks(dayOfMonthAccountCreated);

        scheduledJobCurrentAccount.retainMonthlyMaintenanceCostFromCurrentAccount();

        verify(accountTransactionWithdrawService,times(1)).transactWithdraw(any(AccountTransactionDTO.class));


    }

    @Test
    public void test_whenDaysUntilMaturityPeriodIsGraterThan0_thenDoNotSendInformationToAccountTransactionWithdrawService() {


        ScheduledJobCurrentAccount scheduledJobCurrentAccount =
                new ScheduledJobCurrentAccount(accountTransactionWithdrawService, accountDetailService);

        LocalDateTime dayOfMonthAccountCreated = getDateWithCustom_DaysUntilMaturityPeriod(LocalDateTime.now(), 1);
        getMocks(dayOfMonthAccountCreated);

        scheduledJobCurrentAccount.retainMonthlyMaintenanceCostFromCurrentAccount();

        verify(accountTransactionWithdrawService,times(0)).transactWithdraw(any(AccountTransactionDTO.class));


    }

    private void getMocks(LocalDateTime dayOfMonthAccountCreated) {
        AccountDTO accountDTO = AccountDTO.builder()
                .creatingDate(dayOfMonthAccountCreated)
                .build();

        AccountDetailDTO accountDetailDTO = AccountDetailDTO.builder()
                .account(accountDTO.toEntity())
                .build();
        List<AccountDetailDTO> accountDetailDTOS = new ArrayList<>();
        accountDetailDTOS.add(accountDetailDTO);

        when(accountDetailService.readByAccountTypeAndIsClosedStatus(AccountType.CURRENT_ACCOUNT, false)).thenReturn(accountDetailDTOS);
        doNothing().when(accountTransactionWithdrawService).setAutomaticJob(true);
    }

    private LocalDateTime getDateWithCustom_DaysUntilMaturityPeriod(LocalDateTime currentDay, int daysUntilMaturityPeriod) {
        return currentDay.minusMonths(PERIOD_RETAINING_COST_IN_MONTHS).plusDays(daysUntilMaturityPeriod);
    }
}
