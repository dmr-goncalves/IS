/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import Common.PositionAndOrientation;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class Treasure extends Entity {

    public Treasure(String name, String description, PositionAndOrientation positionAndOrientation) {
        super(name, description, Entity.GLITTER, positionAndOrientation);
    }

    @Override
    public int leaveTracks() {
        return Entity.GLITTER;
    }

    @Override
    public String identify() {
        return Entity.TREASURE;
    }

}
