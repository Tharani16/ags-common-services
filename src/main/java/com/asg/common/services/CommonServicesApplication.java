package com.asg.common.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.asg"})
@EnableJpaRepositories(basePackages = {"com.asg.common.lib.repository", "com.asg.common.services.repository"})
@EntityScan(basePackages = {"com.asg.common.lib.entity", "com.asg.common.services.entity"})
public class CommonServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommonServicesApplication.class, args);
	}

}
