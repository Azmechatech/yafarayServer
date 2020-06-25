/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mediaprocessing;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author Manoj
 */
public class ServerRegistrationHandler extends CustomHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        try {
            long time = System.currentTimeMillis();
            BufferedReader in = new BufferedReader(new InputStreamReader(he.getRequestBody()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            long contentSize = 0;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                contentSize = contentSize + inputLine.length();
            }
            in.close();
            time = System.currentTimeMillis() - time;

            double speed = (double) contentSize / (double) (time == 0 ? -1 : time);
            JSONObject request = new JSONObject(response.toString());
            ACTIVE_SERVERS.SERVERS.add(request.getString("URL"));
            
            //Do a check if server is live or has enough speed available.

            byte[] result = new JSONObject().toString().getBytes();
            he.sendResponseHeaders(200, result.length);
            OutputStream os = he.getResponseBody();
            os.write(result);
            os.close();

        } catch (IOException ex) {
            Logger.getLogger(CustomHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
