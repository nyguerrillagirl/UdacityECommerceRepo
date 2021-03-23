package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public User prepareNewUser(String username) {
		User user = new User();
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
			result = false;
		}
		
		return result;
	}

	public void saveNewUserAccount(User user, String password) {
		user.setPassword(bCryptPasswordEncoder.encode(password));
		userRepository.save(user);	
	}
	
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public User findById(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			return optionalUser.get();
		} else {
			throw new UserNotFoundException("User with id: " + id + " not found.");
		}
	}
}
