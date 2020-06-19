/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mediaprocessing;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Manoj
 */
public class SimpleHTTPHandler implements HttpHandler {

    public SimpleHTTPHandler() {
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        byte[] result = "This is up!".getBytes();
            he.sendResponseHeaders(200, result.length);
            OutputStream os = he.getResponseBody();
            os.write(result);
            os.close();
    }
    
}
