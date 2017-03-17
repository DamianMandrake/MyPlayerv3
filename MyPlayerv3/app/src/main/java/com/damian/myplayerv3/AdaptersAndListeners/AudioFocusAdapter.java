package com.damian.myplayerv3.AdaptersAndListeners;

import android.media.AudioManager;

import com.damian.myplayerv3.Fragments.MusicControllerFragment;
import com.damian.myplayerv3.MusicService;

/**
 * Created by damianmandrake on 2/20/17.
 */
public class AudioFocusAdapter implements AudioManager.OnAudioFocusChangeListener {

    private MusicService musicService;
    public AudioFocusAdapter(MusicService musicService){
        this.musicService=musicService;
    }


    @Override
    public void onAudioFocusChange(int i) {
        System.out.println("Value of i is " + i);
        boolean b=true;
        //for every value of i less than or qual to 0 audio focus is lost
        //for every value >0 audio focus is gained


            this.musicService.pause();
            if (!MusicControllerFragment.didUserTrigger && i>0) {
                this.musicService.startPlaying();
                b=false;
            }
        this.musicService.callRefHandle(b,false);





    }
}
