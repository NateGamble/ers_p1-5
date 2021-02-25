package com.revature.services;

import com.revature.exceptions.InvalidCredentialsException;
import com.revature.exceptions.PersistenceException;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;
import com.revature.repositories.ReimbursementsRepository;
import com.revature.repositories.UserRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ReimbursementServiceTester {
    static Reimbursement minimumReimbursement;
    static Reimbursement fullReimbursement;
    static Reimbursement emptyReimbursement;
    static User validUser;
    static List<Reimbursement> reimbursements;
    
    @InjectMocks
    ReimbursementService service = new ReimbursementService();

    @Mock
    ReimbursementsRepository repo;

    @BeforeClass
    public static void classSetup() {
        validUser = new User("TestUser", "TestPass", "Tester",
        "McTesterson", "TestEmail@Gmail.com");
        minimumReimbursement = new Reimbursement(3.50, "pizza party",
                                    validUser, ReimbursementStatus.PENDING, ReimbursementType.FOOD);
        fullReimbursement = new Reimbursement(1, 8.99, 
                                    new Timestamp(2020, 12, 31, 10, 30, 0, 0),
                                    new Timestamp(2021, 1, 1, 10, 30, 0, 0),
                                    "full", validUser, validUser,
                                    ReimbursementStatus.APPROVED, ReimbursementType.OTHER);
        emptyReimbursement = new Reimbursement();
        reimbursements = new ArrayList<>();
        reimbursements.add(minimumReimbursement);
        reimbursements.add(fullReimbursement);
        reimbursements.add(emptyReimbursement);
    }

    @AfterClass
    public static void teardown() {
        System.out.println("All tests finished");
    }

    @Test
    public void testGetAll() {
        when(repo.getAllReimbursements()).thenReturn(reimbursements);

        List myReimbursements = service.getAllReimb();

        assertEquals(reimbursements, myReimbursements);
    }
    
}
