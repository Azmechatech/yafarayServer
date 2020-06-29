/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.parquet;

/**
 * Original:https://www.arm64.ca/post/reading-parquet-files-java/
 * @author Manoj
 */
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
//import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.apache.parquet.example.Paper.schema;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.example.GroupWriteSupport;

public class ParquetReaderUtils {

//    public static Parquet getParquetData(String filePath) throws IOException {
//        List<SimpleGroup> simpleGroups = new ArrayList<>();
//        ParquetFileReader reader = ParquetFileReader.open(HadoopInputFile.fromPath(new Path(filePath), new Configuration()));
//        MessageType schema = reader.getFooter().getFileMetaData().getSchema();
//        List<Type> fields = schema.getFields();
//        PageReadStore pages;
//        while ((pages = reader.readNextRowGroup()) != null) {
//            long rows = pages.getRowCount();
//            MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);
//            RecordReader recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));
//
//            for (int i = 0; i < rows; i++) {
//                SimpleGroup simpleGroup = (SimpleGroup) recordReader.read();
//                simpleGroups.add(simpleGroup);
//            }
//        }
//        reader.close();
//        return new Parquet(simpleGroups, fields);
//    }
    
       public static List<Group> readFile(File f) throws IOException {
        Configuration conf = new Configuration();
        GroupWriteSupport.setSchema(schema, conf);

        ParquetReader<Group> reader
                = ParquetReader.builder(new GroupReadSupport(), new Path(f.getAbsolutePath()))
                        .withConf(conf)
                        .build();

        Group current;
        List<Group> users = new ArrayList<Group>();

        current = reader.read();
        while (current != null) {
            users.add(current);
            current = reader.read();
        }

        return users;
    }
       
    public static List<Group> readFile(File f, MessageType schema) throws IOException {
        Configuration conf = new Configuration();
        GroupWriteSupport.setSchema(schema, conf);

        ParquetReader<Group> reader
                = ParquetReader.builder(new GroupReadSupport(), new Path(f.getAbsolutePath()))
                        .withConf(conf)
                        .build();

        Group current;
        List<Group> users = new ArrayList<Group>();

        current = reader.read();
        while (current != null) {
            users.add(current);
            current = reader.read();
        }

        return users;
    }
}