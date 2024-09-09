package com.accounts.com.mapper;

import com.accounts.com.dto.CustomerDto;
import com.accounts.com.dto.UserDetailsDto;
import com.accounts.com.entity.Customer;

public class CustomerMapper {

    public static CustomerDto mapToCustomerDto(Customer customer, CustomerDto customerDto) {
        customerDto.setCustomerId(customer.getCustomerId());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setMobileNumber(customer.getMobileNumber());
        customerDto.setPan_number(customer.getPan_number());
        customerDto.setUserId(customer.getUserId());
        return customerDto;
    }

    public static Customer mapToCustomer(CustomerDto customerDto, Customer customer) {
        customer.setCustomerId(customer.getCustomerId());
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setMobileNumber(customerDto.getMobileNumber());
        customer.setPan_number(customerDto.getPan_number());
        customer.setUserId(customerDto.getUserId());
        return customer;
    }






}
