package com.revature.services;

import com.revature.exceptions.InvalidColumnException;
import com.revature.exceptions.InvalidCredentialsException;
import com.revature.exceptions.PersistenceException;
import com.revature.models.Role;
import com.revature.models.User;
import com.revature.repositories.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;


/**
 * Constitutes the SERVICE LAYER for users. concerned with validating all user
 * input before being sent to the database.
 */
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private UserRepository userRepo = new UserRepository();

    Hashtable<Integer, String>
        hm = new Hashtable<Integer,String>();

    /**
     * Gets all users from the DataBase
     * @return A list of Users
     */
    public List<User> getAllUsers(){
        logger.info("Getting all User objects from database.");
        List<User> users = userRepo.getAllusers();
        return users;
    }

    /**
     * Authentication method used by the authentication servlet
     * @param username username of the user
     * @param password password of the user
     * @return the object of the requested user
     */
    public User authenticate(String username, String password){
        logger.info("Authenticating User in database.");
        if (username == null || username.trim().equals("") || password == null || password.trim().equals("")){
            logger.error("Invalid credentials provided.");
            throw new InvalidCredentialsException("Invalid credentials provided");
        }
        password = passHash(password);

        logger.info("Authentication successful!");
        return userRepo.getAUserByUsernameAndPassword(username,password)
                .orElseThrow(PersistenceException::new);
    }

    /**
     * Register a new user in the DB. validates all fields first
     * @param newUser completed user object
     */
    public void register(User newUser) {
        logger.info("Registering new user:\n\t " + newUser.toString());
        if (!isUserValid(newUser)) {
            logger.error("Invalid user field values provided during registration!");
            throw new InvalidCredentialsException("Invalid user field values provided during registration!");
        }
        Optional<User> existingUser = userRepo.getAUserByUsername(newUser.getUsername());
        if (existingUser.isPresent()) {
            logger.error("Username is already in use");
            throw new InvalidCredentialsException("Username is already in use");
        }
        Optional<User> existingUserEmail = userRepo.getAUserByEmail(newUser.getEmail());
        if (existingUserEmail.isPresent()) {
            logger.error("Email is already in use");
            throw new InvalidCredentialsException("Email is already in use");
        }

        logger.info("User saved!");
        newUser.setUserRole(Role.EMPLOYEE.ordinal() + 1);
        setUserPassHash(newUser);
        userRepo.addUser(newUser);
    }

    /**
     * Update a user in the DB.
     * @param newUser user to update
     */
    public void update(User newUser) {
        logger.info("Updating user in database:\n\t" + newUser.toString());
        if (!isUserValid(newUser)) {
            logger.error("Invalid user field values provided during registration!");
            throw new InvalidColumnException("Invalid user field values provided during registration!");
        }
        if (userRepo.getAUserById(newUser.getUserId()).orElseThrow(PersistenceException::new).getPassword()
        == newUser.getPassword()){
            if (!userRepo.updateAUser(newUser)){
                logger.error("There was a problem trying to update the user");
                throw new PersistenceException("There was a problem trying to update the user");
            }
        } else {
            setUserPassHash(newUser);
            if (!userRepo.updateAUser(newUser)){
                logger.error("There was a problem trying to update the user");
                throw new PersistenceException("There was a problem trying to update the user");
            }
        }

        logger.info("Update succesful!");
    }

    /**
     * Deletes a user by changing their role to 4
     * @param id id of user to delete
     * @return true if role was updated in db
     */
    public boolean deleteUserById(int id) {
        logger.info("Deleting User object from database with id: " + id);
        if (id <= 0){
            logger.error("The provided ID cannot be less than or equal to 0.");
            throw new IllegalIdentifierException("The provided ID cannot be less than or equal to 0.");
        }

        logger.info("Deletion successful!");
        return userRepo.deleteAUserById(id);
    }

    /**
     * Method for simple checking of availability of username
     * @param username username to chek
     * @return true if available
     */
    public boolean isUsernameAvailable(String username) {
        logger.info("Checking Username Availability in database.");
        User user = userRepo.getAUserByUsername(username).orElse(null);
        return user == null;
    }

    /**
     * Method for simple checking of availability of email
     * @param email
     * @return true if available
     */
    public boolean isEmailAvailable(String email) {
        logger.info("Checking Username Availability in database.");
        User user = userRepo.getAUserByEmail(email).orElse(null);
        return user == null;
    }

    /**
     * Validates that the given user and its fields are valid (not null or empty strings). Does
     * not perform validation on id or role fields.
     *
     * @param user
     * @return true or false depending on if the user was valid or not
     */
    public boolean isUserValid(User user) {
        if (user == null) return false;
        if (user.getFirstname() == null || user.getFirstname().trim().equals("")) return false;
        if (user.getLastname() == null || user.getLastname().trim().equals("")) return false;
        if (user.getUsername() == null || user.getUsername().trim().equals("")) return false;
        if (user.getPassword() == null || user.getPassword().trim().equals("")) return false;
        return true;
    }

    /**
     * Method used to Hash passwords by taking in the User and accessing Password field.
     * Authored by Lokesh Gupta via HowToDoInJava:
     * https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
     */
    public void setUserPassHash(User user) {
        logger.info("Hashing password for given user: " + user.toString());
        String pass = user.getPassword();
        try {
            // MessageDigest used to "Digest" the password and output Hash.
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Adding password bytes to digest.
            md.update(pass.getBytes());
            //Get's Hash Bytes.
            byte[] bytes = md.digest();
            //Needs to be converted from bytes to Hex
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < bytes.length; i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Gets completed Hash password in hex format.
            user.setPassword(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Something went wrong with the password hashing algorithm.");
            e.printStackTrace();
        }
    }

    /**
     * Method used to Hash passwords by taking in the Password and modifying.
     * Authored by Lokesh Gupta via HowToDoInJava:
     * https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
    */
    public String passHash(String pass) {
        logger.info("Hashing password for a given password.");
        try {
            // MessageDigest used to "Digest" the password and output Hash.
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Adding password bytes to digest.
            md.update(pass.getBytes());
            //Get's Hash Bytes.
            byte[] bytes = md.digest();
            //Needs to be converted from bytes to Hex
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < bytes.length; i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Gets completed Hash password in hex format.
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Something went wrong with the password hashing algorithm.");
            logger.error(e.getStackTrace());
        }
        return null;
    }
}
