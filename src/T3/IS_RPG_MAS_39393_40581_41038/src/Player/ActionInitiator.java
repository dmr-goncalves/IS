/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import Common.Constants;
import Common.Serialization;
import Entities.Character;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.netbeans.xml.schema.characterschema.TCharacter;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class ActionInitiator extends AchieveREInitiator {

    private Player player;

    public static ACLMessage createInitialMessage(String actionOntology, Character c, AID world) {

        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setOntology(actionOntology);
        msg.addReceiver(world);
        TCharacter tChar = Serialization.convertCharacter(c);

        try {
            msg.setContent(Serialization.serializeCharacter(tChar));
        } catch (JAXBException ex) {
            Logger.getLogger(ActionInitiator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }

    public ActionInitiator(Agent a, ACLMessage msg) {
        super(a, msg);
        player = (Player) a;
    }

    @Override
    protected void handleAgree(ACLMessage agree) {
        System.out.println(myAgent.getLocalName() + ": AGREE message received.");
        System.out.println("It's content is: " + agree.getContent());
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        try {
            Character c = Serialization.revertCharacter(Serialization.deserializeCharacter(inform.getContent()));
            player.setStatus(c.getStatus());
            player.setType(c.getType());
            player.setEntityName(c.getEntityName());
            player.setDescription(c.getDescription());
            player.setHealthPoints(c.getHealthPoints());
            player.setWeaponPoints(c.getWeaponPoints());
            player.setPositionAndOrientation(c.getPositionAndOrientation());
            player.setPreviousPAndO(c.getPreviousPAndO());
            player.setTracks(c.getTracks());
            player.UpdateForm(c.getPositionAndOrientation(), c.getStatus(), c.getTracks());
            if (inform.getOntology().equals(Constants.ONTOLOGY_TRACK)) {
                player.canDoGetTracks = true;
            }else if(inform.getOntology().equals(Constants.ONTOLOGY_REGISTER)){
                player.canDoAI = true;
            }
            
        } catch (JAXBException ex) {
            Logger.getLogger(ActionInitiator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
