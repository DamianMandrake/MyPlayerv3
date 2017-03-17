package com.damian.myplayerv3.BackgroundTasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.Playlist;
import com.damian.myplayerv3.Song;
import com.damian.myplayerv3.StoreList;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by damianmandrake on 2/25/17.
 */
public class PlaylistBackTask extends AsyncTask<Void,Void,Integer> {
    private ProgressDialog progressDialog;
    private ArrayList<Playlist> playlists;
    private SetPlaylists setPlaylists=null;
    public PlaylistBackTask(SetPlaylists set){
        System.out.println("in playlistbacktask");
        this.setPlaylists=set;
        this.playlists=new ArrayList<>();
         this.progressDialog=new ProgressDialog(MainActivity.context);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setInverseBackgroundForced(false);

    }









    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("retrieving playlists");
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int num=-1;
        for(File f:MainActivity.PLAYLIST_DIR.listFiles())
            if(f!=null) {
                System.out.println("Playlist read is " + f.getName());

                StoreList storeList = new StoreList(f.getName(), true);
                ArrayList<Song> songs = storeList.readArrayList();
                this.playlists.add(new Playlist(f.getName(), songs));
                ++num;
            }




        return num+1;
    }

    @Override
    protected void onPostExecute(Integer num) {
        super.onPostExecute(num);
        if(num!=0)
            this.setPlaylists.setPlaylistArray(this.playlists);
        this.progressDialog.dismiss();
        MainActivity.toast(num+" playlists retrieved");
    }


    public interface SetPlaylists{
        public void setPlaylistArray(ArrayList<Playlist> retrievedList);

    }



}
