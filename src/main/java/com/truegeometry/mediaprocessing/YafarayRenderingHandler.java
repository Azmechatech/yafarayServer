/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mediaprocessing;

import com.sun.net.httpserver.HttpExchange;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import de.javagl.obj.ObjWriter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.JSONObject;

/**
 *
 * @author Manoj
 */
public class YafarayRenderingHandler extends CustomHandler {
    String yafarayPath="C:\\DevTools\\yafaray_v3\\bin\\yafaray-xml";

    public YafarayRenderingHandler(String yafarayPath){
        this.yafarayPath=yafarayPath;
    }
    
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
            int width = request.getInt("width");
            int height = request.getInt("height");
            String modelSessionKey=request.getString("modelSessionKey");
            File sessionDir=new File("yaftemp");
            sessionDir.mkdir();//Create directory
            sessionDir=new File("yaftemp/"+modelSessionKey);
            sessionDir.mkdir();//Create directory
            String modelName=request.getString("modelName");
            //Check if model exists in the cache 
            Obj model = null;
            if (new File(sessionDir.getAbsolutePath() + "/" + modelName).exists()) {
                String content = new String(Files.readAllBytes(Paths.get(sessionDir.getAbsolutePath() + "/" + modelName)));
                StringReader stringReader = new StringReader(content);
                model = ObjReader.read(stringReader);

            } else {//Use it and cache the file.
                StringReader stringReader = new StringReader(request.getString("model"));
                model = ObjReader.read(stringReader);
                FileWriter myWriter = new FileWriter(sessionDir.getAbsolutePath() + "/" + modelName);
                OutputStream objOutputStream = new FileOutputStream(sessionDir.getAbsolutePath() + "/" + modelName);
                ObjWriter.write(model, objOutputStream);

            }

            Point3D bbxP1 = new Point3D(request.getJSONObject("cornerOne").getDouble("x"), request.getJSONObject("cornerOne").getDouble("y"), request.getJSONObject("cornerOne").getDouble("z"));
            Point3D bbxP2 = new Point3D(request.getJSONObject("cornerTwo").getDouble("x"), request.getJSONObject("cornerTwo").getDouble("y"), request.getJSONObject("cornerTwo").getDouble("z"));
            Point3D up = new Point3D(request.getJSONObject("up").getDouble("x"), request.getJSONObject("up").getDouble("y"), request.getJSONObject("up").getDouble("z"));
            
            String XMLPath = "yaftemp/"+modelSessionKey+"/TGCMP-yafaray.xml";
            File yafScene = new File(XMLPath);
            FileWriter myWriter = new FileWriter(yafScene);
            myWriter.write(RenderingHelper.getYafarayXML(model, new Point3D[]{bbxP1, bbxP2,up}, width, height));
            myWriter.close();
            System.out.println("Successfully wrote to the file.");

            //Then render //Always overwrites previous one.//Intended for single threaded computing.
            String outPutPath = "yaftemp/"+modelSessionKey+"/TGCMP.jpg";
            RenderingHelper.YafarayXMLRender(yafarayPath+" -f jpg -op yaftemp/"+modelSessionKey, "\"" + yafScene.getAbsolutePath() + "\" \"TGCMP\"", true);

            BufferedImage bImage = ImageIO.read(new File(outPutPath));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos);
            byte[] result = bos.toByteArray();
            he.sendResponseHeaders(200, result.length);
            OutputStream os = he.getResponseBody();
            os.write(result);
            os.close();

        } catch (IOException ex) {
            Logger.getLogger(CustomHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}
