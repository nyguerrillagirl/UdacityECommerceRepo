package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.UserOrder;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserNotFoundException;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	private static Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private OrderService orderService;
		
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username, Authentication authentication) {		
		logger.info("OrderController:submit - invoked");
		try {
			UserOrder order = orderService.submitOrder(username, authentication.getName());
			return ResponseEntity.ok(order);
		} catch (UserNotFoundException unfe) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username, Authentication authentication) {
		logger.info("OrderController:getOrdersForUser - invoked");
		try {
			List<UserOrder> lstOrder = orderService.getUserOrderHistory(username, authentication.getName());
			return ResponseEntity.ok(lstOrder);
		} catch (UserNotFoundException unfe) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
