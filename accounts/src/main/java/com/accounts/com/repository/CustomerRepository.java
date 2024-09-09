package com.accounts.com.repository;

import com.accounts.com.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByMobileNumber(String mobileNumber);
    @Query(value = "select * from customer where pan_number=?",nativeQuery = true)
    public Customer findByPanNumber(String pan_number);

    public Customer findByUserId(int userId);



}
