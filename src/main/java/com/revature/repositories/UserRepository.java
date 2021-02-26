package com.revature.repositories;

import com.revature.models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;

import static com.revature.util.AppState.app;

public class UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);

    public UserRepository(){
        super();
    }

    //---------------------------------- CREATE -------------------------------------------- //

    /**
     * A method tho add a new user to the database, hashes passwords before inserting
     * @param newUser the user to be added
     * @return returns true if one and only one row was inserted
     * @throws HibernateException e
     */
    public boolean addUser(User newUser)  {
        logger.info("Adding new user: \n\t" + newUser);
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        Integer valueCheck = null;

        try {
            tx = session.beginTransaction();
            valueCheck = (Integer) session.save(newUser);
            tx.commit();
            logger.info("User saved to database");
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return (valueCheck != null);
    }

    //---------------------------------- READ -------------------------------------------- //

    /**
     * A method to get a list of all Users in the database.
     * @return return a list of User objects.
     * @throws HibernateException e
     */
    @SuppressWarnings("unchecked")
    public List<User> getAllusers() {
        logger.info("Retrieving all users from database");
        List<User> users = new ArrayList<>();
        Transaction tx = null;
        Session session = app.getFactory().openSession();

        try {
            tx = session.beginTransaction();
            users = session.createQuery("FROM User").list();

            tx.commit();
            logger.info("Retrieved users successfully");
        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return users;
    }

    /**
     * A method to get a single User by email
     * @param email the email address to search the DB for
     * @return returns an Optional user
     * @throws HibernateException e
     */
    @SuppressWarnings("unchecked")
    public Optional<User> getAUserByEmail(String email) {
        logger.info("Retrieving user with email: " + email);
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        Optional<User> user = Optional.empty();

        try {
            tx = session.beginTransaction();
            user = session.createQuery("FROM User u where u.email = :email")
                    .setParameter("email", email).stream().findFirst();

            tx.commit();
            logger.info("Retrieved relevant users");
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return user;
    }

    /**
     * A method to get a single User by their unique Username.
     * @param userName Username used to find user.
     * @return Optional object which may or may not contain a User.
     */
    @SuppressWarnings("unchecked")
    public Optional<User> getAUserByUsername(String userName) {
        logger.info("Retrieving user with username: " + userName);
        Optional<User> user = Optional.empty();
        Transaction tx = null;
        Session session = app.getFactory().openSession();

        try {
            tx = session.beginTransaction();
            user = session.createQuery("FROM User u where u.username = :username")
                    .setParameter("username", userName).stream().findFirst();

            tx.commit();
            logger.info("Retrieved relevant users");

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }
        return user;
    }

    /**
     * A method to get a single user by a given username and password
     * @param userName the users username
     * @param password the users password
     * @return Optional object which may or may not contain a User.
     * @throws
     */
    @SuppressWarnings("unchecked")
    public Optional<User> getAUserByUsernameAndPassword(String userName, String password) {
        logger.info("Retrieving user with username: " + userName + 
                        " and password: ******");
        Optional<User> user = Optional.empty();
        Transaction tx = null;
        Session session = app.getFactory().openSession();

        try {
            tx = session.beginTransaction();
            user = session.createQuery("FROM User u where u.username = :username AND u.password = :password")
                    .setParameter("username", userName).setParameter("password", password).stream().findFirst();

            tx.commit();
            logger.info("Retrieved relevant users");
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }
        return user;
    }

    /**
     * A method to get a single user by a given Id
     * @param id the user's id
     * @return Optional object which may or may not contain a User.
     * @throws
     */
    @SuppressWarnings("unchecked")
    public Optional<User> getAUserById(int id) {
        logger.info("Retrieving user with id: " + id);
        Optional<User> user = Optional.empty();
        Transaction tx = null;
        Session session = app.getFactory().openSession();

        try {
            tx = session.beginTransaction();
            user = session.createQuery("FROM User u where u.id = :id")
                    .setParameter("id", id).stream().findFirst();

            tx.commit();
            logger.info("Retrieved relevant users");
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }
        return user;
    }

    //---------------------------------- UPDATE -------------------------------------------- //

    /**
     * A method to add a User to the database.
     * @param newUser User with updated fields to be added.
     * @return
     */
    public boolean updateAUser(User newUser) {
        logger.info("Updating user:\n\t" + newUser);
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        boolean updated = false;

        try {
            tx = session.beginTransaction();

            session.update(newUser);

            updated = true;
            tx.commit();
            logger.info("Updated user");
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }
        return updated;
    }

    //---------------------------------- DELETE -------------------------------------------- //

    /**
     * A method to delete a single User from the database
     * @param userId the ID of the record to be deleted
     * @return returns true if one and only one record is updated
     * @throws HibernateException e
     */
    public boolean deleteAUserById(Integer userId) {
        logger.info("Deleting user with id: " + userId);
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        boolean deleted = false;

        try {
            tx = session.beginTransaction();

            User user = (User) session.get(User.class, userId);
            session.delete(user);

            deleted = true;
            tx.commit();
            logger.info("User deleted");
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return deleted;
    }


}
