/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clientapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author André
 */
public class clientConnection{

    public static String sendContent(String content, String serverIP, int port) {
        String reply = null;
        try {
            System.out.println("Connecting to Server: " + serverIP + " on Port: " + port);
            Socket client = new Socket(serverIP, port);
            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF(content);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            reply = in.readUTF();
            System.out.println("Server says " + reply);
            client.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(clientConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(clientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reply;
    }

}