/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author André
 */
public class Server extends Thread {

    ServerSocket myServer;
    DeviceLib myLib;

    public Server(int port) throws IOException {
        myLib = new DeviceLib();
        myServer = new ServerSocket(port);
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            String received = "";
            try {
                System.out.println("Waiting for client on port " + myServer.getLocalPort() + "...");
                Socket session = myServer.accept();
                System.out.println("Just connected to " + session.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(session.getInputStream());
                received = in.readUTF();
                System.out.println("Content Received: " + received);
                DataOutputStream out = new DataOutputStream(session.getOutputStream());
                String toSend = myLib.callHardware(received);
                out.writeUTF(toSend);
                session.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
