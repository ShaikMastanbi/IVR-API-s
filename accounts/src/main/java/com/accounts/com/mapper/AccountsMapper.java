package com.accounts.com.mapper;

import com.accounts.com.dto.AccountsDto;
import com.accounts.com.entity.Accounts;

import java.sql.Date;

public class AccountsMapper {

    public static AccountsDto mapToAccountsDto(Accounts accounts, AccountsDto accountsDto) {

        accountsDto.setAccountNumber(accounts.getAccountNumber());
        accountsDto.setAccountType(accounts.getAccountType());
        accountsDto.setBranchAddress(accounts.getBranchAddress());
        accountsDto.setStatus(accounts.getStatus());


        //accounts.setStatusDecription(accounts.getStatusDecription());


        return accountsDto;
    }

    public static Accounts mapToAccounts(AccountsDto accountsDto, Accounts accounts) {
        accounts.setAccountNumber(accountsDto.getAccountNumber());
        accounts.setAccountType(accountsDto.getAccountType());
        accounts.setBranchAddress(accountsDto.getBranchAddress());
        accounts.setStatus(accountsDto.getStatus());

        //accounts.setStatusDecription(accounts.getStatusDecription());

        return accounts;
    }

}