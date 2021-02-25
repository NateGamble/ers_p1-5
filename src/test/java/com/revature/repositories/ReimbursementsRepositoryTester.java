package com.revature.repositories;

import java.util.List;

import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;

import org.junit.*;

public class ReimbursementsRepositoryTester {

    private static ReimbursementsRepository repo;

    @Before
    public void classSetup() {
        repo = new ReimbursementsRepository();
    }

    @After
    public void classTeardown() {
        System.out.println("All tests finished");
    }
    
    @Test
    public void testAddReimbursement() {
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setAmount(3.50);
        reimbursement.setAuthorId(2);
        reimbursement.setReimbursementStatus(ReimbursementStatus.PENDING);
        reimbursement.setReimbursementType(ReimbursementType.FOOD);
        repo.addReimbursement(reimbursement);
    }

    @Test
    public void testGetAll() {
        List<Reimbursement> rs = repo.getAllReimbursements();
        rs.forEach(System.out::println);
    }

    @Test
    public void testUpdate_withReimbursement() {
        
    }

}
