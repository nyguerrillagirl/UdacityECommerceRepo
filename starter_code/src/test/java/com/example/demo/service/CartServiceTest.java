package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.service.UtilityService.TestUser;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class CartServiceTest {
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private UtilityService utilityService;

	@Test
	void testAddTocart_AuthorizationException() {
		ModifyCartRequest mcr = new ModifyCartRequest();
		mcr.setUsername("SomethingNotInDB");		
		RuntimeException exception = assertThrows(AuthorizationException.class, () -> {
			cartService.addTocart(mcr, "DoesNotMatchUsername");
	    });

		String expectedMessage = "User is not authorized for that resource.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
	}

	@Test
	void testAddToCart_UserNotFoundException() {
		ModifyCartRequest mcr = new ModifyCartRequest();
		mcr.setUsername("SomethingNotInDB");		
		RuntimeException exception = assertThrows(UserNotFoundException.class, () -> {
			cartService.addTocart(mcr, "SomethingNotInDB");
	    });

		String expectedMessage = "The username: SomethingNotInDB not found.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	void testAddToCart_ItemNotFoundException() {
		// Create user for DB
		String username = "Lorraine Figueroa";
		TestUser testUser = utilityService.createLogin(username, true);
		System.out.println("testUser created: " + testUser.toString());
		
		ModifyCartRequest mcr = new ModifyCartRequest();
		mcr.setUsername(username);	
		mcr.setItemId(100L); // bad item
		mcr.setQuantity(2);
		
		RuntimeException exception = assertThrows(ItemNotFoundException.class, () -> {
			cartService.addTocart(mcr, username);
	    });

		String expectedMessage = "The item with id: 100 not found.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	void testRemoveFromCart_AuthorizationException() {
		ModifyCartRequest mcr = new ModifyCartRequest();
		mcr.setUsername("SomethingNotInDB");		
		RuntimeException exception = assertThrows(AuthorizationException.class, () -> {
			cartService.removeFromCart(mcr, "DoesNotMatchUsername");
	    });

		String expectedMessage = "User is not authorized for that resource.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);	
	}	
	
	@Test
	void testRemoveFromCart_UserNotFoundException() {
		ModifyCartRequest mcr = new ModifyCartRequest();
		mcr.setUsername("SomethingNotInDB");		
		RuntimeException exception = assertThrows(UserNotFoundException.class, () -> {
			cartService.removeFromCart(mcr, "SomethingNotInDB");
	    });

		String expectedMessage = "The username: SomethingNotInDB not found.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}	
	
	@Test
	void testRemoveFromCart_ItemNotFoundException() {
		// Create user for DB
		String username = "Lorraine Figueroa";
		TestUser testUser = utilityService.createLogin(username, true);
		System.out.println("testUser created: " + testUser.toString());
		
		ModifyCartRequest mcr = new ModifyCartRequest();
		mcr.setUsername(username);	
		mcr.setItemId(100L); // bad item
		mcr.setQuantity(2);
		
		RuntimeException exception = assertThrows(ItemNotFoundException.class, () -> {
			cartService.removeFromCart(mcr, username);
	    });

		String expectedMessage = "The item with id: 100 not found.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}	
}
