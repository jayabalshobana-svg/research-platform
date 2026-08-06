package com.guru.researchplatform.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the Research Platform Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = "com.guru.researchplatform")
public class ResearchPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResearchPlatformApplication.class, args);
    }
}
