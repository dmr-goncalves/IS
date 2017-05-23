/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientapp;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andre Rocha
 */
public class CyclicBehaviour extends Thread {

    public CyclicBehaviour() {
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CyclicBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
