package com.capstone.store.service;
import com.capstone.store.constants.Constants;
import com.capstone.store.dto.UserRequest;
import com.capstone.store.model.User;
import com.capstone.store.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void registerUser(UserRequest userRequest) {
        if (userRequest.getEmail() == null || userRequest.getEmail().trim().isEmpty() ||
                userRequest.getPassword() == null || userRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException(Constants.INVALID_REGISTRATION_REQUEST);
        }

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException(Constants.USER_ALREADY_EXISTS);
        }

        String hashedPassword = BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt());
        User user = new User(userRequest.getEmail().trim(), hashedPassword);
        userRepository.save(user);
    }


    public User loginUser(UserRequest userRequest) {
        User user = userRepository.findByEmail(userRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(Constants.INVALID_USER_CREDENTIALS));
        if (!BCrypt.checkpw(userRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException(Constants.INVALID_USER_CREDENTIALS);
        }
        return user;
    }

    public Long getUserIdByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException(Constants.INVALID_EMAIL));
        return user.getId();
    }
}
