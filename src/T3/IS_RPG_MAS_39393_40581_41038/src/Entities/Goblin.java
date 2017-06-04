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
public class Goblin extends Character {

    public Goblin(String name, String description, int healthPoints, int weaponPoints, int status, int tracks, PositionAndOrientation previousPAndO, PositionAndOrientation currentPAndO) {
        super(name, description, healthPoints, weaponPoints, Entity.GOBLIN_TRACKS, status, tracks, previousPAndO, currentPAndO);
    }

    @Override
    public int leaveTracks() {
        return Entity.GOBLIN_TRACKS;
    }

    @Override
    public String identify() {
        return Entity.GOBLIN;
    }

}
