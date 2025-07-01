package com.randomforest.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import java.util.Optional;
import com.randomforest.model.User;
import com.randomforest.repository.UserRepository;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User registerUser(String username, String rawPassword) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(rawPassword);
        return userRepository.save(user);
    }

    public boolean validateUser(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return false;
        
        return userOpt.get().getPassword().equals(rawPassword);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
