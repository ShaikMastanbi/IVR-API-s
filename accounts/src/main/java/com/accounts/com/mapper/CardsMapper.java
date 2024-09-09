package com.accounts.com.mapper;


import com.accounts.com.dto.CardsDto;
import com.accounts.com.entity.Cards;

public class CardsMapper {
    public static CardsDto mapToCardsDto(Cards cards, CardsDto cardsDto) {
        cardsDto.setCardNumber(cards.getCardNumber());
        cardsDto.setCvv(cards.getCvv());
        cardsDto.setCardType(cards.getCardType());
        cardsDto.setMobileNumber(cards.getMobileNumber());
        cardsDto.setExpireDate(cards.getExpireDate());
        cardsDto.setTotalLimit(cards.getTotalLimit());
        cardsDto.setAvailableAmount(cards.getAvailableAmount());
        cardsDto.setAmountUsed(cards.getAmountUsed());
        cardsDto.setStatus(cards.isStatus()==true);
        cardsDto.setAccountNumber(cards.getAccountNumber());
        cardsDto.setStatus_decription(cards.getStatusDecription());

        return cardsDto;
    }

    public static Cards mapToCards(CardsDto cardsDto, Cards cards) {
        cards.setCardNumber(cardsDto.getCardNumber());
        cards.setCardType(cardsDto.getCardType());
        cards.setCvv(cardsDto.getCvv());
        cards.setMobileNumber(cardsDto.getMobileNumber());
        cards.setExpireDate(cardsDto.getExpireDate());
        cards.setTotalLimit(cardsDto.getTotalLimit());
        cards.setAvailableAmount(cardsDto.getAvailableAmount());
        cards.setAmountUsed(cardsDto.getAmountUsed());
        cards.setStatus(cardsDto.isStatus()==true);
        cards.setAccountNumber(cardsDto.getAccountNumber());
        cards.setStatusDecription(cardsDto.getStatus_decription());
        return cards;
    }
}
