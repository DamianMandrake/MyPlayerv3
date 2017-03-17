package com.damian.myplayerv3.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.damian.myplayerv3.AdaptersAndListeners.CustomAdapter;
import com.damian.myplayerv3.AdaptersAndListeners.SearchQueryListener;
import com.damian.myplayerv3.BackgroundTasks.SongListCompressBackTask;
import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.Playlist;
import com.damian.myplayerv3.R;

/**
 * Created by damianmandrake on 2/24/17.
 * this class handles all navBar stuff
 * using a list view to show options right... might think of a better way later
 */
public class NavigationFragment extends Fragment implements NavigationFragmentConstants,AdapterView.OnItemClickListener{


    private ListView listView;
    private CustomAdapter navigationListAdapter;
    private FragmentSwapper fragmentSwapper;

    private Fragment currentFragment;
    private MainActivity activity;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.nav_frag,container,false);
        this.listView=(ListView)view.findViewById(R.id.navListView);
        this.navigationListAdapter=new CustomAdapter(NavigationFragmentConstants.options,MainActivity.context,R.layout.list_item);
        this.listView.setAdapter(this.navigationListAdapter);
        this.listView.setOnItemClickListener(this);
        this.activity=(MainActivity)getActivity();
        this.currentFragment=this.activity.allSongsFragment;//always ... for now


        return view;
    }


    public void setFragmentSwapper(FragmentSwapper fra){
        this.fragmentSwapper=fra;

    }











    //overriding itemClick listener of adapter view


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MainActivity.toast(NavigationFragmentConstants.options[i]);
        Fragment toSwap=this.fragmentSwapper.getFragment(i);
        if(!NavigationFragment.this.currentFragment.equals(toSwap) && toSwap!=null) {
            if(toSwap instanceof AllSongsFragment) {
                PlaylistFrag.setSongs.setMusicServiceList(MainActivity.songList);
                SearchQueryListener.setListUnderConsideration(MainActivity.songList);
                new SongListCompressBackTask((MainActivity)getActivity(),false).execute();

            }else if(toSwap instanceof PlaylistFrag){

            }

            this.fragmentSwapper.initOtherFrag(R.id.musicListFrag, toSwap);
            currentFragment=toSwap;
        }
        fragmentSwapper.closeDrawerLayout();
    }


    //interface func to be fired whenever fragments are to be swapped
    public interface FragmentSwapper{
        public void initOtherFrag(int frameId,Fragment fragment);
        public void closeDrawerLayout();
        public Fragment getFragment(int position);

    }
}
interface NavigationFragmentConstants{
    final static String options[]={"All Songs","Playlists","Go online"};
}