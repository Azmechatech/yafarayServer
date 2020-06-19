/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mediaprocessing;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manoj
 */
public abstract class CustomHandler implements HttpHandler {

    public StringBuffer postToStringBuilder(HttpExchange he) {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(he.getRequestBody()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response;
        } catch (IOException ex) {
            Logger.getLogger(CustomHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new StringBuffer();
    }

    /**
     *
     * @param query
     * @return
     */
    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    /**
     * The following implementation of the method does what the OP requires, it
     * traverses the inheritance hierarchy for every class and interface:
     * http://stackoverflow.com/questions/22031207/find-all-classes-and-interfaces-a-class-extends-or-implements-recursively
     *
     * @param clazz
     * @return
     */
    public static Set<Class<?>> getAllExtendedOrImplementedTypesRecursively(Class<?> clazz) {
        List<Class<?>> res = new ArrayList<>();

        do {
            res.add(clazz);

            // First, add all the interfaces implemented by this class
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                res.addAll(Arrays.asList(interfaces));

                for (Class<?> interfaze : interfaces) {
                    res.addAll(getAllExtendedOrImplementedTypesRecursively(interfaze));
                }
            }

            // Add the super class
            Class<?> superClass = clazz.getSuperclass();

            // Interfaces does not have java,lang.Object as superclass, they have null, so break the cycle and return
            if (superClass == null) {
                break;
            }

            // Now inspect the superclass 
            clazz = superClass;
        } while (!"java.lang.Object".equals(clazz.getCanonicalName()));

        return new HashSet<Class<?>>(res);
    }
}
