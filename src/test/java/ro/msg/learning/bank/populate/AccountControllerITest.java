package ro.msg.learning.bank.populate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ro.msg.learning.bank.controllers.AccountController;
import ro.msg.learning.bank.controllers.BankController;
import ro.msg.learning.bank.dtos.AccountDTO;
import ro.msg.learning.bank.dtos.BankDTO;
import ro.msg.learning.bank.entities.AccountType;
import ro.msg.learning.bank.entities.BankCompany;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class AccountControllerITest {
    @Autowired
    private AccountController accountController;
    private AccountDTO accountDTO;

    @Autowired
    private BankController bankController;
    private BankDTO bankDTO;

    @Before
    public void setUp() throws Exception {
        tearDown();

        bankDTO = BankDTO.builder()
                .name(BankCompany.BTBANK)
                .build();
        bankController.create(bankDTO);

        accountDTO = AccountDTO.builder()
                .iban(1)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(LocalDateTime.now().minusMonths(7))
                .isBlocked(false)
                .isClosed(false)
                .build();

    }

    @After
    public void tearDown()  {
        accountController.deleteAll();
        bankController.deleteAll();
    }

    @Test
    public void test_createAccount_returnAndSave1AccountToDB() {
        accountController.create(accountDTO);
        List<AccountDTO> accountDTOList = accountController.listAll();
        Assert.assertEquals(1, accountDTOList.size());
    }
}
