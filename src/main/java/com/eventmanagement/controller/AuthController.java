package com.eventmanagement.controller;

import com.eventmanagement.dto.LoginRequest;
import com.eventmanagement.dto.RegisterRequest;
import com.eventmanagement.entity.User;
import com.eventmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication controller for login, logout, and user registration
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Register a new user
     * @param registerRequest the registration request
     * @return response entity with success/error message
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if username already exists
        if (userService.existsByUsername(registerRequest.getUsername())) {
            response.put("success", false);
            response.put("message", "Username already exists");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Check if email already exists
        if (userService.existsByEmail(registerRequest.getEmail())) {
            response.put("success", false);
            response.put("message", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Create new user
        User user = new User(
            registerRequest.getUsername(),
            registerRequest.getPassword(),
            registerRequest.getEmail(),
            registerRequest.getFullName(),
            "USER" // Default role
        );
        
        try {
            User createdUser = userService.createUser(user);
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("username", createdUser.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get current authenticated user information
     * @return response entity with user information
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            
            Optional<User> userOptional = userService.findByUsername(authentication.getName());
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                response.put("success", true);
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("fullName", user.getFullName());
                response.put("role", user.getRole());
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("success", false);
        response.put("message", "User not authenticated");
        return ResponseEntity.status(401).body(response);
    }
    
    /**
     * Check authentication status
     * @return response entity with authentication status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        boolean isAuthenticated = authentication != null && 
                                authentication.isAuthenticated() && 
                                !authentication.getName().equals("anonymousUser");
        
        response.put("authenticated", isAuthenticated);
        if (isAuthenticated) {
            response.put("username", authentication.getName());
            response.put("roles", authentication.getAuthorities());
        }
        
        return ResponseEntity.ok(response);
    }
}