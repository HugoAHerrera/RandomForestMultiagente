package com.randomforest.jade;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

import com.randomforest.jade.agent.OrchestratorAgent;
import com.randomforest.jade.agent.PredictionAgent;
import com.randomforest.service.AgentCommunicationService;

@Configuration
public class MainContainerLauncher {

    private AgentContainer mainContainer;

    @Autowired
    private AgentCommunicationService agentCommunicationService;

    @PostConstruct
    public void startJade() {
        try {
            Runtime rt = Runtime.instance();

            Profile profile = new ProfileImpl();

            profile.setParameter(Profile.MAIN, "true");
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.MAIN_PORT, "1200");
            profile.setParameter(Profile.MTPS, "jade.mtp.http.MessageTransportProtocol(http://localhost:7778/acc)");
            //profile.setParameter("jade_mtp_http_port", "7781");
            profile.setParameter(Profile.GUI, "false");

            mainContainer = rt.createMainContainer(profile);
            
            AgentController orchestrator = mainContainer.createNewAgent(
                    "orchestrator",
                    OrchestratorAgent.class.getName(),
                    null
            );
            
            orchestrator.start();
            agentCommunicationService.setOrchestrator(orchestrator);

            int agentAmount = 1;
            
            List<String> predictionAgents = new ArrayList<>();

            for (int i = 1; i <= agentAmount; i++) {
                ProfileImpl secondaryProfile = new ProfileImpl();
                secondaryProfile.setParameter(Profile.MAIN, "false");
                secondaryProfile.setParameter(Profile.MAIN_HOST, "localhost");
                secondaryProfile.setParameter(Profile.MAIN_PORT, "1200");
                secondaryProfile.setParameter(Profile.LOCAL_HOST, "localhost");

                AgentContainer secondaryContainer = rt.createAgentContainer(secondaryProfile);

                String agentName = "predictionAgent" + i;
                predictionAgents.add(agentName);

                AgentController predictionAgent = secondaryContainer.createNewAgent(
                    agentName,
                    PredictionAgent.class.getName(),
                    null);
                predictionAgent.start();
            }
            orchestrator.putO2AObject(predictionAgents, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}