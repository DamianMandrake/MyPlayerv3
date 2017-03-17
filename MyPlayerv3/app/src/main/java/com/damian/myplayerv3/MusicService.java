package com.damian.myplayerv3;


        import android.app.Service;
        import android.content.ContentUris;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.ServiceConnection;
        import android.content.SharedPreferences;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.net.Uri;
        import android.os.Binder;
        import android.os.IBinder;
        import android.os.PowerManager;
        import android.provider.MediaStore;
        import android.support.annotation.Nullable;

        import com.damian.myplayerv3.AdaptersAndListeners.AudioFocusAdapter;
        import com.damian.myplayerv3.AdaptersAndListeners.BroadcastListener;
        import com.damian.myplayerv3.Fragments.MusicControllerFragment;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.concurrent.ThreadLocalRandom;
import com.damian.myplayerv3.NotificationMaker;
/**
 * Created by Damian on 12/30/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,MusicServiceConstants,BroadcastListener.SongController {

    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songsList;
    private int songPosition;// song position will be used to restore state once app is restarted
    private final MusicBinder musicBinder=new MusicBinder();
    private MusicControllerFragment ref;

    public static int repeatState=0;//0 is normal,1 is repeat once,2 is repeat infinitely

    private boolean playCount;

    public static boolean isMediaPlayerPrepared=false;//true in onPrepared and false in onCompleted or in all other cases....
    public static boolean isShuffleOn=false;

    private SharedPreferences.Editor mSharedPreferencesEditor;
    private AudioManager audioManager;
    private BroadcastListener broadcastRecevier;


    public NotificationMaker notificationMaker;



    //prolly really bad programming.... cant thuink of anything else right now



    //might have to implement parcelable to write the current state in a bundle
    //Parcelable

    //since services dont require ctor... use lifecycle methods
    @Override
    public void onCreate(){
        //whenever you call baseclass lifecycle func... android binds and marks the current class as a service
        super.onCreate();
        songsList=new ArrayList<>();
        BroadcastListener.songController=this;

        songPosition=0;
        mediaPlayer=new MediaPlayer();
        initMusicPlayer();
        this.registerBroadcastService();
        this.audioManager= (AudioManager)getSystemService(AUDIO_SERVICE);
        boolean result=this.requestForFocus();
        System.out.println("**********>>>>>>>>>>>>> PERMISSION WAS GIVE <<<<<<<<<<*********"+result);


    }

    private boolean requestForFocus(){
        int res=this.audioManager.requestAudioFocus(new AudioFocusAdapter(this), AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return res==AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

    }

    public void setNotificationMaker(NotificationMaker no){
        this.notificationMaker=no;
    }
    public void setmSharedPreferencesEditor(SharedPreferences.Editor s){
        this.mSharedPreferencesEditor=s;

    }
    public void load(){
        if(MainActivity.resumeApp){
            SharedPreferences sharedPreferences=getApplication().getSharedPreferences("myPref",Context.MODE_PRIVATE);
            ref.loadLastSong(sharedPreferences);

        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("in on destroy of SERVICE");
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        System.out.println(inTaskRemoved);
        this.notificationMaker.dismissNotif();
        this.unregisterBroadcastService();
        //this.removeBroadcastReceiver.unregisterBroadcastService();

        ref.save(mSharedPreferencesEditor);
    }

    public void initMusicPlayer(){

        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);// this lets the service run even when the device is locked
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        //setting the listeners for the MediaPlayer interfaces

        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }
    public void setSongsList(ArrayList<Song> s) {
        songsList=s;this.songPosition=0;
    }

    public boolean isListEmpty(){

        try {

            return songsList.isEmpty();
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }finally {
            return true;
        }
    }


    public class MusicBinder extends Binder{
        MusicService getServiceInstance(){
            return MusicService.this;//return an instance of MusicService... this is being calledin the MainActivity as a part of SrviceConnection callback so that you can actually obtain the instance of the service from the OS
        }
    }
    public ArrayList getSongList(){return songsList;}
    public void playSong(){
            int pos=0;
            if(this.songPosition!=-1)   pos=songPosition;
                Song toBePlayed = songsList.get(pos);
            System.out.println("playing " + toBePlayed.getTitle());

            notificationMaker.setAll(toBePlayed.getLargeImgPath(),toBePlayed.getTitle(),toBePlayed.getArtist());
            ref.setCurrentSong(toBePlayed);
            playCount = !playCount;
            actuallyPlay(toBePlayed.getId());

            //done ... dont have to do this anywhere else;


    }
    public void actuallyPlay(long id){
        mediaPlayer.reset();


        long currSong = id;
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);

        } catch (IOException io) {
            System.out.println("MusicPlayer Excpetion ");
            System.out.println("Couldnt play song for some reason");

        }

        mediaPlayer.prepareAsync();
    }

    public void setSongPosition(int p){
        songPosition=p;
    }
    public int getSongPosition(){return songPosition;}
    public int getPosition(){
        return mediaPlayer.getCurrentPosition();
    }



    public int getDuration() {
        return mediaPlayer.getDuration();

    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    public void pause(){
        mediaPlayer.pause();
    }
    public void startPlaying(){
        mediaPlayer.start();
    }
    public void seekTo(int p){
        mediaPlayer.seekTo(p);
    }

    public void playNext(){

         if(repeatState==PLAY_NORMALLY )
        songPosition= isShuffleOn?ThreadLocalRandom.current().nextInt(0,songsList.size()):(songPosition+1)%songsList.size();
        else if(repeatState==REPEAT_ONCE && !playCount){
            playCount=!playCount;

            repeatState=PLAY_NORMALLY;

            playNext();
            repeatState=REPEAT_ONCE;
        }

        playSong();

    }
    public Song getCurrentlyPlayingSong(){

            return songsList.get(songPosition);

    }


    public void playPrevious(){

        if(repeatState==PLAY_NORMALLY) {
            songPosition = songPosition == 0 ? songsList.size() - 1 : songPosition - 1;
            songPosition =isShuffleOn?ThreadLocalRandom.current().nextInt(0,songsList.size()):songPosition;
        }

        playSong();
    }
    @Override
    public void onTouchNext(){
        playCount=false;
        int repeatButtonStatus=repeatState;
        repeatState=PLAY_NORMALLY;
        playNext();
        repeatState=repeatButtonStatus;



    }
    @Override
    public void onTouchPrev(){
        playCount=false;
        int repeatButtonStatus=repeatState;
        repeatState=PLAY_NORMALLY;
        playPrevious();
        repeatState=repeatButtonStatus;
        //when user pauses the current song and clicks on next/prev song ... the state of the button must change

    }


    public void setRef(MusicControllerFragment m){
        ref=m;
    }

    public MusicControllerFragment getRef(){return this.ref;}




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("About to return musicBinder");
        return musicBinder;
        //return null;//since im using start service... dont require a musicBinder instance
    }
    @Override
    public boolean onUnbind(Intent intent){
        mediaPlayer.stop();
        mediaPlayer.release();

        return false;
    }



    @Override
    public void onCompletion(MediaPlayer mp) {
        try{
        playNext();}catch (ArithmeticException er){

        }
            isMediaPlayerPrepared=false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }


    //this callback is called every time muisc is changed
    @Override
    public void onPrepared(MediaPlayer mp) {
        System.out.println("in onPrepared");
        isMediaPlayerPrepared=true;
        mediaPlayer.start();
        int p=mediaPlayer.getDuration();
        ref.setSeekBarMax(p);p/=1000;
        ref.getEndTime().setText((p / 60) + ":" + p % 60);


        if(ref.hasSavedStateBeenCalled) {//to play song from the point it was paused
            System.out.println("***SEEEKING***... VAL OF retrieved progress is "+ref.retrievedProgress);
            ref.hasSavedStateBeenCalled=false;
            //System.out.println("value before seeeking is "+ref.progress);
            //System.out.println("value of tempProgress before seeeking is "+this.tempProgress);

            //tetsting it out... not really sure of this

            this.seekTo(ref.retrievedProgress);


        }
        this.handleNotifButton(R.mipmap.pause);



    }


    public void callRefHandle(boolean a,boolean b){
        if(ref!=null)
        ref.handleButtons(a,b);
    }


    //BroadcastListener.songcontroller methods


    @Override
    public void handleButtons() {
        System.out.println("INSIDE HANDLE BUTTONS");
        if(this.isPlaying()) {
            ref.handleButtons(true, true);
        }else{
            ref.handleButtons(false,true);
        }



    }
    @Override
    public void resumeSong(){
        this.startPlaying();
    }

    @Override
    public boolean isPaused(){
        return this.isPlaying();
    }

    public void handleNotifButton(int r){

        this.notificationMaker.setPlayPauseImg(r);
    }

    private void registerBroadcastService(){
        this.broadcastRecevier=new BroadcastListener();
        getApplicationContext().registerReceiver(this.broadcastRecevier, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

    public void unregisterBroadcastService(){
        getApplicationContext().unregisterReceiver(this.broadcastRecevier);
    }





}
interface MusicServiceConstants{
     static final byte PLAY_NORMALLY=0,REPEAT_ONCE=1,REPEAT_INFINITELY=2;
    static final String inTaskRemoved="****IN****ON****TASK****REMOVED";
    static final String HANDLER_PROGRESS="handler progress";
}
