import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Sender extends Agent {
    protected void setup() {
        System.out.println("Sender iniciado");

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        AID receiver = new AID("receiver@Main-Container", AID.ISGUID);
        receiver.addAddresses("http://192.168.1.172:7778/acc");

        msg.addReceiver(receiver);
        msg.setContent("1234");
        send(msg);

        System.out.println("Número enviado");
        doDelete();
    }
}
