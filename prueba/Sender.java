import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Sender extends Agent {
    protected void setup() {
        System.out.println("Sender iniciado");
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("receiver", AID.ISLOCALNAME));
        msg.setContent("1234");
        send(msg);
        System.out.println("Número enviado");
        doDelete();
    }
}
