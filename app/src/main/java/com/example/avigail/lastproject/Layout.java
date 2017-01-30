package com.example.avigail.lastproject;

import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by avigail on 14/11/16.
 */
public class Layout {
    int id;
    String layoutName;
    ArrayList<Field> fileds;
    public Layout(int id,String layoutName){
        this.id = id;
        this.layoutName = layoutName;
        this.fileds = new ArrayList<Field>();
    }
    public void setFileds(ArrayList<Field> fileds){
        this.fileds = fileds;
    }

}