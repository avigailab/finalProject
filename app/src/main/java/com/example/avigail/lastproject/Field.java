package com.example.avigail.lastproject;

import java.io.Serializable;

/**
 * Created by avigail on 14/11/16.
 */
public class Field implements Serializable {
    int id;
    String filedName;
    boolean required;
    int order;
    String dataType;
    Field(int id,String fieldName, boolean required,int order, String dataType){
        this.id = id;
        this.filedName = fieldName;
        this.required = required;
        this.order = order;
        this.dataType =dataType;
    }
}
