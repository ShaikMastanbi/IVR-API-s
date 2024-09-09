package com.accounts.com.service.impl;

import com.accounts.com.client.SecurityServiceClient;
import com.accounts.com.dto.AccountsDto;
import com.accounts.com.dto.CustomerDto;
import com.accounts.com.dto.LockUnlockAccounDto;
import com.accounts.com.dto.UserDetailsDto;
import com.accounts.com.entity.Accounts;
import com.accounts.com.entity.Customer;
import com.accounts.com.exceptions.ApiException;
import com.accounts.com.exceptions.CustomerAlreadyExistsException;
import com.accounts.com.exceptions.ResourceNotFoundException;
import com.accounts.com.repository.AccountsRepository;
import com.accounts.com.repository.CustomerRepository;
import com.accounts.com.util.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AccountsServiceImplTest {
    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SecurityServiceClient securityServiceClient;
    @Mock
    private EmailService emailService;


    @Mock
    private RestTemplate restTemplate;



    private AccountsServiceImpl accountsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountsService = new AccountsServiceImpl(accountsRepository, customerRepository);
        accountsService.securityServiceClient = securityServiceClient;
        accountsService.restTemplate = restTemplate;
    }

    @Test
    void fetchAccount_success() {
        // Arrange
        String mobileNumber = "1234567890";
        String jwtToken = "validToken";
        String userId = "testUser";
        Customer customer = new Customer();
        customer.setName(userId);
        Accounts accounts = new Accounts();

        when(securityServiceClient.getUserName(jwtToken)).thenReturn(userId);
        when(customerRepository.findByMobileNumber(mobileNumber)).thenReturn(Optional.of(customer));
        when(accountsRepository.findByCustomerId(any())).thenReturn(Optional.of(accounts));

        // Act
        CustomerDto result = accountsService.fetchAccount(mobileNumber, jwtToken);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAccountsDto());
        verify(securityServiceClient, times(1)).getUserName(jwtToken);
        verify(customerRepository, times(1)).findByMobileNumber(mobileNumber);
        verify(accountsRepository, times(1)).findByCustomerId(any());
    }

    @Test
    void unlockAccount_validAccountNumber_unlockSuccessful() {
        // Arrange
        long accountNumber = 123456789L;
        String jwtToken = "validToken";
        LockUnlockAccounDto lockUnlockAccounDto = new LockUnlockAccounDto(accountNumber);

        Accounts account = new Accounts();
        account.setAccountNumber(accountNumber);
        account.setStatus(true);
        account.setCustomerId(2L); // Set a valid customerId

        Customer customer = new Customer();
        customer.setUserId(1); // Set a valid userId
        customer.setName("John Doe");
        customer.setEmail("John_doe@gmail.com");

        when(securityServiceClient.getUserName(jwtToken)).thenReturn("John Doe");
        when(accountsRepository.findByAccountNumber(accountNumber)).thenReturn(account);
        when(customerRepository.findById(account.getCustomerId())).thenReturn(Optional.of(customer));

        // Act
        accountsService.unlockAccount(lockUnlockAccounDto, jwtToken);

        // Assert
        assertFalse(account.getStatus());
        verify(accountsRepository, times(1)).save(account);
    }
    @Test
    void unlockAccount_invalidAccountNumber_throwsException() {
        // Arrange
        long accountNumber = 123456789L;
        String jwtToken = "validToken";
        LockUnlockAccounDto lockUnlockAccounDto = new LockUnlockAccounDto(accountNumber);

        when(accountsRepository.findByAccountNumber(accountNumber)).thenReturn(null);

        // Act & Assert
        assertThrows(ApiException.class, () -> accountsService.unlockAccount(lockUnlockAccounDto, jwtToken));
    }

    @Test
    void lockAccount_validAccountNumber_lockSuccessful() {
        // Arrange
        long accountNumber = 123456789L;
        String jwtToken = "validToken";
        LockUnlockAccounDto lockUnlockAccounDto = new LockUnlockAccounDto(accountNumber);

        Accounts account = new Accounts();
        account.setAccountNumber(accountNumber);
        account.setStatus(false);

        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("John_doe@gmail.com"); // Set a valid email address

        when(securityServiceClient.getUserName(jwtToken)).thenReturn("John Doe");
        when(accountsRepository.findByAccountNumber(accountNumber)).thenReturn(account);
        when(customerRepository.findById(account.getCustomerId())).thenReturn(Optional.of(customer));

        // Act
        accountsService.lockAccount(lockUnlockAccounDto, jwtToken);

        // Assert
        assertTrue(account.getStatus());
        verify(accountsRepository, times(1)).save(account);
    }
    @Test
    void lockAccount_invalidAccountNumber_throwsException() {
        // Arrange
        long accountNumber = 123456789L;
        String jwtToken = "validToken";
        LockUnlockAccounDto lockUnlockAccounDto = new LockUnlockAccounDto(accountNumber);

        when(accountsRepository.findByAccountNumber(accountNumber)).thenReturn(null);

        // Act & Assert
        assertThrows(ApiException.class, () -> accountsService.lockAccount(lockUnlockAccounDto, jwtToken));
    }
}







