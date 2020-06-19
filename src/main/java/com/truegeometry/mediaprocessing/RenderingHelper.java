/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mediaprocessing;

import com.truegeometry.mediaprocessing.Point3D;
import de.javagl.obj.FloatTuple;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjUtils;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import j2html.tags.UnescapedText;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

/**
 *
 * @author Manoj
 */
public class RenderingHelper {

    public static void main(String... args) {

        ContainerTag xml = new ContainerTag("xml");
        xml.attr("version", "1.0");
        // xml.with(xml);

        ContainerTag scene = new ContainerTag("scene");
        //scene.attr("type", "triangle");
        scene.attr("type", "universal");
        xml.with(scene);

        ContainerTag mesh = new ContainerTag("mesh");
        mesh.attr("id", 1);
        mesh.attr("vertices", 1);
        mesh.attr("faces", 1);
        mesh.attr("has_orco", false);
        mesh.attr("has_uv", false);
        mesh.attr("type", 0);
        mesh.attr("obj_pass_index", 1);

        scene.with(mesh, mesh, generateYafarayBackground(), generateYafarayIntegrator(), generateYafarayRender("cam",100,100));

        //TagCreator.rawHtml(html);
        System.out.println(xml.render());
    }

    public static String getYafarayXML(Obj obj, Point3D[] bbx,int imgWidth,int imgHeight) {

        ContainerTag xml = new ContainerTag("xml");
        xml.attr("version", "1.0");
        // xml.with(xml);

        ContainerTag scene = new ContainerTag("scene");
        scene.attr("type", "triangle");
        //scene.attr("type", "universal");
        xml.with(scene);

        ContainerTag mesh = generateYafarayMesh(obj, 1, "defaultMat");
//,generateYafarayFixCamera()
        scene.with(
                //generateYafarayRender_passes(),
                //generateYafarayLogging_badge(),
                generateYafarayMaterialShiny(),
                //generateYafarayMaterialLight(),
                //generateYafarayMeshLight(1) ,
                generateYafarayFixLight("TopCorner", bbx[1]),
                generateYafarayFixLight("BottomCorner", bbx[0]),
                mesh,
                generateYafarayBackground(),
                //generateYafarayBackgroundGradient(),
                generateYafarayIntegrator(),
                generateYafarayCamera(obj, "cam", bbx,imgWidth,imgHeight),
                //generateYafarayCameraOrtho(obj, "cam", bbx,imgWidth,imgHeight),
                generateYafarayRender("cam",imgWidth,imgHeight));

        return xml.renderFormatted();

    }

    /**
     * Generate Yafaray compatible mesh.
     *
     * @param obj
     * @param id
     * @return
     */
    static ContainerTag generateYafarayMesh(Obj obj, int id, String materialName) {

        obj = ObjUtils.triangulate(obj);

        ContainerTag mesh = new ContainerTag("mesh");
        mesh.attr("id", id);
        mesh.attr("vertices", obj.getNumVertices());
        mesh.attr("faces", obj.getNumFaces());
        mesh.attr("has_orco", false);
        mesh.attr("has_uv", false);
        mesh.attr("type", 0);
        mesh.attr("obj_pass_index", 1);

        Tag[] meshData = new Tag[obj.getNumVertices() + 1 + obj.getNumFaces()];
        for (int vCount = 0; vCount < obj.getNumVertices(); vCount++) {//Regenerate verteics
            //<p x="-4.40469" y="1.44162" z="1.00136e-05" ox="-1" oy="-1" oz="-1"/>
            Tag p = TagCreator.p();

            p.attr("x", obj.getVertex(vCount).getX());
            p.attr("y", obj.getVertex(vCount).getY());
            p.attr("z", obj.getVertex(vCount).getZ());

            p.attr("ox", 1);
            p.attr("oy", 1);
            p.attr("oz", 1);

            meshData[vCount] = p;

        }

        Tag set_material = TagCreator.tag("set_material");
        set_material.attr("sval", "defaultMat");
        meshData[obj.getNumVertices()] = set_material;
        //<set_material sval="Material.008--9223363248046659264"/>
        for (int fCount = 0; fCount < obj.getNumFaces(); fCount++) {//Regenerate faces
            //<f a="2" b="0" c="1"/>

            Tag p = TagCreator.tag("f");
            p.attr("a", obj.getFace(fCount).getVertexIndex(0));
            p.attr("b", obj.getFace(fCount).getVertexIndex(1));
            p.attr("c", obj.getFace(fCount).getVertexIndex(2));

            meshData[fCount + 1 + obj.getNumVertices()] = p;

        }

        mesh.with(meshData);

        return mesh;
    }

    /**
     * <camera name="cam">
     * <aperture fval="0"/>
     * <bokeh_rotation fval="0"/>
     * <bokeh_type sval="disk1"/>
     * <dof_distance fval="0"/>
     * <focal fval="1.09375"/>
     * <from x="8.64791" y="-7.22615" z="8.1295"/>
     * <resx ival="480"/>
     * <resy ival="270"/>
     * <to x="8.03447" y="-6.65603" z="7.58301"/>
     * <type sval="perspective"/>
     * <up x="8.25644" y="-6.8447" z="8.9669"/>
     * <view_name sval=""/>
     * </camera> @param obj
     * @param id
     * @return
     */

    static ContainerTag generateYafarayCamera(Obj obj, String name, Point3D[] bbx,int imgWidth,int imgHeight) {
        
        obj = ObjUtils.triangulate(obj);

        ContainerTag camera = new ContainerTag("camera");
        camera.attr("name", name);

        Tag aperture = TagCreator.tag("aperture");
        aperture.attr("fval", "0");

        Tag bokeh_rotation = TagCreator.tag("bokeh_rotation");
        bokeh_rotation.attr("fval", "0");

        Tag bokeh_type = TagCreator.tag("bokeh_type");
        bokeh_type.attr("sval", "disk1");

        Tag dof_distance = TagCreator.tag("dof_distance");
        dof_distance.attr("fval", "0");

        Tag focal = TagCreator.tag("focal");
        focal.attr("fval", "1");

        Tag from = TagCreator.tag("from");
        from.attr("x", 2 * bbx[1].getX());
        from.attr("y", 2 * bbx[1].getY());
        from.attr("z", 2 * bbx[1].getZ());

        Tag resx = TagCreator.tag("resx");
        resx.attr("ival", imgWidth);

        Tag resy = TagCreator.tag("resy");
        resy.attr("ival", imgHeight);

        Tag to = TagCreator.tag("to");
        to.attr("x", bbx[1].getX() - Math.signum(bbx[1].getX() - bbx[0].getX()) * 1); //Look at center
        to.attr("y", bbx[1].getY() - Math.signum(bbx[1].getY() - bbx[0].getY()) * 1); // Look at cenetr
        to.attr("z", bbx[1].getZ() - Math.signum(bbx[1].getZ() - bbx[0].getZ()) * 1); // Look at cenetr

        Tag type = TagCreator.tag("type");
        type.attr("sval", "perspective");

        Tag up = TagCreator.tag("up");
        up.attr("x", bbx[2].getX());
        up.attr("y", bbx[2].getY());
        up.attr("z", bbx[2].getZ());

        Tag view_name = TagCreator.tag("view_name");
        view_name.attr("sval", "");

        camera.with(aperture, bokeh_rotation, bokeh_type, dof_distance, focal, from, resx, resy, to, type, up, view_name);

        return camera;
    }

        static ContainerTag generateYafarayCameraOrtho(Obj obj, String name, Point3D[] bbx,int imgWidth,int imgHeight) {

        obj = ObjUtils.triangulate(obj);

        ContainerTag camera = new ContainerTag("camera");
        camera.attr("name", name);


        Tag from = TagCreator.tag("from");
        from.attr("x", 2 * bbx[1].getX());
        from.attr("y", 2 * bbx[1].getY());
        from.attr("z", 2 * bbx[1].getZ());

        Tag resx = TagCreator.tag("resx");
        resx.attr("ival", imgWidth);

        Tag resy = TagCreator.tag("resy");
        resy.attr("ival", imgHeight);

        Tag to = TagCreator.tag("to");
        to.attr("x", bbx[1].getX() - Math.signum(bbx[1].getX() - bbx[0].getX()) * 1); //Look at center
        to.attr("y", bbx[1].getY() - Math.signum(bbx[1].getY() - bbx[0].getY()) * 1); // Look at cenetr
        to.attr("z", bbx[1].getZ() - Math.signum(bbx[1].getZ() - bbx[0].getZ()) * 1); // Look at cenetr

        Tag type = TagCreator.tag("type");
        type.attr("sval", "orthographic ");
        
       
        Tag up = TagCreator.tag("up");
        up.attr("x", 0);
        up.attr("y", 0);
        up.attr("z", 1);
        
         Tag scale = TagCreator.tag("scale");
        type.attr("fval", "1 ");
        
        Tag aspect_ratio = TagCreator.tag("aspect_ratio");
        type.attr("fval", "1 ");


//        Tag view_name = TagCreator.tag("view_name");
//        view_name.attr("sval", "");

        camera.with( from, resx, resy, to, type, up,scale,aspect_ratio);

        return camera;
    }
        
    static UnescapedText generateYafarayFixCamera() {
        return TagCreator.rawHtml("<camera name=\"cam\">\n"
                + "	<aperture fval=\"0\"/>\n"
                + "	<bokeh_rotation fval=\"0\"/>\n"
                + "	<bokeh_type sval=\"disk1\"/>\n"
                + "	<dof_distance fval=\"0\"/>\n"
                + "	<focal fval=\"1.09375\"/>\n"
                + "	<from x=\"8.64791\" y=\"-7.22615\" z=\"8.1295\"/>\n"
                + "	<resx ival=\"480\"/>\n"
                + "	<resy ival=\"270\"/>\n"
                + "	<to x=\"8.03447\" y=\"-6.65603\" z=\"7.58301\"/>\n"
                + "	<type sval=\"perspective\"/>\n"
                + "	<up x=\"8.25644\" y=\"-6.8447\" z=\"8.9669\"/>\n"
                + "	<view_name sval=\"\"/>\n"
                + "</camera>");

    }

    static UnescapedText generateYafarayFixLight(String name, Point3D from) {
        return TagCreator.rawHtml("<light name=\"" + name + "\">\n"
                + "	<color r=\"1\" g=\"1\" b=\"1\" a=\"1\"/>\n"
                + "	<from x=\"" + from.getX() + "\" y=\"" + from.getY() + "\" z=\"" + from.getZ() + "\"/>\n"
                + "	<power fval=\"1\"/>\n"
                + "	<type sval=\"sunlight\"/>\n"
                + "</light>");

    }

    /**
     * Generates mesh light
     *
     * @param id
     * @return
     */
    static UnescapedText generateYafarayMeshLight(int id) {
        return TagCreator.rawHtml("<light name=\"MeshLight\">\n"
                + "	<color r=\"0\" g=\"1\" b=\"1\" a=\"1\"/>\n"
                + "	<double_sided bval=\"true\"/>\n"
                + "	<power fval=\"0.5\"/>\n"
                + "	<object ival=\"" + id + "\"/>\n"
                + "	<type sval=\"meshlight\"/>\n"
                + "	<samples ival=\"10\"/>\n"
                + "</light>");

    }

    static UnescapedText generateYafarayRender(String cameraName,int imgWidth,int imgHeight) {
        return TagCreator.rawHtml("<render>\n"
                + "	<AA_clamp_indirect fval=\"0\"/>\n"
                + "	<AA_clamp_samples fval=\"0\"/>\n"
                + "	<AA_dark_detection_type sval=\"linear\"/>\n"
                + "	<AA_dark_threshold_factor fval=\"0\"/>\n"
                + "	<AA_detect_color_noise bval=\"false\"/>\n"
                + "	<AA_inc_samples ival=\"1\"/>\n"
                + "	<AA_indirect_sample_multiplier_factor fval=\"1\"/>\n"
                + "	<AA_light_sample_multiplier_factor fval=\"1\"/>\n"
                + "	<AA_minsamples ival=\"1\"/>\n"
                + "	<AA_passes ival=\"1\"/>\n"
                + "	<AA_pixelwidth fval=\"1.5\"/>\n"
                + "	<AA_resampled_floor fval=\"0\"/>\n"
                + "	<AA_sample_multiplier_factor fval=\"1\"/>\n"
                + "	<AA_threshold fval=\"0.05\"/>\n"
                + "	<AA_variance_edge_size ival=\"10\"/>\n"
                + "	<AA_variance_pixels ival=\"0\"/>\n"
                + "	<adv_auto_min_raydist_enabled bval=\"true\"/>\n"
                + "	<adv_auto_shadow_bias_enabled bval=\"true\"/>\n"
                + "	<adv_base_sampling_offset ival=\"0\"/>\n"
                + "	<adv_computer_node ival=\"0\"/>\n"
                + "	<adv_min_raydist_value fval=\"5e-05\"/>\n"
                + "	<adv_shadow_bias_value fval=\"0.0005\"/>\n"
                + "	<background_name sval=\"world_background\"/>\n"
                + "	<background_resampling bval=\"true\"/>\n"
                + "	<camera_name sval=\"" + cameraName + "\"/>\n"
                + "	<color_space sval=\"sRGB\"/>\n"
                + "	<color_space2 sval=\"sRGB\"/>\n"
                + "	<denoiseEnabled bval=\"true\"/>\n"
                + "	<denoiseHCol ival=\"5\"/>\n"
                + "	<denoiseHLum ival=\"5\"/>\n"
                + "	<denoiseMix fval=\"0.8\"/>\n"
                + "	<film_autosave_interval_passes ival=\"1\"/>\n"
                + "	<film_autosave_interval_seconds fval=\"300\"/>\n"
                + "	<film_autosave_interval_type sval=\"none\"/>\n"
                + "	<film_save_binary_format bval=\"false\"/>\n"
                +//set to false
                "	<film_save_load sval=\"false\"/>\n"
                + //set to false
                "	<filter_type sval=\"gauss\"/>\n"
                + "	<gamma fval=\"1\"/>\n"
                + "	<gamma2 fval=\"1\"/>\n"
                + "	<height ival=\""+imgHeight+"\"/>\n"
                + "	<images_autosave_interval_passes ival=\"1\"/>\n"
                + "	<images_autosave_interval_seconds fval=\"300\"/>\n"
                + "	<images_autosave_interval_type sval=\"none\"/>\n"
                + "	<integrator_name sval=\"default\"/>\n"
                + "	<premult bval=\"false\"/>\n"
                + "	<show_sam_pix bval=\"true\"/>\n"
                + "	<threads ival=\"-1\"/>\n"
                + "	<threads_photons ival=\"-1\"/>\n"
                + "	<tile_size ival=\"32\"/>\n"
                + "	<tiles_order sval=\"centre\"/>\n"
                + "	<type sval=\"none\"/>\n"
                + "	<volintegrator_name sval=\"volintegr\"/>\n"
                + "	<width ival=\""+imgWidth+"\"/>\n"
                + "	<xstart ival=\"0\"/>\n"
                + "	<ystart ival=\"0\"/>\n"
                + "</render>");

    }

    static UnescapedText generateYafarayMaterialShiny() {
        return TagCreator.rawHtml("<material name=\"defaultMat\">\n"
                + "	<IOR fval=\"1.8\"/>\n"
                + "	<additionaldepth ival=\"0\"/>\n"
                + "	<color r=\"1\" g=\".843\" b=\"0\" a=\"1\"/>\n"
                + // GOLD 255,215,0
                "	<diffuse_reflect fval=\"0.5\"/>\n"
                + "	<emit fval=\"0\"/>\n"
                + "	<fresnel_effect bval=\"false\"/>\n"
                + "	<mat_pass_index ival=\"2\"/>\n"
                + "	<mirror_color r=\"1\" g=\"1\" b=\"1\" a=\"1\"/>\n"
                + "	<receive_shadows bval=\"true\"/>\n"
                + "	<samplingfactor fval=\"1\"/>\n"
                + "	<specular_reflect fval=\"0\"/>\n"
                + "	<translucency fval=\"0\"/>\n"
                + "	<transmit_filter fval=\"1\"/>\n"
                + "	<transparency fval=\"0\"/>\n"
                + "	<type sval=\"shinydiffusemat\"/>\n"
                + "	<visibility sval=\"normal\"/>\n"
                + "	<wireframe_amount fval=\".2\"/>\n"
                + "	<wireframe_color r=\"0\" g=\"0\" b=\"0\" a=\"1\"/>\n"
                + "	<wireframe_exponent fval=\"0.1\"/>\n"
                + "	<wireframe_thickness fval=\"0.01\"/>\n"
                + "</material>");
    }

    static UnescapedText generateYafarayMaterialLight() {
        return TagCreator.rawHtml("<material name=\"defaultMat\">\n"
                + "	<type sval=\"light_mat\"/>\n"
                + "	<color r=\".1\" g=\".9\" b=\".9\" a=\"1\"/>\n"
                + "	<double_sided bval=\"true\"/>\n"
                + "</material>");
    }

    static UnescapedText generateYafarayBackground() {
        return TagCreator.rawHtml("<background name=\"world_background\">\n"
                + "	<cast_shadows bval=\"true\"/>\n"
                + "	<cast_shadows_sun bval=\"true\"/>\n"
                + "	<color r=\"0\" g=\"0\" b=\"0\" a=\"1\"/>\n"
                + "	<ibl bval=\"false\"/>\n"
                + "	<ibl_samples ival=\"3\"/>\n"
                + "	<power fval=\"0.5\"/>\n"
                + "	<type sval=\"constant\"/>\n"
                + "	<with_caustic bval=\"true\"/>\n"
                + "	<with_diffuse bval=\"true\"/>\n"
                + "</background>");

    }

    static UnescapedText generateYafarayBackgroundGradient() {
        return TagCreator.rawHtml("<background name=\"world_background\">\n"
                + "	<horizon_color r=\"0.854296\" g=\"0.854296\" b=\"0.854296\" a=\"1\"/>\n"
                + "	<zenith_color r=\"0.54296\" g=\"0.54296\" b=\"0.854296\" a=\"1\"/>\n"
                + "	<horizon_ground_color r=\"0.854296\" g=\"0.854296\" b=\"0.854296\" a=\"1\"/>\n"
                + "	<zenith_ground_color r=\"0.54296\" g=\"0.54296\" b=\"0.54296\" a=\"1\"/>\n"
                + "	<power fval=\"0.5\"/>\n"
                + "	<type sval=\"gradientback\"/>\n"
                + "</background>");

    }

    static UnescapedText generateYafarayIntegrator() {
        return TagCreator.rawHtml("<integrator name=\"default\">\n"
                + "	<AO_color r=\"0.954685\" g=\"0.954685\" b=\"0.954685\" a=\"1\"/>\n"
                + "	<AO_distance fval=\"1\"/>\n"
                + "	<AO_samples ival=\"32\"/>\n"
                + "	<bg_transp bval=\"false\"/>\n"
                + "	<bg_transp_refract bval=\"false\"/>\n"
                + "	<caustics bval=\"false\"/>\n"
                + "	<do_AO bval=\"true\"/>\n"
                + //Ambient Occlusion => AO
                "	<photon_maps_processing sval=\"generate-only\"/>\n"
                + "	<raydepth ival=\"8\"/>\n"
                + "	<shadowDepth ival=\"2\"/>\n"
                + "	<transpShad bval=\"false\"/>\n"
                + "	<type sval=\"directlighting\"/>\n"
                + "</integrator>\n"
                + "\n"
                + "<integrator name=\"volintegr\">\n"
                + "	<type sval=\"none\"/>\n"
                + "</integrator>");

    }

    static UnescapedText generateYafarayRender_passes() {
        return TagCreator.rawHtml("<render_passes name=\"render_passes\">\n"
                + "	<facesEdgeSmoothness fval=\"0.5\"/>\n"
                + "	<facesEdgeThickness ival=\"1\"/>\n"
                + "	<facesEdgeThreshold fval=\"0.01\"/>\n"
                + "	<objectEdgeSmoothness fval=\"0.75\"/>\n"
                + "	<objectEdgeThickness ival=\"2\"/>\n"
                + "	<objectEdgeThreshold fval=\"0.3\"/>\n"
                + "	<pass_AO sval=\"ao-clay\"/>\n"
                + "	<pass_Color sval=\"disabled\"/>\n"
                + "	<pass_Depth sval=\"z-depth-norm\"/>\n"
                + "	<pass_DiffCol sval=\"disabled\"/>\n"
                + "	<pass_DiffDir sval=\"disabled\"/>\n"
                + "	<pass_DiffInd sval=\"disabled\"/>\n"
                + "	<pass_Diffuse sval=\"diffuse\"/>\n"
                + "	<pass_Emit sval=\"disabled\"/>\n"
                + "	<pass_Env sval=\"env\"/>\n"
                + "	<pass_GlossCol sval=\"disabled\"/>\n"
                + "	<pass_GlossDir sval=\"disabled\"/>\n"
                + "	<pass_GlossInd sval=\"disabled\"/>\n"
                + "	<pass_IndexMA sval=\"disabled\"/>\n"
                + "	<pass_IndexOB sval=\"disabled\"/>\n"
                + "	<pass_Indirect sval=\"disabled\"/>\n"
                + "	<pass_Mist sval=\"mist\"/>\n"
                + "	<pass_Normal sval=\"debug-normal-smooth\"/>\n"
                + "	<pass_Reflect sval=\"disabled\"/>\n"
                + "	<pass_Refract sval=\"disabled\"/>\n"
                + "	<pass_Shadow sval=\"shadow\"/>\n"
                + "	<pass_Spec sval=\"disabled\"/>\n"
                + "	<pass_SubsurfaceCol sval=\"disabled\"/>\n"
                + "	<pass_SubsurfaceDir sval=\"disabled\"/>\n"
                + "	<pass_SubsurfaceInd sval=\"disabled\"/>\n"
                + "	<pass_TransCol sval=\"disabled\"/>\n"
                + "	<pass_TransDir sval=\"disabled\"/>\n"
                + "	<pass_TransInd sval=\"disabled\"/>\n"
                + "	<pass_UV sval=\"obj-index-auto\"/>\n"
                + "	<pass_Vector sval=\"mat-index-auto\"/>\n"
                + "	<pass_enable bval=\"true\"/>\n"
                + "	<pass_mask_invert bval=\"false\"/>\n"
                + "	<pass_mask_mat_index ival=\"2\"/>\n"
                + "	<pass_mask_obj_index ival=\"2\"/>\n"
                + "	<pass_mask_only bval=\"false\"/>\n"
                + "	<toonEdgeColor r=\"1\" g=\"1\" b=\"0\" a=\"1\"/>\n"
                + "	<toonPostSmooth fval=\"3\"/>\n"
                + "	<toonPreSmooth fval=\"3\"/>\n"
                + "	<toonQuantization fval=\"0.1\"/>\n"
                + "</render_passes>");

    }

    static UnescapedText generateYafarayLogging_badge() {
        return TagCreator.rawHtml("<logging_badge name=\"logging_badge\">\n"
                + "	<logging_author sval=\"Machine Genetrated\"/>\n"
                + "	<logging_comments sval=\"Reverse engineered geometry.\"/>\n"
                + "	<logging_contact sval=\"\"/>\n"
                + "	<logging_drawAANoiseSettings bval=\"false\"/>\n"
                + "	<logging_drawRenderSettings bval=\"false\"/>\n"
                + "	<logging_fontPath sval=\"\"/>\n"
                + "	<logging_fontSizeFactor fval=\"1\"/>\n"
                + "	<logging_paramsBadgePosition sval=\"bottom\"/>\n"
                + "	<logging_saveHTML bval=\"false\"/>\n"
                + "	<logging_saveLog bval=\"false\"/>\n"
                + "	<logging_title sval=\"TrueGeometry.com\"/>\n"
                + "</logging_badge>");

    }

    public static String YafarayXMLRender(String Engine, String Scene, boolean printDebugMessage) {
        String s = null;

        try {

            // run the Unix "ps -ef" command
            System.out.println("Executing >>" + Engine + " " + Scene);
            Process p = Runtime.getRuntime().exec(Engine + " " + Scene);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            if(printDebugMessage)
            System.out.println("Here is the standard output of the command:\n");
            if(printDebugMessage)
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);

            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            //System.exit(0);
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            //System.exit(-1);
        }

        return s;
    }
    
    /**
     * To make HTTP Post
     * @throws IOException 
     */
    public static void sendPOST(String POST_URL,JSONObject requestBody,String ImagefileToSave) {
        try {
            URL obj = new URL(POST_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            
            // For POST only - START
            con.setDoOutput(true);
            con.setReadTimeout(5*60*1000);//5 Minutes
            OutputStream os = con.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();
            // For POST only - END
            
            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                
                // byte array
                byte[] bytes = toByteArray(con.getInputStream());
                
                System.out.println("Writing to file :: " + ImagefileToSave);
                try (FileOutputStream fos = new FileOutputStream(ImagefileToSave)) {
                    fos.write(bytes);
                    //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                } catch (FileNotFoundException ex) {
                    System.out.println("POST URL Failed :: " + POST_URL);
                    Logger.getLogger(RenderingHelper.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    System.out.println("POST URL Failed :: " + POST_URL);
                    Logger.getLogger(RenderingHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
                
//                
//			BufferedReader in = new BufferedReader(new InputStreamReader(
//					con.getInputStream()));
//			String inputLine;
//			StringBuffer response = new StringBuffer();
//
//			while ((inputLine = in.readLine()) != null) {
//				response.append(inputLine);
//			}
//			in.close();
//
//			// print result
//			System.out.println(response.toString());
            } else {
                System.out.println("POST request not worked");
            }
        } catch (MalformedURLException ex) {
            System.out.println("POST URL Failed :: " + POST_URL);
            Logger.getLogger(RenderingHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("POST URL Failed :: " + POST_URL);
            Logger.getLogger(RenderingHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
    
    public static byte[] toByteArray(InputStream in) throws IOException {

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int len;

		// read bytes from the input stream and store them in buffer
		while ((len = in.read(buffer)) != -1) {
			// write bytes from the buffer into output stream
			os.write(buffer, 0, len);
		}

		return os.toByteArray();
	}

}
