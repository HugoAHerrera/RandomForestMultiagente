package com.randomforest.service;

import jade.wrapper.AgentController;
import org.springframework.stereotype.Service;

import com.randomforest.dto.HeaderDto;
import com.randomforest.dto.ChunkDto;
import com.randomforest.dto.PredictionRequestDto;

import java.util.List;
import java.util.Map;

@Service
public class AgentCommunicationService {

    private AgentController orchestrator;

    public void setOrchestrator(AgentController orchestrator) {
        this.orchestrator = orchestrator;
    }

    public void sendHeader(HeaderDto header) throws Exception {
        if (orchestrator != null) {
            orchestrator.putO2AObject(header, AgentController.ASYNC);
        } else {
            throw new Exception("Orchestrator agent not initialized");
        }
    }

    public void sendChunk(ChunkDto chunk) throws Exception {
        if (orchestrator != null) {
            orchestrator.putO2AObject(chunk, AgentController.ASYNC);
        } else {
            throw new Exception("Orchestrator agent not initialized");
        }
    }

    public void sendRequestedPredictions(List<PredictionRequestDto> predictionsRequestedList) throws Exception {
        if (orchestrator != null) {
            orchestrator.putO2AObject(predictionsRequestedList, AgentController.ASYNC);
        } else {
            throw new Exception("Orchestrator agent not initialized");
        }
    }
}