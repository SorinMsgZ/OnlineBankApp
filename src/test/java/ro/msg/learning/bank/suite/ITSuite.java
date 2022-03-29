package ro.msg.learning.bank.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ro.msg.learning.bank.login.LoginControllerITest;
import ro.msg.learning.bank.login.LoginControllerAuthenticationITest;
import ro.msg.learning.bank.populate.*;
import ro.msg.learning.bank.scenarios.AccountMeasureScenarioITest;
import ro.msg.learning.bank.scenarios.AccountTransactionScenarioITest;
import ro.msg.learning.bank.scenarios.LoginScenarioITest;
import ro.msg.learning.bank.scenarios.OperationScenarioITest;
import ro.msg.learning.bank.schedules.ScheduledJobAutomaticCloseAccountITest;
import ro.msg.learning.bank.schedules.ScheduledJobCurrentAccountITest;
import ro.msg.learning.bank.schedules.ScheduledJobSavingAccountITest;

@RunWith(Suite.class)
@Suite.SuiteClasses
        ({
                LoginControllerAuthenticationITest.class,
                LoginControllerITest.class,
                ScheduledJobCurrentAccountITest.class,
                ScheduledJobSavingAccountITest.class,
                ScheduledJobAutomaticCloseAccountITest.class,
                PopulateDbControllerITest.class,

                AccountControllerITest.class,
                AccountDetailControllerITest.class,
                AccountMeasureControllerITest.class,
                AccountTransactionControllerITest.class,
                AppUserControllerITest.class,
                BankControllerITest.class,
                ClientControllerITest.class,
                LoginControllerITest.class,
                OperationControllerITest.class,
                PopulateDbControllerITest.class,

                AccountMeasureScenarioITest.class,
                AccountTransactionScenarioITest.class,
                LoginScenarioITest.class,
                OperationScenarioITest.class,

                AccountMeasureControllerITest.class,
                AccountTransactionControllerITest.class,
                AccountDetailControllerITest.class

        })
public class ITSuite {
}
