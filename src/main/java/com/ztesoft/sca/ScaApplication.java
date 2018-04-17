package com.ztesoft.sca;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


@SpringBootApplication
public class ScaApplication  extends SpringBootServletInitializer {
	private static Logger logger = Logger.getLogger(ScaApplication.class);
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootApplication.class);
	}
	
	public static void main(String[] args) { 
		SpringApplication.run(ScaApplication.class, args);
		logger.info("-----ScaApplication------aa--");
	}
}
