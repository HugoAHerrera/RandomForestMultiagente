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

    /**
     * Handles the sending of file header information to the agent communication service.
     *
     * @param request HeaderDto object
     * @return ResponseEntity code
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/file/header")
    public ResponseEntity<String> handleColumnTypes(@RequestBody HeaderDto request) {
        if (request == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
        try {
            agentCommunicationService.sendHeader(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body("Error sending header: " + e.getMessage());
        }
    }

    /**
     * Handles the sending of a file chunk to the agent communication service.
     *
     * @param chunk ChunkDto object 
     * @return ResponseEntity code
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/file/chunk")
    public ResponseEntity<String> handleFileChunk(@RequestBody ChunkDto chunk) {
        if (chunk == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
        try {
            agentCommunicationService.sendChunk(chunk);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body("Error handling file rows: " + e.getMessage());
        }
    }
}