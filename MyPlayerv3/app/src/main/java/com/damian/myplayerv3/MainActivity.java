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

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.widget.ListPopupWindow;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityConstants,SongRecycler.GetFromList {


    static int MAX_TRANSLATION;
    static int MAX_WINDOW_HEIGHT=0;
    private static Context context;
    static File STORAGE_DIR;

    public SearchView searchView;
    public void setStorageDir(File f){
        STORAGE_DIR=f;

    }

    //
    //
    // boolean isInSearchView=false;//can useSearchView.isIconified();
    public static Context getContext(){return context;}

    public void setMaxTranslation(int x) {
        MAX_TRANSLATION=x;
    }

    private Button b;
    static boolean resumeApp=false;

    public AllSongsFragment allSongsFragment;
    public MusicControllerFragment musicControllerFragment;//since this has to be accessed by all other classes in the program


    public ProgressDialog progressDialog;
    //public TextView myAppBar;

    public Toolbar toolbar;

    private MusicService musicService;
    private Intent musicPlayerIntent;
    static boolean isPlayerBound=false;
    private FrameLayout frameLayout,musicListFrame;
    private android.support.v4.app.FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;

    private Class mHolder=MusicService.class;
    private float previousY,trans,diff;

    public MenuItem menuItem;

    public static ArrayList<Song> songList=new ArrayList<>();
    private ArrayList<Song> tempList=new ArrayList<>();
    public TextView textView;


    private Song tempSong;

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
    protected void onStart(){
        super.onStart();


        if(musicPlayerIntent==null){

            musicPlayerIntent=new Intent(this,mHolder);
            System.out.println("about to start service");
            startService(musicPlayerIntent);
            getApplicationContext().bindService(musicPlayerIntent, musicConnection, Context.BIND_AUTO_CREATE);//, musicConnection, Context.BIND_AUTO_CREATE);


        }

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check if data is present in savedInstance


        SongRecycler.SongViewHolder.setGetFromList(this);
        System.out.println("setting layout");
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        int p=this.getResources().getDisplayMetrics().heightPixels;
        MAX_WINDOW_HEIGHT= (int)(p*0.30);
        setMaxTranslation(p - (int) (p * TRANSLATION_THRESHOLD_PERCENTAGE));
        setStorageDir(getApplicationContext().getCacheDir());


        try{
            loadPreferences();


        }catch (NullPointerException npe){
            System.out.println("App started without call to onDestroy previously");

        }

        allSongsFragment = new AllSongsFragment();
        musicControllerFragment = new MusicControllerFragment();

                System.out.println("VALUE OF resumeApp is "+resumeApp);
        if(resumeApp){
            StoreList storeList=new StoreList(MusicControllerFragmentConstants.SONG_LIST);
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
                b.setVisibility(View.INVISIBLE);
                allSongsFragment.setBackTaskAndExecute(this);
                setFramesVisible();
                initBotFrag();
                initOtherFrag(allSongsFragment);



            }else
            {
                System.out.println("inside else about to turn button visibility on");//remove asap
                //show button only when perms are denied
                b.setVisibility(View.VISIBLE);
                setFramesInvisible();


            }
        }
        //this takes 2 onclick listeners



    }

    private void handleUi(){
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);


        toolbar.setTitle(getResources().getString(R.string.app_name));
        this.setSupportActionBar(this.toolbar);


        frameLayout=(FrameLayout)findViewById(R.id.musicControllerFragPlaceholder);
        fragmentManager=getSupportFragmentManager();

        musicListFrame=(FrameLayout)findViewById(R.id.musicListFrag);


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
            editor.apply();
            musicControllerFragment.save(editor);
        }catch (Exception op){
            op.printStackTrace();
        }


    }


    void loadPreferences(){
        SharedPreferences sharedPreferences=getSharedPreferences("myPref",Context.MODE_PRIVATE);
        System.out.println("value of resumeApp is " + resumeApp);
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
        fragmentTransaction.replace(R.id.musicControllerFragPlaceholder,musicControllerFragment);
        fragmentTransaction.commit();

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        previousY = motionEvent.getY();

                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float currentY = motionEvent.getY();
                        diff = previousY - currentY;
                        trans = view.getTranslationY();
                        trans -= diff;

                        trans = trans < 0 ? 0 : (trans > MAX_TRANSLATION ? MAX_TRANSLATION : trans);

                        view.setTranslationY(trans);

                        return true;

                    case MotionEvent.ACTION_UP:

                        if (diff < 0) {
                            view.setTranslationY(MAX_TRANSLATION);
                            musicControllerFragment.smallPlayPause.setVisibility(View.VISIBLE);
                        } else if (diff > 0) {
                            view.setTranslationY(0);
                            musicControllerFragment.smallPlayPause.setVisibility(View.INVISIBLE);
                        }


                        return true;


                }

                return view.onTouchEvent(motionEvent);

            }
        });

    }
    private void initOtherFrag(Fragment f){
        fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(musicListFrame.getId(), f);
        fragmentTransaction.commit();

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
                MainActivity.this.musicControllerFragment.play(MainActivity.songList.indexOf(MainActivity.this.tempList.get(i)));
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
                if(s.length()>0 && listPopupWindow!=null && !MainActivity.this.listPopupWindow.isShowing())
                listPopupWindow.show();
                MainActivity.this.tempList=new ArrayList<Song>();
                for(Song so:MainActivity.songList)
                    if(so.getTitle().toUpperCase().contains(s.toUpperCase()))
                        MainActivity.this.tempList.add(so);

                if(MainActivity.this.tempList.size()>4)
                    MainActivity.this.listPopupWindow.setHeight(MainActivity.MAX_WINDOW_HEIGHT);
                else
                    MainActivity.this.listPopupWindow.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);

                MainActivity.this.customAdapter.setFilter(MainActivity.this.tempList);



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

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
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
    private void toast(String a){

        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
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
                    initOtherFrag(allSongsFragment);
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






}

interface MainActivityConstants{
    static final int MY_READ_EXTERNAL=4;
    static final String REQUIRE_PERMS="require permissions for proper functioning";
    /*static final String externalStoragePath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)).getAbsolutePath();
    static final File externalParentDir=new File(externalStoragePath);
    */



    static float TRANSLATION_THRESHOLD_PERCENTAGE=0.175f;
    static final String IS_IN_ON_DESTROY="isInOnDestroy";

    //MORE TRANSLATIONY VALUE IN XML = LESSER SPACE OCCUPIED

}