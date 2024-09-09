package com.accounts.com.service.impl;

import com.accounts.com.client.SecurityServiceClient;
import com.accounts.com.dto.CardsDto;
import com.accounts.com.entity.Accounts;
import com.accounts.com.entity.BlockCard;
import com.accounts.com.entity.Cards;
import com.accounts.com.entity.Customer;

import com.accounts.com.repository.AccountsRepository;
import com.accounts.com.repository.BlockCardRepository;
import com.accounts.com.repository.CardsRepository;
import com.accounts.com.repository.CustomerRepository;
import com.accounts.com.util.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CardsServiceImplTest {

    private CardsServiceImpl cardsService;

    @Mock
    private CardsRepository cardsRepository;

    @Mock
    private BlockCardRepository blockCardRepository;

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private EmailService emailService;

    @Mock
    private SecurityServiceClient securityServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cardsService = new CardsServiceImpl(cardsRepository, blockCardRepository,
                accountsRepository, customerRepository, null, securityServiceClient,emailService);
    }

    @Test
    void testCreateCard_Success() {
        String mobileNumber = "1234567890";
        Long accountNumber = 12345L;
        String jwtToken = "validToken";

        Accounts account = new Accounts();
        account.setAccountNumber(accountNumber);
        account.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setName("testUser");

        when(securityServiceClient.getUserName(jwtToken)).thenReturn("testUser");
        when(accountsRepository.findByAccountNumber(accountNumber)).thenReturn(account);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(cardsRepository.findByMobileNumber(mobileNumber)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> cardsService.createCard(mobileNumber, accountNumber, jwtToken));

        verify(cardsRepository, times(1)).save(any(Cards.class));
    }





    @Test
    void testBlockCard_CardNotFound() {
        String cardNumber = "1234567890123456";
        String reason = "Lost card";
        String jwtToken = "validToken";

        when(cardsRepository.findByCardNumber(cardNumber)).thenReturn(null);

        assertFalse(cardsService.blockCard(cardNumber, reason, jwtToken));

        verify(cardsRepository, never()).save(any(Cards.class));
        verify(blockCardRepository, never()).save(any(BlockCard.class));
    }

    @Test
    void testBlockCard_CardAlreadyBlocked() {
        String cardNumber = "1234567890123456";
        String reason = "Lost card";
        String jwtToken = "validToken";

        Cards card = new Cards();
        card.setCardNumber(cardNumber);
        card.setStatus(false);

        when(cardsRepository.findByCardNumber(cardNumber)).thenReturn(card);

        assertFalse(cardsService.blockCard(cardNumber, reason, jwtToken));

        verify(cardsRepository, never()).save(any(Cards.class));
        verify(blockCardRepository, never()).save(any(BlockCard.class));
    }


    @Test
    void testFetchCard_Success() {
        String mobileNumber = "1234567890";
        String jwtToken = "validToken";

        Cards card = new Cards();
        card.setMobileNumber(mobileNumber);
        card.setAccountNumber(12345L);

        Accounts account = new Accounts();
        account.setAccountNumber(12345L);
        account.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setName("testUser");

        when(securityServiceClient.getUserName(jwtToken)).thenReturn("testUser");
        when(cardsRepository.findByMobileNumber(mobileNumber)).thenReturn(Optional.of(card));
        when(accountsRepository.findByAccountNumber(12345L)).thenReturn(account);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertNotNull(cardsService.fetchCard(mobileNumber, jwtToken));
    }


    @Test
    void blockCard_validCardNumber_success() {
        // Arrange
        String cardNumber = "1234567890123456";
        String reason = "Lost card";
        String jwtToken = "valid_token";
        String userName = "john_doe";

        Cards card = new Cards();
        card.setCardNumber(cardNumber);
        card.setAccountNumber(123456789L);
        card.setStatus(true);

        Accounts account = new Accounts();
        account.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setName(userName);
        customer.setEmail("john_doe@gamil.com"); // Set a valid email address

        when(securityServiceClient.getUserName(jwtToken)).thenReturn(userName);
        when(cardsRepository.findByCardNumber(cardNumber)).thenReturn(card);
        when(accountsRepository.findByAccountNumber(card.getAccountNumber())).thenReturn(account);
        when(customerRepository.findById(account.getCustomerId())).thenReturn(Optional.of(customer));

        // Act
        boolean result = cardsService.blockCard(cardNumber, reason, jwtToken);

        // Assert
        assertTrue(result);
        verify(cardsRepository, times(1)).save(any(Cards.class));
        verify(blockCardRepository, times(1)).save(any(BlockCard.class));
    }

    @Test
    void unblock_validCardNumber_success() {
        // Arrange
        String cardNumber = "1234567890123456";
        String reason = "Found card";
        String jwtToken = "valid_token";
        String userName = "john_doe";

        Cards card = new Cards();
        card.setCardNumber(cardNumber);
        card.setAccountNumber(123456789L);
        card.setStatus(false);

        Accounts account = new Accounts();
        account.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setName(userName);
        customer.setEmail("john_doe@gmail.com");

        when(securityServiceClient.getUserName(jwtToken)).thenReturn(userName);
        when(cardsRepository.findByCardNumber(cardNumber)).thenReturn(card);
        when(accountsRepository.findByAccountNumber(card.getAccountNumber())).thenReturn(account);
        when(customerRepository.findById(account.getCustomerId())).thenReturn(Optional.of(customer));

        // Act
        boolean result = cardsService.unblock(cardNumber, reason, jwtToken);

        // Assert
        assertTrue(result);
        verify(cardsRepository, times(1)).save(any(Cards.class));
        verify(blockCardRepository, times(1)).save(any(BlockCard.class));
    }

    @Test
    void unblock_invalidCardNumber_failure() {
        // Arrange
        String cardNumber = "1234567890123456";
        String reason = "Found card";
        String jwtToken = "valid_token";

        when(cardsRepository.findByCardNumber(cardNumber)).thenReturn(null);

        // Act
        boolean result = cardsService.unblock(cardNumber, reason, jwtToken);

        // Assert
        assertFalse(result);
        verify(cardsRepository, never()).save(any(Cards.class));
        verify(blockCardRepository, never()).save(any(BlockCard.class));
    }

    @Test
    void blockCard_invalidCardNumber_failure() {
        // Arrange
        String cardNumber = "1234567890123456";
        String reason = "Lost card";
        String jwtToken = "valid_token";

        when(cardsRepository.findByCardNumber(cardNumber)).thenReturn(null);

        // Act
        boolean result = cardsService.blockCard(cardNumber, reason, jwtToken);

        // Assert
        assertFalse(result);
        verify(cardsRepository, never()).save(any(Cards.class));
        verify(blockCardRepository, never()).save(any(BlockCard.class));
    }





}