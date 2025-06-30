package com.randomforest.jade.agent;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import com.randomforest.jade.behaviour.StoreHeaderBehaviour;
import com.randomforest.jade.behaviour.StoreRowsChunk;
import com.randomforest.jade.behaviour.CreatePredictionBehaviour;

import com.randomforest.dto.HeaderDto;
import com.randomforest.randomforest.DecisionTree;

import org.apache.commons.csv.CSVPrinter;
import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictionAgent extends BaseAgent {
    private HeaderDto header;
    private CSVPrinter csvPrinter;
    private ByteArrayOutputStream csvBuffer;
    private int csvRowCount = 0;
    private int amountTrees;
    private DecisionTree decisionTree;
    private List<List<Object>> trainingData;

    public void incrementCsvRowCount() {
        this.csvRowCount++;
    }

    @Override
    protected void setup() {
        registerToDF("prediction");
        addBehaviour(new StoreHeaderBehaviour(this));
        addBehaviour(new StoreRowsChunk(this));
        addBehaviour(new CreatePredictionBehaviour (this));
    }
}
