package com.example.avigail.lastproject;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;

/**
 * Created by avigail on 05/03/17.
 */

public class SendRecordSoap extends AsyncTask<Object, Object, Object> {
    private static final String SOAP_ACTION = "http://www.tukuoro.com/ParseImmidiateSingleFromAudio";
    private static final String METHOD_NAME = "ParseImmidiateSingleFromAudio";
    private static final String NAMESPACE = "http://www.tukuoro.com/";
    private static final String URL = "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx";
    private String response;
    private String fieldName="";
    private String fieldType="";
    private String language="";
    public SendRecordSoap(String fieldName, String fieldType, String language){
        this.fieldName = fieldName;
        this.fieldType = fieldType.length() > 0 ? fieldType :"Any";
        this.language = language;
    }
    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    private String encodeAudio() {
        try {

            File file = new File("/storage/emulated/0/AudioRecorder/record.flac");
            byte[] bytes = FileUtils.readFileToByteArray(file);
            String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP).trim();
            Log.i("~~~~~~~~ Encoded: ", encoded);
            return encoded;
        } catch (Exception e) {
            Log.e("audio encode execption","");
            //DiagnosticHelper.writeException(e);
        }
        return "";
    }

    @Override
    protected Object doInBackground(Object... arg0) {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        //------here we read the long string that we get from you-----
            /*InputStream inputStream = getResources().openRawResource(R.raw.audio);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line != null) {
                request.addProperty("audio",line);
            }*/
        //----------------------------------------------------------
        Log.d("field name",this.fieldName);
        Log.d("field type",this.fieldType);
        Log.d("language",this.language);
        //-----here we try to send encode audio that we created----
        request.addProperty("audio",encodeAudio());
        //---------------------------------------------------------
        /*request.addProperty("fieldName",this.fieldName);
        request.addProperty("fieldType",this.fieldType);
        request.addProperty("language",this.language);*/
        request.addProperty("fieldName",this.fieldName.toLowerCase());
        request.addProperty("fieldType","Any");
        request.addProperty("language",this.language);
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
        SoapObject result;
        try {
            result = (SoapObject)envelope.getResponse();
            Log.d("App", "" + envelope.getResponse());
            return result;
            // response = result.getProperty(0).toString();


        } catch (SoapFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("faild to send","!!");
        }
        Log.d("faild to send","!!");
        return null;
    }
}
