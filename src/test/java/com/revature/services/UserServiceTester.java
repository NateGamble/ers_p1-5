package com.revature.services;
import com.revature.exceptions.InvalidColumnException;
import com.revature.exceptions.InvalidCredentialsException;
import com.revature.exceptions.PersistenceException;
import com.revature.models.Role;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTester {


    static User fullTest = new User(1, "FullTester", "BetterPass", "Full",
            "Tester", "Bigger@Gmail.com", Role.ADMIN, true);
    static User emptyTester = new User();
    static List<User> users;
    static User testUser;

    @InjectMocks
    UserService userService = UserService.getInstance();

    @Mock
    UserRepository userRepository;

    @Before
    public void userSetup() {
        testUser = new User("TestUser", "TestPass", "Tester",
                "McTesterson", "TestEmail@Gmail.com");
    }

    @BeforeClass
    public static void testSetUp() {
        users = new ArrayList<>();
        users.add(testUser);
        users.add(fullTest);
        users.add(emptyTester);
    }

    @After
    public void testTearDown() { userService = null; }

    @Test
    public void test_getAllUsers(){
        //Arrange
        when(userRepository.getAllusers()).thenReturn(users);

        //Act
        List myUsers = userService.getAllUsers();

        //Assert
        Assert.assertEquals(myUsers, users);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void test_Authentication_null_user(){
        //Arrange
        String username = null;
        String password = "pass";

        //Act
        userService.authenticate(username, password);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void test_Authentication_trim_user(){
        //Arrange
        String username = "";
        String password = "pass";

        //Act
        userService.authenticate(username, password);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void test_Authentication_null_password(){
        //Arrange
        String username = "user";
        String password = null;

        //Act
        userService.authenticate(username, password);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void test_Authentication_trim_pass(){
        //Arrange
        String username = "user";
        String password = "";

        //Act
        userService.authenticate(username, password);
    }

    @Test
    public void test_authenticate_valid(){
        //Arrange
        userService.setUserPassHash(testUser);
        when(userRepository.getAUserByUsernameAndPassword("TestUser",testUser.getPassword()))
                .thenReturn(Optional.of(testUser));

        //Act
        User user = userService.authenticate("TestUser", "TestPass");

        //Assert
        Assert.assertEquals(user, testUser);
    }

    @Test(expected = PersistenceException.class)
    public void test_authenticate_invalid(){
        //Arrange
        when(userRepository.getAUserByUsernameAndPassword("TestUser", userService.passHash("TestPass")))
                .thenReturn(Optional.empty());

        //Act
        User user = userService.authenticate("TestUser", "TestPass");
    }

    @Test(expected = InvalidCredentialsException.class)
    public void test_register_invalidUser(){
        //Arrange
        User user = null;

        //Act
        userService.register(user);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void test_register_existingUser_isPresent(){
        //Arrange
        User user = null;
        when(userRepository.getAUserByUsername(testUser.getUsername()))
                .thenReturn(Optional.of(testUser));

        //Act
        userService.register(testUser);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void test_register_existingEmail_isPresent(){
        //Arrange
        when(userRepository.getAUserByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));

        //Act
        userService.register(testUser);
    }

    @Test
    public void test_register_valid(){
        userService.register(fullTest);
    }

    @Test(expected = InvalidColumnException.class)
    public void test_update_invalidUser(){
        //Arrange
        User user = null;

        //Act
        userService.update(user);
    }

    @Test(expected = PersistenceException.class)
    public void test_update_invalidUpdate(){
        //Arrange
        when(userRepository.updateAUser(testUser))
                .thenReturn(false);
        when(userRepository.getAUserById(testUser.getUserId()))
                .thenReturn(Optional.of(testUser));

        //Act
        userService.update(testUser);
    }

    @Test
    public void test_update_validUpdate(){
        //Arrange
        when(userRepository.updateAUser(testUser))
                .thenReturn(true);
        when(userRepository.getAUserById(testUser.getUserId()))
                .thenReturn(Optional.of(testUser));

        //Act
        userService.update(testUser);
    }

    @Test(expected = IllegalIdentifierException.class)
    public void test_delete_InvalidID(){
        //Act
        userService.deleteUserById(0);
    }

    @Test(expected = IllegalIdentifierException.class)
    public void test_delete_InvalidID_lessThanZero(){
        //Act
        userService.deleteUserById(-1);
    }

    @Test
    public void test_delete_valid(){
        //Arrange
        when(userRepository.deleteAUserById(fullTest.getUserId())).thenReturn(true);

        //Act
        boolean validity = userService.deleteUserById(fullTest.getUserId());

        //Assert
        Assert.assertEquals(validity,true);
    }

    @Test
    public void test_delete_invalid(){
        //Arrange
        when(userRepository.deleteAUserById(fullTest.getUserId())).thenReturn(false);

        //Act
        boolean validity = userService.deleteUserById(fullTest.getUserId());

        //Assert
        Assert.assertEquals(validity,false);
    }

    @Test
    public void test_isUserAvailable_available(){
        //Arrange
        when(userRepository.getAUserByUsername(testUser.getUsername())).thenReturn(Optional.empty());

        //Act
        boolean validity = userService.isUsernameAvailable(testUser.getUsername());

        //Assert
        Assert.assertEquals(validity,true);
    }

    @Test
    public void test_isUserAvailable_unavailable(){
        //Arrange
        when(userRepository.getAUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        //Act
        boolean validity = userService.isUsernameAvailable(testUser.getUsername());

        //Assert
        Assert.assertEquals(validity,false);
    }

    @Test
    public void test_isEmailAvailable_available(){
        //Arrange
        when(userRepository.getAUserByEmail(testUser.getEmail())).thenReturn(Optional.empty());

        //Act
        boolean validity = userService.isEmailAvailable(testUser.getEmail());

        //Assert
        Assert.assertEquals(validity,true);
    }

    @Test
    public void test_isEmailAvailable_unavailable(){
        //Arrange
        when(userRepository.getAUserByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        //Act
        boolean validity = userService.isEmailAvailable(testUser.getEmail());

        //Assert
        Assert.assertEquals(validity,false);
    }

    @Test
    public void test_isHashingProperly(){
        //Arrange
        String testPass = testUser.getPassword();

        //Act
        testPass = userService.passHash(testPass);

        //Assert
        Assert.assertEquals(testPass, userService.passHash(testUser.getPassword()));
    }

    @Test
    public void test_hashIsntOriginalPassword(){
        //Arrange
        String testPass = testUser.getPassword();

        //Act
        testPass = userService.passHash(testPass);

        //Assert
        Assert.assertNotEquals(testPass, testUser.getPassword());
    }

    @Test
    public void test_isUserHashingProperly(){
        //Arrange
        User fakeUser = testUser;

        //Act
        userService.setUserPassHash(fakeUser);
        userService.setUserPassHash(testUser);

        //Assert
        Assert.assertEquals(testUser, fakeUser);
    }

    @Test
    public void test_hashUserIsntOriginalPassword(){
        //Arrange
        String fakePass = testUser.getPassword();

        //Act
        userService.setUserPassHash(testUser);

        //Assert
        Assert.assertNotEquals(testUser.getPassword(), fakePass);
    }

    @Test
    public void test_checkHashChangesSame(){
        //Arrange
        User fakeUser = testUser;
        String fakePass = testUser.getPassword();

        //Act
        userService.setUserPassHash(fakeUser);
        fakePass = userService.passHash(fakePass);

        //Assert
        Assert.assertEquals(fakePass,fakeUser.getPassword());
    }

    @Test
    public void test_updateDifferentPassword(){
        //Arrange
        when(userRepository.getAUserById(testUser.getUserId()))
                .thenReturn(Optional.of(fullTest));
        String testPass = userService.passHash(testUser.getPassword());
        when(userRepository.updateAUser(testUser))
                .thenReturn(true);

        //Act
        userService.update(testUser);
    }


}
