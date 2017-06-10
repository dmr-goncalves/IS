/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 *
 * @author dmrg
 */
public class PrintHPAndWeaponPoints extends TickerBehaviour {

    Character c;

    public PrintHPAndWeaponPoints(Agent a, long period) {
        super(a, period);
        c = (Character) a;
    }

    @Override
    protected void onTick() {
        System.out.println(c.getLocalName() + " has " + c.getHealthPoints() + " HP and his weapon has " + c.getWeaponPoints() + " points");
    }

}
