package Common;

import javax.xml.bind.JAXBException;
import org.netbeans.xml.schema.characterschema.TCharacter;
import org.netbeans.xml.schema.characterschema.TPositionAndOrientation;
import Entities.Character;
import Player.Player;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.netbeans.xml.schema.characterschema.ObjectFactory;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class Serialization {

    public static String serializeCharacter(TCharacter myCharacter) throws JAXBException {
        //Criar o conteudo da mensagem com um objeto TCharacter serializado 
        String serializedCharacter = new String();

        StringWriter sw = new StringWriter();

        JAXBContext jaxbContext = JAXBContext.newInstance("org.netbeans.xml.schema.characterschema");
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        ObjectFactory obj = new ObjectFactory();

        JAXBElement<TCharacter> jaxbElem = obj.createMyCharacter(myCharacter);
        jaxbMarshaller.marshal(jaxbElem, sw);

        serializedCharacter = sw.toString();

        return serializedCharacter;
    }

    public static TCharacter deserializeCharacter(String content) throws JAXBException {
        //Fazer a operacao inversa da funcao serializeCharacter
        TCharacter tCharacter;
        JAXBElement<TCharacter> myCharacter;

        JAXBContext jaxbContext = JAXBContext.newInstance("org.netbeans.xml.schema.characterschema");
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        myCharacter = (JAXBElement<TCharacter>) jaxbUnmarshaller.unmarshal(new StringReader(content));

        tCharacter = myCharacter.getValue();

        return tCharacter;
    }

    public static TCharacter convertCharacter(Character c) {
        TCharacter myCharacter = new TCharacter();
        myCharacter.setName(c.getName());
        myCharacter.setDescription(c.getDescription());
        myCharacter.setHealthPoints(c.getHealthPoints());
        myCharacter.setWeaponPoints(c.getWeaponPoints());
        myCharacter.setType(c.getType());
        myCharacter.setStatus(c.getStatus());
        myCharacter.setTracks(c.getTracks());
        TPositionAndOrientation myPreviousPositionAndOrientation = new TPositionAndOrientation();
        myPreviousPositionAndOrientation.setLatitude(c.getPreviousPAndO().getLatitude());
        myPreviousPositionAndOrientation.setLongitude(c.getPreviousPAndO().getLongitude());
        myPreviousPositionAndOrientation.setOrientation(c.getPreviousPAndO().getOrientation());
        TPositionAndOrientation myCurrentPositionAndOrientation = new TPositionAndOrientation();
        myCurrentPositionAndOrientation.setLatitude(c.getPositionAndOrientation().getLatitude());
        myCurrentPositionAndOrientation.setLongitude(c.getPositionAndOrientation().getLongitude());
        myCurrentPositionAndOrientation.setOrientation(c.getPositionAndOrientation().getOrientation());
        myCharacter.setPreviousPAndO(myPreviousPositionAndOrientation);
        myCharacter.setCurrentPAndO(myCurrentPositionAndOrientation);
        return myCharacter;
    }

    public static Player revertCharacter(TCharacter myChar) {
        TPositionAndOrientation prevTPAndO, currTPAndO;
        PositionAndOrientation prevPAndO, currPAndO;
        if (myChar.getPreviousPAndO() != null) {
            prevTPAndO = myChar.getPreviousPAndO();
            prevPAndO = new PositionAndOrientation(prevTPAndO.getLongitude(),
                    prevTPAndO.getLatitude(), prevTPAndO.getOrientation());
        } else {
            prevTPAndO = null;
            prevPAndO = null;
        }

        if (myChar.getCurrentPAndO() != null) {
            currTPAndO = myChar.getCurrentPAndO();
            currPAndO = new PositionAndOrientation(currTPAndO.getLongitude(),
                    currTPAndO.getLatitude(), currTPAndO.getOrientation());
        } else {
            currTPAndO = null;
            currPAndO = null;
        }

        Player myPlayer = new Player(myChar.getName(), myChar.getDescription(),
                myChar.getHealthPoints(), myChar.getWeaponPoints(),
                myChar.getStatus(), myChar.getTracks(), prevPAndO, currPAndO,
                null);
        return myPlayer;
    }
}
