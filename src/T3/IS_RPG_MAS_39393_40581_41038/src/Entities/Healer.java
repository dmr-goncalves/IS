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
public class Healer extends Character {

    public Healer(String name, String description, int healthPoints, int weaponPoints, int status, int tracks, PositionAndOrientation previousPAndO, PositionAndOrientation currentPAndO) {
        super(name, description, healthPoints, weaponPoints, Entity.HEALER_TRACKS, status, tracks, previousPAndO, currentPAndO);
        this.addBehaviour(new HealerLooseLifeBehaviour(this, 10000));
    }

    @Override
    public int leaveTracks() {
        return Entity.HEALER_TRACKS;
    }

    public void energizePlayer(Character c) {
        int currentEnerygPoints = c.getHealthPoints();
        c.setHealthPoints(currentEnerygPoints + this.getHealthPoints());
        die(c);
    }

    @Override
    public String identify() {
        return Entity.HEALER;
    }

}
