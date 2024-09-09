package com.accounts.com.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnBlockRequestDto {


    private String cardNumber;

    private String reason;


}
