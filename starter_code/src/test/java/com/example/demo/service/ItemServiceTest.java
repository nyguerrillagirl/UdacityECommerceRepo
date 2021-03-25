package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class ItemServiceTest {

	@Autowired
	private ItemService itemService;
	

	@Test
	void testGetItemById_ItemNotFoundException() {
		RuntimeException exception = assertThrows(ItemNotFoundException.class, () -> {
			itemService.getItemById(100L);
	    });
		
		String expectedMessage = "Item with id: 100 not found in inventory";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}



}
