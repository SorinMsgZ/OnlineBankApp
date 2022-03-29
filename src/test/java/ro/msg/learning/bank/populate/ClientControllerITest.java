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
import ro.msg.learning.bank.controllers.ClientController;
import ro.msg.learning.bank.dtos.BankDTO;
import ro.msg.learning.bank.dtos.ClientDTO;
import ro.msg.learning.bank.entities.BankCompany;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class ClientControllerITest {

    private ClientDTO clientDTO;
    @Autowired
    private ClientController clientController;

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

    }

    @After
    public void tearDown()  {
        clientController.deleteAll();
        bankController.deleteAll();
    }

    @Test
    public void test_createClient_returnAndSave1ClientToDB() {
        clientController.create(clientDTO);
        List<ClientDTO> clientDTOList = clientController.listAll();
        Assert.assertEquals(1, clientDTOList.size());
    }
}
