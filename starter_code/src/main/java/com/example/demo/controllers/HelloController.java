package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
	private static Logger logger = LoggerFactory.getLogger(HelloController.class);
	
	@GetMapping
	public String hello() {
		logger.info("HelloController:hello - invoked");
		return "Welcome to auth-course";
	}

}

