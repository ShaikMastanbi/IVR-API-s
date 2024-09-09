package com.accounts.com.constants;


import java.time.LocalDate;


public class CardsConstants {
    private CardsConstants() {
        // restrict instantiation
    }

    public static final String  CREDIT_CARD = "Credit Card";


    public static final int  NEW_CARD_LIMIT = 1_00_000;
    public static final String  STATUS_201 = "201";
    public static final LocalDate EXPIRE_DATE = LocalDate.of(2030, 12, 12);
    public static final String  MESSAGE_201 = "Card created successfully🎉🎉";
    public static final String  STATUS_200 = "200";
    public static final String  MESSAGE_200 = "Request processed successfully🎉🎉";
    public static final String  STATUS_417 = "417";
    public static final String  MESSAGE_417_UPDATE= "Update operation failed 😔😔";
    public static final String  MESSAGE_417_DELETE= "Delete operation failed😔😔";
    // public static final String  STATUS_500 = "500";
    // public static final String  MESSAGE_500 = "An error occurred. Please try again or contact Dev team";
}
