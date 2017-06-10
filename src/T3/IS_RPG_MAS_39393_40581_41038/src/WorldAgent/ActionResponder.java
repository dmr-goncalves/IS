package WorldAgent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import Common.Constants;
import Common.Serialization;
import Entities.Character;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class ActionResponder extends AchieveREResponder {

    private WorldAgent world;

    public static MessageTemplate getMT() {
        return MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
    }

    public ActionResponder(Agent a, MessageTemplate mt) {
        super(a, mt);
        world = (WorldAgent) a;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        ACLMessage msg = request.createReply();
        msg.setPerformative(ACLMessage.AGREE);
        msg.setContent(request.getContent());
        return msg;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage answer) {
        ACLMessage msg = request.createReply();
        msg.setPerformative(ACLMessage.INFORM);

        try {
            Character c = Serialization.revertCharacter(Serialization.deserializeCharacter(request.getContent()));

            switch (request.getOntology()) {
                case Constants.ONTOLOGY_REGISTER:
                    c = world.setupPlayer(c);
                    break;
                case Constants.ONTOLOGY_ATTACK:
                    c = world.commitAttack(c);
                    break;
                case Constants.ONTOLOGY_MOVE:
                    c = world.commitMove(c);
                    break;
                case Constants.ONTOLOGY_TRACK:
                    c = world.whatCanCTrack(c);
                    break;
                case Constants.ONTOLOGY_USE:
                    c = world.commitUse(c);
                    break;
            }
            world.updateMapGUI();
            world.printWorld();
            msg.setContent(Serialization.serializeCharacter(Serialization.convertCharacter(c)));
        } catch (JAXBException ex) {
            Logger.getLogger(ActionResponder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return msg;
    }
}
