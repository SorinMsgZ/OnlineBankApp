package ro.msg.learning.bank.schedules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.services.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
public class ScheduledJobAutomaticCloseAccountUTest {


    public static final int PERIOD_AUTOMATICALLY_CLOSE_ACCOUNT_IN_MONTHS = 6;
    public static final LocalDateTime DATE_NOW = LocalDateTime.now();
    @MockBean
    private AccountDetailService accountDetailService;
    @MockBean
    private AccountMeasureService accountMeasureService;
    @MockBean
    private AccountTransactionService accountTransactionService;

    @Test
    public void test_whenDateOfLastTransactionOperationHas_0_DaysUntilClosePeriod_thenSendInformationToAccountMeasureService() {


        ScheduledJobAutomaticCloseAccount scheduledJobAutomaticCloseAccount =
                new ScheduledJobAutomaticCloseAccount(accountDetailService, accountMeasureService, accountTransactionService);

        LocalDateTime dayOfMonthAccountCreated = getDateWithCustom_DaysUntilClosePeriod(20);
        LocalDateTime lastOperationTimeStamp= getDateWithCustom_DaysUntilClosePeriod(0);
        getMocks(dayOfMonthAccountCreated,lastOperationTimeStamp);

        scheduledJobAutomaticCloseAccount.closeAutomaticallyAccount();

        verify(accountMeasureService, times(1)).takeMeasure(any(AccountMeasureDTO.class));

    }

    @Test
    public void test_whenDateOfLastTransactionOperationHas_1_DaysUntilClosePeriod_thenDoNot_SendInformationToAccountMeasureService() {


        ScheduledJobAutomaticCloseAccount scheduledJobAutomaticCloseAccount =
                new ScheduledJobAutomaticCloseAccount(accountDetailService, accountMeasureService, accountTransactionService);

        LocalDateTime dayOfMonthAccountCreated = getDateWithCustom_DaysUntilClosePeriod(20);
        LocalDateTime lastOperationTimeStamp= getDateWithCustom_DaysUntilClosePeriod(1);
        getMocks(dayOfMonthAccountCreated,lastOperationTimeStamp);

        scheduledJobAutomaticCloseAccount.closeAutomaticallyAccount();

        verify(accountMeasureService, times(0)).takeMeasure(any(AccountMeasureDTO.class));

    }


    private void getMocks(LocalDateTime dayOfMonthAccountCreated,LocalDateTime lastOperationTimeStamp) {
        AccountDTO accountDTO = AccountDTO.builder()
                .creatingDate(dayOfMonthAccountCreated)
                .build();

        AccountDetailDTO accountDetailDTO = AccountDetailDTO.builder()
                .account(accountDTO.toEntity())
                .build();
        List<AccountDetailDTO> accountDetailDTOS = new ArrayList<>();
        accountDetailDTOS.add(accountDetailDTO);



        OperationDTO operationDTO=OperationDTO.builder()
                .timeStamp(lastOperationTimeStamp)
                .build();

        AccountTransactionDTO accountTransactionDTO = AccountTransactionDTO.builder()
                .account(accountDTO.toEntity())
                .operation(operationDTO.toEntity())
                .build();

        List<AccountTransactionDTO> accountTransactionDTOList = new ArrayList<>();
        accountTransactionDTOList.add(accountTransactionDTO);

        when(accountDetailService.readByIsClosedStatusAndCustomAmount(false, BigDecimal.ZERO))
                .thenReturn(accountDetailDTOS);
        when(accountTransactionService.listAllByIban(anyInt())).thenReturn(accountTransactionDTOList);
    }

    private LocalDateTime getDateWithCustom_DaysUntilClosePeriod(int daysUntilClosePeriod) {
        return DATE_NOW.minusMonths(PERIOD_AUTOMATICALLY_CLOSE_ACCOUNT_IN_MONTHS).plusDays(daysUntilClosePeriod);
    }
}
