package com.randomforest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class RandomForestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RandomForestApplication.class, args);
    }
}
