package com.damian.myplayerv3.AdaptersAndListeners;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.damian.myplayerv3.NotificationMaker;

/**
 * Created by damianmandrake on 2/19/17.
 */
public class BroadcastListener extends BroadcastReceiver {


    public static SongController songController;


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("*()*()INSIDE BROADCAST RECEIVER");
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String action=intent.getAction();
        if(songController!=null)
        if(action.equals(Intent.ACTION_HEADSET_PLUG)){
            System.out.println("*******&&&&&&&^^^^^ HEADSET PLUG RECEIEVD");
            switch (intent.getExtras().getInt("state",-1)){
                case 0:System.out.println("DISCONNECTED");
                    if(songController.isPaused())
                    songController.handleButtons();
                    break;
                case 1:
                    songController.handleButtons();//have to handle button states when the earpods are disconnected
                    songController.resumeSong();

                    System.out.println("CONENCTED");
                    break;
                default:
                    System.out.println("Dk");

            }
        }

        else if(action.equals(NotificationMaker.PLAY_NEXT))
            songController.onTouchNext();
        else if(action.equals(NotificationMaker.PLAY_PREV))
            songController.onTouchPrev();
        else
            songController.handleButtons();




    }




    public interface SongController{
        public void onTouchNext();
        public void onTouchPrev();
        public void handleButtons();
        public void resumeSong();
        public boolean isPaused();
    }
}
