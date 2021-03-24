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

import com.example.demo.security.SecurityConstants;
import com.example.demo.service.UtilityService;
import com.example.demo.service.UtilityService.TestUser;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest2 {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UtilityService utilityService;

	@Test
	public void testLogin() throws Exception {
		String loginBody = utilityService.createLogin(UtilityService.username1, true).getLoginBody();
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		// Save
		assertTrue(headerValue.startsWith("Bearer"));
	}

	@Test
	public void testNotARealUser() throws Exception {
		String loginBodyFakeUser = utilityService.createLogin("donnie", false).getLoginBody();
		this.mockMvc.perform(post("/login").content(loginBodyFakeUser).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isUnauthorized());
	}

	@Test
	public void testGetUserId() throws Exception {
		TestUser testUser = utilityService.createLogin(UtilityService.username2, true);
		String loginBody = testUser.getLoginBody();
		Long id = testUser.getUser().getId();
		
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		this.mockMvc
				.perform(get("/api/user/id/" + id.toString()).accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue))
				.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.username").value("test35"));
	}

	@Test
	public void testGetUserIdFailure() throws Exception {
		String loginBody = utilityService.createLogin(UtilityService.username3, true).getLoginBody();
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");

		this.mockMvc
				.perform(get("/api/user/id/1").accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue))
				.andDo(print()).andExpect(status().is4xxClientError());

	}

	@Test
	void testGetUserByName() throws Exception {
		TestUser testUser = utilityService.createLogin(UtilityService.username4, true);
		String loginBody = testUser.getLoginBody();
		Long id = testUser.getUser().getId();
		
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");

		this.mockMvc
				.perform(get("/api/user/" + UtilityService.username4).accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue))
				.andDo(print()).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.username").value(UtilityService.username4));
	}
	
	@Test
	void testGetUserByNameFailure() throws Exception {
		String loginBody = utilityService.createLogin(UtilityService.username5, true).getLoginBody();
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");

		this.mockMvc
				.perform(get("/api/user/" + UtilityService.username1).accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue))
				.andDo(print()).andDo(print()).andExpect(status().is4xxClientError());
	}
	

}
