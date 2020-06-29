/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.parquet;

/**
 * Original : https://www.arm64.ca/post/reading-parquet-files-java/
 *
 * @author Manoj
 */
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.schema.Type;

import java.util.List;

public class Parquet {

    private List<SimpleGroup> data;
    private List<Type> schema;

    public Parquet(List<SimpleGroup> data, List<Type> schema) {
        this.data = data;
        this.schema = schema;
    }

    public List<SimpleGroup> getData() {
        return data;
    }

    public List<Type> getSchema() {
        return schema;
    }
}
