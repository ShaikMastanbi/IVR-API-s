package com.accounts.com.service.impl;


import com.accounts.com.client.SecurityServiceClient;
import com.accounts.com.constants.AccountsConstants;
import com.accounts.com.dto.AccountsDto;
import com.accounts.com.dto.CustomerDto;
import com.accounts.com.dto.LockUnlockAccounDto;
import com.accounts.com.dto.UserDetailsDto;
import com.accounts.com.entity.Accounts;
import com.accounts.com.entity.Customer;
import com.accounts.com.exceptions.ApiException;
import com.accounts.com.exceptions.CustomerAlreadyExistsException;
import com.accounts.com.exceptions.ResourceNotFoundException;
import com.accounts.com.mapper.AccountsMapper;
import com.accounts.com.mapper.CustomerMapper;
import com.accounts.com.repository.AccountsRepository;
import com.accounts.com.repository.CustomerRepository;
import com.accounts.com.service.IAccountsService;
import com.accounts.com.util.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountsServiceImpl implements IAccountsService {
    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;

    @Autowired
    SecurityServiceClient securityServiceClient;
    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AccountsServiceImpl.class);



    @Autowired
    RestTemplate restTemplate;
@Autowired
    public AccountsServiceImpl(AccountsRepository accountsRepository, CustomerRepository customerRepository) {
        this.accountsRepository = accountsRepository;
        this.customerRepository = customerRepository;
    }
    public String createAccount1(CustomerDto customerDto,String jwtToken) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());

        System.out.println("JWT Token value received inside createAccount1 is : "+jwtToken);


        String userIdReceivedFromValidateEndpoint = securityServiceClient.getUserName(jwtToken);
        System.out.println("userIdReceivedFromValidateEndpoint value : "+userIdReceivedFromValidateEndpoint);

        UserDetailsDto userDetailsDto = fetchUserDetails(customerDto.getUserId());
        System.out.println(userDetailsDto);
        if(!userDetailsDto.getUserName().equals(userIdReceivedFromValidateEndpoint)){
            logger.info("Provided token is valid for username: " + userIdReceivedFromValidateEndpoint +
                    " and you are trying to use the token for " + customer.getName() + ". Unblocking of the account is not allowed.");
            throw new ApiException("Provided token is not valid for username ");
        }
        System.out.println(userDetailsDto);



        if (userDetailsDto != null) {
            if (!customerDto.getName().equals(userDetailsDto.getUserName())) {
                throw new ApiException("The provided name does not match the fetched user details.");
            }

            if (!customerDto.getEmail().equals(userDetailsDto.getEmail())) {
                throw new ApiException("The provided email does not match the fetched user details.");
            }
            customerDto.setUserId(userDetailsDto.getId());
        } else {
            throw new RuntimeException("User details could not be retrieved. User may not be logged in.");
        }

        // Set customer details
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setMobileNumber(customerDto.getMobileNumber());
        customer.setPan_number(customerDto.getPan_number());
        customer.setUserId(customer.getUserId());

        Optional<Customer> existingCustomer = Optional.ofNullable(customerRepository.findByUserId(customerDto.getUserId()));
        if (existingCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with the user ID: " + customerDto.getUserId());
        }


        // Check if customer already exists
        if (customerRepository.findByMobileNumber(customerDto.getMobileNumber()).isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with the given mobile number: " + customerDto.getMobileNumber());
        }

        // Check if customer already exists by PAN number
        if (customerRepository.findByPanNumber(customerDto.getPan_number()) != null) {
            throw new CustomerAlreadyExistsException("Customer already registered with the given PAN number: " + customerDto.getPan_number());
        }

        // Save customer and create a new account
        Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
        return "successfully created";
    }




    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 100000L + new Random().nextInt(900000);


        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        newAccount.setStatusDecription("lock");




        boolean isLock=true;
        newAccount.setStatus(isLock);
        return newAccount;
    }
    @Override
    public CustomerDto fetchAccount(String mobileNumber, String jwtToken) {
        logger.info("Fetching account for mobile number: {}", mobileNumber);
        // Fetch name from token
        String userIdReceivedFromValidateEndpoint = securityServiceClient.getUserName(jwtToken);
        logger.debug("User ID received from security service: {}", userIdReceivedFromValidateEndpoint);

        // Fetch customer by mobile number
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        logger.debug("Customer found: {}", customer);

        // Fetch accounts associated with the customer
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", String.valueOf(customer.getCustomerId())));
        logger.debug("Accounts found: {}", accounts);

        // Check if the token's user matches the fetched customer's name
        if (!customer.getName().equals(userIdReceivedFromValidateEndpoint)) {
            logger.info("Provided token is valid for username: " + userIdReceivedFromValidateEndpoint +
                    " and you are trying to use the token for " + customer.getName() + ". Unblocking of the account is not allowed.");
            throw new ApiException("Provided token is not valid for username ");
        }

        // Map Customer and Accounts entities to DTOs
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        logger.info("Account fetched successfully");

        return customerDto;
    }




    public CustomerDto fetchAccountV2(String mobileNumber) {
        logger.info("Fetching account for mobile number: {}", mobileNumber);
        // Fetch name from token

        // Fetch customer by mobile number
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        logger.debug("Customer found: {}", customer);

        // Fetch accounts associated with the customer
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", String.valueOf(customer.getCustomerId())));
        logger.debug("Accounts found: {}", accounts);


        // Map Customer and Accounts entities to DTOs
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        logger.info("Account fetched successfully");

        return customerDto;
    }




    @Override
   public void unlockAccount(LockUnlockAccounDto lockUnlockAccounDto, String jwtToken) {
       logger.info("Unlocking account for account number: {}", lockUnlockAccounDto.getAccountNumber());
       System.out.println("JWT Token value received inside unlockAccount is : " + jwtToken);

       String userIdReceivedFromValidateEndpoint = securityServiceClient.getUserName(jwtToken);
       logger.debug("User ID received from security service: {}", userIdReceivedFromValidateEndpoint);
       System.out.println("userIdReceivedFromValidateEndpoint value : " + userIdReceivedFromValidateEndpoint);

       Accounts account = accountsRepository.findByAccountNumber(lockUnlockAccounDto.getAccountNumber());
       if (account == null) {
           throw new ApiException("Account with account number " + lockUnlockAccounDto.getAccountNumber() + " not found.");
       }

       logger.debug("Account found: {}", account);
       Long customerId = account.getCustomerId();
       Optional<Customer> customer = this.customerRepository.findById(customerId);

       if (!customer.isPresent()) {
           throw new ApiException("Customer with ID " + customerId + " not found.");
       }

       String name = customer.get().getName();
       logger.debug("Customer found: {}", name);

       System.out.println(customer.get());
       if (!customer.get().getName().equals(userIdReceivedFromValidateEndpoint)) {
           logger.info("Provided token is valid for username : " + userIdReceivedFromValidateEndpoint + " and you are trying to use the token for " + customer.get().getName() + " unblocking of account Not allowed 😒😒😒");
           throw new ApiException("provide token is not valid for this user");
       }

       if (account.getStatus()) {
           account.setStatus(false); // Set status to false to unlock the account
           account.setStatusDecription("unlock");
           accountsRepository.save(account); // Save the updated account
           logger.info("Account unlocked successfully");

           //sending email
           String from = "ivrapis43@gmail.com";
           String to = customer.get().getEmail();
           String subject = "Account Unlocked Notification";
           String message = "Hello " + name + ",\n\n";
           message += "Your account has been successfully unlocked.\n";
           message += "Thank you for using our services.";

           try {
               emailService.sendMail(from, to, subject, message);
               logger.info("Email notification sent successfully to {}", to);
           } catch (MessagingException e) {
               // Handle email sending failure
               logger.error("Failed to send email notification to {}", to);
               e.printStackTrace();
           }
       } else {
           throw new RuntimeException("Account is already unlocked 🧐. Unable to unlock account 🤷‍♀️🤷‍♀️.");
       }
   }

   @Override
   public void lockAccount(LockUnlockAccounDto lockUnlockAccounDto, String jwtToken) {
       logger.info("Locking account for account number: {}", lockUnlockAccounDto.getAccountNumber());

       System.out.println("JWT Token value received inside unlockAccount is : " + jwtToken);
       String userIdReceivedFromValidateEndpoint = securityServiceClient.getUserName(jwtToken);
       logger.debug("User ID received from security service: {}", userIdReceivedFromValidateEndpoint);

       Accounts account = accountsRepository.findByAccountNumber(lockUnlockAccounDto.getAccountNumber());
       if (account == null) {
           throw new ApiException("Account with account number " + lockUnlockAccounDto.getAccountNumber() + " not found.");
       }

       logger.debug("Account found: {}", account);
       Long customerId = account.getCustomerId();
       Optional<Customer> customer = this.customerRepository.findById(customerId);

       if (!customer.isPresent()) {
           throw new ApiException("Customer with ID " + customerId + " not found.");
       }

       String name = customer.get().getName();
       logger.debug("Customer found: {}", name);

       System.out.println(customer.get());
       if (!customer.get().getName().equals(userIdReceivedFromValidateEndpoint)) {
           logger.info("Provided token is valid for username : " + userIdReceivedFromValidateEndpoint + " and you are trying to use the token for " + customer.get().getName() + " locking of account Not allowed 😒😒😒");
           throw new ApiException("token is not valid for this user");
       }

       if (!account.getStatus()) {
           account.setStatus(true); // Set status to true to lock the account
           account.setStatusDecription("locked");
           accountsRepository.save(account); // Save the updated account
           logger.info("Account locked successfully");

           //sending email
           String from = "ivrapis43@gmail.com";
           String to = customer.get().getEmail();
           String subject = "Account Locked Notification";
           String message = "Hello " + name + ",\n\n";
           message += "Your account has been locked.\n";
           message += "If you did not initiate this action, please contact customer support immediately.";

           try {
               emailService.sendMail(from, to, subject, message);
               logger.info("Email notification sent successfully to {}", to);
           } catch (MessagingException e) {
               // Handle email sending failure
               logger.error("Failed to send email notification to {}", to);
               e.printStackTrace();
           }
       } else {
           throw new RuntimeException("Account is already locked 🧐. Unable to lock account 🤷‍♀️🤷‍♀️.");
       }
   }

    @Override
    public boolean updateAccount(CustomerDto customerDto,String jwtToken) {
        logger.info("Updating account for user ID: {}", customerDto.getUserId());
        System.out.println("JWT Token value received inside unlockAccount is : "+jwtToken);
        String userIdReceivedFromValidateEndpoint = securityServiceClient.getUserName(jwtToken);
        logger.debug("User ID received from security service: {}", userIdReceivedFromValidateEndpoint);
        System.out.println("userIdReceivedFromValidateEndpoint value : "+userIdReceivedFromValidateEndpoint);
        UserDetailsDto userDetailsDto = fetchUserDetails(customerDto.getUserId());
        logger.debug("User details retrieved: {}", userDetailsDto);
        System.out.println(userDetailsDto);
        if(!userDetailsDto.getUserName().equals(userIdReceivedFromValidateEndpoint)){
            logger.info("Provided token is valid for username : "+userIdReceivedFromValidateEndpoint+" and you are trying to use the token for "+customerDto.getName()+" Not allowed 😒😒😒");
            throw new ApiException("token is not valid for this user");
        }
        System.out.println(userDetailsDto);
        boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto !=null ){
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "AccountNumber", accountsDto.getAccountNumber().toString())
            );
            logger.debug("Accounts found: {}", accounts);
            accounts.setAccountType(accountsDto.getAccountType());
            //AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString())
            );
            CustomerMapper.mapToCustomer(customerDto,customer);
            customerRepository.save(customer);


            isUpdated = true;
        }
        return  isUpdated;
    }








    private UserDetailsDto fetchUserDetails(int userId) {
        String securityServerUserDetailsUrl = "http://localhost:9043/auth/fetch/" + userId;
        return restTemplate.getForObject(securityServerUserDetailsUrl, UserDetailsDto.class);
    }


}
