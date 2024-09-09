package com.accounts.com.repository;


import com.accounts.com.entity.BlockCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockCardRepository extends JpaRepository<BlockCard, Integer> {

}
