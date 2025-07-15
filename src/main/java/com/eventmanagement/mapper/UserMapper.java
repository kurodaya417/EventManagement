package com.eventmanagement.mapper;

import com.eventmanagement.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis mapper interface for User entity
 */
@Mapper
public interface UserMapper {
    
    /**
     * Find user by username
     * @param username the username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(@Param("username") String username);
    
    /**
     * Find user by email
     * @param email the email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(@Param("email") String email);
    
    /**
     * Create a new user
     * @param user the user to create
     * @return the number of rows affected
     */
    int insert(User user);
    
    /**
     * Update an existing user
     * @param user the user to update
     * @return the number of rows affected
     */
    int update(User user);
    
    /**
     * Find user by ID
     * @param userId the user ID
     * @return Optional containing the user if found
     */
    Optional<User> findById(@Param("userId") Long userId);
    
    /**
     * Get all users
     * @return list of all users
     */
    List<User> findAll();
    
    /**
     * Delete a user by ID
     * @param userId the user ID
     * @return the number of rows affected
     */
    int deleteById(@Param("userId") Long userId);
    
    /**
     * Check if username exists
     * @param username the username
     * @return true if username exists
     */
    boolean existsByUsername(@Param("username") String username);
    
    /**
     * Check if email exists
     * @param email the email
     * @return true if email exists
     */
    boolean existsByEmail(@Param("email") String email);
}