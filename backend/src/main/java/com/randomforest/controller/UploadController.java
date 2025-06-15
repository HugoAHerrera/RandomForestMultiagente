package com.randomforest.controller;

import com.randomforest.jade.JadeBoot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class UploadController {

    @CrossOrigin(origins = "http://localhost")
    @GetMapping("/upload")
    public ResponseEntity<Map<String, String>> getResultado() {

        //CompletableFuture<String> result = new CompletableFuture<>();
        //JadeBoot.startJadePlatform(result);

        String response = "2";

        return ResponseEntity.ok(Map.of("result", response));
    }
}
