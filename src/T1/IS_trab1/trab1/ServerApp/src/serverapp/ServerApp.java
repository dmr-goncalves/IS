/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import java.io.IOException;

/**
 *
 * @author André
 */
public class ServerApp {
    
    private static final int port = 4444;
    
    public static void main(String[] args) throws IOException {
        Server myServer = new Server(port);
    }
}
