/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorldAgent;

import Common.DFInteraction;
import Common.PositionAndOrientation;
import Entities.Entity;
import Entities.Healer;
import Entities.Terrain;
import Entities.Character;
import Entities.Goblin;
import Entities.Trap;
import Entities.Treasure;
import GameGUI.MapGUI;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class WorldAgent extends Agent {

    HashMap<PositionAndOrientation, Entity> map = new HashMap<>();
    private int maxLongitude;
    private int maxLatitude;
    private int numberOfHealers;
    private int numberOfGoblins;
    private int numberOfTraps;
    private Random generator = new Random();
    private MapGUI mapGUI;

    private void initializeWorld() {
        for (int i = 0; i < maxLongitude; i++) {
            for (int j = 0; j < maxLatitude; j++) {
                PositionAndOrientation pAo = new PositionAndOrientation(i, j, PositionAndOrientation.NORTH);
                Terrain nt = new Terrain("Terrain tile" + i + " " + j, "Normal Terrain", pAo);
                map.put(pAo, nt);
            }
        }
    }

    public void printWorld() {
        Entity[][] mapForPrint = mapToMatrix();
        for (int i = maxLatitude - 1; i >= 0; i--) {
            for (int j = 0; j < maxLongitude; j++) {
                Entity e = mapForPrint[j][i];
                System.out.print(e.identify() + " ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    private Entity[][] mapToMatrix() {
        Entity[][] mapForPrint = new Entity[maxLongitude][maxLatitude];
        for (Entity ent : map.values()) {
            mapForPrint[ent.getPositionAndOrientation().getLongitude()][ent.getPositionAndOrientation().getLatitude()] = ent;
        }
        return mapForPrint;
    }

    private PositionAndOrientation createRandomWorldPosition() {
        return new PositionAndOrientation(generator.nextInt(maxLongitude), generator.nextInt(maxLatitude), generator.nextInt(4));
    }

    private PositionAndOrientation createRandomAndFreeWorldPosition() {
        Entity e;
        PositionAndOrientation p;
        do {
            p = createRandomWorldPosition();
            e = map.get(p);
        } while (e.leaveTracks() != Entity.NO_TRACKS);

        return p;
    }

    private boolean deployEntity(Entity e) {
        jade.core.Runtime rtm = jade.core.Runtime.instance();
        Profile p = new ProfileImpl(false);
        ContainerController cc = rtm.createAgentContainer(p);
        try {
            AgentController ac = cc.acceptNewAgent(e.getEntityName(), e);
            ac.start();
        } catch (StaleProxyException ex) {
            System.out.println("Could not deploy agent in the platform");
            return false;
        }
        return true;
    }

    private void setupHealers() {
        for (int i = 0; i < numberOfHealers; i++) {
            PositionAndOrientation pAo = createRandomAndFreeWorldPosition();
            Healer h = new Healer("Healer" + i, "this character restores your health", 100, 0, Character.ALIVE, Entity.NO_TRACKS, pAo, pAo);
            map.put(pAo, h);
            deployEntity(h);
        }
    }

    private void setupGoblins() {
        for (int i = 0; i < numberOfGoblins; i++) {
            PositionAndOrientation pAo = createRandomAndFreeWorldPosition();
            Goblin g = new Goblin("Goblin" + i, "the nasty goblin fight it or run away", 100, 20, Character.ALIVE, Entity.NO_TRACKS, pAo, pAo);
            map.put(pAo, g);
            deployEntity(g);
        }
    }

    private void setupTraps() {
        for (int i = 0; i < numberOfTraps; i++) {
            PositionAndOrientation pAo = createRandomAndFreeWorldPosition();
            Trap t = new Trap("Trap" + i, "don't fall on the trap", pAo);
            map.put(pAo, t);
        }
    }

    private void setupTreasure() {
        PositionAndOrientation pAo = createRandomAndFreeWorldPosition();
        Treasure t = new Treasure("Treasure", "you really want this", pAo);
        map.put(pAo, t);
    }

    private void handleDuplicates(Character c) {
        PositionAndOrientation pAo = null;
        for (Entry e : map.entrySet()) {
            if (((Entity) e.getValue()).getType() == Entity.PLAYER_TRACKS) {
                if (((Character) e.getValue()).equals(c)) {
                    pAo = (PositionAndOrientation) e.getKey();
                }
            }
        }
        if (pAo != null) {
            map.put(pAo, new Terrain("Terrain tile" + pAo.getLongitude() + " " + pAo.getLongitude(), "Normal Terrain", pAo));
        }
    }

    public Character setupPlayer(Character c) {
        handleDuplicates(c);
        PositionAndOrientation pAo = createRandomAndFreeWorldPosition();
        c.setPositionAndOrientation(pAo);
        c.setPreviousPAndO(pAo);
        map.put(pAo, c);
        return c;
    }

    private void setupMap() {
        initializeWorld();
        setupHealers();
        setupGoblins();
        setupTraps();
        setupTreasure();
        updateMapGUI();
        printWorld();
    }

    private boolean ValidatePositionAndOrientation(PositionAndOrientation pAndO) {
        return ((pAndO.getLatitude() >= 0) && (pAndO.getLatitude() < maxLatitude)
                && (pAndO.getLongitude() >= 0) && (pAndO.getLongitude() < maxLongitude));
    }

    private void makeTerrain(PositionAndOrientation p) {
        map.put(p, new Terrain("A tile", "just a normal tile on the board", p));
    }

    public Character commitMove(Character c) {
        PositionAndOrientation pAndO = c.getPositionAndOrientation();
        if (ValidatePositionAndOrientation(pAndO)) {
            Entity ent = map.get(pAndO);
            switch (ent.leaveTracks()) {
                case Entity.GOBLIN_TRACKS:
                    if (!((Goblin) ent).attack(c)) {
                        System.out.println("The player " + c.getEntityName() + " was caught by surprise by the Goblin but survives as he runs back to the previous square");
                        c.revertBackToPreviousPAndO();
                    } else {
                        c.die(((Goblin) ent));
                        makeTerrain(c.getPreviousPAndO());
                    }
                    break;
                case Entity.GLITTER:
                    System.out.println("The player " + c.getEntityName() + " missed the treasure in this square. The gold will be relocated");
                    map.put(pAndO, c);
                    makeTerrain(c.getPreviousPAndO());
                    setupTreasure();
                    break;
                case Entity.HEALER_TRACKS:
                    System.out.println("The player " + c.getEntityName() + " missed a healer in this square. No health bonus!");
                    map.put(pAndO, c);
                    makeTerrain(c.getPreviousPAndO());
                    break;
                case Entity.TRAP_TRACKS:
                    System.out.println("The player " + c.getEntityName() + " has fallen into a trap and died in a bloody mess. You lose!");
                    c.die(ent);
                    makeTerrain(c.getPreviousPAndO());
                    break;
                case Entity.NO_TRACKS:
                    System.out.println("The player " + c.getEntityName() + " advances to a new square");
                    map.put(pAndO, c);
                    makeTerrain(c.getPreviousPAndO());
                    break;
            }
        } else {
            System.out.println("The player " + c.getEntityName() + " moved to the end of the known world and loses");
            c.die(c);
            makeTerrain(c.getPreviousPAndO());
        }
        return c;
    }

    public Character commitAttack(Character c) {
        PositionAndOrientation pAndO = c.getPositionAndOrientation();
        if (ValidatePositionAndOrientation(pAndO)) {
            Entity ent = map.get(pAndO);
            switch (ent.leaveTracks()) {
                case Entity.GOBLIN_TRACKS:
                    if (!c.attack((Goblin) ent)) {
                        System.out.println("The player " + c.getEntityName() + " attacked the Goblin, but it survived.");
                        c.revertBackToPreviousPAndO();
                    } else {
                        System.out.println("The player " + c.getEntityName() + " attacked the Goblin, and it died.");
                        map.put(pAndO, c);
                        makeTerrain(c.getPreviousPAndO());
                        break;
                    }
                    break;
                case Entity.GLITTER:
                    System.out.println("The player " + c.getEntityName() + " destroyed the treasure! You lose!");
                    c.die(ent);
                    makeTerrain(c.getPreviousPAndO());
                    break;
                case Entity.HEALER_TRACKS:
                    System.out.println("The player " + c.getEntityName() + " found a healer " + ent.getEntityName() + " in this square and killed it. You lose!");
                    c.die(ent);
                    makeTerrain(c.getPreviousPAndO());
                    break;
                case Entity.TRAP_TRACKS:
                    System.out.println("The player " + c.getEntityName() + " has fallen into a trap and died. You cannot attack traps, what were you thinking?");
                    c.die(ent);
                    makeTerrain(c.getPreviousPAndO());
                    break;
                case Entity.NO_TRACKS:
                    System.out.println("The player " + c.getEntityName() + " tried to attack nothing at all (that is not very clever) in the process he dropped and damaged his weapon. He moves to a new square.");
                    map.put(pAndO, c);
                    makeTerrain(c.getPreviousPAndO());
                    c.penalizeWeaponPoints();
                    break;
            }
        } else {
            System.out.println("The player " + c.getEntityName() + " moved to the end of the known world and loses");
            c.die(c);
            makeTerrain(c.getPreviousPAndO());
        }
        return c;
    }

    public Character commitUse(Character c) {
        PositionAndOrientation pAndO = c.getPositionAndOrientation();

        if (ValidatePositionAndOrientation(pAndO)) {
            Entity ent = map.get(pAndO);
            switch (ent.leaveTracks()) {
                case Entity.GOBLIN_TRACKS:
                    if (!((Goblin) ent).attack(c)) {
                        System.out.println("The player " + c.getEntityName() + " was caught by surprise by the Goblin but survives as he runs back to the previous square");
                        c.revertBackToPreviousPAndO();
                    } else {
                        c.die(((Goblin) ent));
                        makeTerrain(c.getPreviousPAndO());
                    }
                    break;

                case Entity.HEALER_TRACKS:
                    if (((Healer) ent).getHealthPoints() > 0) {
                        System.out.println("The player " + c.getEntityName() + " used a healer in this square and gained 1 HP bonus!");
                        c.giveHealthPoints();
                    } else {
                        System.out.println("The player " + c.getEntityName() + " used a healer without life so he didn't get the bonus!");
                    }
                    map.put(pAndO, c);
                    makeTerrain(c.getPreviousPAndO());
                    break;

                case Entity.TRAP_TRACKS:
                    System.out.println("The player " + c.getEntityName() + " has fallen into a trap and died. You cannot attack traps, what were you thinking?");
                    c.die(ent);
                    makeTerrain(c.getPreviousPAndO());
                    break;

                case Entity.GLITTER:
                    System.out.println("The player " + c.getEntityName() + " used the treasure! You won!");
                    makeTerrain(c.getPreviousPAndO());
                    break;

                case Entity.NO_TRACKS:
                    System.out.println("The player " + c.getEntityName() + " tried to use nothing at all (that is not very clever) in the process he dropped and damaged his weapon. He moves to a new square.");
                    map.put(pAndO, c);
                    makeTerrain(c.getPreviousPAndO());
                    c.penalizeWeaponPoints();
                    break;
            }
        } else {
            System.out.println("The player " + c.getEntityName() + " moved to the end of the known world and loses");
            c.die(c);
            makeTerrain(c.getPreviousPAndO());
        }
        return c;
    }

    public Character whatCanCTrack(Character c) {
        PositionAndOrientation pAo = c.getNextCell();
        if (ValidatePositionAndOrientation(pAo)) {
            c.setTracks(((Entity) map.get(pAo)).leaveTracks());
        } else {
            c.setTracks(Entity.TRAP_TRACKS);
        }
        return c;
    }

    private void createGUIWindow() {
        JFrame frame = new JFrame("IS TRAB3 MAS-based RPG 39393_40581_41038");
        frame.setResizable(false);
        frame.add(this.mapGUI);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void updateMapGUI() {
        this.mapGUI.updateMapGUI(mapToMatrix());
    }

    @Override
    protected void setup() {

        Object[] args = getArguments();

        if (args.length == 5) { //Stay

            maxLongitude = Integer.parseInt((String) args[0]);
            maxLatitude = Integer.parseInt((String) args[1]);
            numberOfHealers = Integer.parseInt((String) args[2]);
            numberOfGoblins = Integer.parseInt((String) args[3]);
            numberOfTraps = Integer.parseInt((String) args[4]);

            this.mapGUI = new MapGUI(maxLongitude, maxLatitude);
            createGUIWindow();
            this.setupMap();
            DFInteraction.RegisterInDF(this, "World", "World Agent");

            this.addBehaviour(new ActionResponder(this, ActionResponder.getMT()));
        } else { //Leave the platform
            this.doDelete();
        }

    }

    @Override
    protected void takeDown() {
        DFInteraction.DeregisterDF(this, this.getAID());
        super.takeDown();
    }

}
