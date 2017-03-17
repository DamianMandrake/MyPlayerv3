package com.damian.myplayerv3.BackgroundTasks;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import com.damian.myplayerv3.Fragments.MusicControllerFragment;
import com.damian.myplayerv3.Frequents.Keeper;
import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.Song;
import com.damian.myplayerv3.StoreList;

import java.util.ArrayList;
import java.util.HashMap;

import com.damian.myplayerv3.MainActivity;

/**
 * Created by damianmandrake on 1/6/17.
 */
public class SongListCompressBackTask extends AsyncTask<Void,Void,Void> {

    private ArrayList<Song> songList;
    private ContentResolver musicResolver;
    private Context context;
    private MainActivity activity;
    private Handler handler;
    private boolean showProgressDialog;
    ProgressDialog progressDialog;
    private String notifyChangeInList="";


    private Runnable run=new Runnable(){
      public void run(){
          System.out.println("in hanlder");
          System.out.println("value of inFetch is "+inFetch+" VALUE OF isPlayerBound is "+MainActivity.isPlayerBound);
            if(!inFetch && MainActivity.isPlayerBound){
                System.out.println("initializing songList of musicService");

                    activity.getMusicService().setSongsList(SongListCompressBackTask.this.songList);
                System.out.println("removing callback");
                handler.removeCallbacks(this);

                return;

            }
          handler.postDelayed(this,100);

        }
    };


    static boolean inFetch=false;





    public SongListCompressBackTask(MainActivity a,boolean showProgressDialog){
        songList=new ArrayList<Song>();
        activity=a;
        musicResolver=a.getContentResolver();
        context=a.getApplicationContext();

        handler=new Handler();
        handler.postDelayed(run, 100);

        if(this.showProgressDialog=showProgressDialog){
            this.progressDialog=new ProgressDialog(MainActivity.context);
            this.progressDialog.setCancelable(false);
            this.progressDialog.setInverseBackgroundForced(false);
            this.progressDialog.setMessage("Retrieving songs");
        }

        //new HandlerThread()



    }




    @Override
    protected void onPreExecute() {//spwans a ui thread from ui thread
        super.onPreExecute();

        if(this.showProgressDialog){
            this.progressDialog.show();
        }

        System.out.println("in onPreExcecute");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        inFetch=true;
    //worker thread
        findSongs();
        handleList();




        return null;
    }

    private void handleList(){
        if(MainActivity.songList==null) {
            MainActivity.songList = this.songList;
            writeListToStorage();
        }
        else {

            if (MainActivity.resumeApp) {//whenever async is called when the app resumes


                if ( !Keeper.getMd5(MainActivity.songList).equals((Keeper.getMd5(this.songList)))) {
                    writeListToStorage();
                    //System.out.println("HASHCODE OF MAINACTIVITY SONGLIST BEFORE IS " + + " AND OF THIS IS " + );

                    MainActivity.songList = this.songList;
                    System.out.println("HASHCODE OF MAINACTIVITY SONGLIST IS " + Keeper.getMd5(MainActivity.songList)+" AND OF THIS IS "+ Keeper.getMd5(this.songList));

                    notifyChangeInList = "change in listOfSongs noticed";
                    System.out.println("CHANGE NOTICED ");

                }


            }
        }

    }

    private void writeListToStorage(){

        StoreList storeList = new StoreList(SongListCompressBackTask.this.songList, MusicControllerFragment.SONG_LIST,false);
        storeList.writeArrayList();
    }


    @Override
    protected void onPostExecute(Void aVoid) {//spawns a ui thread from ui thread
        inFetch=false;
        if(this.showProgressDialog)
        this.progressDialog.hide();

        //activity.progressDialog.hide();
        //activity.progressDialog=null;

        Toast.makeText(context,songList.size()+" songs retrieved",Toast.LENGTH_SHORT).show();
        if(notifyChangeInList.length()!=0) {
            Toast.makeText(context, notifyChangeInList, Toast.LENGTH_SHORT).show();
            handleRecyclerView();
        }


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public ArrayList<Song> getSongList(){return songList;}




    private void findSongs(){


        Uri musicUri= android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor=musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()) {
            int titleCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idCol= musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistCol=musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumId=musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
            int albumName=musicCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM);
           // int year=musicCursor.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR);
            Cursor albumArtCursor=null;

            do{
                long tempId=musicCursor.getLong(idCol);
                //System.out.println("year is "+y);
                String t=musicCursor.getString(titleCol),a=musicCursor.getString(artistCol),ba=musicCursor.getString(albumId);

                String alName=musicCursor.getString(albumName);
                try {
                    albumArtCursor = musicResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                            MediaStore.Audio.Albums._ID + "=?", new String[]{ba}, null);
                }catch (Exception e){
                    continue;
                }

                String path=null;
                try {
                    if (albumArtCursor.moveToFirst()) {
                        path = albumArtCursor.getString(albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                        System.out.println("ALBUM NAME IS " + alName);
                   /* if(path!=null) {
                        System.out.println("value of path is "+path);
                        path = compressAndGetFilePath(path, ba);
                    }*/
                    }
                }catch (NullPointerException npe){
                    System.out.println("album art couldnt be parsed");
                }
                Song s=new Song(tempId, t, a, path);


                songList.add(s);
                //MainActivity.songMap.put(Keeper.getMd5(s),s);



            }while(musicCursor.moveToNext());
            albumArtCursor.close();
        }
        musicCursor.close();
        System.out.println("waiting");



    }
    /*

    private String compressAndGetFilePath(String path,String albumId){


        System.out.println("Album id is " + albumId);

        File file = new File(MainActivityConstants.externalParentDir,albumId+".txt");
        String p=file.getAbsolutePath();
        //only if file doesnt exist compress the img...
        if (!file.exists()) {

            //to check whether or not the file has already been made... can be reused later...
            // will have to delete it in onDestroy
            System.out.println("inside the if ie file doesnt exist");

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            System.out.println("compressing");

            bitmap.compress(Bitmap.CompressFormat.JPEG, 4, byteArrayOutputStream);
            System.out.println("lenght of compressedfile is "+byteArrayOutputStream.size());
            //writing compressed img to file to be retrieved later





            System.out.println(file.getAbsolutePath());

            FileOutputStream fout = null;
            try {
                System.out.println("Craeting file... "+file.createNewFile());
                System.out.println("initing fileoutput stream");


                fout = new FileOutputStream(file);


                System.out.println("writing to file output stream");
                fout.write(byteArrayOutputStream.toByteArray());

                p=file.getAbsolutePath();


            } catch (FileNotFoundException fne) {
                fne.printStackTrace();
            } catch (IOException o) {
                o.printStackTrace();
            } finally {

                try {
                    if (fout != null) {
                        fout.close();
                    }

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return p;
    }
    */
    private void handleRecyclerView(){

        activity.allSongsFragment.initRecycler(songList);


        System.out.println("setadapter to recyclerView ");

    }






}
