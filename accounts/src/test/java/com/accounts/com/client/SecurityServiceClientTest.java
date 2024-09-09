package com.accounts.com.client;

import com.accounts.com.exceptions.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class SecurityServiceClientTest {

    @InjectMocks
    private SecurityServiceClient securityServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserName_Success() {
        String tokenValue = "validToken";
        String expectedUsername = "Invalid token";
        String validateEndpoint = "http://localhost:9043/auth/validateToken";

        ResponseEntity<String> mockResponse = new ResponseEntity<>(expectedUsername, HttpStatus.OK);

        // Mock the RestTemplate behavior
        when(restTemplate.exchange(
                eq(validateEndpoint),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        String actualUsername = securityServiceClient.getUserName(tokenValue);
        assertEquals(expectedUsername, actualUsername);
    }


}