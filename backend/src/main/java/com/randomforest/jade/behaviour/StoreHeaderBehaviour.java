package com.randomforest.jade.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.randomforest.dto.HeaderDto;
import com.randomforest.jade.agent.PredictionAgent;

public class StoreHeaderBehaviour extends CyclicBehaviour {

    public StoreHeaderBehaviour(PredictionAgent agent) {
        super(agent);
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId("header");
        ACLMessage msg = myAgent.receive(template);
        if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
            try {
                byte[] content = msg.getByteSequenceContent();
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(content));
                Object receivedObject = ois.readObject();

                PredictionAgent agent = (PredictionAgent) myAgent;

                if (receivedObject instanceof HeaderDto) {
                    HeaderDto newHeader = (HeaderDto) receivedObject;
                    agent.setHeader(newHeader);

                    ByteArrayOutputStream csvMemory = new ByteArrayOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(csvMemory));
                    String[] columns = newHeader.getTypes().keySet().toArray(new String[0]);
                    CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(columns));

                    agent.setCsvPrinter(printer);
                    agent.setCsvBuffer(csvMemory);
                    agent.setCsvRowCount(0);

                } 
                else if (receivedObject instanceof Integer) {
                    int amountTrees = (Integer) receivedObject;
                    agent.setAmountTrees(amountTrees);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
