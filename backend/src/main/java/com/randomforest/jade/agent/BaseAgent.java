package com.randomforest.jade.agent;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.List;

public class BaseAgent extends Agent {

    protected void registerToDF(String type) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getAgents(String type) {
        List<String> agents = new ArrayList<>();
        try {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            template.addServices(sd);

            DFAgentDescription[] result = DFService.search(this, template);

            for (DFAgentDescription agentDesc : result) {
                agents.add(agentDesc.getName().getLocalName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return agents;
    }
}
