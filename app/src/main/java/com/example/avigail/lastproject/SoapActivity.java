package com.example.avigail.lastproject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.app.Activity;

public class SoapActivity extends AppCompatActivity {

    private static final String SOAP_ACTION = "http://www.tukuoro.com/ParseImmidiateSingleFromAudio";
    private static final String METHOD_NAME = "ParseImmidiateSingleFromAudio";
    private static final String NAMESPACE = "http://www.tukuoro.com/";
    private static final String URL = "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx";
    private TextView tv;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soap);

        tv= (TextView)findViewById(R.id.txt2);

        myAsyncTask myRequest = new myAsyncTask();
        myRequest.execute();

    }

    private class myAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            tv.setText(response);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        private String encodeAudio() {
            byte[] audioBytes;
            try {

                String fn = "/storage/emulated/0/AudioRecorder/record.flac";
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                FileInputStream fis = new FileInputStream(new File(fn));
                byte[] buf = new byte[1024];
                int n;
                while (-1 != (n = fis.read(buf)))
                    baos.write(buf, 0, n);
                baos.close();
                audioBytes = baos.toByteArray();

                // Here goes the Base64 string
                String _audioBase64 = Base64.encodeToString(audioBytes, Base64.DEFAULT);
                Log.d("ENCODE FILE",_audioBase64);
                return _audioBase64;

            } catch (Exception e) {
                Log.e("audio encode execption","");
                //DiagnosticHelper.writeException(e);
            }
            return "";
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            //------here we read the long string that we get from you-----
            InputStream inputStream = getResources().openRawResource(R.raw.audio);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line != null) {
                request.addProperty("audio",line);
            }
            //----------------------------------------------------------

            //-----here we try to send encode audio that we created----
            //request.addProperty("audio",encodeAudio());
            //---------------------------------------------------------
            request.addProperty("fieldName","date");
            request.addProperty("fieldType","Any");
            request.addProperty("language","en");
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
                Object obj = envelope.bodyIn;
                Log.d("App", "" + envelope.getResponse());
                Log.d("obj--",obj.toString());
               // response = result.getProperty(0).toString();


            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}
