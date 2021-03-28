package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private static Logger logger = LoggerFactory.getLogger(UserController.class);
		
	@Autowired
	private UserService userService;


	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id, Authentication authentication) {
		logger.info("UserController:findById - invoked");
		if (userService.validateUserIdMatchesAuth(id, authentication.getName())) {
			User user = userService.findById(id);
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username, Authentication authentication) {
		logger.info("UserController:findByUserName - invoked");
		if (userService.validateUserNameMatchesAuth(username, authentication.getName())) {
			User user = userService.findByUsername(username);
			return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();	
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) throws Exception {
		logger.info("UserController:createUser - invoked");
		// 1. Check the password is ok and matches confirmed password
		if(!userService.isPasswordValid(createUserRequest.getPassword(), createUserRequest.getConfirmPassword())){
			logger.info("UserController:createUser - bad request returned.");		
			return ResponseEntity.badRequest().build();
		}
		
		try {
			User user = userService.prepareNewUser(createUserRequest.getUsername());

			userService.saveNewUserAccount(user, createUserRequest.getPassword());
			logger.info("UserController:createUser - new user successfully created.");
			
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			logger.error("UserController:createUser - " + e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
	
}
