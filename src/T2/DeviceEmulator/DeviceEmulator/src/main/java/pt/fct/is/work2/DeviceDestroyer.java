/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.fct.is.work2;

import java.io.IOException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * REST Service made available by the server to destroy a device.
 *
 * @author Pedro Lima Monteiro <pedro.monteiro@uninova.pt>
 */
public class DeviceDestroyer extends ServerResource {

    /**
     * Service POST method. Change at your own risk.
     *
     * @param entity - information that comes in the body of the message.
     * @return json object with result: 0 if successful, 1 if not, -1 if the
     * device is not found
     *
     * @throws ResourceException
     */
    @Override
    protected Representation post(Representation entity) throws ResourceException {
        try {
            JSONObject req = new JSONObject(entity.getText());
            JSONObject res = new JSONObject();

            if (DeviceEmulator.devices.get(req.getString("name")) != null) {
                DeviceEmulator.devices.get(req.getString("name")).interrupt();
                DeviceEmulator.devices.get(req.getString("name")).socket.disconnect();

                if (DeviceEmulator.devices.remove(req.getString("name")) != null) {

                    res.put("result", "0");
                } else {
                    res.put("result", "1");
                }
            } else {
                res.put("result", "-1");
            }

            return new StringRepresentation(res.toString(), MediaType.APPLICATION_JSON);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return null;
    }
}
