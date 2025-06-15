package com.randomforest.jade;

import com.randomforest.agent.FrontAgent;
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
}
