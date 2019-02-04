package com.audiobook.nbogdand.playbook.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.audiobook.nbogdand.playbook.Constants;
import com.audiobook.nbogdand.playbook.MainActivity;
import com.audiobook.nbogdand.playbook.NotificationGenerator;
import com.audiobook.nbogdand.playbook.PlaySongActivity;
import com.audiobook.nbogdand.playbook.PlaySongViewModel;
import com.audiobook.nbogdand.playbook.R;

import androidx.annotation.*;

import static com.audiobook.nbogdand.playbook.PlaySongActivity.CHANNEL_ID;

public class AudioService extends Service {

    PlaySongViewModel playSongViewModel;

    public static final String NOTIFY_PAUSE = "com.audiobook.nbogdand.playbook.pause";
    public static final String NOTIFY_MINUS = "com.audiobook.nbogdand.playbook.minus";
    public static final String NOTIFY_PLUS = "com.audiobook.nbogdand.playbook.plus";

    public static final String TAG = "FOREGROUND SERVICE:";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction().equals(Constants.START_FOREGROUND_SERVICE)){
                Log.i(TAG,"foreground service started");


            String songName = intent.getStringExtra("songName");

            //Song is already playing, so next action would be pause --> PAUSE_STATE
            Notification notif = NotificationGenerator.createNotification(getApplicationContext(),songName,Constants.PAUSE_STATE);

            // Id is an identifier for notification
            startForeground(Constants.NOTIFICATION_ID,notif);



        }else if(intent.getAction().equals(Constants.PREV_ACTION)){
            Log.i(TAG,"previous");
        }else if(intent.getAction().equals(Constants.PLAY_ACTION)){
            Log.i(TAG,"play");
        }else if(intent.getAction().equals(Constants.NEXT_ACTION)){
            Log.i(TAG,"next");
        }else if(intent.getAction().equals(Constants.STOP_FOREGROUND_SERVICE)){
            Log.i(TAG,"Stop Fservice: ");
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
