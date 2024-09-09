package com.accounts.com.controller;


import com.accounts.com.client.SecurityServiceClient;
import com.accounts.com.constants.CardsConstants;
import com.accounts.com.dto.*;
import com.accounts.com.exceptions.ApiException;
import com.accounts.com.service.ICardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST APIs for cards",
        description = "CRUD REST APIs in cards to CREATE, UPDATE, FETCH  card details"
)
@RestController
@RequestMapping(path="/ivr",produces = {MediaType.APPLICATION_JSON_VALUE})
public class CardsController {
    @Autowired
    public ICardsService iCardsService;
    private  static final Logger logger = LoggerFactory.getLogger(CardsController.class);
    @Autowired
    private SecurityServiceClient securityServiceClient;

    public CardsController(ICardsService iCardsService, SecurityServiceClient securityServiceClient) {
        this.iCardsService = iCardsService;
        this.securityServiceClient = securityServiceClient;
    }

    @GetMapping("/hello")
    public String hello(){
        logger.info("Received request for hello endpoint");
        return "hello java";
    }
    @Operation(
            summary = "Create Card REST API",
            description = "REST API to create new Card "
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )

    @PostMapping("card/create")
    public ResponseEntity<ResponseDto> createCard(@Valid @RequestBody CreateCardRequestDto requestDto, @RequestHeader (name="Authorization") String token) {
        logger.info("Received request for createCard endpoint with requestDto: {}", requestDto);

        String tokenValue = this.securityServiceClient.extractToken(token);
        iCardsService.createCard(requestDto.getMobileNumber(), requestDto.getAccountNumber(),tokenValue);
        logger.info("Card created successfully");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto("Success", "Card created successfully 🎉🎉"));
    }
    @Operation(
            summary = "Fetch Card Details REST API",
            description = "REST API to fetch card details based on a mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("card/fetch")
    public ResponseEntity<CardsDto> fetchCardDetails(@RequestParam
                                                     String mobileNumber,@RequestHeader (name="Authorization") String token) {

        logger.info("Executing the fetchCardDetails endpoint for mobileNumber ", mobileNumber);
        logger.info("Length of mobileNumber ", mobileNumber.length());

        if (!mobileNumber.matches("^[0-9]{10}$")) {
            throw new ApiException("Mobile number must be 10 digits");
        }
        if(mobileNumber.length() != 10)
            throw new ApiException("Mobile Number should contain 10 digits.");
        logger.info("Received request for fetchCardDetails endpoint for mobileNumber: {}", mobileNumber);

        String tokenValue = this.securityServiceClient.extractToken(token);
        CardsDto cardsDto = iCardsService.fetchCard(mobileNumber,tokenValue);
        logger.info("Card details fetched successfully");
        return ResponseEntity.status(HttpStatus.OK).body(cardsDto);
    }

    @Operation(
            summary = "block Card Details REST API",
            description = "REST API to block card details based on a card number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Expectation Failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("card/block-card")
   public ResponseEntity<CardBlockResponse> blockCard(@RequestBody BlockRequest blockRequest,@RequestHeader (name="Authorization") String token) {
        logger.info("Received request for blockCard endpoint with blockRequest: {}", blockRequest);

        String tokenValue = this.securityServiceClient.extractToken(token);
       boolean success = iCardsService.blockCard(blockRequest.getCardNumber(), blockRequest.getReason(),tokenValue);
       CardBlockResponse response = new CardBlockResponse();

       if (success) {
           response.setStatus("success");
           response.setMessage("Card was successfully blocked🎉🎉");
           response.setResponseCode(HttpStatus.OK);
           logger.info("Card blocked successfully");
           return ResponseEntity.status(HttpStatus.OK).body(response);
       } else {
           response.setStatus("failed");
           response.setMessage("Card not found or already blocked🔍🔍 ");
           response.setResponseCode(HttpStatus.NOT_FOUND);
           logger.warn("Failed to block card");
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
       }
   }

    @PostMapping("card/unblock-card")
    public ResponseEntity<CardBlockResponse> unblockCard(@RequestBody UnBlockRequestDto unblockRequest,@RequestHeader (name="Authorization") String token) {
        logger.info("Received request for unblockCard endpoint with unblockRequest: {}", unblockRequest);


        String tokenValue = this.securityServiceClient.extractToken(token);
        boolean success = iCardsService.unblock(unblockRequest.getCardNumber(),unblockRequest.getReason(),tokenValue);
        CardBlockResponse response = new CardBlockResponse();

        if (success) {
            response.setStatus("success");
            response.setMessage("Card was successfully unblocked🎉🎉");
            response.setResponseCode(HttpStatus.OK);
            logger.info("Card unblocked successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.setStatus("failed");
            response.setMessage("Card not found or already unblocked🔍🔍");
            response.setResponseCode(HttpStatus.NOT_FOUND);
            logger.warn("Failed to unblock card");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Update Card Details REST API",
            description = "REST API to update card details based on a card number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Expectation Failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PutMapping("card/update")
    public ResponseEntity<ResponseDto> updateCardDetails(@Valid @RequestBody CardsDto cardsDto,@RequestHeader (name="Authorization") String token) {

        String tokenValue = this.securityServiceClient.extractToken(token);
        boolean isUpdated = iCardsService.updateCard(cardsDto,tokenValue);
        if(isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CardsConstants.STATUS_200, CardsConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(CardsConstants.STATUS_417, CardsConstants.MESSAGE_417_UPDATE));
        }
    }
}
