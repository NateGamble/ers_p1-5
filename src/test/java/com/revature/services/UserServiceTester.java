package com.revature.services;
import com.revature.exceptions.InvalidCredentialsException;
import com.revature.exceptions.PersistenceException;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

    User testUser = new User("TestUser", "TestPass", "Tester",
            "McTesterson", "TestEmail@Gmail.com");
    User fullTest = new User(1, "FullTester", "BetterPass", "Full",
            "Tester", "Bigger@Gmail.com", 1, true);
    User emptyTester = new User();
    List<User> users;

    @InjectMocks
    UserService userService = new UserService();

    @Mock
    UserRepository userRepository;

    @Before
    public void testSetUp() {
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
        when(userRepository.getAUserByUsernameAndPassword("TestUser","TestPass"))
                .thenReturn(Optional.of(testUser));

        //Act
        User user = userService.authenticate("TestUser", "TestPass");

        //Assert
        Assert.assertEquals(user, testUser);
    }

    @Test(expected = PersistenceException.class)
    public void test_authenticate_invalid(){
        //Arrange
        when(userRepository.getAUserByUsernameAndPassword("TestUser","TestPass"))
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
    public void test_register_existingUser_notPresent(){
        //Arrange
        User user = null;
        when(userRepository.getAUserByUsername("TestUser"))
                .thenReturn(Optional.of(testUser));

        //Act
        userService.register(testUser);
    }



}
