package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.persistence.User;
import com.example.demo.service.UtilityService.TestUser;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class UserServiceTest {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UtilityService utilityService;

	@Test
	void testPrepareNewUser_DuplicateUsernameException() {
		// Insert a user into the database
		String username = "Lorraine Figueroa";
		TestUser testUser = utilityService.createLogin(username, true);
		System.out.println("testUser created: " + testUser.toString());
		// Try to sign/register the same user again		
		RuntimeException exception = assertThrows(DuplicateUsernameException.class, () -> {
			userService.prepareNewUser(username);
	    });
		
		String expectedMessage = "User with username: " + username + " already exists.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
	}

	@Test
	void testIsPasswordValid_TooShort() {
		String password = "figgy";
		assertFalse(userService.isPasswordValid(password, password));
	}
	
	@Test
	void testIsPasswordValid_DontMatch() {
		String password = "ThisIsLongEnough!";
		String confirmedPassword = "figgy";
		assertFalse(userService.isPasswordValid(password, confirmedPassword));
	}

	@Test
	void testFindById() {
		RuntimeException exception = assertThrows(UserNotFoundException.class, () -> {
			userService.findById(1000L);
	    });
		
		String expectedMessage = "User with id: 1000 not found.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	void testValidateUserIdMatchesAuth_IsFalse() {
		// Create a user
		TestUser testUser = utilityService.createLogin(UtilityService.username5, true);
		User user = testUser.getUser();
		
		assertFalse(userService.validateUserIdMatchesAuth(user.getId(), "figgy"));
	}


}
