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

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION_MAIN_ACTIVITY);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationIntent,0);

            Intent previousIntent = new Intent(this,AudioService.class);
            previousIntent.setAction(Constants.PREV_ACTION);
            PendingIntent prevIntent = PendingIntent.getService(this, 0 ,previousIntent, 0);

            Intent playIntent = new Intent(this,AudioService.class);
            playIntent.setAction(Constants.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getService(this,0 ,playIntent, 0);

            Intent nextIntent = new Intent(this, AudioService.class);
            nextIntent.setAction(Constants.NEXT_ACTION);
            PendingIntent nextPIntent = PendingIntent.getService(this, 0, nextIntent, 0);

            RemoteViews notificationLayout = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);

            final NotificationCompat.Builder notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("PlayBook")
                    .setTicker("PlayBook player")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayout)
                  // It means it can not be dismissed unless i do so
                    .setOngoing(true)
                    .setSound(null,0);
                 //   .getBigContentView().setTextViewText(R.id.song_title_notif,songName);
                 //   .addAction(android.R.drawable.ic_media_previous,"Previous",prevIntent)
                 //   .addAction(android.R.drawable.ic_media_play,"Play",pplayIntent)
                 //   .addAction(android.R.drawable.ic_media_next, "Next", nextPIntent);

            notification.getBigContentView().setTextViewText(R.id.song_title_notif,songName);
            final Notification notif = notification.build();

            setListeners(notificationLayout,getApplicationContext());


            // Id is an identifier for notification
            startForeground(1,notif);



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


    private static void setListeners(RemoteViews view, Context context){

        Intent pause = new Intent();
        Intent minus = new Intent();
        Intent plus = new Intent();

        pause.setAction(NOTIFY_PAUSE);
        minus.setAction(NOTIFY_MINUS);
        plus.setAction(NOTIFY_PLUS);

        // Setting up the pendingIntent for buttons in notification
        // layout to do the appropriate action (rewind 30s,pause,fastforward 30s)
        PendingIntent pausePending = PendingIntent.getBroadcast(context,0,pause,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notif_pause,pausePending);

        PendingIntent minusPending = PendingIntent.getBroadcast(context,0,minus,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notif_minus30,minusPending);

        PendingIntent plusPending = PendingIntent.getBroadcast(context,0,plus,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notif_plus30,plusPending);

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
