/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.fct.is.work2;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * Emulator for the device.
 *
 * @author Pedro Lima Monteiro <pedro.monteiro@uninova.pt>
 */
public class Device extends Thread {

    /**
     * Current state of the device.
     */
    public String current_state;
    /**
     * Socket object that enables real-time communication with the server.
     */
    public Socket socket;
    /**
     * Controller for the monitoring state of the device.
     */
    private boolean monitoring = true;
    /**
     * Controller for the suspended state of the device.
     */
    private boolean suspended = false;
    /**
     * Controller for the first time the device as ran.
     */
    private boolean ran = false;
    int i = 0;

    /**
     * Constructor for the device. Change at your own risk.
     *
     * @param device - json describing the device.
     */
    public Device(JSONObject device) {
        current_state = device.getString("current_state");
        try {
            IO.Options options = new IO.Options();
            options.query = "device=" + device.getString("name");
            socket = IO.socket("http://localhost:8080", options);
            socket.on("new_value", (Object... os) -> {
                JSONObject obj = (JSONObject) os[0];
            });
            socket.connect();
            JSONObject newDevName = new JSONObject();
            newDevName.put("name", device.get("name"));
            socket.emit("newDevice", newDevName);
            System.out.println("connected");
        } catch (URISyntaxException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Actual life cycle of the device. Change at your own risk.
     */
    @Override
    public void run() {
        ran = true;
        while (!Thread.interrupted()) {

            emulate();
            synchronized (this) {
                while (suspended && !Thread.interrupted()) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        System.out.println("Thread interrupted.");
                        this.interrupt();
                    }
                }
            }
        }
    }

    public static void pause(int seconds) {
        Date start = new Date();
        Date end = new Date();
        while (end.getTime() - start.getTime() < seconds * 1000) {
            end = new Date();
        }
    }

    /**
     * TO DO: Emulate the operation of the device.
     */
    private void emulate() {
        if (current_state.equals("on")) {
            JSONObject devValue = new JSONObject();
            devValue.put("value", DeviceEmulator.energyProduction(0));
            socket.emit("value", devValue);
            pause(40);
        }
    }

    /**
     * Changes the current state of the device. Change at your own risk.
     *
     * @param new_state - new state of the device.
     * @return true if successful, false otherwise
     */
    public boolean changeState(String new_state) {
        if (!current_state.equals(new_state)) {
            switch (new_state) {
                case "off":
                    this.suspended = true;
                    this.monitoring = false;
                    break;
                case "on":
                    if (!ran) {
                        this.start();
                    } else {
                        synchronized (this) {
                            this.suspended = false;
                            notify();
                        }
                    }
                    this.monitoring = true;
                    break;
                case "unmonitored":
                    this.suspended = false;
                    this.monitoring = false;
                    break;
                default:
                    return false;
            }
            this.current_state = new_state;
            return true;
        }
        return false;
    }
}
