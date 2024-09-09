package com.accounts.com.controller;


import com.accounts.com.client.SecurityServiceClient;
import com.accounts.com.constants.AccountsConstants;
import com.accounts.com.dto.*;
import com.accounts.com.service.IAccountsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Tag(
        name = "CRUD REST APIs for Accounts ",
        description = "CRUD REST APIs in Accounts to CREATE, UPDATE, FETCH  account details"
)
@RestController
@RequestMapping(path="/ivr",produces = {MediaType.APPLICATION_JSON_VALUE})

public class AccountController {

    private IAccountsService iAccountsService;


    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private AccountsContactInfoDto accountsContactInfoDto;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private SecurityServiceClient securityServiceClient;
    @Autowired
    public AccountController(IAccountsService iAccountsService, SecurityServiceClient securityServiceClient) {
        this.iAccountsService = iAccountsService;

        this.securityServiceClient = securityServiceClient;
    }

    @GetMapping("sayHello")
    public String sayHello(){
        logger.info("Received request for sayHello endpoint");

        return "Hello world";
    }

    @Operation(
            summary = "Create Account REST API",
            description = "REST API to create new Customer &  Account inside EazyBank"
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
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto, @RequestHeader (name="Authorization") String token) {
        logger.info("Received request for createAccount endpoint with customerDto: {}", customerDto);

        logger.info("Received token from Headers is : "+token);
        String tokenValue = this.securityServiceClient.extractToken(token);
        iAccountsService.createAccount1(customerDto,tokenValue);
        logger.info("Account created successfully");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }

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
    }
    )
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccountDetails(@RequestParam String mobileNumber,@RequestHeader (name="Authorization") String token) {
        logger.info("Received request for fetchAccountDetails endpoint for mobileNumber: {}", mobileNumber);
        String tokenValue = this.securityServiceClient.extractToken(token);
        CustomerDto customerDto = iAccountsService.fetchAccount(mobileNumber,tokenValue);
        logger.info("Account details fetched successfully");
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }
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
    }
    )
    @GetMapping("/fetch/v2")
    public ResponseEntity<CustomerDto> fetchAccountDetailsV2(@RequestParam String mobileNumber) {
        logger.info("Received request for fetchAccountDetails endpoint for mobileNumber: {}", mobileNumber);
        //String tokenValue = this.securityServiceClient.extractToken(token);
        CustomerDto customerDto = iAccountsService.fetchAccountV2(mobileNumber);
        logger.info("Account details fetched successfully");
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }


    @Operation(
            summary = "Rest API ",
            description = "REST API to unlock the account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "ok"
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


    @PostMapping("/unlock")
    public ResponseEntity<ResponseDto> unlockAccount(@RequestBody LockUnlockAccounDto lockUnlockAccounDto, @RequestHeader (name="Authorization") String token) {
        logger.info("Received request for unlockAccount endpoint with lockUnlockAccounDto: {}", lockUnlockAccounDto);


            String tokenValue = this.securityServiceClient.extractToken(token);
            iAccountsService.unlockAccount(lockUnlockAccounDto,tokenValue);
            logger.info("Account unlocked successfully");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.unlock_msg));

    }

    @PostMapping("/lock")
    public ResponseEntity<ResponseDto> lockAccount(@RequestBody LockUnlockAccounDto lockUnlockAccounDto,@RequestHeader (name="Authorization") String token) {
        logger.info("Received request for lockAccount endpoint with lockUnlockAccounDto: {}", lockUnlockAccounDto);
            String tokenValue = this.securityServiceClient.extractToken(token);
            iAccountsService.lockAccount(lockUnlockAccounDto,tokenValue);
            logger.info("Account locked successfully");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.lock_msg));

    }

    @Operation(
            summary = "Update Account Details REST API",
            description = "REST API to update Customer &  Account details based on a account number"
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
    }
    )
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody CustomerDto customerDto,@RequestHeader (name="Authorization") String token) {
        logger.info("Received request for updateAccountDetails endpoint with customerDto: {}", customerDto);

        String tokenValue = this.securityServiceClient.extractToken(token);
        boolean isUpdated = iAccountsService.updateAccount(customerDto,tokenValue);
        if(isUpdated) {
            logger.info("Account details updated successfully");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else{
            logger.warn("Failed to update account details");
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_UPDATE));
        }
    }

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
    }
    )
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(buildVersion);
    }


    @Operation(
            summary = "Get Java version",
            description = "Get Java versions details that is installed into accounts microservice"
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
    }
    )
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("MAVEN_HOME"));
    }


    @Operation(
            summary = "Get Contact Info",
            description = "Contact Info details that can be reached out in case of any issues"
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
    }
    )
    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactInfoDto);
    }

}
