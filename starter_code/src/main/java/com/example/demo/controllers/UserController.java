package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;

	@GetMapping("/ping")
	public ResponseEntity<String> pingUserApi() {
		logger.info("UserController:pingUserApi - invoked");
		return ResponseEntity.ok("App is alive and well.");
	}
	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		logger.info("UserController:findById - invoked");
		User user = userService.findById(id);
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		logger.info("UserController:findByUserName - invoked");
		User user = userService.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) throws Exception {
		logger.info("UserController:createUser - invoked");
		// 1. Check the password is ok and matches confirmed password
		if(!userService.isPasswordValid(createUserRequest.getPassword(), createUserRequest.getConfirmPassword())){
			logger.info("UserController:createUser - bad request returned.");		
			return ResponseEntity.badRequest().build();
		}
		

		User user = userService.prepareNewUser(createUserRequest.getUsername());

		userService.saveNewUserAccount(user, createUserRequest.getPassword());
		logger.info("UserController:createUser - new user successfully created.");
		
		return ResponseEntity.ok(user);
	}
	
}
