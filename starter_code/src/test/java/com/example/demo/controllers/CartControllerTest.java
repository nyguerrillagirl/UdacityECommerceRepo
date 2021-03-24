package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.security.SecurityConstants;
import com.example.demo.service.UserService;
import com.example.demo.service.UtilityService;
import com.example.demo.service.UtilityService.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UtilityService utilityService;
	
	@Autowired
	private UserService userService;
	
	@Test
	public void testAddToCart() throws Exception {
		
		// Create a user
		TestUser testUser = utilityService.createLogin(UtilityService.username2, true);
		String loginBody = testUser.getLoginBody();
		Long id = testUser.getUser().getId();
		
		// Login this user
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		// remember the JWT
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		
		// get an item to add to the cart	
		Item item = utilityService.getRandomItem();
		System.out.println("Item adding to cart: " + item.toString());
		String requestBody = utilityService.createJsonModifyCartRequest(UtilityService.username2, item.getId(), 3);
		
		// Send a request to add item to the cart
		this.mockMvc
				.perform(post("/api/cart/addToCart").accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue)
						.content(requestBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.user.username").value(UtilityService.username2));
		
	}	
	

	@Test
	public void testRemoveFromCart() throws Exception {
		
		// Create a user
		TestUser testUser = utilityService.createLogin(UtilityService.username2, true);
		String loginBody = testUser.getLoginBody();
		Long id = testUser.getUser().getId();
		
		// Login this user
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		// remember the JWT
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		
		// get an item to add to the cart	
		Item item = utilityService.getRandomItem();
		System.out.println("Item adding to cart: " + item.toString());
		String requestBody = utilityService.createJsonModifyCartRequest(UtilityService.username2, item.getId(), 3);
		
		// Send a request to add item to the cart
		this.mockMvc
				.perform(post("/api/cart/addToCart").accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue)
						.content(requestBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.user.username").value(UtilityService.username2));
		
		// This is where we start the test ...we will remove two items from the cart
		requestBody = utilityService.createJsonModifyCartRequest(UtilityService.username2, item.getId(), 2);
		this.mockMvc
		.perform(post("/api/cart/removeFromCart").accept(MediaType.APPLICATION_JSON)
				.header(SecurityConstants.HEADER_STRING, headerValue)
				.content(requestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists())
		.andExpect(jsonPath("$.user.username").value(UtilityService.username2));
		
		// test that user still has 1 item on their cart
		User user = userService.findByUsername(UtilityService.username2);
		assertTrue(user.getCart().getItems().size() == 1);
	}	

}
