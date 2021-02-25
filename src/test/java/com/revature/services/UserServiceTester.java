package com.revature.services;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
        userService = new UserService();
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

}
