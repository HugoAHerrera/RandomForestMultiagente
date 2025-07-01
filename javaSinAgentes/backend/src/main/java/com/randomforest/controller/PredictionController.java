package com.randomforest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ResponseStatusException;

import com.randomforest.dto.PredictionRequestDto;

import com.randomforest.service.PredictionService;
import com.randomforest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.randomforest.model.User;
import com.randomforest.model.Prediction;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private UserService userService;

    @CrossOrigin(origins = "*")
    @PostMapping("/prediction")
    public ResponseEntity<String> createPrediction(@RequestBody List<PredictionRequestDto> requestList) {
        System.out.println("Request recibido:");
        for (PredictionRequestDto dto : requestList) {
            System.out.println("Nombre: " + dto.getName());
            System.out.println("Usuario: " + dto.getUserName());
            System.out.println("Tarea: " + dto.getTask());
            System.out.println("Target: " + dto.getTarget());
            System.out.println("Archivo: " + dto.getFileName());
            System.out.println("Features: " + dto.getFeatures());
            System.out.println("-----");
        }
        try {
            predictionService.sendRequestedPredictions(requestList);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body("Error handling file rows: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/prediction/{username}")
    public List<Prediction> getHistorial(@PathVariable String username) {
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Integer userId = userOpt.get().getId();
        return predictionService.getPredictionsByUserId(userId);
    }

}
