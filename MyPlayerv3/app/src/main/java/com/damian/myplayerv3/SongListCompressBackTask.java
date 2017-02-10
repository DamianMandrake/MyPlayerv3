package com.damian.myplayerv3;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by damianmandrake on 1/6/17.
 */
public class SongListCompressBackTask extends AsyncTask<Void,Void,Void> {

    private ArrayList<Song> songList;
    private ContentResolver musicResolver;
    private Context context;
    private MainActivity activity;
    private Handler handler;



    private Runnable run=new Runnable(){
      public void run(){
          System.out.println("in hanlder");
          System.out.println("value of inFetch is "+inFetch+" VALUE OF isPlayerBound is "+MainActivity.isPlayerBound);
            if(!inFetch && MainActivity.isPlayerBound){
                System.out.println("initializing songList of musicService");
                activity.getMusicService().setSongsList(songList);
                System.out.println("removing callback");
                handler.removeCallbacks(this);

                handleRecyclerView();
                return;

            }
          handler.postDelayed(this,100);

        }
    };


    static boolean inFetch=false;





    SongListCompressBackTask(MainActivity a){
        songList=new ArrayList<Song>();
        activity=a;
        musicResolver=a.getContentResolver();
        context=a.getApplicationContext();

        handler=new Handler();
        handler.postDelayed(run,100);




    }




    @Override
    protected void onPreExecute() {//spwans a ui thread from ui thread
        super.onPreExecute();
        activity.progressDialog.show();
        activity.progressDialog.setMessage("Fetching songs");
        System.out.println("in onPreExcecute");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        inFetch=true;
    //worker thread
        findSongs();
        if(MainActivity.songList!=null)
        if(this.songList.size()>MainActivity.songList.size())
        MainActivity.songList=this.songList;



        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {//spawns a ui thread from ui thread
        inFetch=false;
        handleRecyclerView();

        activity.progressDialog.hide();
        activity.progressDialog=null;
        Toast.makeText(context,songList.size()+" songs retrieved",Toast.LENGTH_SHORT).show();
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
            Cursor albumArtCursor;
            do{
                long tempId=musicCursor.getLong(idCol);
                //System.out.println("year is "+y);
                String t=musicCursor.getString(titleCol),a=musicCursor.getString(artistCol),ba=musicCursor.getString(albumId);

                String alName=musicCursor.getString(albumName);

                albumArtCursor=musicResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Albums._ID,MediaStore.Audio.Albums.ALBUM_ART},
                        MediaStore.Audio.Albums._ID +"=?",new String[]{ba},null);


                String path=null;
                if(albumArtCursor.moveToFirst()){
                    path=albumArtCursor.getString(albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    System.out.println("ALBUM NAME IS "+alName);
                   /* if(path!=null) {
                        System.out.println("value of path is "+path);
                        path = compressAndGetFilePath(path, ba);
                    }*/
                }

                songList.add(new Song(tempId, t, a, path));



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
