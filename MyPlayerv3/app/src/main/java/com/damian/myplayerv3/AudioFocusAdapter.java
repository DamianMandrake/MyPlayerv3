package com.damian.myplayerv3;

import android.media.AudioManager;

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
        switch (i){
            case AudioManager.AUDIOFOCUS_GAIN:


            case AudioManager.AUDIOFOCUS_LOSS:
                this.musicService.handleButtons();
                return;

            case AudioManager.MODE_IN_CALL:
                musicService.pause();
                musicService.getRef().handleButtons(false,false);
                return;
            default:
                return;


        }

    }
}
