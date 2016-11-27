package com.example.avigail.lastproject;

import java.util.ArrayList;

/**
 * Created by avigail on 14/11/16.
 */
public class User {
    int id;
    String userEmail;
    String password;
    String serviceId;
    String clientId;
    ArrayList<Layout>layouts;
    User(int id,String userEmail, String password, String serviceId, String clientId){
        this.id = id;
        this.userEmail = userEmail;
        this.password = password;
        this.layouts = new ArrayList<Layout>();
        this.serviceId = serviceId;
        this.clientId = clientId;
    }
    public void setLayouts(ArrayList<Layout> layouts){
        this.layouts = layouts;
    }
    public ArrayList<Layout> getLayouts(){
        return this.layouts;
    }


}
