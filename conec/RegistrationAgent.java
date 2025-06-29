package conec;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;
import jade.wrapper.AgentController;

public class RegistrationAgent extends Agent {
    @Override
    protected void setup() {
        String localName = getLocalName();
        System.out.println("RegistrationAgent iniciado: " + localName);

        // Notifica al Orchestrator
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("orchestrator", AID.ISLOCALNAME));
        msg.setContent("register:" + localName);
        send(msg);

        // Espera comandos
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage incoming = receive();
                if (incoming != null && "launch_remote_agent".equals(incoming.getContent())) {
                    try {
                        AgentController agent = getContainerController().createNewAgent(
                            "remote-" + localName,
                            "conec.RemoteAgent",
                            null
                        );
                        agent.start();
                        System.out.println("RemoteAgent creado localmente por " + localName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    block();
                }
            }
        });
    }
}
