/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import Common.PositionAndOrientation;
import Entities.Entity;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import java.util.HashMap;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author dmrg
 */
public class AIBehaviour extends CyclicBehaviour {
    
    Player p;
    HashMap<PositionAndOrientation, String> map = new HashMap<>();
    ArrayList<PositionAndOrientation> pAoList = new ArrayList<>();
    boolean shownFrame = false;
    Random randomGenerator = new Random();
    boolean canChangeOrientation = true;
    
    javax.swing.Timer timer; //Clear visited houses along the time

    public void start_timer() {
        timer.start();
    }
    
    public AIBehaviour(Agent a) {
        super(a);
        p = (Player) a;
        
        timer = new javax.swing.Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (pAoList.size() > 0) {
                    PositionAndOrientation aux = pAoList.remove(0);
                    System.out.println("Visited house long time ago so we can assume that it's not visited anymore");
                    System.out.println("It's coordinates are: " + aux.getLatitude() + " (lat) " + aux.getLongitude() + " (lon)");
                    map.put(aux, null);
                }
            }
        });
    }
    
    @Override
    public void action() {
        if (p.canDoAI) {
            if (p.treasureFound && p.treasureUsed) {
                System.out.println("Player " + p.getLocalName() + " won the game by himself!");
                if (!shownFrame) { //Put the frame visible only once
                    shownFrame = true;
                    JFrame frame = new TheBotWonFrame();
                    frame.setVisible(true);
                    p.removeBehaviour(this);
                }
            } else {
                if (p.treasureFound) {
                    System.out.println("Treasure found! The bot used it!");
                    p.doUse();
                    p.treasureUsed = true;
                } else {
                    
                    if (canChangeOrientation) {
                        int randomInt = randomGenerator.nextInt(4);
                        int orientation = p.getPositionAndOrientation().getOrientation();
                        
                        canChangeOrientation = false;
                        
                        switch (orientation) { //Choose a random orientation to go

                            case 0: //North
                                if (randomInt == 0) { //North
                                    break;
                                } else if (randomInt == 1) { //South
                                    p.doRotateRight();
                                    p.doRotateRight();
                                    break;
                                } else if (randomInt == 2) { //East
                                    p.doRotateRight();
                                    break;
                                } else if (randomInt == 3) { //West
                                    p.doRotateLeft();
                                    break;
                                }
                            
                            case 1: //South
                                if (randomInt == 0) { //North
                                    p.doRotateRight();
                                    p.doRotateRight();
                                    break;
                                } else if (randomInt == 1) { //South
                                    break;
                                } else if (randomInt == 2) { //East
                                    p.doRotateLeft();
                                    break;
                                } else if (randomInt == 3) { //West
                                    p.doRotateRight();
                                    break;
                                }
                            
                            case 2: //East
                                if (randomInt == 0) { //North
                                    p.doRotateLeft();
                                    break;
                                } else if (randomInt == 1) { //South
                                    p.doRotateRight();
                                    break;
                                } else if (randomInt == 2) { //East
                                    break;
                                } else if (randomInt == 3) { //West
                                    p.doRotateRight();
                                    p.doRotateRight();
                                    break;
                                }
                            
                            case 3: //West
                                if (randomInt == 0) { //North
                                    p.doRotateRight();
                                    break;
                                } else if (randomInt == 1) { //South
                                    p.doRotateLeft();
                                    break;
                                } else if (randomInt == 2) { //East
                                    p.doRotateLeft();
                                    p.doRotateLeft();
                                    break;
                                } else if (randomInt == 3) { //West
                                    break;
                                }
                        }
                    }
                    if (map.get(p.getNextPandO(p.getPositionAndOrientation())) == null) { //We have something in the map on that position?

                        if (!p.doTrackDone) {
                            p.canDoGetTracks = false;
                            p.doTrackDone = true;
                            p.doTrack(); //To track what we have in front of us
                        }
                        
                        if (p.canDoGetTracks) {
                            switch (p.getTracks()) { //Get the most updated tracks
                                case Entity.GLITTER:
                                    System.out.println("Treasure Found");
                                    map.put(p.getNextPandO(p.getPositionAndOrientation()), "G");
                                    p.treasureFound = true;
                                    p.doTrackDone = false;
                                    break;
                                case Entity.GOBLIN_TRACKS:
                                    System.out.println("Goblin found. Attacking!");
                                    map.put(p.getPositionAndOrientation(), "V");
                                    pAoList.add(p.getPositionAndOrientation());
                                    start_timer();
                                    p.doAttack();
                                    p.doTrackDone = false;
                                    canChangeOrientation = true;
                                    break;
                                case Entity.TRAP_TRACKS:
                                    System.out.println("Trap found or it's the end of the world. Running away!");
                                    map.put(p.getNextPandO(p.getPositionAndOrientation()), "T");
                                    p.doTrackDone = false;
                                    canChangeOrientation = true;
                                    break;
                                case Entity.HEALER_TRACKS:
                                    System.out.println("Healer found. Maybe it's useful to us!");
                                    if (p.getHealthPoints() != 100) {
                                        map.put(p.getNextPandO(p.getPositionAndOrientation()), "V");
                                        pAoList.add(p.getPositionAndOrientation());
                                        start_timer();
                                        p.doUse();
                                        System.out.println("Healer used because we didn't have full health!");
                                    } else {
                                        map.put(p.getNextPandO(p.getPositionAndOrientation()), "H");
                                    }
                                    canChangeOrientation = true;
                                    p.doTrackDone = false;
                                    break;
                                case Entity.NO_TRACKS:
                                    System.out.println("We're good to go. Moving!");
                                    map.put(p.getPositionAndOrientation(), "V");
                                    pAoList.add(p.getPositionAndOrientation());
                                    start_timer();
                                    p.move();
                                    p.doTrackDone = false;
                                    canChangeOrientation = true;
                                    break;
                            }
                        }
                    } else {  //If we have what is it?
                        switch (map.get(p.getNextPandO(p.getPositionAndOrientation()))) {
                            case "V":
                                System.out.println("Already Visited Found");
                                canChangeOrientation = true;
                                break;
                            case "H":
                                if (p.getHealthPoints() != 100) {//We don't have full life so we don't use the healer
                                    p.doUse();
                                    map.put(p.getNextPandO(p.getPositionAndOrientation()), "V");
                                    pAoList.add(p.getPositionAndOrientation());
                                    start_timer();
                                    System.out.println("Healer used because we didn't have full health!");
                                }
                                canChangeOrientation = true;
                                break;
                            case "T":
                                System.out.println("Trap found or it's the end of the world. Running away!");
                                canChangeOrientation = true;
                                break;
                        }
                    }
                }
            }
        }
    }
}
