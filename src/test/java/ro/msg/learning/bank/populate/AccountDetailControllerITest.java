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
import ro.msg.learning.bank.controllers.*;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.AccountType;
import ro.msg.learning.bank.entities.BankCompany;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class AccountDetailControllerITest {
    @Autowired
    private AccountDetailController accountDetailController;
    @Autowired
    private ClientController clientController;

    @Autowired
    private AppUserController appUserController;
    private AccountDetailDTO accountDetailDTO;
    private ClientDTO clientDTO;
    private AppUserSecurityDTO appUserSecurityDTO;
    private AccountDTO accountDTO;
    @Autowired
    private AccountController accountController;
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

        clientDTO = ClientDTO.builder()
                .firstName("ClientFirstName")
                .lastName("ClientLastName")
                .moneyCash(new BigDecimal(100))
                .build();
        clientController.create(clientDTO);
        appUserSecurityDTO = AppUserSecurityDTO.builder()
                .username("volodimir")
                .password("parola")
                .client(clientDTO.toEntity())
                .build();
        appUserController.create(appUserSecurityDTO);
        accountDTO = AccountDTO.builder()
                .iban(1)
                .type(AccountType.CURRENT_ACCOUNT)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(LocalDateTime.now().minusMonths(7))
                .isBlocked(false)
                .isClosed(false)
                .build();
        accountController.create(accountDTO);
        accountDetailDTO = AccountDetailDTO.builder()
                .appUser(appUserSecurityDTO.toEntity())
                .account(accountDTO.toEntity())
                .accountAmount(new BigDecimal(1000))
                .build();

    }

    @After
    public void tearDown()  {
        accountDetailController.deleteAll();
        bankController.deleteAll();
        clientController.deleteAll();
        appUserController.deleteAll();
        accountController.deleteAll();
    }

    @Test
    public void test_createAccountDetail_returnAndSave1AccountDetailToDB() {
        accountDetailController.create(accountDetailDTO);
        List<AccountDetailDTO> accountDetailDTOList = accountDetailController.listAll();
        Assert.assertEquals(1, accountDetailDTOList.size());
    }
}
