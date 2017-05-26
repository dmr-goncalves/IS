/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.fct.is.work2;

import java.io.IOException;
import java.util.Arrays;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * REST Service made available by the server to create a new device.
 * 
 * @author Pedro Lima Monteiro <pedro.monteiro@uninova.pt>
 */
public class DeviceCreator extends ServerResource {    
    /**
     * Service POST method.
     * Change at your own risk.
     * 
     * @param entity - information that comes in the body of the message.
     * @return json object with result: 0 if successful, 1 otherwise.
     * 
     * @throws ResourceException 
     */
    @Override
    protected Representation post(Representation entity) throws ResourceException {
        try {            
            JSONObject req = new JSONObject(entity.getText());
            JSONObject res = new JSONObject();
            System.out.print(req);
            Device dev = new Device(req);
            DeviceEmulator.devices.put(req.getString("name"), dev);   
            dev.start();
            res.put("result", "0");
            System.out.print(res.toString());
            return new StringRepresentation(res.toString(), MediaType.APPLICATION_JSON);                    
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return null;
    }
}
