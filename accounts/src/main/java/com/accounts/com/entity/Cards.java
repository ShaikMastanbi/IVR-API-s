package com.accounts.com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Cards extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    private Long accountNumber;

    private String mobileNumber;

    private String cardNumber;
    private String cvv;

    private String cardType;

    private int totalLimit;

    private int amountUsed;

    private int availableAmount;
    private boolean status;
    private LocalDate expireDate;
    private String statusDecription;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


}
