package com.accounts.com.repository;


import com.accounts.com.entity.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardsRepository extends JpaRepository<Cards, Long> {
    Optional<Cards> findByMobileNumber(String mobileNumber);
    Cards findByCardNumber(String cardsNumber);
    @Query(value = "select * from cards where account_number=?",nativeQuery = true)
    Cards findByAccountNumber(long accountNumber);


}
