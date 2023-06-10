//package com.example.speech2text;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.media.MediaRecorder;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.PowerManager;
//
//import androidx.annotation.Nullable;
//
//import java.io.File;
//import java.io.IOException;
//
//public class AudioRecorderService extends Service {
//
//    private MediaRecorder mediaRecorder;
//    private File audioFile;
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
//        wakeLock.acquire();
//
//        startRecording();
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        stopRecording();
//        super.onDestroy();
//    }
//
//    private void startRecording() {
//        try {
//            mediaRecorder = new MediaRecorder();
//            audioFile = new File(getExternalFilesDir(null), "recording.mp3");
//
//            // Set audio source and format
//            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                mediaRecorder.setOutputFile(audioFile);
//            }
//
//            // Prepare and start recording
//            mediaRecorder.prepare();
//            mediaRecorder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stopRecording() {
//        if (mediaRecorder != null) {
//            mediaRecorder.stop();
//            mediaRecorder.release();
//            mediaRecorder = null;
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}
