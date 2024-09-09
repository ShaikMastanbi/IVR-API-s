package com.spring.securityapplication.service;


import com.spring.securityapplication.entity.UserCredential;
import com.spring.securityapplication.exceptions.ApiException;
import com.spring.securityapplication.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService {

    @Autowired
    private UserCredentialRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public String saveUser(UserCredential credential) {
        Optional<UserCredential> user=repository.findByUserName(credential.getUserName());
        if(user.isPresent()){
            throw new ApiException("user already present");
        }

        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);

        if (!pattern.matcher(credential.getPassword()).matches()) {
            throw new ApiException("Password must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter, and one special character (@#$%^&+=)");
        }
        // Email validation pattern
        String emailPattern = "^[a-zA-Z]+[0-9]+@gmail\\.com$";
        Pattern emailPatterns = Pattern.compile(emailPattern);

        if (!emailPatterns.matcher(credential.getEmail()).matches()) {
            throw new ApiException("Invalid email format. Email should follow the pattern name_digits@gmail.com");
        }

        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        //credential.setPassword((credential.getPassword()));
        repository.save(credential);
        return "user added to the system";
    }


    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }


    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    public Optional<UserCredential> getUserDetails(int id){

        Optional<UserCredential> userCredential=repository.fetchUser(id);
        System.out.println(userCredential);


        return userCredential;
    }


}
