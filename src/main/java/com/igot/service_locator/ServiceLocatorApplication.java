package com.igot.service_locator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class ServiceLocatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceLocatorApplication.class, args);
    }

}
