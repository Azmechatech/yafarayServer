/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.parquet;

import static com.truegeometry.parquet.ParquetReaderUtils.readFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import static org.apache.parquet.example.Paper.schema;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.example.GroupWriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Manoj
 */
public class WriterTest {

    private static String schemaFilePath="F:\\Truegeometry\\storage\\sch.txt";
    private static String outputDirectoryPath="F:\\Truegeometry\\storage\\";
    private static final Logger logger = LoggerFactory.getLogger(WriterTest.class);

    public static void main(String... args) throws IOException {
        List<List<String>> columns = getDataForFile();
        MessageType schema = getSchemaForParquetFile();
        CustomParquetWriter writer = getParquetWriter(schema);

        for (List<String> column : columns) {
            logger.info("Writing line: " + column.toArray());
            writer.write(column);
        }
        logger.info("Finished writing Parquet file.");

        writer.close();
        
        //Read
        readFile(new File("F:\\Truegeometry\\storage\\1593144099150.parquet"));
        
    }
    
 

    private static CustomParquetWriter getParquetWriter(MessageType schema) throws IOException {
        String outputFilePath = outputDirectoryPath + "/" + System.currentTimeMillis() + ".parquet";
        File outputParquetFile = new File(outputFilePath);
        Path path = new Path(outputParquetFile.toURI().toString());
        return new CustomParquetWriter(
                path, schema, false, CompressionCodecName.SNAPPY
        );
    }

    private static MessageType getSchemaForParquetFile() throws IOException {
        File resource = new File(schemaFilePath);
        String rawSchema = new String(Files.readAllBytes(resource.toPath()));
        return MessageTypeParser.parseMessageType(rawSchema);
    }

    private static List<List<String>> getDataForFile() {
        List<List<String>> data = new ArrayList<>();

        List<String> parquetFileItem1 = new ArrayList<>();
        parquetFileItem1.add("1");
        parquetFileItem1.add("Name1");
        parquetFileItem1.add("true");

        List<String> parquetFileItem2 = new ArrayList<>();
        parquetFileItem2.add("2");
        parquetFileItem2.add("Name2");
        parquetFileItem2.add("false");

        data.add(parquetFileItem1);
        data.add(parquetFileItem2);

        return data;
    }
}
