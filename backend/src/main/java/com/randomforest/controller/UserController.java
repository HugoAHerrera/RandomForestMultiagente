package com.randomforest.controller;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.Map;
import com.randomforest.model.User;
import com.randomforest.service.UserService;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user.
     *
     * @param params Map containing username and password
     * @return ResponseEntity code
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/user/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> params) {
        if (params == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
        String username = params.get("username");
        String password = params.get("password");

        if(userService.usernameExists(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Usuario ya existe"));
        }

        userService.registerUser(username, password);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Logs in a user by validating their credentials.
     *
     * @param params Map containing username and password
     * @return ResponseEntity code
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> params) {
        if (params == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
        String username = params.get("username");
        String password = params.get("password");

        if(userService.validateUser(username, password)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
