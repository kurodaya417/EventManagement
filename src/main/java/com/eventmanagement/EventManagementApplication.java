package com.eventmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Event Management Application
 * 
 * Spring Boot application for managing events with REST API endpoints.
 * Technologies used: Spring Boot, MyBatis, Oracle Database
 */
@SpringBootApplication
public class EventManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventManagementApplication.class, args);
    }
}