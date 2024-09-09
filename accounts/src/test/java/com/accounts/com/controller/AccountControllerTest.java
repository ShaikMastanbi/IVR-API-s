package com.accounts.com.controller;


import com.accounts.com.client.SecurityServiceClient;
import com.accounts.com.constants.AccountsConstants;
import com.accounts.com.dto.CustomerDto;
import com.accounts.com.dto.LockUnlockAccounDto;
import com.accounts.com.dto.ResponseDto;
import com.accounts.com.service.IAccountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;


import static org.mockito.ArgumentMatchers.any;



class AccountControllerTest {
    @Mock
    private IAccountsService iAccountsService;
    @Mock
    private SecurityServiceClient securityServiceClient;

    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountController = new AccountController(iAccountsService,securityServiceClient);
    }

    @Test
    void sayHello() {
        String result = accountController.sayHello();
        assertEquals("Hello world", result);
    }
    @Test
    void testCreateAccount_Success() {
        // Arrange
        CustomerDto customerDto = new CustomerDto();
        String token = "Bearer validToken";
        String extractedToken = "validToken";
        when(securityServiceClient.extractToken(token)).thenReturn(extractedToken);

        // Act
        ResponseEntity<ResponseDto> responseEntity = accountController.createAccount(customerDto, token);

        // Assert
        verify(iAccountsService).createAccount1(eq(customerDto), eq(extractedToken));
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ResponseDto responseDto = responseEntity.getBody();
        assertEquals(AccountsConstants.STATUS_201, responseDto.getStatusCode());
        assertEquals(AccountsConstants.MESSAGE_201, responseDto.getStatusMsg());
    }
    @Test
    public void testFetchAccountDetails_Success() {
        // Arrange
        String mobileNumber = "5566788971";
        String token = "valid-token";
        CustomerDto customerDto = new CustomerDto(); // Mock customer data

        // Mock behavior of security service client
        when(securityServiceClient.extractToken(token)).thenReturn("extracted-token");

        // Mock behavior of accounts service
        when(iAccountsService.fetchAccount(mobileNumber, "extracted-token")).thenReturn(customerDto);

        // Act
        ResponseEntity<CustomerDto> responseEntity = accountController.fetchAccountDetails(mobileNumber, token);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(customerDto, responseEntity.getBody());

        // Verify interactions
        verify(securityServiceClient).extractToken(token);
        verify(iAccountsService).fetchAccount(mobileNumber, "extracted-token");
    }



    @Test
    void testUpdateAccountDetails_Success() {
        // Arrange
        CustomerDto customerDto = new CustomerDto();
        String token = "Bearer validToken";
        String extractedToken = "validToken";
        when(securityServiceClient.extractToken(token)).thenReturn(extractedToken);
        when(iAccountsService.updateAccount(any(), any())).thenReturn(true);

        // Act
        ResponseEntity<ResponseDto> responseEntity = accountController.updateAccountDetails(customerDto, token);

        // Assert
        verify(iAccountsService).updateAccount(eq(customerDto), eq(extractedToken));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDto responseDto = responseEntity.getBody();
        assertEquals(AccountsConstants.STATUS_200, responseDto.getStatusCode());
        assertEquals(AccountsConstants.MESSAGE_200, responseDto.getStatusMsg());
    }


    @Test
    void updateAccountDetails_failure() {
        ResponseDto expectedResponse = new ResponseDto("417", "Failed to update account details😔😔");
        when(iAccountsService.updateAccount(any(CustomerDto.class), any(String.class))).thenReturn(false);
        ResponseEntity<ResponseDto> response = accountController.updateAccountDetails(new CustomerDto(), "Bearer token");
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void unlockAccount_Success() {
        LockUnlockAccounDto lockUnlockAccounDto = new LockUnlockAccounDto();
        // Set properties of lockUnlockAccounDto
        String token = "Bearer token";

        doNothing().when(iAccountsService).unlockAccount(any(), eq(token));

        ResponseEntity<ResponseDto> responseEntity = accountController.unlockAccount(lockUnlockAccounDto, token);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(AccountsConstants.STATUS_201, responseEntity.getBody().getStatusCode());
        assertEquals(AccountsConstants.unlock_msg, responseEntity.getBody().getStatusMsg());
    }


    @Test
    void lockAccount_Success() {
        LockUnlockAccounDto lockUnlockAccounDto = new LockUnlockAccounDto();
        // Set properties of lockUnlockAccounDto
        String token = "Bearer token";

        doNothing().when(iAccountsService).lockAccount(any(), eq(token));

        ResponseEntity<ResponseDto> responseEntity = accountController.lockAccount(lockUnlockAccounDto, token);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(AccountsConstants.STATUS_201, responseEntity.getBody().getStatusCode());
        assertEquals(AccountsConstants.lock_msg, responseEntity.getBody().getStatusMsg());
    }

}









