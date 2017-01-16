package com.example.avigail.lastproject;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by avigail on 14/11/16.
 */
public class AppAdapter {
    private static final String TAG = "app adapter";


    public ArrayList<Layout> getObjectsLayoutsForUser(String arrayOfLayoutInfo) {
        ArrayList<Layout> arrayOfLayouts = new ArrayList<Layout>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputSource is = new InputSource();

        is.setCharacterStream(new StringReader(arrayOfLayoutInfo));

        try {

            Document doc = db.parse(is);
            NodeList layout = doc.getElementsByTagName("LayoutInfo");
            Element element = (Element) layout.item(0);
            int id=0;
            String name="";
            NodeList layoutName = element.getElementsByTagName("LayoutName");
            name=layoutName.item(0).getTextContent();
            Layout currentLayout = new Layout(id,name);
            ArrayList fieldsArray = new ArrayList<Field>() ;
            NodeList fields= element.getElementsByTagName("TkUserDialogField_1");
            String[] stringFields = new String[fields.getLength()];
            String fieldName="";
            boolean required=false;
            int order=-1;
            String dataType="";
            for (int i=0;i<fields.getLength();i++){
                Element fieldElem = (Element)fields.item(i);
                stringFields[i]=fieldElem.getElementsByTagName("FieldName").item(0).getTextContent();
                fieldName=fieldElem.getElementsByTagName("FieldName").item(0).getTextContent();
                Field field = new Field(i,fieldName,required,order,dataType);
                fieldsArray.add(i,field);
            }
            currentLayout.setFileds(fieldsArray);
            arrayOfLayouts.add(0,currentLayout);
            /*user.setLayouts(arrayOfLayouts);
            Log.e("user name----", user.userEmail);
            Log.e("field----", user.layouts.get(0).fileds.get(0).filedName);*/
            //return arrayOfLayoutInfo;

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayOfLayouts;

    }
    public String bestFive(String text){


        return text+"!!!!!!";
    }
    public  ArrayList<String> displayFormLayout( ArrayList<Layout> arrayOfLayouts ){
        final ArrayList<String> stringArray = new ArrayList<String>();
        for (int i=0;i<1;i++){
            ArrayList<Field> currentFileds =arrayOfLayouts.get(i).fileds;
            for (int j=0;j<currentFileds.size();j++) {
                String currentFiled=currentFileds.get(j).filedName;
                stringArray.add(currentFiled);
            }
        }
        return stringArray;

    }
}
