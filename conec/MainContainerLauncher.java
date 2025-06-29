package conec;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import conec.NetworkUtils;

public class MainContainerLauncher {
    // Variable para almacenar la referencia al main container y usarla en otros agentes
    public static AgentContainer mainContainer;

    public static void main(String[] args) {
        try {
            // Obtener la instancia del runtime de JADE
            Runtime rt = Runtime.instance();
            
            // Configurar el perfil para el Main Container
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN, "true");
            // Usa la IP apropiada. Aquí se asume la IP local o la IP publicada necesaria.
            profile.setParameter(Profile.MAIN_HOST, NetworkUtils.getRealWiFiAddress());
            // Activar la GUI (RMA)
            profile.setParameter(Profile.GUI, "true");
            
            // Crear el Main Container
            mainContainer = rt.createMainContainer(profile);
            System.out.println("Main Container iniciado en '192.168.1.172' con GUI.");
            
            // Crear y arrancar el agente WorkAgent2
            AgentController workAgent2 = mainContainer.createNewAgent(
                    "worker2",
                    "conec.WorkAgent2",
                    null
            );
            workAgent2.start();
            System.out.println("Agente WorkAgent2 inicializado y en ejecuci\u00F3n.");
            
            // Crear y arrancar el OrchestratorAgent (encargado de gestionar la creaci\u00F3n de agentes en los containers remotos)
            AgentController orchestrator = mainContainer.createNewAgent(
                    "orchestrator",
                    "conec.OrchestratorAgent",
                    null
            );
            orchestrator.start();
            System.out.println("OrchestratorAgent iniciado.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
