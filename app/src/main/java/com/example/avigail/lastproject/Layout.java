package com.example.avigail.lastproject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by avigail on 14/11/16.
 */
public class Layout implements Serializable {
    int id;
    String layoutName;
    ArrayList<Field> fields;
    public Layout(int id,String layoutName){
        this.id = id;
        this.layoutName = layoutName;
        this.fields = new ArrayList<Field>();
    }
    public void setFields(ArrayList<Field> fields){
        this.fields = fields;
    }

}