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
public class HealerLooseLifeBehaviour extends TickerBehaviour {

    Healer h;

    public HealerLooseLifeBehaviour(Agent a, long period) {
        super(a, period);
        h = (Healer) a;
    }

    @Override
    protected void onTick() {
        if (h.getHealthPoints() > 0) {
            h.removeHealerHealthPoints();
            System.out.println(h.getEntityName() + " lost 10 HP");
        }
    }
}
