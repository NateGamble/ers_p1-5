package com.revature.repositories;

import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;

import static com.revature.util.AppState.app;

import com.revature.services.ReimbursementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * A class to interact with the database to CRUD reimbursement objects
 */
public class ReimbursementsRepository {
    private static final Logger logger = LogManager.getLogger(ReimbursementService.class);


    public ReimbursementsRepository(){
        super();
    }

    //---------------------------------- CREATE -------------------------------------------- //
    /**
     * Adds a reimburement to the database
     * @param reimbursement the reimbursement to be added to the DB
     * @throws SQLException e
     * @throws IOException e
     */
    public boolean addReimbursement(Reimbursement reimbursement) {
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        logger.info("Add Reimbursement to database: \n\t" + reimbursement);

        try {
            tx = session.beginTransaction();
            session.save(reimbursement);

            tx.commit();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
            if (tx!= null) tx.rollback();
        } finally {
            session.close();
        }

        return reimbursement.getId() != null;
    }

    //---------------------------------- READ -------------------------------------------- //

    @SuppressWarnings("unchecked")
    public List<Reimbursement> getAllReimbursements() {
        List<Reimbursement> reimbursements = null;
        Session session = app.getFactory().openSession();
        logger.info("Getting all Reimbursements from database.");

        try {
            reimbursements = session.createQuery("FROM Reimbursement").list();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return reimbursements;
    }

    @SuppressWarnings("unchecked")
    public List<Reimbursement> getAllReimbSetByStatus(ReimbursementStatus status) {
        List<Reimbursement> reimbursements = null;
        Session session = app.getFactory().openSession();
        logger.info("Getting all Reimbursements by Status from database: \n\t" + status.toString());

        try {
            String hql = "FROM Reimbursement r where r.reimbursementStatus = :status";
            // System.out.println("Reimbursement status passed in is: " + status);
            reimbursements = session.createQuery(hql)
                                        .setParameter("status", status)
                                        .list();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return reimbursements;
    }

    /**
     * A method to get Reimbursements by the id of the reimbursement itself
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @return returns an Optional Reimbursement object
     */
    @SuppressWarnings("unchecked")
    public Optional<Reimbursement> getAReimbByReimbId(Integer reimbId) {
        Optional<Reimbursement> reimbursement = Optional.empty();
        logger.info("Getting a Reimbursements by Id from database: \n\t" + reimbId);

        Session session = app.getFactory().openSession();

        try {
            String hql = "FROM Reimbursement r where r.id = :id";
            reimbursement = session.createQuery(hql)
                            .setParameter("id", reimbId)
                            .stream()
                            .findFirst();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return reimbursement;
    }

    /**
     * A method to get all of the records for an author given their id
     * @param authorId the ID of the author of the reimbursement
     * @return a set of reimbursements
     */
    @SuppressWarnings("unchecked")
    public List<Reimbursement> getAllReimbSetByAuthorId(Integer authorId) {
        List<Reimbursement> reimbursements = null;
        Session session = app.getFactory().openSession();
        logger.info("Getting a Reimbursements by AuthorId from database: \n\t" + authorId);

        try {
            String hql = "FROM Reimbursement r where r.authorId = :authorId";
            // System.out.println("Reimbursement id passed in is: " + authorId);
            reimbursements = session.createQuery(hql)
                            .setParameter("authorId", authorId)
                            .list();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return reimbursements;
    }

    /**
     * A method to get all of the records for an author given their id and filter by status
     * @param authorId the ID of the author of the reimbursement
     * @param reStat the status that the reimbursement is to be set to
     * @return a set of reimbursements
     */
    @SuppressWarnings("unchecked")
    public List<Reimbursement> getAllReimbSetByAuthorIdAndStatus(Integer authorId, ReimbursementStatus reStat) {
        List<Reimbursement> reimbursements = null;
        Session session = app.getFactory().openSession();
        logger.info("Getting a Reimbursements by AuthorId and Status from database: \n\t"
                + "AuthorId: " + authorId + " Reimb Status: " + reStat.toString());

        try {
            String hql = "FROM Reimbursement r where r.authorId = :authorId"
                            + " and r.reimbursementStatus = :status";
            // System.out.println("Reimbursement id passed in is: " + authorId);
            reimbursements = session.createQuery(hql)
                            .setParameter("authorId", authorId)
                            .setParameter("status", reStat)
                            .list();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return reimbursements;
    }

    /**
     * A method to get all of the records for an author given their id and filter by type
     * @param authorId ID of the Author User
     * @param reType the Type to update the record to
     * @return a set of reimbursements
     */
    @SuppressWarnings("unchecked")
    public List<Reimbursement> getAllReimbSetByAuthorIdAndType(Integer authorId, ReimbursementType reType) throws SQLException {
        List<Reimbursement> reimbursements = null;
        Session session = app.getFactory().openSession();
        logger.info("Getting a Reimbursements by AuthorId and Type from database \n\t"
                + "AuthorId: " + authorId + " Reimb Type: " + reType.toString());

        try {
            String hql = "FROM Reimbursement r where r.authorId = :authorId"
                            + " and r.reimbursementType = :type";
            reimbursements = session.createQuery(hql)
                            .setParameter("authorId", authorId)
                            .setParameter("type", reType)
                            .list();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return reimbursements;
    }

    @SuppressWarnings("unchecked")
    public List<Reimbursement> getAllReimbSetByType(ReimbursementType type)  {
        List<Reimbursement> reimbursements = null;
        Session session = app.getFactory().openSession();
        logger.info("Getting a Reimbursements by Type from database: \n\t" + type);

        try {
            String hql = "FROM Reimbursement r where r.reimbursementType = :type";
            reimbursements = session.createQuery(hql)
                            .setParameter("type", type)
                            .list();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return reimbursements;
    }

    /**
     * A method to get all of the records for a resolver given their id
     * @param resolverId ID of the Resolver User
     * @return a set of reimbursements
     */
    @SuppressWarnings("unchecked")
    public List<Reimbursement> getAllReimbSetByResolverId(Integer resolverId) {
        List<Reimbursement> reimbursements = null;
        Session session = app.getFactory().openSession();
        logger.info("Getting a Reimbursements by ResolverId from database: \n\t" + resolverId);

        try {
            String hql = "FROM Reimbursement r where r.resolverId = :resolverId";
            reimbursements = session.createQuery(hql)
                            .setParameter("resolverId", resolverId)
                            .list();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return reimbursements;
    }

    /**
     * A method to get all of the records for a resolver given their id and filter by status
     * @param resolverId  ID of the Resolver User
     * @param reStat the status to update the record to
     * @return a set of reimbursements
     */
    @SuppressWarnings("unchecked")
    public List<Reimbursement> getAllReimbSetByResolverIdAndStatus(Integer resolverId, ReimbursementStatus reStat) {
        List<Reimbursement> reimbursements = null;
        Session session = app.getFactory().openSession();
        logger.info("Getting a Reimbursements by ResolverId and Status from database: \n\t" +
                "ResolverId: " + resolverId + " Reimb Status: " + reStat);

        try {
            String hql = "FROM Reimbursement r where r.resolverId = :resolverId"
                            + " and r.reimbursementStatus = :status";
            reimbursements = session.createQuery(hql)
                            .setParameter("resolverId", resolverId)
                            .setParameter("status", reStat)
                            .list();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return reimbursements;
    }

    /**
     * A  method to get all of the records for a resolver given their id and filter by type
     * @param resolverId ID of the Resolver User
     * @param reType type of Reimbursements to select by
     * @return a set of reimbursements
     */
    @SuppressWarnings("unchecked")
    public List<Reimbursement> getAllReimbSetByResolverIdAndType(Integer resolverId, ReimbursementType reType) {
        List<Reimbursement> reimbursements = null;
        Session session = app.getFactory().openSession();
        logger.info("Getting a Reimbursements by ResolverId and Type from database: \n\t" +
                "ResolverId: " + resolverId + " Type: " + reType);

        try {
            String hql = "FROM Reimbursement r where r.resolverId = :resolverId"
                            + " and r.reimbursementType = :type";
            reimbursements = session.createQuery(hql)
                            .setParameter("resolverId", resolverId)
                            .setParameter("type", reType)
                            .list();

        } catch (HibernateException e) {
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return reimbursements;
    }

    //---------------------------------- UPDATE -------------------------------------------- //
    /**
     * 
     * @param reimb
     * @return
     */
    public boolean updateEMP(Reimbursement reimb) {
        boolean updated = false;
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        logger.info("Updating Reimbursement from database: \n\t" + reimb);

        try {
            tx = session.beginTransaction();
            session.update(reimb);
            tx.commit();
            updated = true;
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return updated;
    }

    /**
     * Updates the resolverId and status of a reimbursement using
     * the reimbursement id.
     * @param resolverId
     * @param statusId
     * @param reimbId
     * @return {@code true} if the up {@code false} otherwise
     */
    public boolean updateFIN(Integer resolverId, Integer statusId, Integer reimbId) {
        Reimbursement reimbursement = null;
        boolean updated = false;
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        logger.info("Updating Reimbursement from database by FIN: \n\t"
                + "ResolverId: " + resolverId + " StatusId: " + statusId + " ReimbId: " + reimbId);

        try {
            tx = session.beginTransaction();
            reimbursement = (Reimbursement) session.get(Reimbursement.class, reimbId);
            reimbursement.setResolver(session.get(User.class, resolverId));
            reimbursement.setReimbursementStatus(ReimbursementStatus.getByNumber(statusId));
            session.save(reimbursement);
            tx.commit();
            updated = true;
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return updated;
    }

    /**
     * A method to update only the resolved timestamp by the id of the reimbursement
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param timestamp an SQL timestamp object to set the time resolved to
     * @return returns {@code true} if the up {@code false} otherwise
     */
    public boolean updateResolvedTimeStampByReimbId(Integer reimbId, Timestamp timestamp) {
        Reimbursement reimbursement = null;
        boolean updated = false;
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        logger.info("Updating Reimbursement from database with Timestamp and Reimb Id: \n\t"
                + " Timestamp: " + timestamp + " ReimbId: " + reimbId);

        try {
            tx = session.beginTransaction();
            reimbursement = (Reimbursement) session.get(Reimbursement.class, reimbId);
            reimbursement.setResolved(timestamp);
            session.save(reimbursement);
            tx.commit();
            updated = true;
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return updated;
    }

    /**
     * A method to update only the resolver ID by the id of the reimbursement
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param resolverId the ID of the user that resolves the record to update the record to
     * @return returns {@code true} if the update was successful
     */
    public boolean updateResolverIdByReimbId(Integer reimbId, Integer resolverId) {
        Reimbursement reimbursement = null;
        boolean updated = false;
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        logger.info("Updating Reimbursement from database with Resolver Id and Reimb Id: \n\t"
                + " Resolver Id: " + resolverId + " ReimbId: " + reimbId);

        try {
            tx = session.beginTransaction();
            reimbursement = (Reimbursement) session.get(Reimbursement.class, reimbId);
            reimbursement.setResolver(session.get(User.class, resolverId));
            session.save(reimbursement);
            tx.commit();
            updated = true;
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return updated;
    }

    /**
     * A method to update only the Reimbursement TYPE by the id of the Reimbursement
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param reimbursementType the type to update the record to
     * @return returns {@code true} if the up {@code false} otherwise
     */
    public boolean updateReimbursementTypeByReimbId(Integer reimbId, ReimbursementType reimbursementType) {
        Reimbursement reimbursement = null;
        boolean updated = false;
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        logger.info("Updating Reimbursement from database with Reimb Type and Reimb Id: \n\t"
                + " Reimb Type: " + reimbursementType + " ReimbId: " + reimbId);

        try {
            tx = session.beginTransaction();
            reimbursement = (Reimbursement) session.get(Reimbursement.class, reimbId);
            reimbursement.setReimbursementType(reimbursementType);
            session.save(reimbursement);
            tx.commit();
            updated = true;
        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            logger.error("Something with Hibernate went wrong");
            logger.error(e.getStackTrace());
        } finally {
            session.close();
        }

        return updated;
    }

    /**
     * A method to update the status of a reimbursement in the database
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param newReimbStatus the status to update the record to
     * @return returns {@code true} if the up {@code false} otherwise
     */
    public boolean updateReimbursementStatusByReimbId(Integer reimbId, ReimbursementStatus newReimbStatus) {
        Reimbursement reimbursement = null;
        boolean updated = false;
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        logger.info("Updating Reimbursement from database with Reimb Status and Reimb Id: \n\t"
                + " Reimb Status: " + newReimbStatus + " ReimbId: " + reimbId);

        try {
            tx = session.beginTransaction();
            reimbursement = (Reimbursement) session.get(Reimbursement.class, reimbId);

            reimbursement.setReimbursementStatus(newReimbStatus);
            session.save(reimbursement);
            tx.commit();
            updated = true;
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
     * A method to delete a single Reimbursement from the database
     * @param reimbId the ID of the record to be deleted
     * @return returns true if one and only one record is updated
     */
    public boolean delete(Integer reimbId) {
        Transaction tx = null;
        Session session = app.getFactory().openSession();
        boolean deleted = false;
        logger.info("Deleting Reimbursement from database with Id: " + reimbId);

        try {
            tx = session.beginTransaction();

            Reimbursement reimbursement = (Reimbursement) session.get(Reimbursement.class, reimbId);
            session.delete(reimbursement);
            tx.commit();
            deleted = true;

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
