package com.accounts.com.service;


import com.accounts.com.dto.CardsDto;

public interface ICardsService {
    void createCard(String mobileNumber,Long accountNumber,String jwtToken);
    boolean blockCard(String cardNumber,String reason,String jwtToken);
    CardsDto fetchCard(String mobileNumber,String jwtToken);
    boolean updateCard(CardsDto cardsDto,String jwtToken);

    boolean unblock(String cardNumber,String reason,String jwtToken);
}
