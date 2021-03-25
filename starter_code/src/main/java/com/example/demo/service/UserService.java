package com.example.demo.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@Service
@Transactional
public class UserService {
	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public User prepareNewUser(String username) {
		// First check that this username is not already in the database
		User user = userRepository.findByUsername(username);
		if (user != null) {
			logger.info("UserService:prepareNewUser - DuplicateUsernameException.");
			throw new DuplicateUsernameException("User with username: " + username + " already exists.");
		} else {
			user = new User();
		}
		
		user.setUsername(username);
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		return user;
	}
	
	public boolean isPasswordValid(String password, String confirmedPassword) {
		boolean result = true; // assume password is okay
		if(password.length()<7 ||
				!password.equals(confirmedPassword)){
			logger.info("UserService:isPasswordValid - password validation failed.");
			result = false;
		}		
		return result;
	}

	public void saveNewUserAccount(User user, String password) {
		user.setPassword(bCryptPasswordEncoder.encode(password));
		userRepository.save(user);	
	}
	
	public User findByUsername(String username) {
		logger.info("===> findByUsername - " + username);
		User user = userRepository.findByUsername(username);
		if (user != null) {
			logger.info("===> findByUsername:user from database - " + user.toString());
		}
		return user;
	}

	public User findById(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			return optionalUser.get();
		} else {
			logger.info("UserService:findById - UserNotFoundException");
			throw new UserNotFoundException("User with id: " + id + " not found.");
		}
	}

	public boolean validateUserIdMatchesAuth(Long id, String name) {
		boolean result = true;
		// Obtain db user (using name) match with id
		User user = this.userRepository.findByUsername(name);
		if (user == null) {
			result = false;
		} else {
			result = user.getId() == id.longValue();
		}
		return result;
	}

	public boolean validateUserNameMatchesAuth(String username, String name) {
		return username.equals(name);
	}
}
