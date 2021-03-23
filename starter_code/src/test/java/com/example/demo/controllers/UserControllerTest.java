package com.example.demo.controllers;



import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
	
	
	public static final String createBody = "{\r\n"
			+ "    \"username\" : \"test34\",\r\n"
			+ "    \"password\" : \"somepassword\",\r\n"
			+ "    \"confirmPassword\" : \"somepassword\"\r\n"
			+ "}";
	
	@Autowired
	private UserController userController;
	
	@Autowired
	private MockMvc mockMvc;


	@Test
	public void contextLoads() throws Exception {
		assertThat(userController).isNotNull();
	}

	
	@Test
	public void testCreateUser() throws Exception {
		this.mockMvc
				.perform(post("/api/user/create").content(createBody).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.username").value("test34"));
	}	

}
