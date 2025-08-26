package com.library_services.user_service.service;

import com.library_services.user_service.entity.User;
import com.library_services.user_service.exception.ResourceNotFoundException;
import com.library_services.user_service.mapper.UserMapper;
import com.library_services.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    final UserRepository userRepository;
    UserMapper mapper;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create user
    public com.library_services.user_service.pojo.User createUser(com.library_services.user_service.pojo.User pojo) {
        User user = mapper.toEntity(pojo);
        User savedUser = userRepository.save(user);
        return mapper.toPojo(savedUser);
    }

    // Get all users
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

    // Get user by ID
    public com.library_services.user_service.pojo.User  getUserById(Long id) {
        mapper=new UserMapper();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return mapper.toPojo(user);
    }

    // Update user
    public com.library_services.user_service.pojo.User updateUser(Long id, com.library_services.user_service.pojo.User  pojo) {
        mapper=new UserMapper();
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
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        userRepository.delete(user);
    }

}
