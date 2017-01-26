package com.example.avigail.lastproject;
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.Locale;

public class TextToSpeechActivity extends Activity implements
        TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private Button button;
    private EditText inputText;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);
        button = (Button) findViewById(R.id.button1);
        inputText = (EditText) findViewById(R.id.inputText);
        textToSpeech = new TextToSpeech(this, this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    convertTextToSpeech();
            }

        });
        convertTextToSpeech();
    }

    /**
     * a callback to be invoked indicating the completion of the TextToSpeech
     * engine initialization.
     *
     * @see android.speech.tts.TextToSpeech.OnInitListener#onInit(int)
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            } else {
                convertTextToSpeech();
            }
        } else {
            Log.e("error", "Initilization Failed!");
        }
    }

    /**
     * Releases the resources used by the TextToSpeech engine. It is good
     * practice for instance to call this method in the onDestroy() method of an
     * Activity so the TextToSpeech engine can be cleanly stopped.
     *
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public void onDestroy() {
        textToSpeech.shutdown();
    }

    /**
     * Speaks the string using the specified queuing strategy and speech
     * parameters.
     */
    private void convertTextToSpeech() {
        String text = inputText.getText().toString();
        if (null == text || "".equals(text)) {
            text = "Please give some input.";
        }
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}