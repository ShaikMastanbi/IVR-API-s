package com.accounts.com.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCardRequestDto {
    @NotBlank
    private String mobileNumber;

    @NotNull
    private Long accountNumber;
}
