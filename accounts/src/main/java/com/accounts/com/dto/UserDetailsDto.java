package com.accounts.com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class UserDetailsDto {
    private int id;
    private String UserName;
    private String email;
    private String password;
}
