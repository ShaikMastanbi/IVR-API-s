package com.accounts.com.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class CardBlockResponse {
    @Schema(
            description = "status of the api ",example = "success"
    )
    public String status;
    @Schema(
            description = " reponse code for api"
    )
    public HttpStatus responseCode;
    @Schema(
            description = " message for api"
    )
    public String message;
    public CardBlockResponse(){

    }


}
