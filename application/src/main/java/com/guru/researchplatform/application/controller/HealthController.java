package com.guru.researchplatform.application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the application health endpoint.
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    /**
     * Returns the running-state message for the application.
     *
     * @return the application running-state message
     */
    @GetMapping("/health")
    public String health() {
        return "Research Platform Running";
    }
}
