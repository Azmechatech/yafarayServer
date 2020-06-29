/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.parquet;

import de.javagl.obj.Obj;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;
import org.apache.parquet.schema.Type;
import org.hsqldb.Row;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Dataset;

/**
 * Needs a re-look with combination of spark due to the query and storage of obj data.
 * @author Manoj
 */
public class ObjParquetHelper {

    public static MessageType getSchemaForVert() throws IOException {
        String rawSchema = "message m { required binary name; required binary type; required binary id; required binary v; required DOUBLE x; required DOUBLE y; required DOUBLE z;}";
        return MessageTypeParser.parseMessageType(rawSchema);
    }

    public static MessageType getSchemaForFace() throws IOException {
        String rawSchema = "message m { required binary name; required binary type; required binary id; required binary f; required INT64 v1; required INT64 v2; required INT64 v3;}";
        return MessageTypeParser.parseMessageType(rawSchema);
    }

    /**
     * Assuming the object3d is triangulated.
     *
     * @param object3D
     * @param name
     * @return
     */
    public static Pair<List<List<String>>, List<List<String>>> convertToParquet(Obj object3D, String name, String type) {

        List<List<String>> data = new ArrayList<>();

        for (int i = 0; i < object3D.getNumVertices(); i++) {
            List<String> parquetFileItem1 = new ArrayList<>();
            parquetFileItem1.add(name);
            parquetFileItem1.add(type);
            parquetFileItem1.add(i + "");
            parquetFileItem1.add("v");
            parquetFileItem1.add(object3D.getVertex(i).getX() + "");
            parquetFileItem1.add(object3D.getVertex(i).getY() + "");
            parquetFileItem1.add(object3D.getVertex(i).getZ() + "");
            data.add(parquetFileItem1);
        }

        //Assuming triangulated surface
        List<List<String>> dataf = new ArrayList<>();
        for (int i = 0; i < object3D.getNumFaces(); i++) {
            List<String> parquetFileItem1 = new ArrayList<>();
            parquetFileItem1.add(name);
            parquetFileItem1.add(type);
            parquetFileItem1.add(i + "");
            parquetFileItem1.add("f");
            parquetFileItem1.add(object3D.getFace(i).getVertexIndex(0) + "");
            parquetFileItem1.add(object3D.getFace(i).getVertexIndex(1) + "");
            parquetFileItem1.add(object3D.getFace(i).getVertexIndex(2) + "");
            dataf.add(parquetFileItem1);
        }
        return new Pair<>(data, dataf);
    }

    /**
     *
     * @param objects3D
     * @return
     */
    public static Pair<List<List<String>>, List<List<String>>> convertToParquet(HashMap<String, Obj> objects3D) {
        List<List<String>> data = new ArrayList<>();
        //Assuming triangulated surface
        List<List<String>> dataf = new ArrayList<>();
        objects3D.entrySet().forEach(kv -> {
            Pair<List<List<String>>, List<List<String>>> thisdata = convertToParquet(kv.getValue(), kv.getKey(), "GENERIC");

            data.addAll(thisdata.getKey());
            dataf.addAll(thisdata.getValue());

        });

        return new Pair<>(data, dataf);
    }
/**
 * 
 * @param objects3D
 * @return 
 */
    public static Pair<List<List<String>>, List<List<String>>> convertToParquet2(List<Pair<String, Pair<String, Obj>>> objects3D) {
        List<List<String>> data = new ArrayList<>();
        //Assuming triangulated surface
        List<List<String>> dataf = new ArrayList<>();
        objects3D.stream().forEach(kv -> {
            Pair<List<List<String>>, List<List<String>>> thisdata = convertToParquet(kv.getValue().getValue(), kv.getKey(), kv.getValue().getKey());

            data.addAll(thisdata.getKey());
            dataf.addAll(thisdata.getValue());

        });

        return new Pair<>(data, dataf);
    }

    /**
     *
     * @param schema
     * @param outputFilePath
     * @return
     * @throws IOException
     */
    public static CustomParquetWriter getParquetWriter(MessageType schema, String outputFilePath) throws IOException {
        //String outputFilePath = outputDirectoryPath + "/" + System.currentTimeMillis() + ".parquet";
        File outputParquetFile = new File(outputFilePath);
        Path path = new Path(outputParquetFile.toURI().toString());
        return new CustomParquetWriter(
                path, schema, false, CompressionCodecName.GZIP
        );
    }

    /**
     *
     * @param columns
     * @param schema
     * @param writer
     * @throws IOException
     */
    public static void write(List<List<String>> columns, MessageType schema, CustomParquetWriter writer) throws IOException {
        for (List<String> column : columns) {
            writer.write(column);
        }
        writer.close();
    }

    /**
     *
     * @param object3D
     * @param outputFolder ./abcd/modelName Program will add Suffix
     * @return
     * @throws IOException
     */
    public static Pair<String, String> write(HashMap<String, Obj> object3D, String outputFolder) throws IOException {
        Pair<List<List<String>>, List<List<String>>> pqtData = convertToParquet(object3D);
        MessageType vSchema = getSchemaForVert();
        MessageType fSchema = getSchemaForFace();

        String outputFilePathVert = outputFolder + "/vert.parquet";
        String outputFilePathFace = outputFolder + "/face.parquet";

        CustomParquetWriter cpwVert = getParquetWriter(vSchema, outputFilePathVert);
        CustomParquetWriter cpwFace = getParquetWriter(fSchema, outputFilePathFace);

        for (List<String> column : pqtData.getKey()) {
            cpwVert.write(column);
        }
        cpwVert.close();

        for (List<String> column : pqtData.getValue()) {
            cpwFace.write(column);
        }
        cpwFace.close();

        return new Pair<String, String>(outputFilePathVert, outputFilePathFace);
    }
    
    /**
     * 
     * @param objects3D
     * @param outputFolder
     * @param prefix
     * @return
     * @throws IOException 
     */
    public static Pair<String, String> write(List<Pair<String, Pair<String, Obj>>> objects3D, String outputFolder, String prefix) throws IOException {
        Pair<List<List<String>>, List<List<String>>> pqtData = convertToParquet2(objects3D);
        MessageType vSchema = getSchemaForVert();
        MessageType fSchema = getSchemaForFace();

        String outputFilePathVert = outputFolder + "/" + prefix + "vert.parquet";
        String outputFilePathFace = outputFolder + "/" + prefix + "face.parquet";

        CustomParquetWriter cpwVert = getParquetWriter(vSchema, outputFilePathVert);
        CustomParquetWriter cpwFace = getParquetWriter(fSchema, outputFilePathFace);

        for (List<String> column : pqtData.getKey()) {
            try {
                cpwVert.write(column);
            } catch (Exception ex) {
            }
        }
        cpwVert.close();

        for (List<String> column : pqtData.getValue()) {
            try {
                cpwFace.write(column);
            } catch (Exception ex) {
            }
        }
        cpwFace.close();

        return new Pair<String, String>(outputFilePathVert, outputFilePathFace);
    }
    
    /**
     * 
     * @param outputFolder
     * @param prefix
     * @param name
     * @param type
     * @return
     * @throws IOException 
     */
    public static Obj getObj(String outputFolder,String prefix,String name,String type) throws IOException{
        String outputFilePathVert = outputFolder + "/" + prefix + "vert.parquet";
        String outputFilePathFace = outputFolder + "/" + prefix + "face.parquet";

        List<Group> resultVert = ParquetReaderUtils.readFile(new File(outputFilePathVert), getSchemaForVert());
        resultVert.stream().forEach(grp -> {
            printGroup(grp);
        });
        
        List<Group> resultFace = ParquetReaderUtils.readFile(new File(outputFilePathFace), getSchemaForVert());
        resultFace.stream().forEach(grp -> {
            printGroup(grp);
        });
        
        return null;
       
    }

    /**
     * https://spark.apache.org/docs/latest/sql-data-sources-parquet.html
     * @param g
     * @param name
     * @param type
     * @return 
     */
    private static List<List<String>> filterData(Group g, String name, String type) {
        return null;
        // Dataset<Row> peopleDF = spark.read().json("examples/src/main/resources/people.json");

// DataFrames can be saved as Parquet files, maintaining the schema information
//peopleDF.write().parquet("people.parquet");
// Read in the Parquet file created above.
// Parquet files are self-describing so the schema is preserved
// The result of loading a parquet file is also a DataFrame
//        Dataset<Row> parquetFileDF = spark.read().parquet("people.parquet");
//
//// Parquet files can also be used to create a temporary view and then used in SQL statements
//        parquetFileDF.createOrReplaceTempView("parquetFile");
//        Dataset<Row> namesDF = spark.sql("SELECT name FROM parquetFile WHERE age BETWEEN 13 AND 19");
//        Dataset<String> namesDS = namesDF.map(
//                (MapFunction<Row, String>) row -> "Name: " + row.getString(0),
//                Encoders.STRING());
//        namesDS.show();
    }
    
    private static void printGroup(Group g) {
        int fieldCount = g.getType().getFieldCount();
        for (int field = 0; field < fieldCount; field++) {
            int valueCount = g.getFieldRepetitionCount(field);

            Type fieldType = g.getType().getType(field);
            String fieldName = fieldType.getName();

            for (int index = 0; index < valueCount; index++) {
                if (fieldType.isPrimitive()) {
                    System.out.println(fieldName + " " + g.getValueToString(field, index));
                }
            }
        }
        System.out.println("");
    }
    
    public static void main(String... args) throws FileNotFoundException, IOException {
        // Read an OBJ file
//        InputStream objInputStream
//                = new FileInputStream("F:\\Truegeometry\\obj\\jzb865er6v-IronMan\\IronMan\\EXP_IronMan.obj");
//        Obj object = ObjUtils.triangulate(ObjReader.read(objInputStream)); //Just triangulate.
//        HashMap<String, Obj> sample = new HashMap<>();
//        sample.put("sample", object);
//        write(sample, "F:\\Truegeometry\\storage\\test");
//        
       List<Group>result= ParquetReaderUtils.readFile(new File("F:\\Truegeometry\\store\\af7588d8c9b0b9f6b5a80cb963bd09e9\\IronMan.objvert.parquet"), getSchemaForVert() );
       result.stream().forEach(grp->{
           printGroup(grp);
       });
    }
}