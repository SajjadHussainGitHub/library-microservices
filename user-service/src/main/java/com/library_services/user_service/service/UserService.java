package com.library_services.user_service.service;

import com.library_services.user_service.entity.User;
import com.library_services.user_service.exception.ResourceNotFoundException;
import com.library_services.user_service.mapper.UserMapper;
import com.library_services.user_service.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private UserMapper mapper;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create user
    @CacheEvict(value = "users", allEntries = true) // clear cache when a new user is created
    public com.library_services.user_service.pojo.User createUser(com.library_services.user_service.pojo.User pojo) {
        mapper = new UserMapper();
        User user = mapper.toEntity(pojo);
        User savedUser = userRepository.save(user);
        return mapper.toPojo(savedUser);
    }

    // Get all users
    @Cacheable(value = "users", key = "'all'") // cache the full list
    @CircuitBreaker(name = "userService", fallbackMethod = "getAllUsersFallback")
    @Retry(name = "userService")
    public List<com.library_services.user_service.pojo.User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(entity -> {
                    com.library_services.user_service.pojo.User pojo = new com.library_services.user_service.pojo.User();
                    pojo.setId(entity.getId());
                    pojo.setUsername(entity.getUsername());
                    pojo.setEmail(entity.getEmail());
                    pojo.setFullName(entity.getFullName());
                    pojo.setPasswordHash(entity.getPasswordHash());
                    pojo.setCreatedAt(entity.getCreatedAt());
                    pojo.setUpdatedAt(entity.getUpdatedAt());
                    return pojo;
                })
                .collect(Collectors.toList());
    }

    public List<com.library_services.user_service.pojo.User> getAllUsersFallback(Throwable t) {
        return List.of(); // Return empty list if service fails
    }

    // Get user by ID
    @Cacheable(value = "users", key = "#id") // cache individual users
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    @Retry(name = "userService")
    public com.library_services.user_service.pojo.User getUserById(Long id) {
        mapper = new UserMapper();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return mapper.toPojo(user);
    }

    public com.library_services.user_service.pojo.User getUserByIdFallback(Long id, Throwable t) {
        com.library_services.user_service.pojo.User fallback = new com.library_services.user_service.pojo.User();
        fallback.setId(id);
        fallback.setUsername("Unavailable");
        fallback.setEmail("Unavailable");
        return fallback;
    }

    // Update user
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),       // remove cached user
            @CacheEvict(value = "users", key = "'all'")     // remove cached full list
    })
    public com.library_services.user_service.pojo.User updateUser(Long id, com.library_services.user_service.pojo.User pojo) {
        mapper = new UserMapper();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setUsername(pojo.getUsername());
        user.setEmail(pojo.getEmail());
        user.setFullName(pojo.getFullName());
        user.setPasswordHash(pojo.getPasswordHash());

        User updatedUser = userRepository.save(user);
        return mapper.toPojo(updatedUser);
    }

    // Delete user
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),      // remove cached user
            @CacheEvict(value = "users", key = "'all'")     // remove cached full list
    })
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        userRepository.delete(user);
    }
}
