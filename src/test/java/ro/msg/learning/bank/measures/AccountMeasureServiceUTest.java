package ro.msg.learning.bank.measures;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ro.msg.learning.bank.dtos.*;
import ro.msg.learning.bank.entities.*;
import ro.msg.learning.bank.exceptions.LimitException;
import ro.msg.learning.bank.exceptions.NotOperableException;
import ro.msg.learning.bank.exceptions.WarningLimitException;
import ro.msg.learning.bank.repositories.AccountMeasureRepository;
import ro.msg.learning.bank.repositories.AccountRepository;
import ro.msg.learning.bank.repositories.OperationRepository;
import ro.msg.learning.bank.services.AccountDetailService;

import ro.msg.learning.bank.services.AccountMeasureService;
import ro.msg.learning.bank.services.AccountService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
public class AccountMeasureServiceUTest {
    public static final String MOCK_USERNAME = "MockUsername";
    public static final String FIRST_NAME = "Ionut";
    public static final String LAST_NAME = "Indrei";
    public static final BigDecimal MONEY_CASH = new BigDecimal(500);
    @Mock
    private AccountMeasureRepository accountMeasureRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountDetailService accountDetailService;
    @Mock
    private OperationRepository operationRepository;


    private final double minRequiredAmount = AccountCost.CLOSING_TAXES.getCost().doubleValue();
    public static final LocalDateTime OPERATION_TIME_STAMP = LocalDateTime.of(2022, 3, 28, 12, 12, 10);

    @Test
    public void test_blockInactiveOpenedAccount_isBlocked_True() {
        AccountMeasureDTO accountMeasureDTO =
                getAccountMeasureDTO(new OperationDTOFactory(), "BLOCKACCOUNT", false, false);
        getMock_AccountMeasureDTO(accountMeasureDTO, false, false, new BigDecimal(6));
        AccountMeasureService accountMeasureService =
                new AccountMeasureService(accountMeasureRepository, accountService, accountRepository, accountDetailService, operationRepository);

        accountMeasureService.takeMeasure(accountMeasureDTO);

        Assert.assertTrue(accountMeasureDTO.getAccount().isBlocked());
    }

    @Test
    public void test_blockActiveOpenedAccount_throwException() {
        AccountMeasureDTO accountMeasureDTO =
                getAccountMeasureDTO(new OperationDTOFactory(), "BLOCKACCOUNT", true, false);
        getMock_AccountMeasureDTO(accountMeasureDTO, true, false, new BigDecimal(6));
        AccountMeasureService accountMeasureService =
                new AccountMeasureService(accountMeasureRepository, accountService, accountRepository, accountDetailService, operationRepository);

        Assert.assertThrows(NotOperableException.class, () -> {
            accountMeasureService.takeMeasure(accountMeasureDTO);
        });
    }

    @Test(expected = NotOperableException.class)
    public void test_blockActiveClosedAccount_throwException() {
        AccountMeasureDTO accountMeasureDTO =
                getAccountMeasureDTO(new OperationDTOFactory(), "BLOCKACCOUNT", false, true);
        getMock_AccountMeasureDTO(accountMeasureDTO, false, true, new BigDecimal(6));
        AccountMeasureService accountMeasureService =
                new AccountMeasureService(accountMeasureRepository, accountService, accountRepository, accountDetailService, operationRepository);

        accountMeasureService.takeMeasure(accountMeasureDTO);

    }

    @Test
    public void test_unblockInactiveOpenedAccount_isBlocked_False() {
        AccountMeasureDTO accountMeasureDTO =
                getAccountMeasureDTO(new OperationDTOFactory(), "UNBLOCKACCOUNT", true, false);
        getMock_AccountMeasureDTO(accountMeasureDTO, true, false, new BigDecimal(6));
        AccountMeasureService accountMeasureService =
                new AccountMeasureService(accountMeasureRepository, accountService, accountRepository, accountDetailService, operationRepository);

        accountMeasureService.takeMeasure(accountMeasureDTO);

        Assert.assertFalse(accountMeasureDTO.getAccount().isBlocked());
    }

    @Test
    public void test_unblockActiveOpenedAccount_throwException() {
        AccountMeasureDTO accountMeasureDTO =
                getAccountMeasureDTO(new OperationDTOFactory(), "UNBLOCKACCOUNT", false, false);
        getMock_AccountMeasureDTO(accountMeasureDTO, false, false, new BigDecimal(6));
        AccountMeasureService accountMeasureService =
                new AccountMeasureService(accountMeasureRepository, accountService, accountRepository, accountDetailService, operationRepository);

        Assert.assertThrows(NotOperableException.class, () -> {
            accountMeasureService.takeMeasure(accountMeasureDTO);
        });
    }

    @Test(expected = NotOperableException.class)
    public void test_unblockInactiveClosedAccount_throwException() {
        AccountMeasureDTO accountMeasureDTO =
                getAccountMeasureDTO(new OperationDTOFactory(), "UNBLOCKACCOUNT", true, true);
        getMock_AccountMeasureDTO(accountMeasureDTO, true, true, new BigDecimal(6));
        AccountMeasureService accountMeasureService =
                new AccountMeasureService(accountMeasureRepository, accountService, accountRepository, accountDetailService, operationRepository);
        accountMeasureService.takeMeasure(accountMeasureDTO);

    }

    @Test(expected = NotOperableException.class)
    public void test_closeClosedAccount_throwException() {
        AccountMeasureDTO accountMeasureDTO =
                getAccountMeasureDTO(new OperationDTOFactory(), "CLOSEACCOUNT", false, true);
        getMock_AccountMeasureDTO(accountMeasureDTO, false, true, new BigDecimal(6));
        AccountMeasureService accountMeasureService =
                new AccountMeasureService(accountMeasureRepository, accountService, accountRepository, accountDetailService, operationRepository);

        accountMeasureService.takeMeasure(accountMeasureDTO);
    }

    @Test
    public void test_closeOpenedAccountWithMinRequiredAmount_isClosed_True() {
        AccountMeasureDTO accountMeasureDTO =
                getAccountMeasureDTO(new OperationDTOFactory(), "CLOSEACCOUNT", false, false);

        getMock_AccountMeasureDTO(accountMeasureDTO, false, false, new BigDecimal(minRequiredAmount));
        AccountMeasureService accountMeasureService =
                new AccountMeasureService(accountMeasureRepository, accountService, accountRepository, accountDetailService, operationRepository);

        accountMeasureService.takeMeasure(accountMeasureDTO);

        Assert.assertTrue(accountMeasureDTO.getAccount().isClosed());
    }

    @Test(expected = LimitException.class)
    public void test_closeOpenedAccountWithUnderMinRequiredAmount_isClosed_True() {
        AccountMeasureDTO accountMeasureDTO =
                getAccountMeasureDTO(new OperationDTOFactory(), "CLOSEACCOUNT", false, false);

        getMock_AccountMeasureDTO(accountMeasureDTO, false, false, new BigDecimal(minRequiredAmount - 1));
        AccountMeasureService accountMeasureService =
                new AccountMeasureService(accountMeasureRepository, accountService, accountRepository, accountDetailService, operationRepository);

        accountMeasureService.takeMeasure(accountMeasureDTO);

    }

    @Test(expected = WarningLimitException.class)
    public void test_closeOpenedAccountWithOverMinRequiredAmount_isClosed_True() {
        AccountMeasureDTO accountMeasureDTO =
                getAccountMeasureDTO(new OperationDTOFactory(), "CLOSEACCOUNT", false, false);

        getMock_AccountMeasureDTO(accountMeasureDTO, false, false, new BigDecimal(minRequiredAmount + 1));
        AccountMeasureService accountMeasureService =
                new AccountMeasureService(accountMeasureRepository, accountService, accountRepository, accountDetailService, operationRepository);

        accountMeasureService.takeMeasure(accountMeasureDTO);

    }

    private void getMock_AccountMeasureDTO(AccountMeasureDTO accountMeasureDTO, boolean isBlocked, boolean isClosed,
                                           BigDecimal amount) {

        Account expectedAccount = accountMeasureDTO.getAccount();
        expectedAccount.setType(AccountType.CURRENT_ACCOUNT);
        expectedAccount.setBank(accountMeasureDTO.getAccount().getBank());
        expectedAccount.setBlocked(isBlocked);
        expectedAccount.setClosed(isClosed);
        expectedAccount.setCreatingDate(LocalDateTime.of(LocalDate.of(2020, 2, 3), LocalTime.of(12, 12, 12)));
        AccountMeasureDTO expectedAccountMeasure = AccountMeasureDTO.builder()
                .account(expectedAccount)
                .build();



        AccountDetailDTO expectedAccountDetail = AccountDetailDTO.builder()
                .appUser(accountMeasureDTO.getOperation().getUserDetail())
                .account(expectedAccount)
                .accountAmount(amount)
                .build();
        List<AccountDetailDTO> accountDetailDTOList = new ArrayList<>();
        accountDetailDTOList.add(expectedAccountDetail);

        List<Operation> operationList= new ArrayList<>();
        Operation operation= accountMeasureDTO.getOperation();
        operationList.add(operation);


        when(accountMeasureRepository.save(any(AccountMeasure.class))).thenReturn(expectedAccountMeasure.toEntity());
        when(accountService
                .updateByIban(expectedAccount.getIban(), AccountDTO.of(expectedAccount)))
                .thenReturn(AccountDTO.of(expectedAccount));
        when(accountDetailService.readByAccountIban(anyInt())).thenReturn(accountDetailDTOList);

        when(operationRepository.findAll()).thenReturn(operationList);
        when(operationRepository.findByTimeStamp(any(LocalDateTime.class))).thenReturn(Optional.of(operation));
        when(accountRepository.findByIban(anyInt())).thenReturn(Optional.of(expectedAccount));

    }


    private AccountMeasureDTO getAccountMeasureDTO(OperationDTOFactory operationDTOFactory, String measure,
                                                   boolean isBlocked, boolean isClosed) {

        AccountMeasureDTO accountMeasureDTO = (AccountMeasureDTO) operationDTOFactory.getOperationDTO(measure);

        ClientDTO clientDTO = ClientDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .moneyCash(MONEY_CASH)
                .build();

        AppUserDTO appUserDTO = AppUserDTO.builder()
                .username(MOCK_USERNAME)
                .client(clientDTO.toEntity())
                .build();

        Operation operation = new Operation();
        operation.setType(OperationType.valueOf(measure));
        operation.setTimeStamp(OPERATION_TIME_STAMP);
        operation.setUserDetail(appUserDTO.toEntity());
        accountMeasureDTO.setOperation(operation);

        AccountDTO accountDTO = AccountDTO.builder()
                .iban(123)
                .bankCompany(BankCompany.BTBANK)
                .creatingDate(LocalDateTime.of(LocalDate.of(2000, 6, 13), LocalTime.of(15, 15, 15)))
                .isBlocked(isBlocked)
                .isClosed(isClosed)
                .build();
        accountMeasureDTO.setAccount(accountDTO.toEntity());

        return accountMeasureDTO;
    }
}
