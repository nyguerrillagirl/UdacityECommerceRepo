package com.example.demo.service;

import java.util.Optional;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@Service
@Transactional
public class CartService {
	private static Logger logger = LoggerFactory.getLogger(CartService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private UserService userService;

	public Cart addTocart(ModifyCartRequest request, String authUsername) {
		logger.info("CartService:addTocart - authUsername: " + authUsername);
		if (userService.validateUserNameMatchesAuth(request.getUsername(), authUsername)) {
			User user = userRepository.findByUsername(request.getUsername());
			if (user == null) {
				logger.info("CartService:addTocart - UserNotFoundException.");
				throw new UserNotFoundException("The username: " + authUsername + " not found.");
			}
			logger.info("CartService:addTocart - processing user: " + user.toString());
			Optional<Item> optionalItem = itemRepository.findById(request.getItemId());
			logger.info("CartService:addTocart - processing item: " + request.getItemId());
			Item item = optionalItem.get();
			if (!optionalItem.isPresent()) {
				logger.info("CartService:addTocart - ItemNotFoundException.");
				throw new ItemNotFoundException("The item with id: " + request.getItemId() + " not found.");
			}
			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity()).forEach(i -> cart.addItem(item));
			cartRepository.save(cart);
			return cart;
		} else {
			logger.info("CartService:addTocart - AuthorizationException.");
			throw new AuthorizationException("User is not authorized for that resource.");
		}
	}

	public Cart removeFromCart(ModifyCartRequest request, String authUsername) {

		logger.info("CartService:removeFromCart - authUsername: " + authUsername);
		if (userService.validateUserNameMatchesAuth(request.getUsername(), authUsername)) {
			logger.info("CartService:addTocart - locating user record.");
			User user = userRepository.findByUsername(request.getUsername());
			if (user == null) {
				throw new UserNotFoundException("The username: " + authUsername + " not found.");
			}
			Optional<Item> item = itemRepository.findById(request.getItemId());
			if (!item.isPresent()) {
				throw new ItemNotFoundException("The item with id: " + request.getItemId() + " not found.");
			}
			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity()).forEach(i -> cart.removeItem(item.get()));
			cartRepository.save(cart);
			return cart;
		} else {			
			logger.info("CartService:removeFromCart - AuthorizationException.");
			throw new AuthorizationException("User is not authorized for that resource.");
		}
	}
}
