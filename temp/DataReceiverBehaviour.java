package com.randomforest.jade.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.Agent;

import com.randomforest.dto.HeaderDto;
import com.randomforest.dto.ChunkDto;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataReceiverBehaviour extends CyclicBehaviour {

    private HeaderDto header;
    private final List<Map<String, String>> receivedRows = new CopyOnWriteArrayList<>();
    private boolean headerRequested = false;

    public static final String HEADER_REQUEST = "send-header";

    public DataReceiverBehaviour(Agent a) {
        super(a);
        myAgent.putO2AObject(receivedRows, Agent.QUEUE_MODE);
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            try {
                byte[] content = msg.getByteSequenceContent();
                if (content != null) {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(content));
                    Object data = ois.readObject();

                    if (data instanceof HeaderDto) {
                        header = (HeaderDto) data;
                        System.out.println("[" + myAgent.getLocalName() + "] Header recibido.");
                        headerRequested = false;
                    }

                    else if (data instanceof ChunkDto) {
                        if (header == null && !headerRequested) {
                            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                            request.addReceiver(msg.getSender());
                            request.setContent(HEADER_REQUEST);
                            myAgent.send(request);
                            headerRequested = true;
                            System.out.println("[" + myAgent.getLocalName() + "] No hay header. Solicitando...");
                        } else {
                            ChunkDto chunk = (ChunkDto) data;
                            receivedRows.addAll(chunk.getRows());
                            System.out.println("[" + myAgent.getLocalName() + "] Chunk recibido con " + chunk.getRows().size() + " filas.");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
