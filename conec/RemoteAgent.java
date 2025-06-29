package conec;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class RemoteAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + " activo en container remoto.");

        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID("orchestrator", AID.ISLOCALNAME));
        msg.setContent("hola desde " + getLocalName());
        send(msg);
    }
}
