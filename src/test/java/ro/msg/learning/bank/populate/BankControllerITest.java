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
import ro.msg.learning.bank.controllers.BankController;
import ro.msg.learning.bank.dtos.BankDTO;
import ro.msg.learning.bank.entities.BankCompany;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class BankControllerITest {
    @Autowired
    private BankController bankController;
    private BankDTO bankDTO;

    @Before
    public void setUp() throws Exception {
        tearDown();

        bankDTO = BankDTO.builder()
                .name(BankCompany.BTBANK)
                .build();
    }

    @After
    public void tearDown()  {
        bankController.deleteAll();
    }

    @Test
    public void test_createBank_returnAndSave1BankToDB() {
        bankController.create(bankDTO);
        List<BankDTO> bankDTOList = bankController.listAll();
        Assert.assertEquals(1, bankDTOList.size());
    }
}
