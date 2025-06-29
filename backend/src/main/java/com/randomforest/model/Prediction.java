package com.randomforest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prediction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PredictionType type;

    @Column(columnDefinition = "json", nullable = false)
    private String result;

    @Column(columnDefinition = "json", nullable = false)
    private String parameters;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "accuracy", length = 50)
    private String accuracy;
}
