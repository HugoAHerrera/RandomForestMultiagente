package com.randomforest.agent;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class FrontAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("FrontAgent iniciado.");

        addBehaviour(new Behaviour() {
            private boolean done = false;

            @Override
            public void action() {
                // Crear y enviar mensajes a Worker1 y Worker2
                ACLMessage msg1 = new ACLMessage(ACLMessage.REQUEST);
                msg1.addReceiver(getAID("worker1"));
                msg1.setContent("dame_numero");
                send(msg1);

                ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
                msg2.addReceiver(getAID("worker2"));
                msg2.setContent("dame_numero");
                send(msg2);

                System.out.println("Enviado");

                int recibidos = 0;
                int total = 0;

                while (recibidos < 2) {
                    ACLMessage reply = blockingReceive();
                    if (reply != null) {
                        total += Integer.parseInt(reply.getContent());
                        recibidos++;
                    }
                }

                // Guardar el resultado en el sistema para que lo recoja Spring
                getArguments();
                if (getArguments() != null && getArguments().length > 0) {
                    if (getArguments()[0] instanceof java.util.concurrent.CompletableFuture) {
                        @SuppressWarnings("unchecked")
                        java.util.concurrent.CompletableFuture<String> future =
                                (java.util.concurrent.CompletableFuture<String>) getArguments()[0];
                        future.complete(String.valueOf(total));
                    }
                }

                done = true;
            }

            @Override
            public boolean done() {
                return done;
            }
        });
    }
}
