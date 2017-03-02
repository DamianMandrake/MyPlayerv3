package com.damian.myplayerv3.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damian.myplayerv3.AdaptersAndListeners.RecyclerScrollListener;
import com.damian.myplayerv3.AdaptersAndListeners.SongRecycler;
import com.damian.myplayerv3.BackgroundTasks.PlaylistBackTask;
import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.Playlist;
import com.damian.myplayerv3.AdaptersAndListeners.PlaylistRecyclerAdpater;
import com.damian.myplayerv3.R;
import com.damian.myplayerv3.Song;

import java.util.ArrayList;

/**
 * Created by damianmandrake on 2/25/17.
 * ******NOTE********
 * ******This fragment will replace the frame with id R.id.musicListFrame
 */
public class PlaylistFrag extends Fragment implements PlaylistRecyclerAdpater.PlayListSetter,PlaylistBackTask.SetPlaylists {

    private Playlist playlistRef;//stores the ref of playlist which was clicked in the recycler view
    private ArrayList<Playlist> playlists;

    private RecyclerView recyclerView,songListRecyclerView;
    private PlaylistRecyclerAdpater playlistRecyclerAdpater;
    private PlaylistBackTask playlistBackTask;
    private MainActivity activity;
    public static SetSongs setSongs;

    //todo set adapter to songListRecycler and set function to update adapter


    //was getting an illegal state exception whenever i replaced all songs fragment from the oncreate of this frag
    /*
    * what happens is whenever you replace a layout with a fragment its id changes ... which means its stateor cpu context changes(i think)
    * so when im swapping back to the allSongsfragment from nav view it lead to an illegal state exception since the current state had been changed by playlistfrag
    * but the allsongs fragment had its old state stored in the FragmentManager
    * Also note if you return a new instance of a fragment it doesnt matter since fragments are persistently stored in the FragmentManager
    * Creating a new instance of the fragment makes another key value pair which is created with the parentLayotu its going to replace
    * So this means the fragments state/cpu context is mapped to its id...[ Hence the illegal state exception since one frag instance can be mapped to only one]
    * This also means 1 instance can be mapped to only 1 state and 1 keyValue (<- common sense)
    * Illegal state arises when 1 frag has 2 ids that is when it is occupying 2 different layouts which gives it 2 diff idss
    * */


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.playlist_frag,container,false);


        PlaylistRecyclerAdpater.PlaylistViewHolder.setPlayListSetter(this);

        this.recyclerView=(RecyclerView)view.findViewById(R.id.playlistSongRecycler);
        this.songListRecyclerView=(RecyclerView)view.findViewById(R.id.playlistSongListRecycler);
        this.playlistBackTask=new PlaylistBackTask(this);
        this.playlistBackTask.execute();

        //Playlist playlist=new Playlist("playlist2", MainActivity.songList);playlist=new Playlist("playlist 3",MainActivity.songList);
        //retrieve all playlists that are currently available




        return view;
    }


    public static void setSetSongs(SetSongs s){
        PlaylistFrag.setSongs=s;
    }





    //overriding playlist setter funcs
    @Override
    public void setPlaylistAndInitSongFragment(int position){

        //todo set playlist and set adapter of allSongsFragment also set playlist to last played playlist


        this.playlistRef=playlists.get(position);
        ArrayList<Song> newList=this.playlistRef.getSongList();
        PlaylistFrag.setSongs.setMusicServiceList(newList);
        this.songListRecyclerView.setAdapter(new SongRecycler(getContext(), newList));
        this.songListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



    }

    //overriding playlistbacktask's setPlaylists function
    @Override
    public void setPlaylistArray(ArrayList<Playlist> arr){
        this.playlists=arr;
        //also initing the recycler
        this.playlistRecyclerAdpater=new PlaylistRecyclerAdpater(getContext(),arr);
        this.recyclerView.setAdapter(this.playlistRecyclerAdpater);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        this.recyclerView.setLayoutManager(linearLayoutManager);
        System.out.println("recycler view initialized");
    }

    public interface SetSongs {
        public void setMusicServiceList(ArrayList<Song> songs);
    }


}
