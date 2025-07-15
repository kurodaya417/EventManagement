package com.eventmanagement.service;

import com.eventmanagement.entity.User;
import com.eventmanagement.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for user management and authentication
 */
@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userMapper.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        User user = userOptional.get();
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .disabled(!user.isEnabled())
                .build();
    }
    
    /**
     * Find user by username
     * @param username the username
     * @return Optional containing the user if found
     */
    public Optional<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    /**
     * Find user by email
     * @param email the email address
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
    
    /**
     * Create a new user
     * @param user the user to create
     * @return the created user
     */
    public User createUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        return user;
    }
    
    /**
     * Update an existing user
     * @param user the user to update
     * @return the updated user
     */
    public User updateUser(User user) {
        // Encode password if it's changed
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userMapper.update(user);
        return user;
    }
    
    /**
     * Find user by ID
     * @param userId the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> findById(Long userId) {
        return userMapper.findById(userId);
    }
    
    /**
     * Get all users
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
    
    /**
     * Delete a user by ID
     * @param userId the user ID
     * @return true if user was deleted
     */
    public boolean deleteUser(Long userId) {
        return userMapper.deleteById(userId) > 0;
    }
    
    /**
     * Check if username exists
     * @param username the username
     * @return true if username exists
     */
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }
    
    /**
     * Check if email exists
     * @param email the email
     * @return true if email exists
     */
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }
    
    /**
     * Authenticate user with username and password
     * @param username the username
     * @param password the raw password
     * @return Optional containing the user if authentication is successful
     */
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOptional = findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.isEnabled() && passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
}