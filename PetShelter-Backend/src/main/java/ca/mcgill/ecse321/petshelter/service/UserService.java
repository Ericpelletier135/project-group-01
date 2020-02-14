package ca.mcgill.ecse321.petshelter.service;

import ca.mcgill.ecse321.petshelter.dto.UserDTO;
import ca.mcgill.ecse321.petshelter.model.User;
import ca.mcgill.ecse321.petshelter.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * Service to handle login and registration of users.
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    
    
    private EmailingService emailingService;
    
    public UserService(EmailingService emailingService) {
        this.emailingService = emailingService;
    }
    
    @Transactional
    public List<User> getAllUsers() {
        return toList(userRepository.findAll());
    }
    
    //TODO, not to sure about this
    @Transactional
    public User findUser(String name) {
        return userRepository.findUserByUserName(name);
    }
    
    
    //TODO create helper methods to shorten the code here
    // register users
    public User addUser(UserDTO user) {
        //check that the email and username are unique
        if (userRepository.findUserByEmail(user.getEmail()) != null)
            throw new RegisterException("Email is already taken.");
        if (userRepository.findUserByUserName(user.getUsername()) != null)
            throw new RegisterException("Username is already taken.");
        if (validateUserEmail(user.getEmail())) {
            throw new RegisterException("Invalid Email.");
        }
        //create the user and set its attributes
        User user1 = new User();
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        user1.setEmail(user.getEmail());
        user1.setUserName(user.getUsername());
        user1.setUserType(user.getUserType());
        String token = jwtTokenProvider.createToken(user1.getUserName());
        user1.setApiToken(token);
        //save it
        userRepository.save(user1);
        // Send email
        emailingService.userCreationEmail(user.getEmail(), user.getUsername(), token);
        return user1;
    }
    
    /**
     * Check if the email input is valid
     * Credits to https://howtodoinjava.com/regex/java-regex-validate-email-address/, for regex.
     *
     * @param email user's email
     * @return false if ok, true if invalid
     */
    private boolean validateUserEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }
    
    // method that only checks if a user could be logged in
    public User loginUser(UserDTO user) throws LoginException {
        // if no user is found by its username, it does not exist
        User user1 = userRepository.findUserByUserName(user.getUsername());
        if (user1 == null) {
            throw new LoginException("Username not found");
        }
        // if the password doesnt match the saved one
        String expectedPW = user1.getPassword();
        if (!passwordEncoder.matches(user.getPassword(), expectedPW)) {
            throw new LoginException("Incorrect password");
        }
        // if the user has not verified their account through email
        if (!user1.isIsEmailValidated())
            throw new LoginException("Account not verified");
        return user1;
    }
    
    public String generateRandomPassword() {
        String upperCaseLetters = RandomStringUtils.random(1, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(1, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(1);
        String totalChars = RandomStringUtils.randomAlphanumeric(6);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters).concat(numbers).concat(totalChars);
        List<Character> pwdChars = combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        return pwdChars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
    
    private <T> List<T> toList(Iterable<T> iterable) {
        List<T> resultList = new ArrayList<T>();
        for (T t : iterable) {
            resultList.add(t);
        }
        return resultList;
    }
}