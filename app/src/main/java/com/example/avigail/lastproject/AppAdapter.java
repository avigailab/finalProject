package com.example.avigail.lastproject;

import android.os.StrictMode;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by avigail on 14/11/16.
 */
public class AppAdapter {
    private static final String TAG = "app adapter";
    private static final String SOAP_ACTION = "http://www.tukuoro.com/SubmitUserInputForLayout";
    private static final String METHOD_NAME = "SubmitUserInputForLayout";
    private static final String NAMESPACE = "http://www.tukuoro.com/";
    private static final String URL = "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx";

    public ArrayList<Layout> getObjectsLayoutsForUser(String arrayOfLayoutInfo) {
        Log.d("AppAdapter",arrayOfLayoutInfo);
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
            NodeList layouts = doc.getElementsByTagName("LayoutInfo");
            for (int i=0;i<layouts.getLength();i++) {
            //for (int i=0;i<1;i++) {

                Element element = (Element) layouts.item(i);
                String name = "";
                String id="a";
                NodeList layoutName = element.getElementsByTagName("LayoutName");
                name = layoutName.item(0).getTextContent();
                NodeList layoutId = element.getElementsByTagName("LayoutId");
                id = layoutId.item(0).getTextContent();
                Layout currentLayout = new Layout(id, name);
                ArrayList fieldsArray = new ArrayList<Field>();
                NodeList fields = element.getElementsByTagName("TkUserDialogField_1");
                String[] stringFields = new String[fields.getLength()];
                String fieldName = "";
                boolean required = false;
                int order = -1;
                String dataType = "";
                for (int j = 0; j < fields.getLength(); j++) {
                //for (int j = 0; j < 1; j++) {
                    Element fieldElem = (Element) fields.item(j);
                    stringFields[j] = fieldElem.getElementsByTagName("FieldName").item(0).getTextContent();
                    fieldName = fieldElem.getElementsByTagName("FieldName").item(0).getTextContent();
                    dataType = fieldElem.getElementsByTagName("DataType").item(0).getTextContent();
                    required = Boolean.parseBoolean(fieldElem.getElementsByTagName("Required").item(0).getTextContent());
                    Field field = new Field(j, fieldName, required, order, dataType);
                    fieldsArray.add(j, field);
                }
                currentLayout.setFields(fieldsArray);
                arrayOfLayouts.add(i, currentLayout);
            }
            /*user.setLayouts(arrayOfLayouts);
            Log.e("user name----", user.userEmail);
            Log.e("field----", user.layouts.get(0).fields.get(0).filedName);*/
            //return arrayOfLayoutInfo;

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayOfLayouts;

    }

    public  ArrayList<String> displayFormLayout( ArrayList<Layout> arrayOfLayouts ){
        final ArrayList<String> stringArray = new ArrayList<String>();
        for (int i=0;i<1;i++){
            ArrayList<Field> currentFileds =arrayOfLayouts.get(i).fields;
            for (int j=0;j<currentFileds.size();j++) {
                String currentFiled=currentFileds.get(j).filedName;
                stringArray.add(currentFiled);
            }
        }
        return stringArray;

    }
    public boolean submitLayoutForUser(Layout layout){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        SoapObject object1 = new SoapObject(NAMESPACE, "Values");
        object1.addProperty("string",layout.fields.get(0).filedAnswer);
        //init property of request
        request.addProperty("sfLogin","orayrs@gmail.com");
        request.addProperty("LayoutId",layout.id);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("Values");
        Map<String, String> map = new HashMap<String, String>();
        map.put("string",layout.fields.get(0).filedAnswer);
        pi.setValue(map);
        request.addProperty("input",new SoapObject(NAMESPACE, "UserInputItem").addProperty("Value",layout.fields.get(0).filedName).addProperty("Values","<string>"+layout.fields.get(0).filedAnswer+"</string>"));
        request.addProperty("language","en_US");
        request.addProperty("clientId","68174861");
        request.addProperty("serviceId","58469251");
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(URL);

        httpTransport.debug = true;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } //send request
        Object result;
        try {
            result = envelope.getResponse();
            Log.d("App", "" + envelope.getResponse());
            return (boolean) envelope.getResponse();
            // response = result.getProperty(0).toString();


        } catch (SoapFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }
}
