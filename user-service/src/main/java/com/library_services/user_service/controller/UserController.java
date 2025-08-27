package com.library_services.user_service.controller;

import com.library_services.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create
    @PostMapping
    public ResponseEntity<com.library_services.user_service.pojo.User> createUser(@Valid @RequestBody com.library_services.user_service.pojo.User userPojo) {
        return ResponseEntity.ok(userService.createUser(userPojo));
    }

    // Get all
    @GetMapping("/users")
    public ResponseEntity<List<com.library_services.user_service.pojo.User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<com.library_services.user_service.pojo.User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<com.library_services.user_service.pojo.User> updateUser(@PathVariable Long id,
                                               @Valid @RequestBody com.library_services.user_service.pojo.User userPojo) {
        return ResponseEntity.ok(userService.updateUser(id, userPojo));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}