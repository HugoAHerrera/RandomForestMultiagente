package com.randomforest.jade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;
import org.springframework.stereotype.Component;
import com.randomforest.jade.agent.HolaAgent;
import com.randomforest.jade.agent.ReceptorAgent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

@Configuration
public class JadeBoot {
    /*
    private AgentController holaAgent;

    @PostConstruct
    public void startJade() {
        int receptorAmount = 3;

        Runtime runtime = Runtime.instance();

        Profile mainProfile = new ProfileImpl();
        mainProfile.setParameter(Profile.GUI, "false");
        ContainerController mainContainer = runtime.createMainContainer(mainProfile);
        System.out.println("Main container creado");

        List<String> receptorNames = new ArrayList<>();
        List<ContainerController> agentContainers = new ArrayList<>();

        for (int i = 0; i < receptorAmount; i++) {
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            ContainerController agentContainer = runtime.createAgentContainer(profile);
            agentContainers.add(agentContainer);

            String receptorName = "ReceptorAgent" + i;
            receptorNames.add(receptorName);

            try {
                AgentController receptor = agentContainer.createNewAgent(receptorName, ReceptorAgent.class.getName(), null);
                receptor.start();
                System.out.println("ReceptorAgent creado: " + receptorName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Object[] args = new Object[] { receptorNames };
            this.holaAgent = mainContainer.createNewAgent("HolaAgent", HolaAgent.class.getName(), args);
            this.holaAgent.start();
            System.out.println("HolaAgent creado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public AgentController holaAgent() {
        return this.holaAgent;
    }*/
}



/*import com.randomforest.agent.FrontAgent;
import com.randomforest.agent.WorkAgent1;
import com.randomforest.agent.WorkAgent2;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;

import java.util.concurrent.CompletableFuture;

public class JadeBoot {

    public static void startJadePlatform(CompletableFuture<String> result) throws Exception {
        Runtime rt = Runtime.instance();

        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "false");

        AgentContainer container = rt.createMainContainer(p);

        // Crear agentes
        AgentController worker1 = container.createNewAgent("worker1", WorkAgent1.class.getName(), null);
        AgentController worker2 = container.createNewAgent("worker2", WorkAgent2.class.getName(), null);
        AgentController front = container.createNewAgent("front", FrontAgent.class.getName(), new Object[]{result});

        // Lanzar agentes
        worker1.start();
        worker2.start();
        front.start();
    }
}*/
