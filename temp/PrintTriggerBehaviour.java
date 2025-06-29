package com.randomforest.jade.behaviour;

import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import com.randomforest.jade.agent.OrchestratorAgent;

public class PrintTriggerBehaviour extends TickerBehaviour {
    private boolean printed = false;

    public PrintTriggerBehaviour(OrchestratorAgent agent) {
        super(agent, 500);
    }

    @Override
    protected void onTick() {
        OrchestratorAgent ag = (OrchestratorAgent) myAgent;
        if (!ag.hasRecentChunks() && !printed && ag.hasPredictionAgents()) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            for (String name : ag.getPredictionAgents())
                msg.addReceiver(ag.getAID(name));
            msg.setContent("print-mean");
            ag.send(msg);
            printed = true;
            System.out.println("[" + ag.getLocalName() + "] Solicitada media a agentes.");
        }
    }
}
