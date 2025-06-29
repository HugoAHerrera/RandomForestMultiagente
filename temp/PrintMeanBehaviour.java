package com.randomforest.jade.behaviour;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;
import java.util.Map;

public class PrintMeanBehaviour extends TickerBehaviour {

    private boolean alreadyPrinted = false;

    public PrintMeanBehaviour(Agent a) {
        super(a, 500); // cada 0.5 segundos
    }

    @Override
    protected void onTick() {
        ACLMessage msg = myAgent.receive();
        if (msg != null && "imprimo".equals(msg.getContent()) && !alreadyPrinted) {
            Object obj = myAgent.getO2AObject();
            if (obj instanceof List) {
                List<Map<String, String>> rows = (List<Map<String, String>>) obj;
                if (!rows.isEmpty()) {
                    double sum = 0;
                    int count = 0;
                    for (Map<String, String> row : rows) {
                        String val = row.get("petal_length");
                        if (val != null) {
                            try {
                                sum += Double.parseDouble(val);
                                count++;
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                    double mean = (count > 0) ? sum / count : 0;
                    System.out.println("[" + myAgent.getLocalName() + "] Media de petal_length: " + mean);
                    alreadyPrinted = true;
                }
            }
        } else {
            block();
        }
    }
}
