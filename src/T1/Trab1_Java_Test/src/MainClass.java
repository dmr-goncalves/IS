/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dmrg
 */
public class MainClass {

    static {
        System.loadLibrary("Trab1FirstDll");
        System.loadLibrary("Trab1SecondDLLForTeacherProjectAndOurs");
    }

    native static float energyProduction(int device_id);

    native static int turnOn(int state, int device_id);

    native static int isOn(int device_id);

    native static String error();

    native static float max(int device_id);

    native static float min(int device_id);

    native static float mean(int device_id);

    native static int latitude();

    native static int longitude();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        for (int x = 0; x < 10; x++) {
            System.out.println(energyProduction(1));
        }
        System.out.println(turnOn(1, 1));
        System.out.println(isOn(1));
        System.out.println(turnOn(0, 2));
        System.out.println(isOn(2));
        System.out.println(error());
        System.out.println(max(1));
        System.out.println(min(1));
        System.out.println(mean(1));
        System.out.println(latitude());
        System.out.println(longitude());
    }

}
