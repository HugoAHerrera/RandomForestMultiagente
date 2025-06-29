package com.randomforest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.randomforest.dto.HeaderDto;
import com.randomforest.dto.ChunkDto;

import com.randomforest.service.AgentCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@RestController
public class FileController {

    int rowNumber = 0;

    @Autowired
    private AgentCommunicationService agentCommunicationService;

    @CrossOrigin(origins = "*")
    @PostMapping("/file/header")
    public ResponseEntity<String> handleColumnTypes(@RequestBody HeaderDto request) {
        try {
            agentCommunicationService.sendHeader(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body("Error sending header: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/file/chunk")
    public ResponseEntity<String> handleFileChunk(@RequestBody ChunkDto chunk) {
        try {
            agentCommunicationService.sendChunk(chunk);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body("Error handling file rows: " + e.getMessage());
        }
    }
}