/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author André
 */
public class DeviceLib {

    native static float energyProduction(int device_id);

    native static int turnOn(int state, int device_id);

    native static int isOn(int device_id);

    native static String error();

    native static float max(int device_id);

    native static float min(int device_id);

    native static float mean(int device_id);

    native static int longitude();

    native static int latitude();

    public DeviceLib() {
        //AULA 3 - inicializar as lib
        System.loadLibrary("Trab1FirstDll");
        System.loadLibrary("Trab1SecondDLLForTeacherProjectAndOurs");
    }

    protected String callHardware(String content) {
        String reply = null;
        int device_id = 0;
        StringTokenizer st = new StringTokenizer(content, utilities.constants.token);
        StringTokenizer st2 = new StringTokenizer(content, utilities.constants.newToken);

        String operation = st2.nextToken();
        StringTokenizer st3 = new StringTokenizer(st2.nextToken(), utilities.constants.token);
        if (st3.hasMoreTokens()) {
            device_id = Integer.parseInt(st3.nextToken());
        }
        
        switch (operation) {
            case "state":
                int state;
                state = isOn(device_id);
                System.out.println(state);
                reply = "" + state;
                break;
            case "on":
                turnOn(1, device_id);
                reply = "Turned ON";
                break;
            case "off":
                turnOn(0, device_id);
                reply = "Turned OFF";
                break;
            case "location":
                int latitude = latitude();
                int longitude = longitude();
                StringBuilder sb = new StringBuilder();
                ;
                sb.append(latitude);
                sb.append("#");
                sb.append(longitude);
                sb.append("#");
                reply = sb.toString();
                break;
            case "value":
                float value;
                value = energyProduction(device_id);
                reply = "" + value;
                break;
            case "error":
                int error;
                error = Integer.parseInt(error());
                reply = "" + error;
                break;
            case "max":
                float max;
                max = max(device_id);
                reply = "" + max;
                break;
            case "min":
                float min;
                min = min(device_id);
                reply = "" + min;
                break;
            case "mean":
                float mean;
                mean = mean(device_id);
                reply = "" + mean;
                break;
        }
        return reply;

    }
}
