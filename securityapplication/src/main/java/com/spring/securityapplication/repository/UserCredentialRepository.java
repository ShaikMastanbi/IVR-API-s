package com.spring.securityapplication.repository;


import com.spring.securityapplication.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential,Integer> {
    Optional<UserCredential> findByUserName(String username);
    @Query(value="select * from user where id=?",nativeQuery = true)
    Optional<UserCredential> fetchUser(int id);




}
