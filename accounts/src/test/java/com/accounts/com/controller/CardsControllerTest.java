package com.accounts.com.controller;

import com.accounts.com.client.SecurityServiceClient;
import com.accounts.com.constants.CardsConstants;
import com.accounts.com.dto.*;
import com.accounts.com.exceptions.ApiException;
import com.accounts.com.service.ICardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;





import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


class CardsControllerTest {
    @Mock
    private ICardsService iCardsService;

    private CardsController cardsController;
    @Mock
    private SecurityServiceClient securityServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cardsController = new CardsController(iCardsService,securityServiceClient);
        cardsController.iCardsService = iCardsService;
    }
    @Test
    void hello() {
        String result = cardsController.hello();
        assertEquals("hello java", result);
    }


   @Test
   void testFetchCardDetails_Success() {
       // Arrange
       String mobileNumber = "1234567890";
       String token = "Bearer validToken";
       String extractedToken = "validToken";
       CardsDto cardsDto = new CardsDto();
       when(securityServiceClient.extractToken(token)).thenReturn(extractedToken);
       when(iCardsService.fetchCard(mobileNumber, extractedToken)).thenReturn(cardsDto);

       // Act
       ResponseEntity<CardsDto> responseEntity = cardsController.fetchCardDetails(mobileNumber, token);

       // Assert
       verify(iCardsService).fetchCard(eq(mobileNumber), eq(extractedToken));
       assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
       assertEquals(cardsDto, responseEntity.getBody());
   }

    @Test
    void testFetchCardDetails_InvalidMobileNumber() {
        // Arrange
        String mobileNumber = "123456789"; // Invalid mobile number

        // Act & Assert
        assertThrows(ApiException.class, () -> cardsController.fetchCardDetails(mobileNumber, "token"));
    }

    @Test
    void testUpdateCardDetails_Success() {
        // Arrange
        CardsDto cardsDto = new CardsDto();
        String token = "Bearer validToken";
        String extractedToken = "validToken";
        when(securityServiceClient.extractToken(token)).thenReturn(extractedToken);
        when(iCardsService.updateCard(any(), any())).thenReturn(true);

        // Act
        ResponseEntity<ResponseDto> responseEntity = cardsController.updateCardDetails(cardsDto, token);

        // Assert
        verify(iCardsService).updateCard(eq(cardsDto), eq(extractedToken));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDto responseDto = responseEntity.getBody();
        assertEquals(CardsConstants.STATUS_200, responseDto.getStatusCode());
        assertEquals(CardsConstants.MESSAGE_200, responseDto.getStatusMsg());
    }

    @Test
    void testUpdateCardDetails_Failure() {
        // Arrange
        CardsDto cardsDto = new CardsDto();
        String token = "Bearer validToken";
        String extractedToken = "validToken";
        when(securityServiceClient.extractToken(token)).thenReturn(extractedToken);
        when(iCardsService.updateCard(any(), any())).thenReturn(false);

        // Act
        ResponseEntity<ResponseDto> responseEntity = cardsController.updateCardDetails(cardsDto, token);

        // Assert
        verify(iCardsService).updateCard(eq(cardsDto), eq(extractedToken));
        assertEquals(HttpStatus.EXPECTATION_FAILED, responseEntity.getStatusCode());
        ResponseDto responseDto = responseEntity.getBody();
        assertEquals(CardsConstants.STATUS_417, responseDto.getStatusCode());
        assertEquals(CardsConstants.MESSAGE_417_UPDATE, responseDto.getStatusMsg());
    }



    @Test
    void blockCard_failure() {
        BlockRequest blockRequest = new BlockRequest("1000000217330091", "Reason");
        when(iCardsService.blockCard(any(String.class), any(String.class), any(String.class))).thenReturn(false); // Assuming blocking the card fails

        CardBlockResponse expectedResponse = new CardBlockResponse("failed", HttpStatus.NOT_FOUND, "Card not found or already blocked🔍🔍");
        ResponseEntity<CardBlockResponse> response = cardsController.blockCard(blockRequest, "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedResponse.getStatus(), response.getBody().getStatus());
        assertEquals(expectedResponse.getResponseCode(), response.getBody().getResponseCode());
        assertEquals(expectedResponse.getMessage().trim(), response.getBody().getMessage().trim());
    }



    @Test
    void unblockCard_failure() {
        UnBlockRequestDto unblockRequest = new UnBlockRequestDto("1000000217330091", "Reason");
        when(iCardsService.unblock(String.valueOf(any(Long.class)), any(String.class), any(String.class))).thenReturn(false);
        CardBlockResponse expectedResponse = new CardBlockResponse("failed",HttpStatus.NOT_FOUND, "Card not found or already unblocked🔍🔍" );
        ResponseEntity<CardBlockResponse> response = cardsController.unblockCard(unblockRequest, "Bearer token");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }


    @Test
    void updateCardDetails_failure() {
        // Create a mock CardsDto object
        CardsDto cardsDto = new CardsDto();

        // Mock the behavior of iCardsService.updateCard method to return false
        when(iCardsService.updateCard(any(CardsDto.class), any(String.class))).thenReturn(false);

        // Create an expected ResponseDto object
        ResponseDto expectedResponse = new ResponseDto("417", "Update operation failed 😔😔");

        // Invoke the updateCardDetails method of the cardsController
        ResponseEntity<ResponseDto> response = cardsController.updateCardDetails(cardsDto, "Bearer token");

        String expectedMessage = expectedResponse.getStatusMsg().trim().replaceAll("\\s+", " ");
        String actualMessage = response.getBody().getStatusMsg().trim().replaceAll("\\s+", " ");

// Assertions
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode()); // Check the HTTP status code
        assertEquals(expectedMessage, actualMessage); // Check the content of the response body
    }



}