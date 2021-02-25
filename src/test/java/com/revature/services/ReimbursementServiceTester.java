package com.revature.services;

import com.revature.exceptions.EnumOutOfBoundsException;
import com.revature.exceptions.InvalidColumnException;
import com.revature.exceptions.InvalidCredentialsException;
import com.revature.exceptions.PersistenceException;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;
import com.revature.repositories.ReimbursementsRepository;
import com.revature.util.StatusCodeConverter;

import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    static List<Reimbursement> nonEmptyReimbursements;
    
    @InjectMocks
    ReimbursementService service = new ReimbursementService();

    @Mock
    ReimbursementsRepository repo;

    @BeforeClass
    public static void classSetup() {
        validUser = new User(1, "FullTester", "BetterPass", "Full",
        "Tester", "Bigger@Gmail.com", 1, true);
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
        nonEmptyReimbursements = new ArrayList<>();
        nonEmptyReimbursements.add(minimumReimbursement);
        nonEmptyReimbursements.add(fullReimbursement);
    }

    @AfterClass
    public static void teardown() {
        System.out.println("All tests finished");
    }

    @Test
    public void testGetAll() {
        // Arrange
        when(repo.getAllReimbursements()).thenReturn(reimbursements);

        // Act
        List myReimbursements = service.getAllReimb();

        // Assert
        assertEquals(reimbursements, myReimbursements);
    }

    @Test
    public void testGetReimbursementByUserId_withValidId() {
        // Arrange
        when(repo.getAllReimbSetByAuthorId(validUser.getUserId())).thenReturn(nonEmptyReimbursements);

        // Act
        List myReimbursements = service.getReimbByUserId(validUser.getUserId());

        // Assert
        assertEquals(nonEmptyReimbursements, myReimbursements);
    }

    @Test(expected = IllegalIdentifierException.class)
    public void testGetReimbursementByUserId_withInvalidId() {
        // Act
        List myReimbursements = service.getReimbByUserId(0);
    }

    @Test
    public void testGetReimbursementByType_withValidTypeId() {
        // Arrange
        List<Reimbursement> expectedReimbursements = new ArrayList<Reimbursement>();
        expectedReimbursements.add(fullReimbursement);
        when(repo.getAllReimbSetByType(ReimbursementType.OTHER)).thenReturn(expectedReimbursements);

        // Act
        List myReimbursements = service.getReimbByType(4);

        // Assert
        assertEquals(expectedReimbursements, myReimbursements);
    }

    @Test(expected = EnumOutOfBoundsException.class)
    public void testGetReimbursementByType_withInvalidTypeId() {
        service.getReimbByType(15);
    }

    @Test
    public void testGetReimbursementByType_withValidStatusId() {
        // Arrange
        List<Reimbursement> expectedReimbursements = new ArrayList<Reimbursement>();
        expectedReimbursements.add(fullReimbursement);
        when(repo.getAllReimbSetByStatus(ReimbursementStatus.APPROVED)).thenReturn(expectedReimbursements);

        // Act
        List myReimbursements = service.getReimbByStatus(2);

        // Assert
        assertEquals(expectedReimbursements, myReimbursements);
    }

    @Test(expected = EnumOutOfBoundsException.class)
    public void testGetReimbursementByType_withInvalidStatusId() {
        service.getReimbByStatus(15);
    }

    @Test
    public void testSave_withValidReimbursement() {
        // Arrange
        when(repo.addReimbursement(minimumReimbursement)).thenReturn(true);

        // Act
        service.save(minimumReimbursement);
    }

    @Test(expected = InvalidColumnException.class)
    public void testSave_withInvalidReimbursement() {
        service.save(emptyReimbursement);
    }

    @Test(expected = PersistenceException.class)
    public void testSave_withReimbursementNotPersisted() {
        // Arrange
        when(repo.addReimbursement(minimumReimbursement)).thenReturn(false);

        // Act
        service.save(minimumReimbursement);
    }

    @Test
    public void testUpdateEMP_withValidReimbursement() {
        // Arrange
        Reimbursement updated = minimumReimbursement;
        updated.setDescription("updated description");
        when(repo.updateEMP(updated)).thenReturn(true);

        // Act
        service.updateEMP(updated);
    }

    @Test(expected = InvalidColumnException.class)
    public void testUpdateEMP_withInvalidReimbursement() {
        // Arrange
        Reimbursement updated = emptyReimbursement;
        updated.setDescription("updated description");

        // Act
        service.updateEMP(updated);
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateEMP_withReimbursementNotPersisted() {
        // Arrange
        Reimbursement updated = minimumReimbursement;
        updated.setDescription("updated description");
        when(repo.updateEMP(updated)).thenReturn(false);

        // Act
        service.updateEMP(updated);
    }

    @Test
    public void testApprove_withValidInputs() {
        // Arrange
        int statusCode = new StatusCodeConverter().convertToDatabaseColumn(ReimbursementStatus.APPROVED);
        when(repo.updateFIN(fullReimbursement.getId(), statusCode, validUser.getUserId())).thenReturn(true);

        // Act
        service.approve(fullReimbursement.getId(), validUser.getUserId());
    }

    @Test(expected = IllegalIdentifierException.class)
    public void testApprove_withInvalidInputs() {
        User invalidUser = new User("TestUser", "TestPass", "Tester",
        "McTesterson", "TestEmail@Gmail.com");
        service.approve(fullReimbursement.getId(), invalidUser.getUserId());
    }

    @Test(expected = PersistenceException.class)
    public void testApprove_withReimbursementNotPersisted() {
        // Arrange
        int statusCode = new StatusCodeConverter().convertToDatabaseColumn(ReimbursementStatus.APPROVED);
        when(repo.updateFIN(fullReimbursement.getId(), statusCode, validUser.getUserId())).thenReturn(false);

        // Act
        service.approve(fullReimbursement.getId(), validUser.getUserId());
    }
    
}
