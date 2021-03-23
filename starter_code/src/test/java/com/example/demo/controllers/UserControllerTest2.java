package com.example.demo.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest2 {
	
	public static final String createBody = "{\r\n"
			+ "    \"username\" : \"test34\",\r\n"
			+ "    \"password\" : \"somepassword\",\r\n"
			+ "    \"confirmPassword\" : \"somepassword\"\r\n"
			+ "}";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void testUserControllerPing() throws Exception {
		this.mockMvc.perform(get("/api/user/ping"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("App is alive and well.")));
	}
	
	@Test
	public void testCreateUser() throws Exception {
		this.mockMvc.perform(post("/api/user/create")
					.content(createBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
					.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("test34"));
	}
	
	@Test
	public void testCreateUser2() throws Exception {
		MvcResult  mvcResult = this.mockMvc.perform(post("/api/user/create")
					.content(createBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		System.out.println("headerValue: " + headerValue);
					
	}

}
