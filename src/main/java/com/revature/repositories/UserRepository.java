package com.revature.repositories;

import com.revature.models.Reimbursement;
import com.revature.models.User;
import com.revature.util.ConnectionFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;

import static com.revature.util.AppState.app;

public class UserRepository {

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
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        Integer valueCheck = null;

        try {
            tx = session.beginTransaction();
            valueCheck = (Integer) session.save(newUser);

            System.out.println();

            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
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
    public List<User> getAllusers() {
        List<User> users = new ArrayList<>();
        Transaction tx = null;
        Session session = app.getFactory().openSession();

        try {
            tx = session.beginTransaction();
            users = session.createQuery("FROM User").list();

            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
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
    public Optional<User> getAUserByEmail(String email) {
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        Optional<User> user = Optional.empty();

        try {
            tx = session.beginTransaction();
            user = session.createQuery("FROM User u where u.email = :email")
                    .setParameter("email", email).stream().findFirst();

            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
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
    public Optional<User> getAUserByUsername(String userName) {
        Optional<User> user = Optional.empty();
        Transaction tx = null;
        Session session = app.getFactory().openSession();

        try {
            tx = session.beginTransaction();
            user = session.createQuery("FROM User u where u.username = :username")
                    .setParameter("username", userName).stream().findFirst();

            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
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
    public Optional<User> getAUserByUsernameAndPassword(String userName, String password) {
        Optional<User> user = Optional.empty();
        Transaction tx = null;
        Session session = app.getFactory().openSession();

        try {
            tx = session.beginTransaction();
            user = session.createQuery("FROM User u where u.username = :username AND u.password = :password")
                    .setParameter("username", userName).setParameter("password", password).stream().findFirst();

            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
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
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        boolean updated = false;

        try {
            tx = session.beginTransaction();

            session.update(newUser);

            updated = true;
            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
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
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        boolean deleted = false;

        try {
            tx = session.beginTransaction();

            User user = (User) session.get(User.class, userId);
            session.delete(user);

            deleted = true;
            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return deleted;
    }



//    //---------------------------------- UTIL -------------------------------------------- //
//
//    /**
//     * A method to map the result sets from the users queries
//     * @param rs a result set
//     * @return a set of users
//     * @throws SQLException e
//     */
//    private List<User> mapResultSet(ResultSet rs) throws SQLException {
//        List<User> users = new ArrayList<>();
//        while (rs.next()){
//            User temp = new User();
//            temp.setUserId(rs.getInt("id"));
//            temp.setUsername(rs.getString("username"));
//            temp.setPassword(rs.getString("password"));
//            temp.setEmail(rs.getString("email"));
//            temp.setFirstname(rs.getString("first_name"));
//            temp.setLastname(rs.getString("last_name"));
//            temp.setUserRole(rs.getInt("user_role_id"));
//            users.add(temp);
//        }
//        return users;
//    }


}
