package com.accounts.com.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Schema(
        name = "Accounts",
        description = "Schema to hold Account information"
)
public class AccountsDto {
    @NotEmpty(message = "AccountNumber can not be a null or empty")
    @Pattern(regexp="(^$|[0-9]{6})",message = "AccountNumber must be 10 digits")
    @Schema(
            description = "Account Number of  Bank account", example = "3454433243"
    )
    private Long accountNumber;
    @NotEmpty(message = "AccountType can not be a null or empty")
    @Schema(
            description = "Account type of  Bank account", example = "Savings"
    )
    private String accountType;
    @NotEmpty(message = "BranchAddress can not be a null or empty")
    @Schema(
            description = "bank branch address", example = "123 NewYork"
    )
    private String branchAddress;
    @Schema(
            description = "status of account lock or unlock"
    )
    private Boolean status;
    //private String statusDecription;






}
