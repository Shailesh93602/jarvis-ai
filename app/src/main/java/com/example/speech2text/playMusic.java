package com.example.speech2text;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class playMusic {
    private static final String TAG = "playMusic";
    private MediaPlayer mediaPlayer;

    public void playMusic(String url){
        try{
            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource("/storage/emulated/0/downloads/download/music/Main_Rang_Sharbaton_ka.mp3");
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "Medida player prepared");
                    mediaPlayer.start();
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int i, int i1) {
                    Log.e(TAG, "MediaPlayer error: " + i + ", " + i1);
                    return false;
                }
            });
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void stopMusic(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
