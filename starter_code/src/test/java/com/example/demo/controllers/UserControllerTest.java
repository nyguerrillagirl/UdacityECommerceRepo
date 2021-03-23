package com.example.demo.controllers;



import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;

	public static final String createBody = "{\r\n"
			+ "    \"username\" : \"test34\",\r\n"
			+ "    \"password\" : \"somepassword\",\r\n"
			+ "    \"confirmPassword\" : \"somepassword\"\r\n"
			+ "}";
	
	@Autowired
	private UserController userController;

	@Test
	public void contextLoads() throws Exception {
		assertThat(userController).isNotNull();
	}

	@Test
	public void pingTest() {
		String url = "http://localhost:" + port + "/api/user/ping";
		assertThat(this.restTemplate.getForObject(url, String.class)).contains("App is alive and well.");
	}

}
