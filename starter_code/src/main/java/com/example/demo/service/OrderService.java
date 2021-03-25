package com.example.demo.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;

@Service
@Transactional
public class OrderService {
	private static Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private UserService userService;

	@Autowired
	private OrderRepository orderRepository;

	public UserOrder submitOrder(String username, String authName) {
		logger.info("OrderService:submitOrder - username: " + username);
		logger.info("OrderService:submitOrder - authName: " + authName);
		
		if (userService.validateUserNameMatchesAuth(username, authName)) {
			User user = userService.findByUsername(username);

			if (user == null) {
				logger.info("OrderService:submitOrder - UserNotFoundException.");
				throw new UserNotFoundException("The username '" + username + "' could not be found.");
			}

			UserOrder order = UserOrder.createFromCart(user.getCart());
			orderRepository.save(order);
			return order;
		} else {
			logger.info("OrderService:submitOrder - Authorization Exception.");
			throw new AuthorizationException("The user '" + username + "' is not authorized.");
		}
	}

	public List<UserOrder> getUserOrderHistory(String username, String authName) {
		if (userService.validateUserNameMatchesAuth(username, authName)) {
			User user = userService.findByUsername(username);

			if (user == null) {
				logger.info("OrderService:getUserOrderHistory - UserNotFoundException");
				throw new UserNotFoundException("The username '" + username + "' could not be found.");
			}
			return orderRepository.findByUser(user);
		} else {
			logger.info("OrderService:getUserOrderHistory - AuthorizationException");
			throw new AuthorizationException("The user '" + username + "' is not authorized.");
		}

	}
}
