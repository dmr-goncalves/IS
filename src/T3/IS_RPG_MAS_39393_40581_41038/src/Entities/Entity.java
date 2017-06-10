/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import Common.PositionAndOrientation;
import jade.core.Agent;
import java.util.Objects;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public abstract class Entity extends Agent {

    public static final int GLITTER = 0;
    public static final int GOBLIN_TRACKS = 1;
    public static final int HEALER_TRACKS = 2;
    public static final int TRAP_TRACKS = 3;
    public static final int NO_TRACKS = 4;
    public static final int PLAYER_TRACKS = 5;
    public static final int CHARACTER_TRACKS = 6;
    public static final String TREASURE = "T";
    public static final String GOBLIN = "G";
    public static final String HEALER = "H";
    public static final String TRAP = "X";
    public static final String NORMAL_SQUARE = "_";
    public static final String PLAYER = "+";
    private PositionAndOrientation positionAndOrientation;
    private String entityName;
    private String description;
    private int type;

    public Entity(String name, String description, int type, PositionAndOrientation positionAndOrientation) {
        this.entityName = name;
        this.description = description;
        this.type = type;
        this.positionAndOrientation = positionAndOrientation;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PositionAndOrientation getPositionAndOrientation() {
        return positionAndOrientation;
    }

    public void setPositionAndOrientation(PositionAndOrientation positionAndOrientation) {
        this.positionAndOrientation = positionAndOrientation;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.entityName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        if (!Objects.equals(this.entityName, other.entityName)) {
            return false;
        }
        return true;
    }

    public abstract int leaveTracks();

    public abstract String identify();
}
