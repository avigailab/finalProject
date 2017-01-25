package com.example.avigail.lastproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ReadOut extends Activity implements TextToSpeech.OnInitListener, OnClickListener {

    boolean paused = false;
    String leftToRead = null;
    String res = null;
    final String TAG="ReadOut";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_out);
        Intent intent = getIntent();
        res="hello";
        /*res = intent.getExtras().getString("response");
        TextView textv = (TextView) findViewById(R.id.textView1);
        textv.setText(res);
        textv.setMovementMethod(new ScrollingMovementMethod());
        android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        textv.setHeight((int)(display.getHeight()*0.76));*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    public String speakFull(String text){
        Log.e("Speaking: " , text);
        TextToSpeech tts = new TextToSpeech(this, this);
        Log.e(TAG,"Speaking");
            if(!tts.isSpeaking())
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        return null;
    }

    @Override
    public void onInit(int arg0) {
        leftToRead = speakFull(res);
    }

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
        // TODO Auto-generated method stub

    }

}
