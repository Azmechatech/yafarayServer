/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.parquet;

/**
 *Original:https://github.com/contactsunny/Parquet_File_Writer_POC/blob/master/src/main/java/com/contactsunny/poc/parquet_file_writer_poc/parquet/CustomParquetWriter.java
 * @author Manoj
 */
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;

import java.io.IOException;
import java.util.List;

public class CustomParquetWriter extends ParquetWriter<List<String>> {

    public CustomParquetWriter(
            Path file,
            MessageType schema,
            boolean enableDictionary,
            CompressionCodecName codecName
    ) throws IOException {
        super(file, new CustomWriteSupport(schema), codecName, DEFAULT_BLOCK_SIZE, DEFAULT_PAGE_SIZE, enableDictionary, false);
    }
}