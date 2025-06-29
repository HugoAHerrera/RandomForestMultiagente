package com.randomforest.jade.agent;

import org.springframework.beans.factory.annotation.Autowired;

import jade.core.Agent;
import com.randomforest.jade.behaviour.ShareHeaderBehaviour;
import com.randomforest.jade.behaviour.SendRowsChunkBehaviour;
import com.randomforest.jade.behaviour.RequestPredictionsBehaviour;

import com.randomforest.service.PredictionService;
import com.randomforest.dto.HeaderDto;
import com.randomforest.dto.PredictionRequestDto;
import com.randomforest.dto.ChunkDto;
import com.randomforest.SpringContext;

import java.util.List;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrchestratorAgent extends BaseAgent {

    private List<String> predictionAgents;
    private int currentAgent = 0;
    private HeaderDto fileHeader;
    private List<PredictionRequestDto> predictionsList;

    @Override
    protected void setup() {
        setEnabledO2ACommunication(true, 40000000);

        PredictionService predictionService = SpringContext.getBean(PredictionService.class);

        addBehaviour(new ShareHeaderBehaviour(this));
        addBehaviour(new SendRowsChunkBehaviour(this));
        addBehaviour(new RequestPredictionsBehaviour(this, predictionService));
    }
}
