package ro.msg.learning.bank.schedules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ro.msg.learning.bank.OnlineBankApplication;

import ro.msg.learning.bank.services.ScheduledJobSavingAccount;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = OnlineBankApplication.class)
@TestPropertySource("classpath:test.properties")
@ActiveProfiles("scheduledJobs")
public class ScheduledJobSavingAccountITest {

    @SpyBean
    private ScheduledJobSavingAccount scheduledJobSavingAccount;

    @Test
    public void test_whenWaitFiveSecond_thenScheduledIsCalledAtLeastThreeTimes() {

        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(scheduledJobSavingAccount, atLeast(3))
                        .addMonthlyInterestRateToSavingAccount());

    }
}
