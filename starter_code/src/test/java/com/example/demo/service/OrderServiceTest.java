package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class OrderServiceTest {
	
	public static final String BAD_USER_NAME = "figgy";
	
	@Autowired
	private OrderService orderService;
	
	@Test
	void testSubmitOrder_UserNotFoundException() {
		RuntimeException exception = assertThrows(UserNotFoundException.class, () -> {
			orderService.submitOrder(BAD_USER_NAME, BAD_USER_NAME);
	    });
		
		String expectedMessage = "The username 'figgy' could not be found.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);

	}

	@Test
	void testGetUserOrderHistory_UserNotFoundException() {
		RuntimeException exception = assertThrows(UserNotFoundException.class, () -> {
			orderService.getUserOrderHistory(BAD_USER_NAME, BAD_USER_NAME);
	    });
		
		String expectedMessage = "The username 'figgy' could not be found.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);

	}
	
	@Test
	void testGetUserOrderHistory_AuthorizationException() {
		RuntimeException exception = assertThrows(AuthorizationException.class, () -> {
			orderService.getUserOrderHistory(BAD_USER_NAME, UtilityService.username1);
	    });
		
		String expectedMessage = "The user 'figgy' is not authorized.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
	}
}
