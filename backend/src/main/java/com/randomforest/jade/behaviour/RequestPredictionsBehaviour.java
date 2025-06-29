package com.randomforest.jade.behaviour;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.lang.acl.MessageTemplate;

import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

import com.randomforest.service.PredictionService;
import com.randomforest.dto.PredictionRequestDto;
import com.randomforest.dto.PredictionResultDto;
import com.randomforest.randomforest.SampleResults;
import com.randomforest.jade.agent.OrchestratorAgent;

public class RequestPredictionsBehaviour extends CyclicBehaviour {

    private final PredictionService predictionService;

    public RequestPredictionsBehaviour(OrchestratorAgent agent, PredictionService predictionService) {
        super(agent);
        this.predictionService = predictionService;
    }

    @Override
    public void action() {
        OrchestratorAgent agent = (OrchestratorAgent) myAgent;
        Object obj = agent.getO2AObject();

        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;

            if (!list.isEmpty() && list.get(0) instanceof PredictionRequestDto) {
                @SuppressWarnings("unchecked")
                List<PredictionRequestDto> dtoList = (List<PredictionRequestDto>) list;
                agent.setPredictionsList(dtoList);

                if (!agent.getPredictionAgents().isEmpty()) {
                    ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
                    requestMsg.setConversationId("make-predictions");
                    ArrayList<PredictionRequestDto> serializableList = new ArrayList<>(dtoList);
                    try {
                        requestMsg.setContentObject(serializableList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (String agentName : agent.getPredictionAgents()) {
                        requestMsg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
                    }

                    myAgent.send(requestMsg);

                    int expectedAnswers = 0;

                    for (int i = 0; i < agent.getPredictionAgents().size(); i++) {
                        MessageTemplate mtAgree = MessageTemplate.and(
                            MessageTemplate.MatchConversationId("make-predictions"),
                            MessageTemplate.MatchPerformative(ACLMessage.AGREE)
                        );
                        ACLMessage agreeReply = myAgent.blockingReceive(mtAgree);
                        if (agreeReply != null) {
                            expectedAnswers++;
                        }
                    }

                    int responsesReceived = 0;
                    List<SampleResults> resultsList = new ArrayList<>();

                    while (responsesReceived < expectedAnswers) {
                        MessageTemplate mtResponse = MessageTemplate.and(
                            MessageTemplate.MatchConversationId("make-predictions"),
                            MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                MessageTemplate.MatchPerformative(ACLMessage.FAILURE)
                            )
                        );

                        ACLMessage reply = myAgent.blockingReceive(mtResponse);
                        if (reply != null) {
                            responsesReceived++;

                            if (reply.getPerformative() == ACLMessage.INFORM) {
                                try {
                                    SampleResults agentResults = (SampleResults) reply.getContentObject();
                                    resultsList.add(agentResults);
                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    List<PredictionResultDto> predictionResults = new ArrayList<>();
                    Map<String, List<Object>> accumulatedSamples = new HashMap<>();
                    Map<String, List<Double>> accuracyMap = new HashMap<>();

                    for (SampleResults sampleResult : resultsList) {
                        Map<String, Object> sampleOutputs = sampleResult.getSampleOutputs();

                        for (Map.Entry<String, Object> entry : sampleOutputs.entrySet()) {
                            String sampleName = entry.getKey();
                            Object value = entry.getValue();

                            if (value instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, ?> content = (Map<String, ?>) value;

                                accumulatedSamples.putIfAbsent(sampleName, new ArrayList<>());
                                accumulatedSamples.get(sampleName).add(content);

                                accuracyMap.putIfAbsent(sampleName, new ArrayList<>());
                                accuracyMap.get(sampleName).add(sampleResult.getAverageAccuracy());
                            }
                        }
                    }

                    for (Map.Entry<String, List<Object>> entry : accumulatedSamples.entrySet()) {
                        String sampleName = entry.getKey();
                        List<Object> values = entry.getValue();

                        PredictionRequestDto predictionRequest = agent.getPredictionsList()
                            .stream()
                            .filter(dto -> dto.getName().equals(sampleName))
                            .findFirst()
                            .orElse(null);

                        if (predictionRequest == null) continue;

                        List<Double> accuracies = accuracyMap.get(sampleName);
                        double averageAccuracy = accuracies.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                        Map<String, String> targetMap;

                        if ("clasificacion".equalsIgnoreCase(predictionRequest.getTask())) {
                            Map<String, Integer> counts = new HashMap<>();

                            for (Object classificationObj : values) {
                                if (classificationObj instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Integer> classes = (Map<String, Integer>) classificationObj;
                                    for (Map.Entry<String, Integer> classEntry : classes.entrySet()) {
                                        counts.merge(classEntry.getKey(), classEntry.getValue(), Integer::sum);
                                    }
                                }
                            }

                            String predictedClass = Collections.max(counts.entrySet(), Map.Entry.comparingByValue()).getKey();
                            targetMap = Map.of(predictionRequest.getTarget(), predictedClass);

                        } else if ("regresion".equalsIgnoreCase(predictionRequest.getTask())) {
                            double sumValues = 0.0;
                            int valueCount = 0;

                            for (Object regressionObj : values) {
                                if (regressionObj instanceof Map) {
                                    Object rawValue = ((Map<?, ?>) regressionObj).get("value");
                                    if (rawValue instanceof Number) {
                                        sumValues += ((Number) rawValue).doubleValue();
                                        valueCount++;
                                    }
                                }
                            }

                            double averageValue = (valueCount > 0) ? Math.round((sumValues / valueCount) * 100.0) / 100.0 : 0.0;
                            targetMap = Map.of(predictionRequest.getTarget(), String.valueOf(averageValue));

                        } else {
                            continue;
                        }

                        PredictionResultDto predictionResult = new PredictionResultDto(
                            predictionRequest.getUserName(),
                            targetMap,
                            predictionRequest.getTask(),
                            predictionRequest.getFeatures(),
                            predictionRequest.getFileName(),
                            averageAccuracy
                        );

                        predictionResults.add(predictionResult);
                    }

                    predictionService.storeResults(predictionResults);

                }
            }

        } else {
            block();
        }
    }
}
