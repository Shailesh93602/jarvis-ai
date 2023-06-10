package com.example.speech2text;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.Nullable;

public class MusicPlayerService extends Service {
    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        player.setLooping(true);
        player.start();

        return START_STICKY;

    }

//    public void onDestroy(){
//        super.onDestroy();
//        player.stop();
//    }
}
