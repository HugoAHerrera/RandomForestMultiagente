package com.randomforest.service;

import com.randomforest.model.Prediction;
import com.randomforest.model.PredictionType;
import com.randomforest.repository.PredictionRepository;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.randomforest.dto.PredictionResultDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.randomforest.service.UserService;
import com.randomforest.model.User;

import java.util.List;

@Service
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public PredictionService(PredictionRepository predictionRepository, UserService userService) {
        this.predictionRepository = predictionRepository;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    public List<Prediction> getPredictionsByUserId(Integer userId) {
        return predictionRepository.findByUserId(userId);
    }

    public void storeResults(List<PredictionResultDto> resultados) {
        for (PredictionResultDto dto : resultados) {

            Integer userId = userService.findByUsername(dto.getUserName())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserName()));

            Prediction prediction = new Prediction();
            prediction.setUserId(userId);

            PredictionType tipo = "regresion".equalsIgnoreCase(dto.getTask())
                ? PredictionType.Regresión
                : PredictionType.Clasificación;
            prediction.setType(tipo);

            try {
                prediction.setResult(objectMapper.writeValueAsString(dto.getTarget()));
                prediction.setParameters(objectMapper.writeValueAsString(dto.getFeatures()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error creating JSON", e);
            }

            prediction.setFileName(dto.getFileName());

            double roundedAccuracy = Math.round(dto.getAccuracy() * 100.0) / 100.0;
            prediction.setAccuracy(roundedAccuracy + "%");

            predictionRepository.save(prediction);
        }
    }

}
