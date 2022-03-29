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
import ro.msg.learning.bank.services.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
public class ScheduledJobSavingAccountUTest {


    public static final int PERIOD_INTEREST_RATE_IN_MONTHS = 1;
    public static final BigDecimal ACCOUNT_AMOUNT_BEFORE_ADDING_INTEREST_RATE = new BigDecimal(100);
    @MockBean
    private AccountTransactionDepositService accountTransactionDepositService;
    @MockBean
    private AccountDetailService accountDetailService;

    @Test
    public void test_whenDaysUntilInterestDayIs0_thenSendInformationToAccountTransactionDepositService() {


        ScheduledJobSavingAccount scheduledJobSavingAccount =
                new ScheduledJobSavingAccount(accountTransactionDepositService, accountDetailService);

        LocalDateTime dayOfMonthAccountCreated = getDateWithCustom_DaysUntilMaturityPeriod(LocalDateTime.now(), 0);
        getMocks(dayOfMonthAccountCreated);

        scheduledJobSavingAccount.addMonthlyInterestRateToSavingAccount();

        verify(accountTransactionDepositService, times(1)).transactDeposit(any(AccountTransactionDTO.class));


    }

    @Test
    public void test_whenDaysUntilInterestDayIsOver0_thenDoNotSendInformationToAccountTransactionDepositService() {


        ScheduledJobSavingAccount scheduledJobSavingAccount =
                new ScheduledJobSavingAccount(accountTransactionDepositService, accountDetailService);

        LocalDateTime dayOfMonthAccountCreated = getDateWithCustom_DaysUntilMaturityPeriod(LocalDateTime.now(), 1);
        getMocks(dayOfMonthAccountCreated);

        scheduledJobSavingAccount.addMonthlyInterestRateToSavingAccount();

        verify(accountTransactionDepositService, times(0)).transactDeposit(any(AccountTransactionDTO.class));


    }

    private void getMocks(LocalDateTime dayOfMonthAccountCreated) {
        AccountDTO accountDTO = AccountDTO.builder()
                .creatingDate(dayOfMonthAccountCreated)
                .build();

        AccountDetailDTO accountDetailDTO = AccountDetailDTO.builder()
                .account(accountDTO.toEntity())
                .accountAmount(ACCOUNT_AMOUNT_BEFORE_ADDING_INTEREST_RATE)
                .build();
        List<AccountDetailDTO> accountDetailDTOS = new ArrayList<>();
        accountDetailDTOS.add(accountDetailDTO);

        when(accountDetailService.readByAccountTypeAndIsClosedStatus(AccountType.SAVING_ACCOUNT, false))
                .thenReturn(accountDetailDTOS);
        doNothing().when(accountTransactionDepositService).setAutomaticJob(true);
    }

    private LocalDateTime getDateWithCustom_DaysUntilMaturityPeriod(LocalDateTime currentDay, int daysUntilMaturityPeriod) {
        return currentDay.minusMonths(PERIOD_INTEREST_RATE_IN_MONTHS).plusDays(daysUntilMaturityPeriod);
    }
}
