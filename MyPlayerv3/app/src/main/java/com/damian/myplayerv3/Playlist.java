package com.damian.myplayerv3;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by damianmandrake on 2/25/17.
 */
public class Playlist implements Serializable{
    static String APPENDER="playlist/";
    private ArrayList<Song> songList;
    private String name,imgPath;

    transient private StoreList storeList;
    public Playlist(String name,ArrayList<Song> songList){
        this.songList=songList;
        this.name=name;
        this.imgPath=(songList.get(songList.size()-1)).getLargeImgPath();
        this.storeList=new StoreList(songList,name,true);
        this.storeList.writeArrayList();
    }

    public ArrayList<Song> getSongList(){return this.songList;}
    public String getName(){return this.name;}
    public String getLargeImgPath(){return this.imgPath;}




}
