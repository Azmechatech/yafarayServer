/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mediaprocessing;

import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import org.json.JSONObject;

/**
 *
 * @author Manoj
 */
public class Server {

    public static void main(String[] args) throws Exception {
        System.out.println("args[0] => port and args[1]=> parallel connection allowed. Keep=Cores-1");
        System.out.println("args[2] => yafaray path and  args[3]=> remote server ");
        System.out.println("args[4] => public URL of this server ");
        
        int port = 8000;
        int parallelConn = 2;
        String yafarayPath = "C:\\DevTools\\yafaray_v3\\bin\\yafaray-xml";
        String registrationURL = "http://localhost:8000/register";
        String selfPublicURL = "http://localhost:8000";
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            parallelConn = Integer.parseInt(args[1]);
        }

        if (args.length >= 3) {
            yafarayPath = args[2];
        }

        if (args.length >= 4) {
            registrationURL = args[3];
        }
        
        if (args.length >= 5) {
            selfPublicURL = args[4];
        }
        

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new SimpleHTTPHandler());
        server.createContext("/yafaray", new YafarayRenderingHandler(yafarayPath));
        server.createContext("/register", new ServerRegistrationHandler());
        server.createContext("/servers", new ActiveServerHandler());

        server.setExecutor(new ForkJoinPool(parallelConn)); // creates a default executor
        server.start();

        System.out.println("#Server Started");
        //Register
        JSONObject postData=new JSONObject();
        postData.put("URL", selfPublicURL);
        
        System.out.println("#Server Registered response>>"+executePost(registrationURL, postData.toString()));
    }

    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response  
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
