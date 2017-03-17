package com.damian.myplayerv3.AdaptersAndListeners;

import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;

import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.R;
import com.damian.myplayerv3.Song;

import java.util.ArrayList;

/**
 * Created by damianmandrake on 3/3/17.
 */
public class SearchQueryListener implements SearchView.OnQueryTextListener {

    private static ArrayList<Song> listUnderConsideration;

    private static ArrayList<Song> tempList;

    private static FilterSongs filterSongs;
    private static ListPopupWindow listPopupWindow;
    private static SongRecycler.PlaySong playSong;

    //todo require musicController reference
    public SearchQueryListener(FilterSongs filterSongs,ListPopupWindow listPopupWindow,SongRecycler.PlaySong playSong){
        this(filterSongs);
        SearchQueryListener.listPopupWindow=listPopupWindow;
        SearchQueryListener.playSong=playSong;





    }

    public SearchQueryListener(FilterSongs filterSongs){
        SearchQueryListener.filterSongs=filterSongs;
        SearchQueryListener.listPopupWindow=null;
    }




    public static void setListUnderConsideration(ArrayList<Song> t){
        System.out.println("list under consideration ");
        SearchQueryListener.listUnderConsideration=t;
        setCustomAdapter();

    }
    private static void setCustomAdapter(){
        CustomAdapter customAdapter=new CustomAdapter(SearchQueryListener.listUnderConsideration,MainActivity.context, R.layout.list_item);
        SearchQueryListener.filterSongs=customAdapter;
        SearchQueryListener.listPopupWindow.setAdapter(customAdapter);
        SearchQueryListener.listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("in item click listener of popup");
                Song s = (SearchQueryListener.tempList.get(i));
                SearchQueryListener.playSong.play(s);
                SearchQueryListener.playSong.setSongPosition(SearchQueryListener.listUnderConsideration.indexOf(s));
                SearchQueryListener.listPopupWindow.dismiss();
            }
        });
    }
    //public static void setListUnderConsideration(ArrayList<String> ar){}



    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

         SearchQueryListener.tempList = new ArrayList<Song>();
        for (Song so : SearchQueryListener.listUnderConsideration)
            if (so.getTitle().toUpperCase().contains(s.toUpperCase()) || so.getArtist().toUpperCase().contains(s.toUpperCase()))
                this.tempList.add(so);


        SearchQueryListener.filterSongs.setFilter(SearchQueryListener.tempList);
        if(SearchQueryListener.listPopupWindow!=null) {
            if (SearchQueryListener.tempList.size() > 0)
                SearchQueryListener.listPopupWindow.show();
            else
                SearchQueryListener.listPopupWindow.dismiss();
        }


        return true;
    }
}
