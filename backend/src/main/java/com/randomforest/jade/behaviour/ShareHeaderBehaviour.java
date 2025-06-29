package com.randomforest.jade.behaviour;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.randomforest.jade.agent.OrchestratorAgent;
import com.randomforest.jade.agent.BaseAgent;
import com.randomforest.dto.HeaderDto;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class ShareHeaderBehaviour extends CyclicBehaviour {

    public ShareHeaderBehaviour(OrchestratorAgent agent) {
        super(agent);
    }

    @Override
    public void action() {
        OrchestratorAgent agent = (OrchestratorAgent) myAgent;
        Object obj = agent.getO2AObject();

        if (obj instanceof HeaderDto) {
            agent.setFileHeader((HeaderDto) obj);
            
            try {
                List<String> agents = ((BaseAgent) agent).getAgents("prediction");
                agent.setPredictionAgents(agents);
                sendHeaders(agent, agents);

            } catch (Exception e) {
                e.printStackTrace();
            }
           
        } else {
            block();
        }
    }

    private void sendHeaders(OrchestratorAgent ag, List<String> dests) {
        try {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setConversationId("header");

            for (String name : dests) {
                msg.addReceiver(ag.getAID(name));
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(ag.getFileHeader());
            oos.flush();

            msg.setByteSequenceContent(bos.toByteArray());
            ag.send(msg);

            int amountTrees = 100 / dests.size();
            ACLMessage msgAmount = new ACLMessage(ACLMessage.INFORM);
            msgAmount.setConversationId("header");
            for (String name : dests) {
                msgAmount.addReceiver(ag.getAID(name));
            }
            ByteArrayOutputStream bosAmount = new ByteArrayOutputStream();
            ObjectOutputStream oosAmount = new ObjectOutputStream(bosAmount);
            oosAmount.writeObject(Integer.valueOf(amountTrees));
            oosAmount.flush();
            msgAmount.setByteSequenceContent(bosAmount.toByteArray());
            ag.send(msgAmount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
