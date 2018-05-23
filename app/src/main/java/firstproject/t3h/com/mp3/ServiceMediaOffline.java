package firstproject.t3h.com.mp3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;


import java.io.IOException;
import java.util.List;

/**
 * Created by LE VAN KHAI on 4/11/2018.
 */

public class ServiceMediaOffline extends Service {
    public static ManagerMediaplayer manager;
    public static AudioManager audioManager;
    public static int positionPlaying;
    private RemoteViews views;
    private RemoteViews bigViews;
    public static boolean clickPause;
    private boolean isPausing;

    public static boolean isClickPause() {
        return clickPause;
    }

    public static void setClickPause(boolean clickPause) {
        ServiceMediaOffline.clickPause = clickPause;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        manager = new ManagerMediaplayer();
        audioManager = new AudioManager(this);
        audioManager.getAllAudio();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constant.ACTION.PREV_ACTION)) {
            clickPrevious();
        } else if (intent.getAction().equals(Constant.ACTION.PLAY_ACTION)) {
            clickPause();
            setClickPause(true);
            inisImgResoursePause();


        } else if (intent.getAction().equals(Constant.ACTION.NEXT_ACTION)) {
            clickNext();

//        } else if (intent.getAction().equals(Constant.ACTION.PAUSE_ACTION)) {
//            play(getPositionPlaying());
//            views.setImageViewResource(R.id.status_bar_play,
//                    R.drawable.apollo_holo_dark_pause);
//            bigViews.setImageViewResource(R.id.status_bar_play,
//                    R.drawable.apollo_holo_dark_pause);


        } else if (intent.getAction().equals(
                Constant.ACTION.STOPFOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();
        }
//        return START_STICKY;

        return START_NOT_STICKY;
    }

    private void inisImgResoursePause() {
        views.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_play);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_play);
    }

    public void showNotification(int position) {


//        Log.d("", "showNotification: " + data.getTitle());
// Using RemoteViews to bind custom layouts into Notification
        views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

// showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constant.getDefaultAlbumArt(this));
        bigViews.setImageViewBitmap(R.id.status_bar_icon,
                Constant.getDefaultAlbumArt(this));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constant.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, ServiceMediaOffline.class);
        previousIntent.setAction(Constant.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, ServiceMediaOffline.class);
        playIntent.setAction(Constant.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, ServiceMediaOffline.class);
        nextIntent.setAction(Constant.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, ServiceMediaOffline.class);
        closeIntent.setAction(Constant.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

//        views.setImageViewResource(R.id.status_bar_play,
//                R.drawable.apollo_holo_dark_pause);
//        bigViews.setImageViewResource(R.id.status_bar_play,
//                R.drawable.apollo_holo_dark_pause);

        AudioOffline data = audioManager.getAudioOfflines().get(position);

        views.setTextViewText(R.id.status_bar_track_name, data.getDisplayName());
        bigViews.setTextViewText(R.id.status_bar_track_name, data.getDisplayName());

        views.setTextViewText(R.id.status_bar_artist_name, data.getArtis());
        bigViews.setTextViewText(R.id.status_bar_artist_name, data.getArtis());

        bigViews.setTextViewText(R.id.status_bar_album_name, data.getAlbum());

        Notification status;
        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.ic_library_music_white_24dp;
        status.contentIntent = pendingIntent;
        startForeground(Constant.NOTIFICATION_ID.FOREGROUND_SERVICE, status);

    }

    @Override
    public void onDestroy() {
        manager.release();
        super.onDestroy();
    }

    public static int getPositionPlaying() {
        return positionPlaying;
    }

    public static void setPositionPlaying(int positionPlaying) {
        ServiceMediaOffline.positionPlaying = positionPlaying;
    }

    public List<AudioOffline> getAudioOfflines() {
        return audioManager.getAudioOfflines();
    }

    public void clickNext() {
        int position = ServiceMediaOffline.getPositionPlaying();

        if (position == ServiceMediaOffline.audioManager.getAudioOfflines().size() - 1) {
            play(position);
            return;
        }

        position++;
        setPositionPlaying(position);
        try {
            manager.setDataSource(audioManager.getAudioOfflines().get(position).getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isPausing==true){
            return;
        }
        manager.play();
        setPositionPlaying((position));
    }

    public void clickPause() {
        manager.pause();
        inisImgResoursePause();
        isPausing=true;


    }

    public void clickPrevious() {
        int position = ServiceMediaOffline.getPositionPlaying();

        if (getPositionPlaying() == 0) {
            return;
        }
        position--;
        try {
            manager.setDataSource(audioManager.getAudioOfflines().get(position).getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isPausing==true){
            return;
        }
        manager.play();
        setPositionPlaying((position));
    }

    public void play(int position) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    showNotification(getPositionPlaying());
                }

            }
        });
        thread.start();
        try {
            manager.setDataSource(audioManager.getAudioOfflines().get(position).getPath());
            manager.play();
            setPositionPlaying((position));


        } catch (IOException e) {
            e.printStackTrace();
        }
//        createNotification(position);

        showNotification(position);
        inisImgResoursePlay();
    }

    public void inisImgResoursePlay() {
        views.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);
    }


    public static final class MyBinder extends Binder {
        private ServiceMediaOffline service;

        public ServiceMediaOffline getService() {
            return service;
        }

        public void setService(ServiceMediaOffline service) {
            this.service = service;
        }

        public ServiceMediaOffline getServiceMediaOffline() {
            return service;
        }

        public MyBinder(ServiceMediaOffline service) {
            this.service = service;
        }
    }
}
