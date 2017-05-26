/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.fct.is.work2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.service.CorsService;

/**
 * Main entry point of the Device Emulator Program. Creates and launches a
 * Restlet server that answers REST calls.
 *
 * @author Pedro Lima Monteiro <pedro.monteiro@uninova.pt>
 */
public class DeviceEmulator {

    static {
        System.loadLibrary("Trab1FirstDll");
        System.loadLibrary("DLLForNetbeans");
    }

    /**
     * Object that holds pointers to all the devices currently in the system.
     */
    public static HashMap<String, Device> devices;

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
     * Main entry point of the Device Emulator program. Change at your own risk.
     *
     * @param args - N.A.
     */
    public static void main(String[] args) {
        devices = new HashMap<>();
        CorsService corsService = new CorsService();
        corsService.setAllowedOrigins(new HashSet(Arrays.asList("*")));
        corsService.setAllowedCredentials(true);
        corsService.setAllowingAllRequestedHeaders(true);
        corsService.setSkippingResourceForCorsOptions(true);

        Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8182);
        component.getServices().add(corsService);
        component.getDefaultHost().attach("/api/device/add", DeviceCreator.class);
        component.getDefaultHost().attach("/api/device/state", DeviceStateController.class);
        component.getDefaultHost().attach("/api/device/remove", DeviceDestroyer.class);
        
        
        try {
            component.start();
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
}
