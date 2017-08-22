package com.fortscale.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class TestMockProjectApplication {
	private static Logger log = LoggerFactory.getLogger(TestMockProjectApplication.class);

	public static void main(String[] args) {

		log.info(String.join(",",args));

		SpringApplication.run(TestMockProjectApplication.class, args);
	}
}
