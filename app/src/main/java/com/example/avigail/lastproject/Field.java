package com.example.avigail.lastproject;

/**
 * Created by avigail on 14/11/16.
 */
public class Field {
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
