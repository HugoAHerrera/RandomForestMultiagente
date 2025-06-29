package com.randomforest.jade.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import com.randomforest.jade.agent.OrchestratorAgent;
import com.randomforest.dto.ChunkDto;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class ChunkSenderBehaviour extends CyclicBehaviour {
    public ChunkSenderBehaviour(OrchestratorAgent agent) { super(agent); }

    @Override
    public void action() {
        OrchestratorAgent ag = (OrchestratorAgent) myAgent;
        Object obj = ag.getO2AObject();
        if (obj instanceof ChunkDto) {
            String agentName = ag.nextPredictionAgent();
            try {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(ag.getAID(agentName));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(obj);
                oos.flush();
                msg.setByteSequenceContent(bos.toByteArray());
                ag.send(msg);
                System.out.println("[" + ag.getLocalName() + "] Chunk enviado a " + agentName);
            } catch (Exception e) { e.printStackTrace(); }
        } else block();
    }
}
