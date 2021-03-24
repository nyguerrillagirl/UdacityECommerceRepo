package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@Service
@Transactional
public class ItemService {
	private static Logger logger = LoggerFactory.getLogger(ItemService.class);

	@Autowired
	private ItemRepository itemRepository;

	public List<Item> getItems() {
		return itemRepository.findAll();
	}
	
	public Item getItemById(Long id) {
		Optional<Item> optionalItem = itemRepository.findById(id);
		if (optionalItem.isPresent()) {
			return optionalItem.get();
		} else {
			logger.info("ItemService:getItemById - ItemNotFoundException.");
			throw new ItemNotFoundException("Item with id: " + id + " not found in inventory");
		}
		
	}
	
	public List<Item> getItemsByName(String name) {
		List<Item> items = itemRepository.findByName(name);
		
		return items;
	}
	
}
