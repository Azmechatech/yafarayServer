/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mediaprocessing;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author Manoj
 */
public class Server {

    public static void main(String[] args) throws Exception {
        System.out.println("args[0] => port and args[1]=> parallel connection allowed. Keep=Cores-1");
        int port = 8000;
        int parallelConn=2;
        String yafarayPath="C:\\DevTools\\yafaray_v3\\bin\\yafaray-xml";
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            parallelConn = Integer.parseInt(args[1]);
        }
        
        if (args.length >= 3) {
           yafarayPath = args[2];
        }
        
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new SimpleHTTPHandler());
        server.createContext("/yafaray", new YafarayRenderingHandler(yafarayPath));

        server.setExecutor(new ForkJoinPool(parallelConn)); // creates a default executor
        server.start();

        System.out.println("#Server Started");
    }

}
