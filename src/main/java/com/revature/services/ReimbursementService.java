package com.revature.services;

// import com.revature.dtos.RbDTO;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.repositories.ReimbursementsRepository;

import org.hibernate.boot.model.naming.IllegalIdentifierException;

import java.util.List;

/**
 * Service layer for validating reimbursements before sending to or from the Database
 */
public class ReimbursementService {
    private final ReimbursementsRepository reimbRepo = new ReimbursementsRepository();

    /**
     * Gets all Reimbursements from the DataBase
     * @return A list of Reimbursement objects
     */
    public List<Reimbursement> getAllReimb(){
        List<Reimbursement> reimbursements = reimbRepo.getAllReimbursements();
        
        return reimbursements;
    }

    /**
     * Gets all reimbursements for a usre given their Id
     * @param userId user id requested
     * @return A list of Reimbursement objects
     */
    public List<Reimbursement> getReimbByUserId(Integer userId){
        if (userId <= 0){
            throw new IllegalIdentifierException("THE PROVIDED USER ID CANNOT BE LESS THAN OR EQUAL TO ZERO");
        }
        List<Reimbursement> reimb = reimbRepo.getAllReimbSetByAuthorId(userId);
        return reimb;
    }

    /**
     * Gets all reimbursements by a specified type
     * @param typeId ordinal number of the type requested, between 1-4
     * @return A list of Reimbursement objects
     */
    public List<Reimbursement> getReimbByType(Integer typeId){
        if (typeId <= 0 || typeId >=5){
            throw new RuntimeException("THE PROVIDED USER ID CANNOT BE LESS THAN OR EQUAL TO ZERO");
        }
        List<Reimbursement> reimb = reimbRepo.getAllReimbSetByType(ReimbursementType.getByNumber(typeId));
        
        return reimb;
    }

    /**
     * Gets all reimbursements by a specified status
     * @param statusId ordinal number of the type requested, between 1-3
     * @return A list of Reimbursement objects
     */
    public List<Reimbursement> getReimbByStatus(Integer statusId){
        if (statusId <= 0 || statusId >= 4){
            throw new RuntimeException("THE PROVIDED USER ID CANNOT BE LESS THAN OR EQUAL TO ZERO");
        }
        List<Reimbursement> reimb = reimbRepo.getAllReimbSetByStatus(ReimbursementStatus.getByNumber(statusId));
        
        return reimb;
    }

    /**
     * Saves a reimbursement after validation
     * @param reimb the completed reimbursement object
     */
    public void save(Reimbursement reimb){
        if (!isReimbursementValid(reimb)){
            throw new RuntimeException("Invalid user field values provided!");
        }
        if(!reimbRepo.addReimbursement(reimb)){
            throw new RuntimeException("Something went wrong trying to save this reimbursement");
        }
        System.out.println(reimb);
    }

    /**
     * Update a reimbursement
     * @param reimb the completed reimbursement object
     */
    public void updateEMP(Reimbursement reimb) {
        if (!isReimbursementValid(reimb)){
            throw new RuntimeException("Invalid user field values provided!");
        }
        if(!reimbRepo.updateEMP(reimb)){
            throw new RuntimeException("Something went wrong trying to save this reimbursement");
        }
        System.out.println(reimb);
    }

    /**
     * Approve a Reimb.
     * @param resolverId the Id of the fin manager resolving the reimb.
     * @param reimbId id of the Reimb. to approve or disapprove.
     */
    public void approve(Integer resolverId, Integer reimbId) {
        if (reimbId <= 0 || resolverId <=0){
            throw new IllegalIdentifierException("Invalid user field values provided!");
        }
        if(!reimbRepo.updateFIN(resolverId, 2, reimbId)){
            throw new RuntimeException("Something went wrong trying to approve this reimbursement");
        }
    }

    /**
     * Deny a reimb.
     * @param resolverId the Id of the fin manager resolving the reimb.
     * @param reimbId id of the Reimb. to approve or disapprove.
     */
    public void deny(Integer resolverId, Integer reimbId) {
        if (reimbId <= 0){
            throw new IllegalIdentifierException("Invalid user field values provided!");
        }
        if(!reimbRepo.updateFIN(resolverId, 3, reimbId)){
            throw new RuntimeException("Something went wrong trying to deny this reimbursement");
        }
    }

    /**
     * Validates feilds of a reimbursement
     * @param reimb reimb. to be validated
     * @return true or false based on fields
     */
    public boolean isReimbursementValid(Reimbursement reimb){
        if (reimb == null) return false;
        if (reimb.getAmount() == null || reimb.getAmount() <= 0 ) return false;
        if (reimb.getDescription() == null || reimb.getDescription().trim().equals("")) return false;
        if (reimb.getAuthorId() <= 0 ) return false;
        if (reimb.getReimbursementType() == null ) return false;
        return true;
    }


}
