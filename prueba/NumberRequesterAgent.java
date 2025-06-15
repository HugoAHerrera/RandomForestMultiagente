package prueba;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class NumberRequesterAgent extends Agent {
    protected void setup() {
        System.out.println(getLocalName() + " listo.");

        addBehaviour(new jade.core.behaviours.OneShotBehaviour(this) {
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                AID receiver = new AID("number-provider@Platform2", AID.ISGUID);
                receiver.addAddresses("http://192.168.1.168:7778/acc");
                msg.addReceiver(receiver);

                msg.setContent("Dame un número");
                send(msg);
                System.out.println("Solicitud enviada a number-provider@Platform2");

                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage reply = blockingReceive(mt, 5000);
                if (reply != null) {
                    System.out.println("Número recibido: " + reply.getContent());
                } else {
                    System.out.println("No hubo respuesta.");
                }
            }
        });
    }
}
