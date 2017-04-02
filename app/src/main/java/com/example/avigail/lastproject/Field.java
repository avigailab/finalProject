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
    String filedAnswer;
    String language;
    Field(int id,String fieldName, boolean required,int order, String dataType, String lang){
        this.id = id;
        this.filedName = fieldName;
        this.required = required;
        this.order = order;
        this.dataType = dataType;
        this.filedAnswer="";
        this.language = lang;
    }
    public void setFiledAnswer(String filedAnswer){
        this.filedAnswer=filedAnswer;
    }
    public String getFiledAnswer (){
        return this.filedAnswer;
    }

}
