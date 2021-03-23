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

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.SecurityConstants;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest2 {

	public static final String username1 = "test34";

	public static final String username2 = "test35";

	public static final String username3 = "test36";

	public static final String username4 = "test37";
	
	public static final String username5 = "test38";
	
	public static final String TEST_PASSWORD = "somepassword";
	public static final String TEST_PASSWORD_ENCODED = "$2a$10$1VK35oNQiY9kZdbQy1klhuEX18VAUKEMDxD3R2ntDdLKkZPR5iwe.";
	

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;


	// Used to create login information for a user
	public TestUser createLogin(String username, boolean saveUserRecord) {
		System.out.println("createLogin ==> username: " + username);
		TestUser testUser = new TestUser();
		
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"username\" : \"" + username + "\",");
		sb.append("\"password\" : \"" + TEST_PASSWORD + "\" }");
		testUser.setLoginBody(sb.toString());
		
		if (saveUserRecord) {
			// Add this user to the database
			User user1 = new User();
			user1.setUsername(username);
			user1.setPassword(TEST_PASSWORD_ENCODED); // somepassword
			System.out.println("Saving user1: " + user1.toString());
			userRepository.save(user1);
			System.out.println("AFTER Saving user1: " + user1.toString());
			// Create cart
			Cart cart1 = new Cart();
			cart1.setId(user1.getId());
			cartRepository.save(cart1);
			testUser.setUser(user1);
		}
		
		return testUser;

	}

	@Test
	public void testLogin() throws Exception {
		String loginBody = createLogin(username1, true).getLoginBody();
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		// Save
		assertTrue(headerValue.startsWith("Bearer"));
	}

	@Test
	public void testNotARealUser() throws Exception {
		String loginBodyFakeUser = createLogin("donnie", false).getLoginBody();
		this.mockMvc.perform(post("/login").content(loginBodyFakeUser).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isUnauthorized());
	}

	@Test
	public void testGetUserId() throws Exception {
		TestUser testUser = createLogin(username2, true);
		String loginBody = testUser.getLoginBody();
		Long id = testUser.getUser().getId();
		
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");
		System.out.println("==> Authorization: " + headerValue);
		this.mockMvc
				.perform(get("/api/user/id/" + id.toString()).accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue))
				.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.username").value("test35"));
	}

	@Test
	public void testGetUserIdFailure() throws Exception {
		String loginBody = createLogin(username3, true).getLoginBody();
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");

		this.mockMvc
				.perform(get("/api/user/id/1").accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue))
				.andDo(print()).andExpect(status().isBadRequest());

	}

	@Test
	void testGetUserByName() throws Exception {
		TestUser testUser = createLogin(username4, true);
		String loginBody = testUser.getLoginBody();
		Long id = testUser.getUser().getId();
		
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");

		this.mockMvc
				.perform(get("/api/user/" + username4).accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue))
				.andDo(print()).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.username").value(this.username4));
	}
	
	@Test
	void testGetUserByNameFailure() throws Exception {
		String loginBody = createLogin(username5, true).getLoginBody();
		MvcResult mvcResult = this.mockMvc.perform(post("/login").content(loginBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
		String headerValue = mvcResult.getResponse().getHeader("Authorization");

		this.mockMvc
				.perform(get("/api/user/" + username1).accept(MediaType.APPLICATION_JSON)
						.header(SecurityConstants.HEADER_STRING, headerValue))
				.andDo(print()).andDo(print()).andExpect(status().isBadRequest());
	}
	
	class TestUser {
		User user;
		String loginBody;
		
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
		public String getLoginBody() {
			return loginBody;
		}
		public void setLoginBody(String loginBody) {
			this.loginBody = loginBody;
		}
		
		
	}
}
