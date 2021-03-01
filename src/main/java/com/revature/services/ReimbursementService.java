package com.revature.services;

import com.revature.exceptions.AuthenticationException;
// import com.revature.dtos.RbDTO;
import com.revature.exceptions.EnumOutOfBoundsException;
import com.revature.exceptions.InvalidColumnException;
import com.revature.exceptions.PersistenceException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;
import com.revature.repositories.ReimbursementsRepository;
import com.revature.util.StatusCodeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.util.List;

/**
 * Service layer for validating reimbursements before sending to or from the Database
 */
public class ReimbursementService {
    private static final Logger logger = LogManager.getLogger(ReimbursementService.class);
    private ReimbursementsRepository reimbRepo = new ReimbursementsRepository();
    private static final ReimbursementService service = new ReimbursementService();

    private ReimbursementService() {
        super();
    }

    public static ReimbursementService getInstance() {
        return service;
    }

    /**
     * Gets all reimbursements from the DataBase
     * @return A list of {@code Reimbursement} objects
     */
    public List<Reimbursement> getAllReimb(){
        logger.info("getting all reimbursements items from the database");
        List<Reimbursement> reimbursements = reimbRepo.getAllReimbursements();
        
        return reimbursements;
    }

    /**
     * Gets all reimbursements for a user given their id
     * @param userId id of user
     * @return A list of {@code Reimbursement} objects
     */
    public List<Reimbursement> getReimbByUserId(Integer userId){
        logger.info("Getting reimbursements by user id: " + userId);
        if (userId <= 0){
            logger.error("The provided user id can't be <= 0");
            throw new IllegalIdentifierException("The provided user id cannot be less than or equal to 0. Id provided: " + userId);
        }
        List<Reimbursement> reimb = reimbRepo.getAllReimbSetByAuthorId(userId);
        return reimb;
    }

    /**
     * Gets all reimbursements by a specified type
     * @param typeId ordinal number of the type requested, between 1-4
     * @return A list of {@code Reimbursement} objects
     * @throws EnumOutOfBoundsException if {@code typeId} is less than 1 or greater than 4
     */
    public List<Reimbursement> getReimbByType(Integer typeId){
        logger.info("Getting reimbursement by type: " + ReimbursementType.getByNumber(typeId));
        if (typeId <= 0 || typeId >=5){
            logger.error("Provided enum id is out of bounds");
            throw new EnumOutOfBoundsException("The provided type id must be between 1 and 4 inclusive");
        }
        List<Reimbursement> reimb = reimbRepo.getAllReimbSetByType(ReimbursementType.getByNumber(typeId));
        
        return reimb;
    }

    /**
     * Gets all reimbursements by a specified type
     * @param type a {@code ReimbursementType} to search for
     * @return A list of {@code Reimbursement} objects
     * @throws EnumOutOfBoundsException if {@code type} is {@code null}
     */
    public List<Reimbursement> getReimbByType(ReimbursementType type){
        if (type == null) {
            logger.error("Provided enum is invalid");
            throw new EnumOutOfBoundsException("The provided enum must not be null!");
        }
        logger.info("Getting reimbursements by type: " + type);
        List<Reimbursement> reimb = reimbRepo.getAllReimbSetByType(type);
        
        return reimb;
    }

    /**
     * Gets all reimbursements by a specified id
     * @param reimbId id for a specific Reimbursement
     * @return A {@code Reimbursement} object
     * @throws ResourceNotFoundException if there is no {@code Reimbursement} with the specified id 
     */
    public Reimbursement getReimbById(Integer reimbId){
        if (reimbId <= 0 ){
            throw new IllegalIdentifierException("The reimbursement id cannot be less than or equal to 0. Reimbursement id provided: " + reimbId);
        }

        return reimbRepo.getAReimbByReimbId(reimbId).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * Gets all reimbursements by a specified status
     * @param statusId ordinal number of the type requested, between 1-4
     * @return A list of {@code Reimbursement} objects
     * @throws EnumOutOfBoundsException if {@code statusId} is less than 1 or greater than 4
     */
    public List<Reimbursement> getReimbByStatus(Integer statusId){
        logger.info("Getting reimbursements by status: " + ReimbursementStatus.getByNumber(statusId));
        if (statusId <= 0 || statusId >= 5){
            logger.error("Provided enum id is out of bounds");
            throw new EnumOutOfBoundsException("The provided status id must be between 1 and 4 inclusive");
        }
        List<Reimbursement> reimb = reimbRepo.getAllReimbSetByStatus(ReimbursementStatus.getByNumber(statusId));
        
        return reimb;
    }

    /**
     * Gets all reimbursements by a specified status
     * @param status a {@code ReimbursementStatus} to search for
     * @return A list of {@code Reimbursement} objects
     * @throws EnumOutOfBoundsException if {@code status} is {@code null}
     */
    public List<Reimbursement> getReimbByStatus(ReimbursementStatus status){
        if (status == null) {
            throw new EnumOutOfBoundsException("Status must not be null!");
        }
        logger.info("Getting reimbursements by status: " + status);
        List<Reimbursement> reimb = reimbRepo.getAllReimbSetByStatus(status);
        
        return reimb;
    }

    /**
     * Saves a reimbursement after validation
     * @param reimb the completed {@code Reimbursement} object
     */
    public void save(Reimbursement reimb){
        logger.info("Attempting to save reimbursement:\n\t" + reimb);
        if (!isReimbursementValid(reimb)){
            logger.error("Provided reimbursement is invalid");
            throw new InvalidColumnException("Invalid reimbursement field values provided!");
        }
        if(!reimbRepo.addReimbursement(reimb)){
            logger.error("Provided reimbursement was not saved to database");
            throw new PersistenceException("Something went wrong trying to save this reimbursement");
        }
        logger.info("Reimbursement saved!");
    }

    /**
     * Update a reimbursement
     * @param reimb the updated {@code Reimbursement} object
     */
    public void updateEMP(Reimbursement reimb) {
        logger.info("Attempting to update reimbursement:\n\t" + reimb);
        if (!isReimbursementValid(reimb)){
            logger.error("Provided reimbursement is invalid");
            throw new InvalidColumnException("Invalid reimbursement field values provided!");
        }
        // Change author to actual author so Hibernate doesn't think we need to persist any changes to author
        User u = UserService.getInstance().getUserByUsername(reimb.getAuthor().getUsername());
        reimb.setAuthor(u);
        if(!reimbRepo.updateEMP(reimb)){
            logger.error("Provided reimbursement was not updated in the database");
            throw new PersistenceException("Something went wrong trying to save this reimbursement");
        }
        logger.info("Reimbursement updated!");
    }

    /**
     * Approves a {@code Reimbursement} object
     * @param resolverId the id of the fin manager resolving the reimbursement
     * @param reimbId id of the reimbursement to approve
     * @throws IllegalIdentifierException if the id for the reimbursement or resolver is less than 1
     * @throws PersistenceException if something goes wrong with updating database
     */
    public void approve(Integer resolverId, Integer reimbId) {
        logger.info("Attempting to approve reimbursement with id: " + reimbId +
                    " and resolver id: " + resolverId);
        if (reimbId <= 0 || resolverId <=0){
            logger.error("Invalid reimbursement id or resolver id provided");
            throw new IllegalIdentifierException("Invalid reimbursement id or resolver id provided!");
        }
        int statusCode = new StatusCodeConverter().convertToDatabaseColumn(ReimbursementStatus.APPROVED);
        if(!reimbRepo.updateFIN(resolverId, statusCode, reimbId)) {
            logger.error("Reimbursement was not updated in the database");
            throw new PersistenceException("Something went wrong trying to approve this reimbursement");
        }
    }

    /**
     * Denies a {@code Reimbursement} object
     * @param resolverId the id of the fin manager resolving the reimbursement
     * @param reimbId id of the reimbursement to disapprove
     * @throws IllegalIdentifierException if the id for the reimbursement or resolver is less than 1
     * @throws PersistenceException if something goes wrong with updating database
     */
    public void deny(Integer resolverId, Integer reimbId) {
        logger.info("Attempting to deny reimbursement with id: " + reimbId +
                    " and resolver id: " + resolverId);
        if (reimbId <= 0 || resolverId <=0){
            logger.error("Invalid reimbursement id or resolver id provided");
            throw new IllegalIdentifierException("Invalid reimbursement id or resolver id provided!");
        }
        int statusCode = new StatusCodeConverter().convertToDatabaseColumn(ReimbursementStatus.DENIED);
        if(!reimbRepo.updateFIN(resolverId, statusCode, reimbId)){
            logger.error("Reimbursement was not updated in the database");
            throw new PersistenceException("Something went wrong trying to deny this reimbursement");
        }
    }

    /**
     * Validates feilds of a {@code Reimbursement}
     * @param reimb reimbursement to be validated
     * @return {@code true} if the reimbursement is valid, {@code false} otherwise
     */
    public boolean isReimbursementValid(Reimbursement reimb){
        if (reimb == null) return false;
        if (reimb.getAmount() == null || reimb.getAmount() <= 0 ) return false;
        if (reimb.getDescription() == null || reimb.getDescription().trim().equals("")) return false;
        if (reimb.getAuthor() == null) return false;
        UserService service = UserService.getInstance();
        try {
            service.authenticate(reimb.getAuthor().getUsername(), reimb.getAuthor().getPassword());
        } catch (AuthenticationException e) {
            return false;
        }
        if (reimb.getReimbursementType() == null ) return false;
        if (reimb.getReimbursementStatus() == null) return false;
        return true;
    }


}
