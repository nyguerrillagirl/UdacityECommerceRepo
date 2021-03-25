package com.example.demo.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import com.example.demo.security.SecurityConstants;
import com.example.demo.service.UtilityService;
import com.example.demo.service.UtilityService.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UtilityService utilityService;

	@Test
	void testSubmitPassed() throws Exception {
		// Create a user
		TestUser testUser = utilityService.createLogin(UtilityService.username1, true);
		String loginBody = testUser.getLoginBody();
		
		// Login this user
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		// remember the JWT
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		
		// get an item to add to the cart	
		Item item = utilityService.getRandomItem();
		System.out.println("Item adding to cart: " + item.toString());
		String requestBody = utilityService.createJsonModifyCartRequest(UtilityService.username1, item.getId(), 3);
		
		// Send a request to add item to the cart
		this.mockMvc
				.perform(post("/api/cart/addToCart").accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue)
						.content(requestBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.user.username").value(UtilityService.username1));
		
		this.mockMvc
		.perform(post("/api/order/submit/" + UtilityService.username1 ).accept(MediaType.APPLICATION_JSON)
				.header(SecurityConstants.HEADER_STRING, headerValue))
		.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.id").exists())
		.andExpect(jsonPath("$.user.username").value(UtilityService.username1));	
	}
	
	@Test
	void testSubmitFailed()  throws Exception {
		// Create a user
		TestUser testUser = utilityService.createLogin(UtilityService.username2, true);
		String loginBody = testUser.getLoginBody();
		
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
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.user.username").value(UtilityService.username2));
		
		this.mockMvc
		.perform(post("/api/order/submit/" + UtilityService.username1 ).accept(MediaType.APPLICATION_JSON)
				.header(SecurityConstants.HEADER_STRING, headerValue))
				.andDo(print()).andExpect(status().is4xxClientError());
	}
	
	@Test
	void testGetOrdersForUserPassed()  throws Exception {
		// Create a user
		TestUser testUser = utilityService.createLogin(UtilityService.username3, true);
		String loginBody = testUser.getLoginBody();
		
		// Login this user
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		// remember the JWT
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		
		// get an item to add to the cart	
		Item item = utilityService.getRandomItem();
		System.out.println("Item adding to cart: " + item.toString());
		String requestBody = utilityService.createJsonModifyCartRequest(UtilityService.username3, item.getId(), 3);
		
		// Send a request to add item to the cart
		this.mockMvc
				.perform(post("/api/cart/addToCart").accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue)
						.content(requestBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.user.username").value(UtilityService.username3));
		
		this.mockMvc
		.perform(post("/api/order/submit/" + UtilityService.username3 ).accept(MediaType.APPLICATION_JSON)
				.header(SecurityConstants.HEADER_STRING, headerValue))
		.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.id").exists())
		.andExpect(jsonPath("$.user.username").value(UtilityService.username3));	
		
		// Now get back this order
		this.mockMvc
		.perform(get("/api/order/history/" + UtilityService.username3).accept(MediaType.APPLICATION_JSON)
				.header(SecurityConstants.HEADER_STRING, headerValue))
		.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.[0].id").exists());
	}
	
	@Test
	void testGetOrdersForUserNoOrders()  throws Exception  {
		// Create a user
		TestUser testUser = utilityService.createLogin(UtilityService.username4, true);
		String loginBody = testUser.getLoginBody();
		
		// Login this user
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		// remember the JWT
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		
		
		// Now get back this order
		this.mockMvc
		.perform(get("/api/order/history/" + UtilityService.username4).accept(MediaType.APPLICATION_JSON)
				.header(SecurityConstants.HEADER_STRING, headerValue))
		.andDo(print()).andExpect(status().isOk())
		.andExpect(content().string("[]"));
	}
}
