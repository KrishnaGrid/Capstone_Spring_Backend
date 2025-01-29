package com.capstone.store.service;

import com.capstone.store.model.User;
import com.capstone.store.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists.");
        }
        User user = new User();
        user.setEmail(email);
        String hashedPassword = passwordEncoder.encode(password);  // Debugging step
        System.out.println("Hashed password: " + hashedPassword);  // Check if hashing is working
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);
    }


    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        return UUID.randomUUID().toString(); // Generate a mock session ID
    }
}
