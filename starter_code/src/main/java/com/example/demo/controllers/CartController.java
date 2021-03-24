package com.example.demo.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.service.AuthorizationException;
import com.example.demo.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	private static Logger logger = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private CartService cartService;

	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request, Authentication authentication) {
		logger.info("CartController:addTocart - invoked");
		try {
			Cart cart = cartService.addTocart(request, authentication.getName());
			logger.info("CartController:addTocart - sending back cart items.");
			return ResponseEntity.ok(cart);
		} catch (AuthorizationException ae) {
			logger.error("AuthorizationException: " + ae.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request, Authentication authentication) {
		logger.info("CartController:removeFromcart - invoked");
		try {
			Cart cart = cartService.removeFromCart(request, authentication.getName());
			return ResponseEntity.ok(cart);
		} catch (AuthorizationException ae) {
			logger.error("AuthorizationException: " + ae.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}
