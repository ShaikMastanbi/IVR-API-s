package com.accounts.com.service.impl;



import com.accounts.com.client.SecurityServiceClient;
import com.accounts.com.constants.CardsConstants;
import com.accounts.com.dto.CardsDto;
import com.accounts.com.entity.Accounts;
import com.accounts.com.entity.BlockCard;
import com.accounts.com.entity.Cards;
import com.accounts.com.entity.Customer;
import com.accounts.com.exceptions.ApiException;
import com.accounts.com.exceptions.CardAlreadyExistsException;
import com.accounts.com.exceptions.CustomerAlreadyExistsException;
import com.accounts.com.exceptions.ResourceNotFoundException;
import com.accounts.com.mapper.CardsMapper;
import com.accounts.com.repository.AccountsRepository;
import com.accounts.com.repository.BlockCardRepository;
import com.accounts.com.repository.CardsRepository;
import com.accounts.com.repository.CustomerRepository;
import com.accounts.com.service.ICardsService;
import com.accounts.com.util.EmailService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class CardsServiceImpl implements ICardsService {
    @Autowired
    private CardsRepository cardsRepository;
    @Autowired
    private BlockCardRepository blockCardRepository;
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    SecurityServiceClient securityServiceClient;
    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(CardsServiceImpl.class);



    @Override
    public void createCard(String mobileNumber,Long accountNumber,String jwtToken) {
        logger.info("Creating card for mobile number: {}", mobileNumber);
        System.out.println("JWT Token value received inside create card is : "+jwtToken);
        String userIdReceivedFromValidateEndpoint = securityServiceClient.getUserName(jwtToken);
        logger.debug("User ID received from security service: {}", userIdReceivedFromValidateEndpoint);
        System.out.println("userIdReceivedFromValidateEndpoint value : "+userIdReceivedFromValidateEndpoint);

        if (mobileNumber.length() != 10) {
            throw new ApiException("Invalid mobile number. Mobile number must be 10 digits.");
        }

        Accounts account = accountsRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new ApiException("Account with account number 🤦🏻‍♀️🤦🏻‍♀️" + accountNumber + " not found.");
        }
        Optional<Cards> optionalCardsByAccount = Optional.ofNullable(cardsRepository.findByAccountNumber(accountNumber));
        if (optionalCardsByAccount.isPresent()) {
            throw new CardAlreadyExistsException("Card already registered with given account number 🤦🏻‍♀️🤦🏻‍♀️" + accountNumber);
        }

        // Obtain customer ID from the account information
        Long customerId = account.getCustomerId();

        // Fetch customer details using the customer ID
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new ApiException("Customer with ID " + customerId + " not found.");
        }



        String dbUsername = customerOptional.get().getName();

        // Compare the username from the JWT token with the username from the database
        if (!userIdReceivedFromValidateEndpoint.equals(dbUsername)) {
            logger.info("Provided token is valid for username : " + userIdReceivedFromValidateEndpoint +
                    " and you are trying to use the token for " + dbUsername + " Not allowed 😒😒😒");
            throw new ApiException("Token is not valid for this user");
        }

        Optional<Cards> optionalCards = cardsRepository.findByMobileNumber(mobileNumber);
        if (optionalCards.isPresent()) {
            throw new CardAlreadyExistsException("Card already registered with given mobileNumber 🤦🏻‍♀️🤦🏻‍♀️" + mobileNumber);
        }


        cardsRepository.save(createNewCard(mobileNumber,accountNumber));
        logger.info("Card created successfully");
    }



    private Cards createNewCard(String mobileNumber,Long accountNumber) {
        Cards newCard = new Cards();
        long randomCardNumber = 1000000000000000L + new Random().nextInt(900000000);
        newCard.setCardNumber(Long.toString(randomCardNumber));

        newCard.setAccountNumber(accountNumber);

        long randomCardCvv = 100L + new Random().nextInt(900);
        newCard.setCvv(Long.toString(randomCardCvv));
        newCard.setMobileNumber(mobileNumber);
        newCard.setCardType(CardsConstants.CREDIT_CARD);
        newCard.setExpireDate(CardsConstants.EXPIRE_DATE);
        newCard.setTotalLimit(CardsConstants.NEW_CARD_LIMIT);
        newCard.setAmountUsed(0);
        newCard.setAvailableAmount(CardsConstants.NEW_CARD_LIMIT);
        newCard.setStatusDecription("active");
        newCard.setStatus(true);

        return newCard;
    }
    @Override
    public boolean blockCard(String cardNumber, String reason,String jwtToken) {
        logger.info("Blocking card for card number: {}", cardNumber);
        System.out.println("JWT Token value received inside block card is : "+jwtToken);
        String userIdReceivedFromValidateEndpoint = securityServiceClient.getUserName(jwtToken);
        logger.debug("User ID received from security service: {}", userIdReceivedFromValidateEndpoint);
        System.out.println("userIdReceivedFromValidateEndpoint value : "+userIdReceivedFromValidateEndpoint);
        Cards cards1 = cardsRepository.findByCardNumber(cardNumber);
        if (cards1 == null || !cards1.isStatus()) { // Check if card exists and is not already blocked
            logger.warn("Card not found or already blocked: {}", cardNumber);
            return false; // Return false indicating card not found or already blocked
        }

        // Extract account number from the fetched card details
        Long accountNumber = cards1.getAccountNumber();

        // Fetch account details from the database using the account number
        Accounts account = accountsRepository.findByAccountNumber(accountNumber);
        if (account == null) {

            return false; // Return false indicating account not found
        }

        // Extract customer ID from the fetched account details
        Long customerId = account.getCustomerId();

        // Fetch customer details from the database using the customer ID
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return false; // Return false indicating customer not found
        }

        // Extract name from the fetched customer details
        String customerName = customerOptional.get().getName();

        // Compare the fetched customer name with the token username
        if (!customerName.equals(userIdReceivedFromValidateEndpoint)) {
            logger.info("Provided token is valid for username : " + userIdReceivedFromValidateEndpoint +
                    " and you are trying to use the token for " + customerName + " Not allowed 😒😒😒");
            throw new ApiException("token is not valid for this user");
        }


        Cards cards = cardsRepository.findByCardNumber(cardNumber);
        System.out.println(cards);
        if (cards != null && cards.isStatus() ) {// Check if card exists and is not already blocked
            System.out.println(cards);
            cards.setStatus(false);// Update card status to blocked
            cards.setStatusDecription("blocked");
            cardsRepository.save(cards);

            // Create a new block card entry
            BlockCard blockCard = new BlockCard();
            blockCard.setCardNumber(cards.getCardNumber());
            blockCard.setStatus("success");
            blockCard.setReason(reason); // Use the reason parameter passed to the method
            blockCardRepository.save(blockCard);
            logger.info("Card blocked successfully: {}", cardNumber);

            // Sending email notification
            //Optional<Customer> customerOptional1 = customerRepository.findById(customerId);
            String customerEmail=customerOptional.get().getEmail();
            String from = "ivrapis43@gmail.com";
            String to = customerEmail;
            String subject = "Card Blocked Notification";
            String message = "Hello,\n\n";
            message += "Your card with number " + cardNumber + " has been blocked due to the following reason: " + reason + ".\n";
            message += "If you did not initiate this action, please contact customer support immediately.";

            try {
                emailService.sendMail(from, to, subject, message);
                logger.info("Email notification sent successfully to {}", to);
            } catch (MessagingException e) {
                // Handle email sending failure
                logger.error("Failed to send email notification to {}", to);
                e.printStackTrace();
            }



            return true; // Return true indicating successful blocking
        } else {
            logger.warn("Card not found or already blocked: {}", cardNumber);
            return false; // Return false indicating card not found or already blocked
        }
    }


    @Override
    public CardsDto fetchCard(String mobileNumber,String jwtToken) {

        if (mobileNumber.length() != 10) {
            throw new ApiException("Invalid mobile number. Mobile number must be 10 digits.");
        }
        System.out.println("JWT Token value received inside fetch card is : "+jwtToken);
        String userIdReceivedFromValidateEndpoint = securityServiceClient.getUserName(jwtToken);
        System.out.println("userIdReceivedFromValidateEndpoint value : "+userIdReceivedFromValidateEndpoint);
        Cards cards = cardsRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ApiException("Card not found for mobile number: " + mobileNumber));

        // Extract account number from the fetched card details
        Long accountNumber = cards.getAccountNumber();

        // Fetch account details from the database using the account number
        Accounts account = accountsRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new ResourceNotFoundException("Account", "accountNumber", accountNumber.toString());
        }

        // Extract customer ID from the fetched account details
        Long customerId = account.getCustomerId();

        // Fetch customer details from the database using the customer ID
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        Customer customer = customerOptional.orElseThrow(() -> new ResourceNotFoundException("Customer", "customerId", customerId.toString()));

        // Extract name from the fetched customer details
        String customerName = customer.getName();

        // Compare the fetched customer name with the token username
        if (!customerName.equals(userIdReceivedFromValidateEndpoint)) {
            logger.info("Provided token is valid for username : " + userIdReceivedFromValidateEndpoint +
                    " and you are trying to use the token for " + customerName + " Not allowed 😒😒😒");
            throw new ApiException("token is not valid for this user");
        }
        Cards cards1 = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        return CardsMapper.mapToCardsDto(cards1, new CardsDto());
    }

    @Override
    public boolean updateCard(CardsDto cardsDto,String jwtToken) {
        System.out.println("JWT Token value received inside update card is : "+jwtToken);
        String userIdReceivedFromValidateEndpoint = securityServiceClient.getUserName(jwtToken);
        System.out.println("userIdReceivedFromValidateEndpoint value : "+userIdReceivedFromValidateEndpoint);
        Optional<Cards> optionalCards1 = Optional.ofNullable(cardsRepository.findByCardNumber(cardsDto.getCardNumber()));
        Cards cards1 = optionalCards1.get();
        Long accountNumber = cards1.getAccountNumber();
        Accounts account = accountsRepository.findByAccountNumber(accountNumber);
        Long customerId = account.getCustomerId();
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        String customerName = customerOptional.get().getName();
        if (!customerName.equals(userIdReceivedFromValidateEndpoint)) {
            logger.info("Provided token is valid for username : " + userIdReceivedFromValidateEndpoint +
                    " and you are trying to use the token for " + customerName + " Not allowed 😒😒😒");
            throw new ApiException("Token is not valid for this user");
        }

        Optional<Cards> optionalCards = Optional.ofNullable(cardsRepository.findByCardNumber(cardsDto.getCardNumber()));
        if (optionalCards.isPresent()) {
            Cards cards = optionalCards.get();

            // Check if the card status is active
            if (cards.getStatusDecription().equals("active")) {

                // Check if the amount used is less than the limit before updating
                if (cards.getAmountUsed() < cards.getTotalLimit()) {
                    // Reduce the amount from available amount
                    cards.setAvailableAmount(cards.getAvailableAmount() - cardsDto.getAmountUsed());

                    // If the new amount used exceeds the limit, set it to the limit
                    if (cards.getAmountUsed() + cardsDto.getAmountUsed() > cards.getTotalLimit()) {
                        cards.setAmountUsed(cards.getTotalLimit());
                    } else {
                        // Otherwise, add the amount used
                        cards.setAmountUsed(cards.getAmountUsed() + cardsDto.getAmountUsed());
                    }

                    cardsRepository.save(cards);
                    return true;
                } else {
                    throw new ApiException("Limit reached for card number: " + cardsDto.getCardNumber());
                }
            } else {
                throw new ApiException("Card status is Blocked. Cannot update.");
            }
        } else {
            throw new ResourceNotFoundException("Card", "CardNumber", cardsDto.getCardNumber());
        }


    }

    @Override
    public boolean unblock(String cardNumber,String reason,String jwtToken) {
        logger.info("Unblocking card for card number: {}", cardNumber);
        System.out.println("JWT Token value received inside unlock card is : "+jwtToken);
        String userIdReceivedFromValidateEndpoint = securityServiceClient.getUserName(jwtToken);
        logger.debug("User ID received from security service: {}", userIdReceivedFromValidateEndpoint);
        System.out.println("userIdReceivedFromValidateEndpoint value : "+userIdReceivedFromValidateEndpoint);
        Cards cards1 = cardsRepository.findByCardNumber(cardNumber);
        if (cards1 == null || cards1.isStatus()) { // Check if card exists and is currently blocked
            return false; // Return false indicating card not found or already unblocked
        }

        // Extract account number from the fetched card details
        Long accountNumber = cards1.getAccountNumber();

        // Fetch account details from the database using the account number
        Accounts account = accountsRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            return false; // Return false indicating account not found
        }

        // Extract customer ID from the fetched account details
        Long customerId = account.getCustomerId();

        // Fetch customer details from the database using the customer ID
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return false; // Return false indicating customer not found
        }
        String customerEmail = customerOptional.get().getEmail();

        // Extract name from the fetched customer details
        String customerName = customerOptional.get().getName();

        // Compare the fetched customer name with the token username
        if (!customerName.equals(userIdReceivedFromValidateEndpoint)) {
            logger.info("Provided token is valid for username : " + userIdReceivedFromValidateEndpoint +
                    " and you are trying to use the token for " + customerName + " Not allowed 😒😒😒");
            throw new ApiException("Token is not valid for this user");
        }



        Cards cards = cardsRepository.findByCardNumber(cardNumber);

        if (cards != null && !cards.isStatus()) { // Check if card exists and is currently blocked
            cards.setStatus(true); // Update card status to unblocked
            cards.setStatusDecription("active");
            cardsRepository.save(cards);

            BlockCard blockCard1 = new BlockCard();
            blockCard1.setCardNumber(cards.getCardNumber());
            blockCard1.setStatus("success");
            blockCard1.setReason(reason.isEmpty() ? null : reason); // Set reason to null if it's an empty string
            System.out.println(blockCard1);
            blockCardRepository.save(blockCard1);

            // Sending email notification
            String from = "ivrapis43@gmail.com";
            String to = customerEmail;
            String subject = "Card Unblocked Notification";
            String message = "Hello,\n\n";
            message += "Your card with number " + cardNumber + " has been unblocked.\n";
            message += "If you have any questions, please contact customer support.";

            try {
                emailService.sendMail(from, to, subject, message);
                logger.info("Email notification sent successfully to {}", to);
            } catch (MessagingException e) {
                // Handle email sending failure
                logger.error("Failed to send email notification to {}", to);
                e.printStackTrace();
            }


            return true; // Return true indicating successful unblocking
        } else {
            return false; // Return false indicating card not found or already unblocked
        }
    }


}
