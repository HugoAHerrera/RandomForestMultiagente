package com.randomforest.jade.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.wrapper.AgentController;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.randomforest.dto.ChunkDto;
import com.randomforest.jade.agent.OrchestratorAgent;

public class SendRowsChunkBehaviour extends CyclicBehaviour {

    int cuenta = 0;

    public SendRowsChunkBehaviour(OrchestratorAgent agent) {
        super(agent);
    }

    @Override
    public void action() {
        OrchestratorAgent agent = (OrchestratorAgent) myAgent;
        Object obj = agent.getO2AObject();

        if (obj instanceof ChunkDto) {
            ChunkDto chunk = (ChunkDto) obj;

            List<String> predictionAgents = agent.getPredictionAgents();
            if (predictionAgents == null || predictionAgents.isEmpty()) {
                block();
                return;
            }

            try {
                String destName = predictionAgents.get(agent.getCurrentAgent());
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(agent.getAID(destName));
                msg.setConversationId("rowsChunk");

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(chunk);
                oos.flush();

                msg.setByteSequenceContent(bos.toByteArray());
                agent.send(msg);

                int next = agent.getCurrentAgent() + 1;
                if (next >= predictionAgents.size()) next = 0;
                agent.setCurrentAgent(next);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (obj != null) {
                try {
                    agent.putO2AObject(obj, AgentController.ASYNC);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        block();
    }

}
