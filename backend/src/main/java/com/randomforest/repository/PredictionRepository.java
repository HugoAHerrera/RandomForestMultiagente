package com.randomforest.repository;

import com.randomforest.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Integer> {
    List<Prediction> findByUserId(Integer userId);
}