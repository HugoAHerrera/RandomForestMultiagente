package conec;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class WorkAgent2 extends Agent {
    @Override
    protected void setup() {
        System.out.println("WorkAgent2 iniciado.");
        
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if ("dame_numero".equals(msg.getContent())) {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("7");
                        send(reply);
                    }
                } else {
                    block();
                }
            }
        });
    }
}
