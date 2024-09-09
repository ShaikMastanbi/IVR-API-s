package com.accounts.com.repository;

import com.accounts.com.entity.Accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
    Optional<Accounts> findByCustomerId(Long customerId);
    @Query(value = "select * from accounts where account_number=?",nativeQuery = true)
    public Accounts findByAccountNumber(long accountNumber);

}
