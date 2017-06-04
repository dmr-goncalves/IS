/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import Common.Constants;
import Common.DFInteraction;
import Common.PositionAndOrientation;
import Player.AIBehaviour;
import Player.ActionInitiator;
import Player.Player;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import java.util.StringTokenizer;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public abstract class Character extends Entity {

    public static final int ALIVE = 0;
    public static final int DEAD = 1;
    private int healthPoints;
    private int weaponPoints;
    private PositionAndOrientation previousPAndO;
    private AID world;
    private int status = ALIVE;
    private int tracks = NO_TRACKS;
    protected DFAgentDescription[] worldAgent;

    public Character(String name, String description, int healthPoints, int weaponPoints, int type, int status, int tracks, PositionAndOrientation previousPAndO, PositionAndOrientation currentPAndO) {
        super(name, description, type, currentPAndO);

        this.healthPoints = healthPoints;
        this.weaponPoints = weaponPoints;
        this.previousPAndO = previousPAndO;
        this.status = status;
        this.tracks = tracks;

        this.addBehaviour(new PrintHPAndWeaponPoints(this, 5000));
    }

    public boolean findWorldInDF() {

        worldAgent = DFInteraction.findAgents(this, "World", "World Agent");
        if (worldAgent.length > 0) {
            world = worldAgent[0].getName();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void setup() {
        findWorldInDF();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTracks() {
        return tracks;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public PositionAndOrientation getPreviousPAndO() {
        return previousPAndO;
    }

    public PositionAndOrientation getNextPandO(PositionAndOrientation pAo) {
        PositionAndOrientation PaO = pAo;
        PositionAndOrientation aux = new PositionAndOrientation(pAo.getLongitude(), pAo.getLatitude(), pAo.getOrientation());

        int orientation = PaO.getOrientation();

        switch (orientation) {
            case 0: //North
                aux.setLatitude(PaO.getLatitude() + 1);
                break;
            case 1: //South
                aux.setLatitude(PaO.getLatitude() - 1);
                break;
            case 2: //East
                aux.setLongitude(PaO.getLongitude() + 1);
                break;
            case 3: //West
                aux.setLongitude(PaO.getLongitude() - 1);
                break;
        }
        return aux;
    }

    public void setPreviousPAndO(PositionAndOrientation previousPAndO) {
        this.previousPAndO = previousPAndO;
    }

    public AID getWorld() {
        return world;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void removeHealerHealthPoints() {
        this.healthPoints -= 10;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public void giveHealthPoints() {
        this.healthPoints += 1;
    }

    public int getWeaponPoints() {
        return weaponPoints;
    }

    public void setWeaponPoints(int weaponPoints) {
        this.weaponPoints = weaponPoints;
    }

    public void penalizeWeaponPoints() {
        weaponPoints -= 5;
    }

    public void revertBackToPreviousPAndO() {
        setPositionAndOrientation(previousPAndO);
    }

    public boolean attack(Character c) {
        int currentEnergy = c.getHealthPoints();
        int outcome = currentEnergy - this.weaponPoints;
        if (outcome > 0) {
            c.setHealthPoints(outcome);
            return false;
        } else {
            c.die(this);
            return true;
        }
    }

    public PositionAndOrientation getNextCell() {
        int orientation = getPositionAndOrientation().getOrientation();
        PositionAndOrientation target = null;
        switch (orientation) {
            case PositionAndOrientation.EAST:
                target = getPositionAndOrientation().getEasternCell();
                break;
            case PositionAndOrientation.NORTH:
                target = getPositionAndOrientation().getNorthernCell();
                break;
            case PositionAndOrientation.SOUTH:
                target = getPositionAndOrientation().getSouthernCell();
                break;
            case PositionAndOrientation.WEST:
                target = getPositionAndOrientation().getWesternCell();
                break;
        }
        return target;
    }

    public void rotateRight() {
        switch (getPositionAndOrientation().getOrientation()) {
            case PositionAndOrientation.EAST:
                getPositionAndOrientation().setOrientation(PositionAndOrientation.SOUTH);
                break;
            case PositionAndOrientation.NORTH:
                getPositionAndOrientation().setOrientation(PositionAndOrientation.EAST);
                break;
            case PositionAndOrientation.SOUTH:
                getPositionAndOrientation().setOrientation(PositionAndOrientation.WEST);
                break;
            case PositionAndOrientation.WEST:
                getPositionAndOrientation().setOrientation(PositionAndOrientation.NORTH);
                break;
        }
    }

    public void rotateLeft() {
        switch (getPositionAndOrientation().getOrientation()) {
            case PositionAndOrientation.EAST:
                getPositionAndOrientation().setOrientation(PositionAndOrientation.NORTH);
                break;
            case PositionAndOrientation.NORTH:
                getPositionAndOrientation().setOrientation(PositionAndOrientation.WEST);
                break;
            case PositionAndOrientation.SOUTH:
                getPositionAndOrientation().setOrientation(PositionAndOrientation.EAST);
                break;
            case PositionAndOrientation.WEST:
                getPositionAndOrientation().setOrientation(PositionAndOrientation.SOUTH);
                break;
        }
    }

    public void register() {
        addBehaviour(new ActionInitiator(this, ActionInitiator.createInitialMessage(Constants.ONTOLOGY_REGISTER, this, world)));
    }

    public void registerAuto() {
        addBehaviour(new ActionInitiator(this, ActionInitiator.createInitialMessage(Constants.ONTOLOGY_REGISTER, this, world)));
        addBehaviour(new AIBehaviour(this));
    }

    private void updatePosition() {
        previousPAndO = this.getPositionAndOrientation();
        this.setPositionAndOrientation(getNextCell());
    }

    public void move() {
        updatePosition();
        addBehaviour(new ActionInitiator(this, ActionInitiator.createInitialMessage(Constants.ONTOLOGY_MOVE, this, world)));
    }

    public void moveAndAttack() {
        updatePosition();
        addBehaviour(new ActionInitiator(this, ActionInitiator.createInitialMessage(Constants.ONTOLOGY_ATTACK, this, world)));
    }

    public void moveAndUse() {
        updatePosition();
        addBehaviour(new ActionInitiator(this, ActionInitiator.createInitialMessage(Constants.ONTOLOGY_USE, this, world)));
    }

    public void sense() {
        addBehaviour(new ActionInitiator(this, ActionInitiator.createInitialMessage(Constants.ONTOLOGY_TRACK, this, world)));
    }

    public void die(Entity cause) {
        status = DEAD;
        System.out.println("The character " + getEntityName() + " has died due to " + cause.getDescription());
    }
    private static final String SEPARATOR = "*";

    public static Character fromString(String character) {
        StringTokenizer st = new StringTokenizer(character, SEPARATOR);
        int status = Integer.parseInt(st.nextToken());
        int type = Integer.parseInt(st.nextToken());
        String name = st.nextToken();
        String description = st.nextToken();
        int healthPoints = Integer.parseInt(st.nextToken());
        int weaponPoints = Integer.parseInt(st.nextToken());
        PositionAndOrientation pos = PositionAndOrientation.fromString(st.nextToken());
        PositionAndOrientation previousPos = PositionAndOrientation.fromString(st.nextToken());
        int tracks = Integer.parseInt(st.nextToken());

        switch (type) {
            case Entity.HEALER_TRACKS:
                return new Healer(name, description, healthPoints, weaponPoints, status, tracks, previousPos, pos);
            case Entity.PLAYER_TRACKS:
                return new Player(name, description, healthPoints, weaponPoints, status, tracks, previousPos, pos, null);
            case Entity.GOBLIN_TRACKS:
                return new Goblin(name, description, healthPoints, weaponPoints, status, tracks, previousPos, pos);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(status);
        sb.append(SEPARATOR);
        sb.append(this.getType());
        sb.append(SEPARATOR);
        sb.append(this.getEntityName());
        sb.append(SEPARATOR);
        sb.append(this.getDescription());
        sb.append(SEPARATOR);
        sb.append(this.getHealthPoints());
        sb.append(SEPARATOR);
        sb.append(this.getWeaponPoints());
        sb.append(SEPARATOR);
        sb.append(this.getPositionAndOrientation().toString());
        sb.append(SEPARATOR);
        sb.append(this.getPreviousPAndO().toString());
        sb.append(SEPARATOR);
        sb.append(this.tracks);
        sb.append(SEPARATOR);
        return sb.toString();
    }

}
