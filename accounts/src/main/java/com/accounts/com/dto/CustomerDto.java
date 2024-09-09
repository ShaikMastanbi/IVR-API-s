package com.accounts.com.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(
        name = "Customer",
        description = "Schema to hold Customer and Account information"
)
public class CustomerDto {
    private long customerId;
    @NotEmpty(message = "Name can not be a null or empty")
    @Schema(
            description = "Name of the customer", example = "mastanbi"
    )
    private String name;
    @NotEmpty(message = "Email address can not be a null or empty")
    @Email(message = "Email address should be a valid value")
    @Schema(
            description = "Email address of the customer", example = "tutor@gmail.com"
    )
    private String email;
    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$", message = "Invalid PAN number format")
    @Schema(
            description = "Pan number of the customer", example = "ASEDR2345D"
    )
    private String pan_number;
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    @Schema(
            description = "mobile number of customer", example = "9845671238"
    )
    private String mobileNumber;

    private AccountsDto accountsDto;
    private int userId;
}
