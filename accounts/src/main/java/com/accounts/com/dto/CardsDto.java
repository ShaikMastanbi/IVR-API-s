package com.accounts.com.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Schema(name = "Cards",
        description = "Schema to hold Card information"
)
@Data
@Getter
@Setter
public class CardsDto {

    @NotEmpty(message = "Mobile Number can not be a null or empty")
    @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile Number must be 10 digits")
    @Schema(
            description = "Mobile Number of Customer", example = "4354437687"
    )
    private String mobileNumber;

    @NotEmpty(message = "Card Number can not be a null or empty")
    @Pattern(regexp="(^$|[0-9]{16})",message = "CardNumber must be 16 digits")
    @Schema(
            description = "Card Number of the customer", example = "1000000468396289"
    )
    private String cardNumber;

    @NotEmpty(message = "CardType can not be a null or empty")
    @Schema(
            description = "Card type of the customer", example ="savings"
    )
    private String cardType;

    @NotEmpty(message = "cvv can not be a null or empty")
    @Pattern(regexp="(^$|[0-9]{3})",message = "cvv must be 3 digits")
    @Schema(
            description = "Card cvv of the customer", example = "123"
    )
    private String cvv;

    @Positive(message = "Total card limit should be greater than zero")
    @Schema(
            description = "Total amount limit available against a card", example = "100000"
    )
    private int totalLimit;

    @PositiveOrZero(message = "Total amount used should be equal or greater than zero")
    @Schema(
            description = "Total amount used by a Customer", example = "1000"
    )
    private int amountUsed;

    @PositiveOrZero(message = "Total available amount should be equal or greater than zero")
    @Schema(
            description = "Total available amount against a card", example = "90000"
    )
    private int availableAmount;
    @Schema(
            description = "status of card", example = "block"
    )
    private boolean status;
    @Schema(
            description = "expiredate of card"
    )
    private LocalDate expireDate;

    private Long accountNumber;

    private String status_decription;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}