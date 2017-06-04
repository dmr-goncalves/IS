/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import Entities.Character;
import Entities.Entity;
import Common.PositionAndOrientation;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class Player extends Character implements FormToPlayerComm {

    private PlayerFrame pf;
    boolean treasureFound = false;
    boolean treasureUsed = false;
    boolean doTrackDone = false;
    boolean canDoGetTracks = false;
    boolean canDoAI = false;

    public Player(String name, String description, int healthPoints, int weaponPoints, int status, int tracks, PositionAndOrientation previousPAndO, PositionAndOrientation positionAndOrientation, PlayerFrame pf) {
        super(name, description, healthPoints, weaponPoints, Entity.PLAYER_TRACKS, status, tracks, previousPAndO, positionAndOrientation);
        this.pf = pf;
    }

    @Override
    public int leaveTracks() {
        return Entity.PLAYER_TRACKS;
    }

    public void UpdateForm(PositionAndOrientation pAo, int state, int smell) {
        pf.UpdateForm(pAo, state, smell);
    }

    @Override
    public String identify() {
        return Entity.PLAYER;
    }

    @Override
    public void doMove() {
        super.move();
    }

    @Override
    public void doAttack() {
        super.moveAndAttack();
    }

    @Override
    public void doUse() {
        super.moveAndUse();
    }

    @Override
    public void doTrack() {
        super.sense();
    }

    @Override
    public void doRotateLeft() {
        super.rotateLeft();
    }

    @Override
    public void doRotateRight() {
        super.rotateRight();
    }

    @Override
    public void doRegister() {
        super.register();
    }

    @Override
    public void doRegisterAuto() {
        super.registerAuto();
    }
}
