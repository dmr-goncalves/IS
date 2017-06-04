/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class DFInteraction {

    //find agent by service, name or both
    static public DFAgentDescription[] findAgents(Agent agentOwner, String serviceName, String serviceType) {

        DFAgentDescription dfd = new DFAgentDescription();

        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);
        sd.setName(serviceName);
        dfd.addServices(sd);

        DFAgentDescription[] result = null;
        try {
            result = DFService.search(agentOwner, dfd);
        } catch (FIPAException ex) {
            Logger.getLogger(DFInteraction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    //register with one service
    static public void RegisterInDF(Agent agentToBeRegistered, String serviceName, String serviceType) {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agentToBeRegistered.getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);
        sd.setName(serviceName);
        dfd.addServices(sd);

        try {
            DFService.register(agentToBeRegistered, dfd);
        } catch (FIPAException ex) {
            Logger.getLogger(DFInteraction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public void DeregisterDF(Agent myAgent, AID name) {
        try {
            DFService.deregister(myAgent, name);
        } catch (FIPAException ex) {
            Logger.getLogger(DFInteraction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
