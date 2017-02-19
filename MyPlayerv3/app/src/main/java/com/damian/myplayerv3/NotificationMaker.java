package com.damian.myplayerv3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.NetworkOnMainThreadException;
import android.widget.RemoteViews;

/**
 * Created by damianmandrake on 2/19/17.
 */
public class NotificationMaker implements NotificationMakerConstants{

    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private RemoteViews remoteView;
    private int notificationId;
    private Context context;
    private String previousSongPath;
    public NotificationMaker(Context context){
        this.context=context;
        this.previousSongPath="";
        this.initRemoteView();
        this.initNotiBuilder();

    }

    private void initRemoteView(){
        this.notificationId=(int)System.currentTimeMillis();
        this.notificationManager= (NotificationManager)this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.remoteView= new RemoteViews(this.context.getPackageName(),R.layout.notif_layout);


        Intent prevIntent=new Intent(PLAY_PREV),playPauseIntent=new Intent(PLAY_PAUSE),nextIntent=new Intent(PLAY_NEXT);
        PendingIntent prevPendingIntent= PendingIntent.getBroadcast(this.context,123,prevIntent,0);
        PendingIntent playPendingIntent= PendingIntent.getBroadcast(this.context,234,playPauseIntent,0);
        PendingIntent nextPendingIntent= PendingIntent.getBroadcast(this.context,354,nextIntent,0);

        //binding pendingIntents with their respective views
        this.remoteView.setOnClickPendingIntent(R.id.notiPrev,prevPendingIntent);
        this.remoteView.setOnClickPendingIntent(R.id.notiNext,nextPendingIntent);
        this.remoteView.setOnClickPendingIntent(R.id.notiPlayPause, playPendingIntent);
        System.out.println("LEAVING initRemoteVIew");
    }

    private void initNotiBuilder(){
        this.builder=new Notification.Builder(this.context);
        //making default intent to redirect to main activoty
        PendingIntent pendingIntent= PendingIntent.getActivity(this.context,123,new Intent(this.context,MainActivity.class),0);




        this.builder.setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher).setOngoing(true);
        this.builder.setContentTitle("AudFm");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            this.builder.setContent(this.remoteView);
        else
            this.builder.setCustomBigContentView(this.remoteView);

        System.out.println("LEAVING initNotiBuilder");



    }


    public void makeNotification(){
        this.notificationManager.notify(this.notificationId, builder.build());
    }
    public void dismissNotif(){
        this.notificationManager.cancel(this.notificationId);
    }

    public void setAll(String bitmap,String title,String artist){
        this.setAlbumArt(bitmap);
        this.setSongTitle(title);
        this.setArtist(artist);
        this.makeNotification();

    }
    private void setAlbumArt(final String bitmap){
        if(!previousSongPath.equals(bitmap)) {
         /* new Thread(new Runnable() {
                @Override
                public void run() {*/
                    NotificationMaker.this.remoteView.setImageViewBitmap(R.id.notiAlbumArt, BitmapFactory.decodeFile(bitmap));
               /* }
        });*/
            this.previousSongPath=bitmap;
        }



    }
    private void setSongTitle(String s){
        this.remoteView.setTextViewText(R.id.notifSongTitle, s);
    }
    private void setArtist(String s){
        this.remoteView.setTextViewText(R.id.notifArtist,s);
    }



    public void setPlayPauseImg(int resourceId){
       // if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        //this.remoteView.setImageViewIcon(R.id.notiPlayPause, Icon.createWithResource(this.context,resourceId));
        //else
        this.remoteView.setImageViewResource(R.id.notiPlayPause,resourceId);
        this.makeNotification();

    }




}
interface NotificationMakerConstants{
    public static final String PLAY_PREV="notifPlayPrevious",PLAY_PAUSE="notifPlayPause",PLAY_NEXT="notifPlayNext";
}
