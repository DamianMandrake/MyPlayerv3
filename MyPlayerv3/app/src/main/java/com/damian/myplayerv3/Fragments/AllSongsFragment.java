package com.damian.myplayerv3.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damian.myplayerv3.AdaptersAndListeners.RecyclerScrollListener;
import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.R;
import com.damian.myplayerv3.Song;
import com.damian.myplayerv3.BackgroundTasks.SongListCompressBackTask;
import com.damian.myplayerv3.AdaptersAndListeners.SongRecycler;
import java.util.ArrayList;


/**
 * Created by damianmandrake on 1/17/17.
 */
public class AllSongsFragment extends Fragment {


    private RecyclerView recyclerView;
    private SongListCompressBackTask backTask;
    private Context context;
    private SongRecycler songRecycler;
    private RecyclerScrollListener recyclerScrollListener;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view=inflater.inflate(R.layout.frag_main,container,false);
        //((MainActivity)getActivity()).myAppBar.setText("All Songs");
        ((MainActivity)getActivity()).toolbar.setTitle("All songs");
        recyclerView=(RecyclerView)view.findViewById(R.id.songRecycler);
        if(MainActivity.resumeApp)
        initRecycler(MainActivity.songList);//could lead to npe on other devices...
        // prolly only if i add the readObject to a thread... dont know for sure... will try it out later

        return view;
    }

    public void setRecyclerView(RecyclerScrollListener s){
        this.recyclerScrollListener=s;
        //this.recyclerView.addOnScrollListener(this.recyclerScrollListener);
    }

    public void setBackTaskAndExecute(MainActivity a){
        backTask=new SongListCompressBackTask(a,!MainActivity.resumeApp);
        backTask.execute();


    }
    public void initRecycler(ArrayList<Song> s){
        context=context==null?MainActivity.context:context;

        System.out.println("INSIDE INIT RECYCLER");

        if(recyclerView!=null) {
            System.out.println("recycler view is NOT NULL");
            this.songRecycler = new SongRecycler(context, s);
            //this.songRecycler.setPlaySongReference(((MainActivity) getActivity()).musicControllerFragment);
            this.recyclerView.setAdapter(songRecycler);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(context));

            if(this.recyclerScrollListener!=null)this.recyclerView.addOnScrollListener(this.recyclerScrollListener);
            this.recyclerView.setVisibility(View.VISIBLE);

        }

    }
    public SongListCompressBackTask getBackTask(){return backTask;}





}
