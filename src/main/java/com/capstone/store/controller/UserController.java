package com.capstone.store.controller;

import com.capstone.store.constants.Constants;
import com.capstone.store.dto.UserRequest;
import com.capstone.store.model.User;
import com.capstone.store.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequest userRequest) {
        try {
            userService.registerUser(userRequest);
            return ResponseEntity.status(201).body(Constants.USER_REGISTERED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserRequest userRequest, HttpSession session) {
        try {
            User user = userService.loginUser(userRequest);
            String sessionId = UUID.randomUUID().toString();
            session.setAttribute(Constants.USER_ID, user.getId());
            return ResponseEntity.ok(Map.of(Constants.SESSION_ID, sessionId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(Constants.ERROR, e.getMessage()));
        }
    }
}