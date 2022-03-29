package ro.msg.learning.bank.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ro.msg.learning.bank.deposit.AccountTransactionDepositServiceUTest;
import ro.msg.learning.bank.login.LoginControllerUTest;
import ro.msg.learning.bank.measures.AccountMeasureControllerUTest;
import ro.msg.learning.bank.measures.AccountMeasureServiceUTest;
import ro.msg.learning.bank.schedules.ScheduledJobAutomaticCloseAccountUTest;
import ro.msg.learning.bank.schedules.ScheduledJobCurrentAccountUTest;
import ro.msg.learning.bank.schedules.ScheduledJobSavingAccountUTest;
import ro.msg.learning.bank.transfer.AccountTransactionTransferServiceUTest;
import ro.msg.learning.bank.withdraw.AccountTransactionWithdrawServiceUTest;

@RunWith(Suite.class)
@Suite.SuiteClasses
        ({
                LoginControllerUTest.class,
                AccountMeasureControllerUTest.class,
                AccountMeasureServiceUTest.class,
                AccountTransactionWithdrawServiceUTest.class,
                AccountTransactionDepositServiceUTest.class,
                AccountTransactionTransferServiceUTest.class,
                ScheduledJobCurrentAccountUTest.class,
                ScheduledJobSavingAccountUTest.class,
                ScheduledJobAutomaticCloseAccountUTest.class
        })
public class UTSuite {
}
