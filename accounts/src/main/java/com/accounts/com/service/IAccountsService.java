package com.accounts.com.service;

import com.accounts.com.dto.CustomerDto;
import com.accounts.com.dto.LockUnlockAccounDto;
import com.accounts.com.entity.Accounts;

public interface IAccountsService {


    CustomerDto fetchAccount(String mobileNumber,String jwtToken);

    CustomerDto fetchAccountV2(String mobileNumber);

    public void unlockAccount(LockUnlockAccounDto lockUnlockAccounDto,String jwtToken);

    boolean updateAccount(CustomerDto customerDto,String jwtToken);
    public void lockAccount(LockUnlockAccounDto lockUnlockAccounDto,String jwtToken);

    public String createAccount1(CustomerDto customerDto,String jwtToken);




}
