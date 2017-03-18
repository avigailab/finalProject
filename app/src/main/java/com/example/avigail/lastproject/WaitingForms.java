package com.example.avigail.lastproject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by op3 on 15/03/17.
 */

public class WaitingForms implements Serializable {
    public ArrayList<Layout> waitingForms;

    public void WaitingForms(){
        this.waitingForms=new ArrayList<Layout>();
    }
    public void addForm(Layout form){
        this.waitingForms.add(0,form);
    }
    public void removeForm(Layout form){
        this.waitingForms.remove(form);
    }
    public ArrayList<Layout> getWaitingForms(){
        return this.waitingForms;
    }
}
