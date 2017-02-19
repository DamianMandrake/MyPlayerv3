package com.damian.myplayerv3;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by damianmandrake on 2/19/17.
 */
public class BroadcastListener extends BroadcastReceiver {


    static SongController songController;


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("*()*()INSIDE BROADCAST RECEIVER");
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String action=intent.getAction();

        if(action.equals(NotificationMaker.PLAY_NEXT))
            songController.next();
        else if(action.equals(NotificationMaker.PLAY_PREV))
            songController.prev();
        else
            songController.handleButtons();




    }




    interface SongController{
        public void next();
        public void prev();
        public void handleButtons();
    }
}
