package com.revature.services;

import com.revature.exceptions.AuthenticationException;
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
import java.util.List;
import java.util.Optional;


/**
 * Constitutes the SERVICE LAYER for users. concerned with validating all user
 * input before being sent to the database.
 */
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private UserRepository userRepo = new UserRepository();
    private static final UserService service = new UserService();

    private UserService() {
        super();
    }

    public static UserService getInstance() {
        return service;
    }

    /**
     * Gets all users from the database
     * @return A list of {@code User} objects
     */
    public List<User> getAllUsers(){
        logger.info("Getting all User objects from database.");
        List<User> users = userRepo.getAllusers();
        return users;
    }

    /**
     * Gets a {@code User} with the specified username
     * @param username
     * @return the {@code User} associated with given {@code username} or {@code null}
     *          if none exists
     */
    public User getUserByUsername(String username) {
        logger.info("Getting user with username " + username);
        User u = userRepo.getAUserByUsername(username).orElse(null);
        return u;
    }

    /**
     * Authentication method used by the authentication servlet
     * @param username username of the user
     * @param password password of the user
     * @return the object of the requested {@code User}
     * @throws InvalidCredentialsException if username or password are either
     *      {@code null} or empty
     * @throws AuthenticationException if a {@code User} with the provided username
     *      and password does not exist
     */
    public User authenticate(String username, String password){
        logger.info("Authenticating user in database with username: " + username);
        if (username == null || username.trim().equals("") || password == null || password.trim().equals("")){
            logger.error("Invalid credentials provided.");
            throw new InvalidCredentialsException("Invalid credentials provided");
        }
        password = passHash(password);

        User u = userRepo.getAUserByUsernameAndPassword(username,password).orElse(null);
        if (u == null) {
            logger.info("Username or password provided were wrong");
            throw new AuthenticationException("Could not find username/password combination in database.");
        }
        logger.info("Authentication successful!");
        return u;
    }

    /**
     * Register a new user in the database. Validates the {@code User} object first
     * @param newUser completed user object
     * @throws InvalidColumnException if {@code newUser} is not valid for persistence
     * @throws InvalidCredentialsException if username or email is already in use
     */
    public void register(User newUser) {
        logger.info("Registering new user:\n\t " + newUser.toString());
        if (!isUserValid(newUser)) {
            logger.error("Invalid user field values provided during registration!");
            throw new InvalidColumnException("Invalid user field values provided during registration!");
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

        if (newUser.getUserRole() == null) {
            newUser.setUserRole(Role.EMPLOYEE);
        }
        setUserPassHash(newUser);
        userRepo.addUser(newUser);
        logger.info("User saved!");
    }

    /**
     * Update a user in the database.
     * @param updatedUser user to update
     * @throws InvalidColumnException if some value in {@code updatedUser} is invalid
     * @throws PersistenceException if there's some issue updating the user in the database
     */
    public void update(User updatedUser) {
        logger.info("Updating user in database:\n\t" + updatedUser.toString());
        if (!isUserValid(updatedUser)) {
            logger.error("Invalid user field values provided during registration!");
            throw new InvalidColumnException("Invalid user field values provided during registration!");
        }
        User tempUser = userRepo.getAUserByUsername(updatedUser.getUsername()).orElseThrow(PersistenceException::new);
        updatedUser.setUserId(tempUser.getUserId());
        // If password hasn't changed (and doesn't need to be hashed)
        if (tempUser.getPassword() == updatedUser.getPassword()){
            if (!userRepo.updateAUser(updatedUser)){
                logger.error("There was a problem trying to update the user");
                throw new PersistenceException("There was a problem trying to update the user");
            }
        } // If new password (or unhashed password), hash password before updating
        else {
            setUserPassHash(updatedUser);
            if (!userRepo.updateAUser(updatedUser)){
                logger.error("There was a problem trying to update the user");
                throw new PersistenceException("There was a problem trying to update the user");
            }
        }

        logger.info("Update succesful!");
    }

    /**
     * Deletes a user
     * @param id id of user to delete
     * @return {@code true} if the user was deleted in the database
     */
    public boolean deleteUserById(int id) {
        logger.info("Deleting User object from database with id: " + id);
        if (id <= 0){
            logger.error("The provided ID cannot be less than or equal to 0.");
            throw new IllegalIdentifierException("The provided ID cannot be less than or equal to 0.");
        }
        User u = userRepo.getAUserById(id).orElse(null);
        if (u == null) {
            throw new IllegalIdentifierException("A user with the provided ID value is not in the database");
        }
        boolean deleted = userRepo.deleteAUserById(id);
        if (!deleted) {
            throw new PersistenceException("There was an error deleting the given user");
        }
        logger.info("Deletion successful!");
        return deleted;
    }

    /**
     * Method for simple checking of availability of a username
     * @param username username to check
     * @return {@code true} if available, {@code false} otherwise
     */
    public boolean isUsernameAvailable(String username) {
        logger.info("Checking Username availability in database: " + username);
        User user = userRepo.getAUserByUsername(username).orElse(null);
        return user == null;
    }

    /**
     * Method for simple checking of availability of an email
     * @param email
     * @return {@code true} if available, {@code false} otherwise
     */
    public boolean isEmailAvailable(String email) {
        logger.info("Checking email availability in database: " + email);
        User user = userRepo.getAUserByEmail(email).orElse(null);
        return user == null;
    }

    /**
     * Validates that the given user and its fields are valid (not {@code null}
     * or empty strings). Does not perform validation on id or role fields.
     * @param user
     * @return {@code true} if the {@code user} was valid, {@code false} otherwise
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
     * Method used to Hash passwords by taking in the {@code User} and accessing Password field.
     * Sets the password field in {@code user} to the hash of the current password
     * Authored by Lokesh Gupta via HowToDoInJava:
     * https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
     * @param user
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
            logger.error(e.getStackTrace());
        }
    }

    /**
     * Method used to Hash passwords by taking in the password and modifying it.
     * Authored by Lokesh Gupta via HowToDoInJava:
     * https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
     * @param pass the password for a {@code User} object
     * @return the hash of {@code pass} or {@code null} if the algorithm fails
     *      due to being patched out
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
