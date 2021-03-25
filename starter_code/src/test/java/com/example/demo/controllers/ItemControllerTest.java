package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.example.demo.security.SecurityConstants;
import com.example.demo.service.UtilityService;
import com.example.demo.service.UtilityService.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UtilityService utilityService;
	
	@Test
	void testGetItems() throws Exception{
		// Create a user
		TestUser testUser = utilityService.createLogin(UtilityService.username1, true);
		String loginBody = testUser.getLoginBody();
		
		// Login this user
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		// remember the JWT
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		
		this.mockMvc
		.perform(get("/api/item").accept(MediaType.APPLICATION_JSON)
				.header(SecurityConstants.HEADER_STRING, headerValue))
		.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.[0].id").exists())
		.andExpect(jsonPath("$.[0].name").exists());
	}	

	@Test
	void testGetItemById() throws Exception {
		// Create a user
		TestUser testUser = utilityService.createLogin(UtilityService.username2, true);
		String loginBody = testUser.getLoginBody();
		
		// Login this user
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		// remember the JWT
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		
		Item randomItem = utilityService.getRandomItem();
		this.mockMvc
		.perform(get("/api/item/" + randomItem.getId()).accept(MediaType.APPLICATION_JSON)
				.header(SecurityConstants.HEADER_STRING, headerValue))
		.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.id").exists())
		.andExpect(jsonPath("$.name").value(randomItem.getName()));
		
	}

	@Test
	void testGetItemsByName() throws Exception{
		// Create a user
		TestUser testUser = utilityService.createLogin(UtilityService.username3, true);
		String loginBody = testUser.getLoginBody();
		
		// Login this user
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		// remember the JWT
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		
		Item randomItem = utilityService.getRandomItem();
		this.mockMvc
		.perform(get("/api/item/name/" + randomItem.getName()).accept(MediaType.APPLICATION_JSON)
				.header(SecurityConstants.HEADER_STRING, headerValue))
		.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$..id").exists());
		//.andExpect(jsonPath("$..name").value(randomItem.getName()));
	}

}
