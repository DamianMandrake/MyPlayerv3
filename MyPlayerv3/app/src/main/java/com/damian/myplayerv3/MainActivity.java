package com.damian.myplayerv3;

import android.Manifest;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ComponentName;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.widget.ListPopupWindow;

import com.damian.myplayerv3.AdaptersAndListeners.CustomAdapter;
import com.damian.myplayerv3.AdaptersAndListeners.FrameOnTouchListener;
import com.damian.myplayerv3.AdaptersAndListeners.SongRecycler;
import com.damian.myplayerv3.Fragments.AllSongsFragment;
import com.damian.myplayerv3.Fragments.MusicControllerFragment;
import com.damian.myplayerv3.Fragments.NavigationFragment;
import com.damian.myplayerv3.Fragments.PlaylistFrag;
import com.damian.myplayerv3.Fragments.PopupDialogFrag;
import com.damian.myplayerv3.Fragments.StreamerFragment;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityConstants,SongRecycler.GetFromList,FrameOnTouchListener.ToggleNavDrawerDrawable,NavigationFragment.FragmentSwapper,PlaylistFrag.SetSongs {



    public static int MAX_TRANSLATION;
    public static int MAX_WINDOW_HEIGHT=0;
    public static int MAX_MUSIC_FRAME_SIZE=0;
    static View coordinatorContent=null;
    public static Context context;
    public static File STORAGE_DIR,PLAYLIST_DIR;
    private int tempNotiId;
    public SearchView searchView;
    public void setStorageDir(File f){
        STORAGE_DIR=f;

        PLAYLIST_DIR=new File(MainActivity.STORAGE_DIR.getAbsolutePath()+"/playlists/");
        System.out.println(PLAYLIST_DIR.getAbsolutePath());




    }

    //
    //
    // boolean isInSearchView=false;//can useSearchView.isIconified();


    public void setMaxTranslation(int x) {
        MAX_TRANSLATION=x;
    }

    private Button b;
    static public boolean resumeApp=false;

    public AllSongsFragment allSongsFragment;
    public MusicControllerFragment musicControllerFragment;//since this has to be accessed by all other classes in the program
    public NavigationFragment navigationFragment;
    public PlaylistFrag playlistFrag;
    public StreamerFragment streamerFragment;

    public ProgressDialog progressDialog;
    //public TextView myAppBar;

    public Toolbar toolbar;

    private MusicService musicService;
    private Intent musicPlayerIntent;
    public static boolean isPlayerBound=false;
    private FrameLayout frameLayout,musicListFrame,navigationFrame;
    private DrawerLayout drawerLayout;
    private android.support.v4.app.FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;

    private Class mHolder=MusicService.class;
    private float previousY,trans,diff;

    public MenuItem menuItem;

    public static ArrayList<Song> songList=new ArrayList<>();
    private ArrayList<Song> tempList=new ArrayList<>();
    public TextView textView;


    private Song tempSong;
    NotificationMaker notificationMaker;

    private ListPopupWindow listPopupWindow;
    private CustomAdapter customAdapter;





    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            System.out.println("inside onServiceConnected of musicConnection which inits musicService");
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) service;
            System.out.println(musicBinder.toString()+" music BINDER");
            MainActivity.this.musicService = musicBinder.getServiceInstance();


            MainActivity.this.musicService.setRef(MainActivity.this.musicControllerFragment);
            MainActivity.this.musicService.setNotificationMaker(MainActivity.this.notificationMaker);

            MainActivity.this.musicControllerFragment.setMusicService(MainActivity.this.musicService);


            System.out.println("MUSIC SERVICE HAS VAL " + musicService.toString());
            isPlayerBound = true;

            if(resumeApp ) {

                System.out.println("songlist got set to musicService");
                MainActivity.this.musicService.setSongsList(MainActivity.songList);

                if (musicService.isPlaying()) {
                    System.out.println("Inside service if");
                    Song s = musicService.getCurrentlyPlayingSong();//happens only if musicService is running ie most times... have to check it out
                    if (s != null) {
                        System.out.println("setting song to curr");


                        musicControllerFragment.setCurrentSong(s);
                        int maxDur = musicService.getDuration();
                        musicControllerFragment.setSeekBarMax(maxDur);
                        musicControllerFragment.setMaxDuration(maxDur);


                    }
                } else {
                    musicService.load();
                }
            }



        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("inside onServiceDisconnected");
            //savePreferences();
            isPlayerBound = false;

        }
    };


    @Override
    public void setMusicServiceList(ArrayList<Song> songs){
        this.musicService.setSongsList(songs);

    }


    @Override
    protected void onStart(){
        super.onStart();


        if(musicPlayerIntent==null){

            musicPlayerIntent=new Intent(this,mHolder);
            System.out.println("about to start service");
            startService(musicPlayerIntent);
            getApplicationContext().bindService(musicPlayerIntent, musicConnection, Context.BIND_AUTO_CREATE);//, musicConnection, Context.BIND_AUTO_CREATE);


        }

    }

    public static int SCREEN_HEIGHT=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check if data is present in savedInstance


        SongRecycler.SongViewHolder.setGetFromList(this);
        setContentView(R.layout.activity_main);
        context=this;
        int p=this.getResources().getDisplayMetrics().heightPixels;
        SCREEN_HEIGHT=p;
        System.out.println("HEIGHT OF SCREEN IS "+p);
        MAX_WINDOW_HEIGHT= (int)(p*0.30);
        MAX_MUSIC_FRAME_SIZE=(int)(p*MUSIC_LIST_FRAME_PERCENT);
        setMaxTranslation(p - (int) (p * TRANSLATION_THRESHOLD_PERCENTAGE));
        setStorageDir(getApplicationContext().getCacheDir());

        coordinatorContent=findViewById(android.R.id.content);
        PlaylistFrag.setSetSongs(this);



        try{
            loadPreferences();


        }catch (NullPointerException npe){
            System.out.println("App started without call to onDestroy previously");

        }
        notificationMaker=new NotificationMaker(this,this.tempNotiId);



        allSongsFragment = new AllSongsFragment();
        musicControllerFragment = new MusicControllerFragment();
        streamerFragment=new StreamerFragment();
        navigationFragment=new NavigationFragment();navigationFragment.setFragmentSwapper(this);
        playlistFrag=new PlaylistFrag();

                System.out.println("VALUE OF resumeApp is " + resumeApp);
        if(resumeApp){
            StoreList storeList=new StoreList(MusicControllerFragment.SONG_LIST,false);
            songList=storeList.readArrayList();
            if(songList==null) {
                resumeApp = false;
                System.out.println("ARRAYLIST IS NUULL");
            }
            //allSongsFragment.initRecycler(songList);//never going to happen since recycler view is null
            // and onCreateView hasnt been called
        }

       handleUi();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

            System.out.println("INSIDE perm if");



            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                System.out.println(" permissions had been granted before about to start async");
                if(!PLAYLIST_DIR.exists())
                    System.out.println("folder of playlists was created " + PLAYLIST_DIR.mkdirs());
                b.setVisibility(View.INVISIBLE);
                allSongsFragment.setBackTaskAndExecute(this);
                setFramesVisible();
                initBotFrag();
                initOtherFrag(musicListFrame.getId(), allSongsFragment);
                initOtherFrag(navigationFrame.getId(),navigationFragment);



            }else
            {
                System.out.println("inside else about to turn button visibility on");//remove asap
                //show button only when perms are denied
                b.setVisibility(View.VISIBLE);
                setFramesInvisible();


            }
        }



    }

    private void handleUi(){
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);


        toolbar.setTitle(getResources().getString(R.string.app_name));
        this.setSupportActionBar(this.toolbar);
        System.out.println("toolbar height is " + toolbar.getHeight());


        this.drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        this.navigationFrame=(FrameLayout)findViewById(R.id.navFragHolder);


        frameLayout=(FrameLayout)findViewById(R.id.musicControllerFragPlaceholder);
        frameLayout.setTranslationY(MAX_TRANSLATION);
        fragmentManager=getSupportFragmentManager();

        musicListFrame=(FrameLayout)findViewById(R.id.musicListFrag);

        //layoutparams let you place your ui elements witht the help of java code
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,MAX_MUSIC_FRAME_SIZE);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        musicListFrame.setLayoutParams(layoutParams);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);

        progressDialog.setMessage("Initializing MusicPlayer");






        b=(Button)findViewById(R.id.permButton);

    }



    void savePreferences(){
        try {
            System.out.println("inside savepreferences of mainActivity");
            SharedPreferences sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            musicService.setmSharedPreferencesEditor(editor);

            resumeApp = true;
            editor.putBoolean(IS_IN_ON_DESTROY, resumeApp);
            editor.putInt(NOTIFICATION_ID,this.notificationMaker.getNotificationId());
            editor.apply();
            musicControllerFragment.save(editor);
        }catch (Exception op){
            op.printStackTrace();
        }


    }


    void loadPreferences(){
        SharedPreferences sharedPreferences=getSharedPreferences("myPref",Context.MODE_PRIVATE);
        System.out.println("value of resumeApp is " + resumeApp);
        this.tempNotiId=sharedPreferences.getInt(NOTIFICATION_ID,-1);
        resumeApp=sharedPreferences.getBoolean(IS_IN_ON_DESTROY,false);

    }


    public MusicService getMusicService(){
        return musicService;
    }
    void setFramesInvisible(){
        musicListFrame.setVisibility(View.INVISIBLE);
        frameLayout.setVisibility(View.INVISIBLE);
    }
    void setFramesVisible(){
        frameLayout.setVisibility(View.VISIBLE);
        musicListFrame.setVisibility(View.VISIBLE);
    }

    private void initBotFrag(){
        System.out.println("inside initBotFrag");

        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.musicControllerFragPlaceholder, musicControllerFragment);
        fragmentTransaction.commit();

        FrameOnTouchListener f=new FrameOnTouchListener(this.musicControllerFragment);
        f.setNavDrawerDrawable(this);
        frameLayout.setOnTouchListener(f);


    }
    //overriding FragmentSwapper funcs
    @Override
    public void initOtherFrag(int frameId,Fragment f){
        fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(frameId, f);
        fragmentTransaction.commit();

    }
    @Override
    public void closeDrawerLayout(){
        this.drawerLayout.closeDrawers();
    }
    @Override
    public Fragment getFragment(int i){
        switch (i){
            case 0:
                return this.allSongsFragment;
            case 1:
                return this.playlistFrag;//replace with playlist
            case 2:
                return this.streamerFragment;//replace with soundcloud fragment

        }
        return null;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    //To set toolbar search icon to a custom one
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MainActivity.this.menuItem=menu.findItem(R.id.actionSearch);

        this.searchView=(SearchView) MenuItemCompat.getActionView(menuItem);
        handleSearchViewUi();
        initListPopUpWindow();
        handleSearchViewCallbacks();




        return true;

    }

    private void initListPopUpWindow(){
        this.listPopupWindow=new ListPopupWindow(this);
        this.customAdapter=new CustomAdapter(MainActivity.songList,this,R.layout.list_item);
        this.listPopupWindow.setAdapter(this.customAdapter);
        this.listPopupWindow.setWidth(ActionBar.LayoutParams.MATCH_PARENT);
        this.listPopupWindow.setHeight(MainActivity.MAX_WINDOW_HEIGHT);
        this.listPopupWindow.setModal(false);
        this.listPopupWindow.setDropDownGravity(Gravity.CENTER);
        this.listPopupWindow.setAnchorView(this.searchView);

        this.listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Song s = (MainActivity.this.tempList.get(i));
                MainActivity.this.musicControllerFragment.play(s);
                MainActivity.this.musicService.setSongPosition(MainActivity.songList.indexOf(s));
                MainActivity.this.listPopupWindow.dismiss();
            }
        });
    }



    private void handleSearchViewCallbacks(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                MainActivity.this.tempList = new ArrayList<Song>();
                for (Song so : MainActivity.songList)
                    if (so.getTitle().toUpperCase().contains(s.toUpperCase()) || so.getArtist().toUpperCase().contains(s.toUpperCase()))
                        MainActivity.this.tempList.add(so);


                MainActivity.this.customAdapter.setFilter(MainActivity.this.tempList);
                if (MainActivity.this.tempList.size() > 0)
                    MainActivity.this.listPopupWindow.show();
                else
                    MainActivity.this.listPopupWindow.dismiss();


                return true;
            }
        });
    }

    private void handleSearchViewUi(){

        int searchImgId= android.support.v7.appcompat.R.id.search_button;
        ImageView v=(ImageView)searchView.findViewById(searchImgId);
        v.setImageResource(R.mipmap.search);


        int texViewId= android.support.v7.appcompat.R.id.search_src_text;
        textView=(TextView)searchView.findViewById(texViewId);
        textView.setTextColor(Color.WHITE);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            textView.setBackgroundColor(getResources().getColor(R.color.darkTwitter, getTheme()));
            textView.setBackgroundColor(getResources().getColor(R.color.darkTwitter, getTheme()));
        }
        else {
            textView.setBackgroundColor(getResources().getColor(R.color.darkTwitter));
            textView.setBackgroundColor(getResources().getColor(R.color.darkTwitter));
        }
        //for hint

        textView.setHintTextColor(Color.WHITE);
        textView.setHint("enter name of song");


        int cancelButton=android.support.v7.appcompat.R.id.search_close_btn;
        ImageView imageView=(ImageView)searchView.findViewById(cancelButton);
        imageView.setImageResource(R.mipmap.close);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.actionCreatePlaylist:
                this.showPlaylistDialog();
                break;
        }

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


    private void showPlaylistDialog(){
        if(this.fragmentManager!=null) {
            PopupDialogFrag popupDialogFrag = PopupDialogFrag.getInstance("Create Playlist");
            popupDialogFrag.show(this.fragmentManager,"playlist");
        }

    }





    public void askPerms(View v){
        //has to be public so that the xml code can access it
        System.out.println("about to ask for perm");
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //code for a dialog explaining why i need those permissions
            }
        }else
            toast(REQUIRE_PERMS);


        System.out.println("about to get into activity compat");
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_READ_EXTERNAL);


    }
    public static void toast(String a){

        Snackbar.make(MainActivity.coordinatorContent,a,Snackbar.LENGTH_SHORT).show();
    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_READ_EXTERNAL:
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED) {
                    toast(REQUIRE_PERMS);
                    setFramesInvisible();
                    b.setVisibility(View.VISIBLE);
                }
                else {
                    System.out.println("ABOUT TO CALL AsyncThread");
                    b.setVisibility(View.INVISIBLE);

                    allSongsFragment.setBackTaskAndExecute(this);
                    initBotFrag();
                    initOtherFrag(musicListFrame.getId(), allSongsFragment);
                    initOtherFrag(navigationFrame.getId(),navigationFragment);
                    setFramesVisible();

                }
                return;

            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();

    }
    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();


    }


    @Override
    public void setCurrSong(int p){
        this.tempSong=MainActivity.songList.get(p);

    }
    @Override
    public String getCurrSongImgPath(){
        try {
            return this.tempSong.getLargeImgPath();
        }catch (NullPointerException npe){
            return null;
        }
    }

    //overriding navDrawerToggle
    @Override
    public void toggleNavDrawerDrawable(boolean b){
        int shouldItToggle= b? DrawerLayout.LOCK_MODE_UNLOCKED:DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(shouldItToggle);


    }






}

interface MainActivityConstants{
    static final int MY_READ_EXTERNAL=4;
    static final String REQUIRE_PERMS="require permissions for proper functioning";
    /*static final String externalStoragePath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)).getAbsolutePath();
    static final File externalParentDir=new File(externalStoragePath);
    */

    static final File DOWNLOADS_DIRECTORY=(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

    static float TRANSLATION_THRESHOLD_PERCENTAGE=0.125f;
    static float MUSIC_LIST_FRAME_PERCENT=0.78f;
    static final String IS_IN_ON_DESTROY="isInOnDestroy";
    static final String NOTIFICATION_ID="notiId";
    static final int MY_INTERNET=44;


    //MORE TRANSLATIONY VALUE IN XML = LESSER SPACE OCCUPIED

}