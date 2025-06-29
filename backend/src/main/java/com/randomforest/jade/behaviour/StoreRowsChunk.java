package com.randomforest.jade.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.randomforest.dto.ChunkDto;
import com.randomforest.dto.HeaderDto;
import com.randomforest.jade.agent.PredictionAgent;

public class StoreRowsChunk extends CyclicBehaviour {

    public StoreRowsChunk(PredictionAgent agent) {
        super(agent);
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId("rowsChunk");
        ACLMessage msg = myAgent.receive(template);

        if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
            try {
                PredictionAgent agent = (PredictionAgent) myAgent;
                HeaderDto header = agent.getHeader();

                byte[] content = msg.getByteSequenceContent();
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(content));
                ChunkDto chunk = (ChunkDto) ois.readObject();

                for (var row : chunk.getRows()) {

                    List<Object> parsedRow = new ArrayList<>();
                    boolean skipRow = false;

                    for (String column : header.getTypes().keySet()) {
                        String type = header.getTypes().get(column);
                        Object rawValue = row.get(column);

                        if (rawValue == null) {
                            skipRow = true;
                            break;
                        }

                        if ("Continua".equalsIgnoreCase(type)) {
                            try {
                                float val = Float.parseFloat(rawValue.toString());
                                if (val == (int) val) {
                                    parsedRow.add((int) val);
                                } else {
                                    parsedRow.add(val);
                                }
                            } catch (NumberFormatException e) {
                                skipRow = true;
                                break;
                            }
                        } else {
                            parsedRow.add(rawValue.toString());
                        }
                    }

                    agent.getCsvPrinter().printRecord(parsedRow);
                    agent.getCsvPrinter().flush();
                    agent.incrementCsvRowCount();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
