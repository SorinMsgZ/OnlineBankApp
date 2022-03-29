package ro.msg.learning.bank.measures;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ro.msg.learning.bank.controllers.AccountMeasureController;
import ro.msg.learning.bank.dtos.AccountDTO;
import ro.msg.learning.bank.dtos.AccountMeasureDTO;
import ro.msg.learning.bank.dtos.OperationDTOFactory;
import ro.msg.learning.bank.entities.Account;

import ro.msg.learning.bank.services.AccountMeasureService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.Mockito.when;



@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
public class AccountMeasureControllerUTest {

    @Mock
    private AccountMeasureService accountMeasureService;



    @Test
    public void test_accountMeasureIsBlockActiveAndOpenedAccount_AccountIsBlockedTrue() {
        AccountMeasureDTO accountMeasureDTO = getAccountMeasureDTO(new OperationDTOFactory(), "BLOCKACCOUNT", false,false);
        getMock_AccountMeasureDTO(accountMeasureDTO,true,false);
        AccountMeasureController accountMeasureController = new AccountMeasureController(accountMeasureService);

        accountMeasureController.takeMeasure(accountMeasureDTO);

        Assert.assertTrue(accountMeasureDTO.getAccount().isBlocked());

    }

    @Test
    public void test_accountMeasureIsUnblockInactiveOpenedAccount_AccountIsBlockedFalse() {
        AccountMeasureDTO accountMeasureDTO = getAccountMeasureDTO(new OperationDTOFactory(),"UNBLOCKACCOUNT",true, false);
        getMock_AccountMeasureDTO(accountMeasureDTO,false, false);
        AccountMeasureController accountMeasureController = new AccountMeasureController(accountMeasureService);

        accountMeasureController.takeMeasure(accountMeasureDTO);

        Assert.assertFalse(accountMeasureDTO.getAccount().isBlocked());

    }

    @Test
    public void test_accountMeasureIsCloseOpenedAccount_AccountIsClosedTrue() {
        AccountMeasureDTO accountMeasureDTO = getAccountMeasureDTO(new OperationDTOFactory(),"CLOSEACCOUNT",true,false);
        getMock_AccountMeasureDTO(accountMeasureDTO,false,true);
        AccountMeasureController accountMeasureController = new AccountMeasureController(accountMeasureService);

        accountMeasureController.takeMeasure(accountMeasureDTO);

        Assert.assertTrue(accountMeasureDTO.getAccount().isClosed());

    }

    private void getMock_AccountMeasureDTO(AccountMeasureDTO accountMeasureDTO, boolean isBlocked, boolean isClosed) {
        Account expectedAccount = accountMeasureDTO.getAccount();
        expectedAccount.setBlocked(isBlocked);
        expectedAccount.setClosed(isClosed);
        AccountMeasureDTO expectedAccountMeasure = AccountMeasureDTO.builder()
                .account(expectedAccount)
                .build();
        when(accountMeasureService.takeMeasure(accountMeasureDTO)).thenReturn(expectedAccountMeasure);
    }


    private AccountMeasureDTO getAccountMeasureDTO(OperationDTOFactory operationDTOFactory, String measure,boolean isBlocked, boolean isClosed) {

        AccountMeasureDTO accountMeasureDTO = (AccountMeasureDTO) operationDTOFactory.getOperationDTO(measure);

        AccountDTO accountDTO = AccountDTO.builder()
                .iban(123)
                .creatingDate(LocalDateTime.of(LocalDate.of(2000, 6, 13), LocalTime.of(15, 15, 15)))
                .isBlocked(isBlocked)
                .isClosed(isClosed)
                .build();
        accountMeasureDTO.setAccount(accountDTO.toEntity());
        return accountMeasureDTO;
    }


}
