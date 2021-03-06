package com.damian.myplayerv3.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import android.os.Handler;


import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.MusicService;
import com.damian.myplayerv3.R;
import com.damian.myplayerv3.Song;
import com.damian.myplayerv3.AdaptersAndListeners.SongRecycler;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

/**
 * Created by damianmandrake on 1/12/17.
 */
public class MusicControllerFragment extends Fragment implements CompoundButton.OnCheckedChangeListener,View.OnClickListener,MusicControllerFragmentConstants,SongRecycler.PlaySong {



    /*  issue #1-> when you quit the app while playing muisc by pressing the back button the state of the fragment buttons also needs to be saved
                    NOTE-> Service instance present throughout the course of execution of the program until the user explicitly kills app is ONE... this means if
                            user selects repeat once and pressess back button  the state has been registered in the MusicService class ... not in your fragment

                            Sol: 1.store val of songPosition on preferences and reload it ONLY WHEN musicService ISNT PLAYING since MainActivity handles this when its playing...
                            ... prolly call saveSong whenever pause is pressed? or whenever activity itself is destroyed so in activity onPause()

        issue #2-> once the app is quit this fragment must point to the song and on click of play must play the song


    */

    public static boolean didUserTrigger=false;

    private TextView smallSongTitle,artist,progressTime,endTime;
    private ImageView smallAlbumArt,imageAlbumArt;
    private ImageButton prev,next;
    private ToggleButton playPause;public ToggleButton smallPlayPause;
    private SeekBar seekBar;
    private Handler handler,reloadStateHandler;//reload state handler checks coninuously for initialkziation of music service
    //after which the song will be played .... or not
    private Button b,shuffle;
    private boolean isInTouch=false;public boolean hasSavedStateBeenCalled=false;
    private Song song;
    private int res;
    private NumberFormat numberFormat;

    private View view;

     int progress;//this is for the seekbar
    public int retrievedProgress;//this is for loadLastSong();


    private int songPos;//songPOs is for reloadState only

    private Runnable musicServiceStateChecker=new Runnable() {
        @Override
        public void run() {
            if(musicService!=null){


                reloadStateHandler.removeCallbacks(this);
                return;

            }
            reloadStateHandler.postDelayed(this,100);
        }
    };







    private MusicService musicService;

    public void setCurrentSong(Song s){
        this.song=s;

        smallSongTitle.setText(s.getTitle());
        //songName.setText(s.getTitle());
        System.out.println("artist is  " + artist == null);
        artist.setText(s.getArtist());

        int p=seekBar.getMax()/1000;

        endTime.setText((p/60)+":"+(p%60));


        //seekBar.setMax(musicService.getDuration());leads to an illegal state since media player hasnt been intited yet

        if(s.getLargeImgPath()!=null) {
            imageAlbumArt.setImageBitmap(BitmapFactory.decodeFile(s.getLargeImgPath()));
            smallAlbumArt.setImageBitmap(BitmapFactory.decodeFile(s.getLargeImgPath()));
        }else{
            imageAlbumArt.setImageResource(R.drawable.notfound);
            smallAlbumArt.setImageResource(R.drawable.notfound);
        }



    }
    public void setMusicService(MusicService a){
        musicService=a;

        //loadLastSong();
        //HAVE TO REWRITE.....
        /*try{
            //    loadLastSong();


        }catch (NullPointerException npe){
            System.out.println("IN setMusicService of NPE OF  FRAGMENT");
            npe.printStackTrace();
            hasSavedStateBeenCalled=false;
        }*/


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{


        }catch (NullPointerException nppe){
            nppe.printStackTrace();
        }

    }
    public void setSeekBarMax(int max){
        seekBar.setMax(max);

        if(musicService.isPlaying())
        updateSeekbar();
        //also setting the handler for the seekbar updations




    }
    public void setMaxDuration(int p){
        p/=1000;
        endTime.setText(p / 60 + ":" + p % 60);
    }
    Runnable seekbarUpdater=new Runnable() {
        @Override
        public void run() {

            int p = musicService.getPosition();
            seekBar.setProgress(p);
            //System.out.println("VALUE OF P is " + p);
            int temp=p/1000;
            //System.out.println("TEMP IS "+temp);
            //float time = temp/60 + (temp%60f) /100;
            progressTime.setText((temp/60)+":"+MusicControllerFragment.this.numberFormat.format(temp%60));
            //progressTime.setText(((int) ( temp/60 )) +((temp % 60f)/100) );
            handler.postDelayed(this, 1000);
//            song.setCurrDuration(p);
        }
    };
    void updateSeekbar(){
        handler.post(seekbarUpdater);

    }

    public TextView getEndTime(){return endTime;}




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view= view==null?inflater.inflate(R.layout.music_cotroller_frag, container, false):view;

        SongRecycler.setPlaySongReference(this);
        System.out.println("is in oncReate");
        handler=new Handler();reloadStateHandler=new Handler();


        smallSongTitle=(TextView)view.findViewById(R.id.smallSongTitle);
        //songName=(TextView)view.findViewById(R.id.songName);
        artist=(TextView)view.findViewById(R.id.artist);
        b=(Button)view.findViewById(R.id.repeater);

        this.numberFormat= new DecimalFormat("00");
        playPause=(ToggleButton)view.findViewById(R.id.playPause);
        smallPlayPause=(ToggleButton)view.findViewById(R.id.smallPlayPlause);
        smallAlbumArt=(ImageView)view.findViewById(R.id.smallAlbumArt);
        imageAlbumArt=(ImageView)view.findViewById(R.id.imageAlbumArt);
        imageAlbumArt.setScaleType(ImageView.ScaleType.FIT_XY);

        progressTime=(TextView)view.findViewById(R.id.currentTime);
        endTime=(TextView)view.findViewById(R.id.endTime);


        shuffle=(Button)view.findViewById(R.id.shuffle);








        playPause.setOnCheckedChangeListener(this);
        smallPlayPause.setOnCheckedChangeListener(this);
        next=(ImageButton)view.findViewById(R.id.nextSong);

        prev=(ImageButton)view.findViewById(R.id.prev);

        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        b.setOnClickListener(this);
        shuffle.setOnClickListener(this);



        seekBar=(SeekBar)view.findViewById(R.id.musicSeekbar);// add its listener

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                MusicControllerFragment.this.progress = i;


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(seekbarUpdater);
                isInTouch = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println("final progress is " + progress);
                if (isInTouch) {
                    musicService.seekTo(MusicControllerFragment.this.progress);
                    isInTouch = false;
                }
                updateSeekbar();
            }
        });








        return view;
    }
    void toast(String a){
        MainActivity.toast(a);


    }

    public void handleButtons(boolean b,boolean shouldItDoAnything){
        System.out.println("************************** value of handle buttons is " + b);
        res= b?R.mipmap.play:R.mipmap.pause;
        playPause.setBackgroundResource(res);
        if(musicService!=null)
        musicService.handleNotifButton(res);

        smallPlayPause.setBackgroundResource(res);
        if(!shouldItDoAnything)
            return;
        if(b) {

            System.out.println("inside true of handle buttons");

            if(musicService.isPlaying()) {//dont need to check whether or not player is prep'd since player is unprep'd when its not playing
                System.out.println("about to pause the song");
                musicService.pause();
                MusicControllerFragment.didUserTrigger=true;
            }
            saveLastSong();
        }else{
            System.out.println("has savedStateBeenCalled is "+hasSavedStateBeenCalled);
                if(!MusicService.isMediaPlayerPrepared)//dont let the next statements to be processesed since player isnt prepared yet
                    return;
                if(shouldItDoAnything)
                if(!musicService.isPlaying())//what if no songs set... or musicService is null... or songs running while my button shows play
                {
                    musicService.startPlaying();
                    didUserTrigger=false;
                }
        }


    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        System.out.println("onCheckedChanged called");
        handleButtons(b, true);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prev:
                musicService.onTouchPrev();
                if(playPause.isChecked())
                    handleButtons(false,false);

                return;

            case R.id.nextSong:
                if(playPause.isChecked())//when music is paused and the next song is tapped the button state needs to be maintained
                    handleButtons(false,false);
                musicService.onTouchNext();
                return;

            case R.id.repeater:
                MusicService.repeatState=(MusicService.repeatState+1)%3;
                setRepeatButton();
                return;

            case R.id.shuffle:
                MusicService.isShuffleOn=!MusicService.isShuffleOn;
                String x="Shuffle is Off";
                if(MusicService.isShuffleOn)
                    x="Shuffle is on";
                Toast.makeText(getContext(),x,Toast.LENGTH_LONG).show();

                //return;//not need since its the last one




        }
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("in onpause");
        try {
            saveLastSong();

        }catch(NullPointerException npe){
            System.out.println("ONPAUSE OF MUSIC CONTROLLER FRAGMENT CAAUSE NPE ");
        }
    }

    private void saveLastSong(){

            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            //editor.clear();
            save(editor);
            //for gc
            sharedPreferences=null;
            editor=null;
            //System.out.println("SONG BEING SAVED IS " + song.toString());





    }
    public void save(SharedPreferences.Editor editor){

        editor.putInt(CURR_SONG_POS_REF, musicService.getSongPosition());//since ive put songPos i can call setSongPos and call play
        editor.putInt(REPEAT_BUTTON_STATUS, MusicService.repeatState);
        editor.putBoolean(HAS_SAVE_BEEN_CALLED, true);
        System.out.println("progress being saved is "+progress);
        editor.putInt(SEEKBAR_POS, progress);



        //TBD save songList state


        editor.putInt("seekbarMax", seekBar.getMax());
        editor.commit();




    }




    public void loadLastSong(SharedPreferences s){
        if(!musicService.isPlaying()) {
            try {
                SharedPreferences sharedPreferences = s;
                Map t = sharedPreferences.getAll();
                hasSavedStateBeenCalled = true;
                songPos = sharedPreferences.getInt(CURR_SONG_POS_REF,0);
                MusicControllerFragment.this.retrievedProgress = sharedPreferences.getInt(SEEKBAR_POS,0);

                songPos= songPos>MainActivity.songList.size()?0:songPos;
                this.play(songPos);
                //prolly will have to seek
                //musicService.seekTo(progress);//leads to illegal state hence need to do it in onPRepared state of mediaplayer
                System.out.println("PROGRESS IN loadstate is " + MusicControllerFragment.this.retrievedProgress);


                MusicService.repeatState = (Integer) t.get(REPEAT_BUTTON_STATUS);
                int p = (Integer) t.get("seekbarMax");
                setRepeatButton();
                System.out.println("progress retrieved is " + this.progress);
                seekBar.setProgress(MusicControllerFragment.this.progress);

                seekBar.setMax(p);
                this.handleButtons(false, false);
                //handleButtons(true);//not pausing the song ... since till the time the true part is executed the musicService hasnt really started playing the song...

            }catch (NullPointerException npe){
                toast("Couldnt load song");
            }

        }
    }

    //to be called while restoring state... also called whenever its clicked
    private void setRepeatButton(){
        switch (MusicService.repeatState){

            case 1:
                b.setBackgroundResource(R.mipmap.repeat_infinite);
                toast(REPEAT_ONCE);
                break;

            case 2:
                b.setBackgroundResource(R.mipmap.play_once);
                toast(REPEAT_INFINITE);
                break;

            default://0
                toast(PLAY_NORM);
                b.setBackgroundResource(R.mipmap.repeat_once);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("IN FRAG ON DESTROY");
        //sa);
    }


    //Overriding the callback i wrote in the SongRecycler class... everytime onClick is generated in SongRecycler this callback will be called
    @Override
    public void play(int p){
        System.out.println("music service is null " + musicService == null);
        if(musicService!=null) {

        musicService.setSongPosition(p);
            musicService.playSong();
        handleButtons(false, false);
    }

    }

    @Override
    public void play(Song s){
        System.out.println("going to invoke playSong from play(Song)");
        System.out.println("PLAYING****SONG NAME "+s.getTitle());
        System.out.println("Song imgPath is " + s.getLargeImgPath());

        musicService.actuallyPlay(s.getId());
        this.setCurrentSong(s);
        handleButtons(false, false);


    }
    @Override
    public void setSongPosition(int i){
        musicService.setSongPosition(i);
    }




}
interface MusicControllerFragmentConstants{
    final static String PLAY_NORM="Song will be played once",REPEAT_ONCE="Song will be repeated once",REPEAT_INFINITE="Song will be repeated infinitely";
    final static String CURR_SONG_POS_REF="SONG_POSITION";
    final static String HAS_SAVE_BEEN_CALLED="in onSavePreferences";
    final static String REPEAT_BUTTON_STATUS="repeat button status";
    final static String SEEKBAR_POS="seekbar position";
    final static String SONG_LIST="songlist";

}

