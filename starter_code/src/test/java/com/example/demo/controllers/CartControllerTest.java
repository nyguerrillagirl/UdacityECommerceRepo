package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.security.SecurityConstants;
import com.example.demo.service.UtilityService;
import com.example.demo.service.UtilityService.TestUser;
import com.google.gson.Gson;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UtilityService utilityService;
	
	@Test
	public void testAddToCart() throws Exception {
		
		// Create a user
		TestUser testUser = utilityService.createLogin(utilityService.username2, true);
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
		String requestBody = utilityService.createJsonModifyCartRequest(utilityService.username2, item.getId(), 3);
		
		// Send a request to add item to the cart
		this.mockMvc
				.perform(post("/api/cart/addToCart").accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue)
						.content(requestBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.user.username").value(utilityService.username2));
		
	}	
	



}
