package conec;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

import java.util.HashSet;
import java.util.Set;

public class OrchestratorAgent extends Agent {

    private Set<String> remoteAgents = new HashSet<>();

    @Override
    protected void setup() {
        System.out.println("Orchestrator iniciado.");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getContent().startsWith("register:")) {
                    String agentName = msg.getSender().getLocalName();

                    if (!remoteAgents.contains(agentName)) {
                        remoteAgents.add(agentName);
                        System.out.println("Registrado contenedor remoto desde agente: " + agentName);

                        // Envía orden para crear RemoteAgent
                        ACLMessage order = new ACLMessage(ACLMessage.REQUEST);
                        order.addReceiver(new AID(agentName, AID.ISLOCALNAME));
                        order.setContent("launch_remote_agent");
                        send(order);
                    }
                } else {
                    block();
                }
            }
        });
    }
}

