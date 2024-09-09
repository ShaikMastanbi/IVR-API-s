package com.accounts.com.client;


import com.accounts.com.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SecurityServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    public String getUserName(String tokenValue) {
        String userIdReceivedFromValidateEndpoint = "http://localhost:9043/auth/validateToken";

        // Create HttpHeaders and add Authorization header with tokenValue
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenValue);

        // Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Send HTTP GET request and retrieve the response
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(
                userIdReceivedFromValidateEndpoint,
                HttpMethod.GET,
                entity,
                String.class);

        // Check if the response status is OK (200)
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ApiException("Failed to validate token: " + responseEntity.getStatusCode());
        }

        // Extract and return the username from the response body
        String responseBody = responseEntity.getBody();
        // Assuming responseBody contains the username, parse it accordingly
        return responseBody;
    }

    public String extractToken(String jwtToken){
        String[] parts = jwtToken.split(" ");
        String tokenValue = "";
        if (parts.length == 2 && parts[0].equals("Bearer")) {
            tokenValue = parts[1];
            System.out.println("Token: " + tokenValue);
        } else {
            System.out.println("Invalid token format");
        }
        System.out.println("Received token from Headers is : "+jwtToken);
        return tokenValue;
    }




}