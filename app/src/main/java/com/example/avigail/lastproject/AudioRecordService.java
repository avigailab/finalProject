package com.example.avigail.lastproject;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

public class AudioRecordService extends Service {
    private static String TAG = "AudioRecordService";

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int SLINCE_RANGE = 350;
    double silenceLimit = 1300;
    long distance;
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private Long currentTime=null;

    public AudioRecordService() {
    }

    private static String LOG_TAG = "AudioRecordService";
    private IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
        bufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        // Initialize AudioRecorder.
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"Great!");
            }
            @Override
            public void onFailure(Exception error) {
                Log.i(TAG,"FFmpeg is not supported by device");
            }
        });

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }

    public void startRecordAudio(){
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

        int i = recorder.getState();
        if(i==1)
            recorder.startRecording();
        writeAudioDataToFile();

    }
    private void writeAudioDataToFile(){

        float tempFloatBuffer[] = new float[3];
        int tempIndex           = 0;

        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;
        int count=0;
        if(null != os){

            while(true) {
                float totalAbsValue = 0.0f;
                short sample        = 0;
                read = recorder.read(data, 0, bufferSize);
                // Analyze Sound.
                for( int i=0; i<bufferSize; i+=2 )
                {
                    sample = (short)( (data[i]) | data[i + 1] << 8 );
                    totalAbsValue += Math.abs( sample ) / (bufferSize/2);
                }

                // Analyze temp buffer.
                tempFloatBuffer[tempIndex % 3] = totalAbsValue;

                float amplitude = 0.0f;
                for (int i = 0; i < 3; ++i)
                    amplitude += tempFloatBuffer[i];

                //Silence detect
                if ((amplitude >= 0 && amplitude <= SLINCE_RANGE) && isRecording == false) {
                    Log.i("TAG", "1");
                    if(currentTime==null) {
                        Log.d("currentTime is null","set current time");
                        currentTime = System.currentTimeMillis();
                    }
                    else{
                        //Set seconds of silence
                        distance = System.currentTimeMillis()-currentTime;
                        Log.d("distance is",distance + "!!");

                        //if there no word - add more time for silence
                        if(count==0){
                            silenceLimit = 1400;
                        }
                        Log.d("silenceLimit",silenceLimit+"??");
                        //if the silence is too long -stop recording
                        if(distance> silenceLimit){
                            currentTime = null;
                            stopRecording();
                            Log.d("very long time","stop recording!!");
                            break;
                        }
                       /* if(distance>500){
                            tempIndex++;
                            continue;
                        }*/
                    }
                }
                //Speech detect and we start recording
                if (amplitude > SLINCE_RANGE && isRecording == false) {
                    silenceLimit = silenceLimit*0.8 + (System.currentTimeMillis() - currentTime)*0.2;
                    Log.i("TAG", "2");
                    currentTime = System.currentTimeMillis();
                    Log.d("TAG 2","set current time");
                    isRecording = true;
                }
                //Silence detect - after say word and maybe before say next word
                if ((amplitude >= 0 && amplitude <= SLINCE_RANGE) && isRecording == true) {

                    count++;

                   // currentTime = null;
                   Log.d("number of words",count+"");
                   isRecording=false;
                }
                tempIndex++;
                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/record" + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private String getTempFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }
    private void stopRecording(){
        if(null != recorder){
            isRecording = false;

            int i = recorder.getState();
            if(i==1)
                recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        copyWaveFile(getTempFilename(),getFilename());
        deleteTempFile();
        convertWAVToFLAC();

    }
    private void convertWAVToFLAC(){
        File wavFile = new File(getFilename());
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                Log.i(TAG,"So fast? Love it!");
            }
            @Override
            public void onFailure(Exception error) {
                Log.i(TAG,"Oops! Something went wrong");
            }
        };
        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(wavFile)

                // Your desired audio format
                .setFormat(cafe.adriel.androidaudioconverter.model.AudioFormat.FLAC)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();

    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }

    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 1;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
    public class MyBinder extends Binder {
        AudioRecordService getService() {
            return AudioRecordService.this;
        }
    }
}

