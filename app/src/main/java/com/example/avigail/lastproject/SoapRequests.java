import android.util.Log;

import com.example.avigail.lastproject.User;

import java.net.Proxy;

public class SoapRequests {

    private static final boolean DEBUG_SOAP_REQUEST_RESPONSE = true;
    private static final String MAIN_REQUEST_URL = "http://abc.xyz.com/WSClient/WSServiceSoapHttpPort";
    private static final String NAMESPACE = "http://wsclient.xyz.com//";
    private static final String SOAP_ACTION = "http://wsclient.xyz.com//loginservice";
    private static String SESSION_ID;

    private final void testHttpResponse(HttpTransportSE ht) {
        ht.debug = DEBUG_SOAP_REQUEST_RESPONSE;
        if (DEBUG_SOAP_REQUEST_RESPONSE) {
            Log.v("SOAP RETURN", "Request XML:\n" + ht.requestDump);
            Log.v("SOAP RETURN", "\n\n\nResponse XML:\n" + ht.responseDump);
        }
    }

    public User getUserData(String name, String pwd){
        User user = null;
        String methodname = "loginservice";

        SoapObject request = new SoapObject(NAMESPACE, methodname);

        PropertyInfo userName =new PropertyInfo();
        userName.setName("username");
        userName.setValue(name);
        userName.setType(String.class);
        request.addProperty(userName);

        PropertyInfo password =new PropertyInfo();
        password.setName("password");
        password.setValue(pwd);
        password.setType(String.class);
        request.addProperty(password);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        HttpTransportSE ht = getHttpTransportSE();

        try {
            ht.call(SOAP_ACTION, envelope);
            testHttpResponse(ht);
            SoapPrimitive resultsString = (SoapPrimitive)envelope.getResponse();
            String data = resultsString.toString();
            Log.v("***********RESPONSE*******************", data);

        } catch (SocketTimeoutException t) {
            t.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (Exception q) {
            q.printStackTrace();
        }

        // some code to set user data
        ....
        return user;

    }

    private SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        return envelope;
    }

    private final HttpTransportSE getHttpTransportSE() {
        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,MAIN_REQUEST_URL,60000);
        ht.debug = true;
        ht.setXmlVersionTag("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>");
        return ht;
    }
}