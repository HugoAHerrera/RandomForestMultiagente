package com.randomforest.jade.behaviour;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.randomforest.jade.agent.PredictionAgent;
import com.randomforest.dto.PredictionRequestDto;
import com.randomforest.randomforest.Node;
import com.randomforest.randomforest.SampleResults;
import com.randomforest.randomforest.DecisionTree;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import jade.lang.acl.UnreadableException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CreatePredictionBehaviour extends CyclicBehaviour {

    public CreatePredictionBehaviour(Agent agent) {
        super(agent);
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchConversationId("make-predictions");
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            PredictionAgent agent = (PredictionAgent) myAgent;

            if (agent.getHeader() == null || agent.getCsvBuffer() == null) {
                ACLMessage agreeMsg = msg.createReply();
                agreeMsg.setPerformative(ACLMessage.REFUSE);
                agreeMsg.setContent("Refused");
                myAgent.send(agreeMsg);
            } else {
                ACLMessage agreeMsg = msg.createReply();
                agreeMsg.setPerformative(ACLMessage.AGREE);
                agreeMsg.setContent("Agreed");
                myAgent.send(agreeMsg);
            }
               
            List<List<Object>> trainingData = new ArrayList<>();
            try {
                InputStreamReader isr = new InputStreamReader(
                        new ByteArrayInputStream(agent.getCsvBuffer().toByteArray()),
                        StandardCharsets.UTF_8
                );
                CSVParser parser = new CSVParser(isr, CSVFormat.DEFAULT.withFirstRecordAsHeader());
                Map<String, String> types = agent.getHeader().getTypes();
                for (CSVRecord record : parser) {
                    List<Object> row = new ArrayList<>();
                    for (String column : types.keySet()) {
                        String type = types.get(column);
                        String rawValue = record.get(column);
                        if ("Continua".equalsIgnoreCase(type)) {
                            try {
                                float val = Float.parseFloat(rawValue);
                                row.add(val == (int) val ? (int) val : val);
                            } catch (NumberFormatException e) {
                                row.add(rawValue);
                            }
                        } else {
                            row.add(rawValue);
                        }
                    }
                    trainingData.add(row);
                }
                parser.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Object content = null;
            try{
                content = msg.getContentObject();
            }catch(Exception e) {
                e.printStackTrace();
            }
            
            if (content instanceof ArrayList<?>) {
                ArrayList<?> list = (ArrayList<?>) content;
                if (!list.isEmpty() && list.get(0) instanceof PredictionRequestDto) {
                    @SuppressWarnings("unchecked")
                    ArrayList<PredictionRequestDto> dtoList = (ArrayList<PredictionRequestDto>) list;
                    
                    String taskType = null;
                    String targetColumn = null;

                    for (PredictionRequestDto dto : dtoList) {
                        targetColumn = dto.getTarget();
                        taskType = dto.getTask();
                    }

                    SampleResults result = new SampleResults();
                    Map<String, Object> summary = result.getSampleOutputs();
                    List<Double> accuracies = new ArrayList<>();

                    for (PredictionRequestDto sampleData : dtoList) {
                        if (taskType.equalsIgnoreCase("clasificacion")) {
                            summary.put(sampleData.getName(), new HashMap<String, Integer>());
                        } else {
                            summary.put(sampleData.getName(), new ArrayList<Double>());
                        }
                    }

                    for(int i = 0; i<agent.getAmountTrees(); i++){
                        agent.setDecisionTree(new DecisionTree(taskType, agent.getCsvRowCount(), agent.getHeader(), trainingData, targetColumn));
                        Node node = agent.getDecisionTree().generateDecisionTree();
                        String accuracyStr = agent.getDecisionTree().getAccuracy(node);
                        String clean = accuracyStr.replace(",", ".").replaceAll("[^\\d.\\-]", "");
                        double val = Double.parseDouble(clean);
                        accuracies.add(val);

                        List<String> predictions = agent.getDecisionTree().predictSamples(dtoList, node);

                        for (int j = 0; j < dtoList.size(); j++) {
                            String name = dtoList.get(j).getName();
                            String pred = predictions.get(j);
                            
                            if (taskType.equalsIgnoreCase("clasificacion")) {
                                Map<String, Integer> count = (Map<String, Integer>) summary.get(name);
                                count.merge(pred, 1, Integer::sum);
                            } else {
                                List<Double> values = (List<Double>) summary.get(name);
                                try {
                                    values.add(Double.parseDouble(pred));
                                } catch (NumberFormatException e) {
                                    values.add(0.0);
                                }
                            }
                        }
                    }

                    if (taskType.equalsIgnoreCase("regresion")) {
                        for (String key : new ArrayList<>(summary.keySet())) {
                            List<Double> values = (List<Double>) summary.get(key);
                            double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                            Map<String, Double> wrapped = new HashMap<>();
                            wrapped.put("value", mean);

                            summary.put(key, wrapped);
                        }
                    }

                    double meanAccuracy = accuracies.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    result.setAverageAccuracy(meanAccuracy);

                    ACLMessage replyMsg = msg.createReply();

                    if (result == null) {
                        replyMsg.setPerformative(ACLMessage.FAILURE);
                        replyMsg.setContent("Prediction failed.");
                    } else {
                        replyMsg.setPerformative(ACLMessage.INFORM);
                        try {
                            replyMsg.setContentObject(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                            replyMsg.setPerformative(ACLMessage.FAILURE);
                            replyMsg.setContent("Error.");
                        }
                    }

                    myAgent.send(replyMsg);
                }
            }
        } else {
            block();
        }
    }
}
