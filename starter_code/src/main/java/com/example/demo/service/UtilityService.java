package com.example.demo.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.google.gson.Gson;

@Service
public class UtilityService {
	
	public static final String username1 = "test34";

	public static final String username2 = "test35";

	public static final String username3 = "test36";

	public static final String username4 = "test37";
	
	public static final String username5 = "test38";
	
	public static final String TEST_PASSWORD = "somepassword";
	public static final String TEST_PASSWORD_ENCODED = "$2a$10$1VK35oNQiY9kZdbQy1klhuEX18VAUKEMDxD3R2ntDdLKkZPR5iwe.";
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ItemRepository itemRepository;

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
			userRepository.save(user1);
			// Create cart
			Cart cart1 = new Cart();
			cart1.setUser(user1);
			user1.setCart(cart1);
			cartRepository.save(cart1);		
			userRepository.save(user1);		
			testUser.setUser(user1);
		}
		
		return testUser;

	}
	public class TestUser {
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
	public Item getRandomItem() {
		List<Item> lstItems = itemRepository.findAll();
		Random rand = new Random();		 
		return lstItems.get(rand.nextInt(lstItems.size()));
	}
	
	public String createJsonModifyCartRequest(String username, Long itemId, int quantity) {
		ModifyCartRequest mcr = new ModifyCartRequest();
		mcr.setUsername(username);
		mcr.setItemId(itemId);
		mcr.setQuantity(quantity);
		Gson gson = new Gson();
		String requestBody = gson.toJson(mcr);
		return requestBody;
	}

}
