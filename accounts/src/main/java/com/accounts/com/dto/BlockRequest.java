package com.accounts.com.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
        name = "Block request",
        description = "Schema to hold block request information"
)
@Data
public class BlockRequest {
    @Schema(
            description = "Card Number of the customer", example = "1000000468396289"
    )
    private String cardNumber;
    @Schema(
            description = "reson to block card", example = "lost"
    )
    private String reason;

    public BlockRequest(String cardNumber, String reason) {
        this.cardNumber = cardNumber;
        this.reason = reason;
    }
}
