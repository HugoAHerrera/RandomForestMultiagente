package conec;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainContainerLauncher {
    public static void main(String[] args) {
        try {
            // Obtener la instancia del runtime de JADE
            Runtime rt = Runtime.instance();
            
            // Crear el perfil para el Main Container
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN, "true");
            profile.setParameter(Profile.MAIN_HOST, "192.168.1.172");
            // Activamos la GUI (RMA)
            profile.setParameter(Profile.GUI, "true");
            
            // Crear el Main Container
            AgentContainer mainContainer = rt.createMainContainer(profile);
            System.out.println("Main Container iniciado en 'localhost' con GUI.");
            
            // Crear y arrancar el agente WorkAgent2
            // Ahora se utiliza "conec.WorkAgent2" porque la clase se encuentra en el paquete 'conec'
            AgentController workAgent2 = mainContainer.createNewAgent(
                    "worker2",
                    "conec.WorkAgent2",
                    null
            );
            workAgent2.start();
            System.out.println("Agente WorkAgent2 inicializado y en ejecución.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
