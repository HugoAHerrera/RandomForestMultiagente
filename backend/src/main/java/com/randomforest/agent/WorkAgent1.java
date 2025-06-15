package com.randomforest.agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class WorkAgent1 extends Agent {
    @Override
    protected void setup() {
        System.out.println("WorkerAgent1 iniciado.");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getContent().equals("dame_numero")) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("3"); // Número fijo o aleatorio
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }
}
