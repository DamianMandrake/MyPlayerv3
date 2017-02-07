package com.damian.myplayerv3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by damianmandrake on 1/17/17.
 */
public class AllSongsFragment extends Fragment {


    private RecyclerView recyclerView;
    private SongListCompressBackTask backTask;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view=inflater.inflate(R.layout.frag_main,container,false);
        ((MainActivity)getActivity()).myAppBar.setText("All Songs");
        recyclerView=(RecyclerView)view.findViewById(R.id.songRecycler);
        if(backTask!=null)backTask.setRecyclerView(recyclerView);
        return view;
    }

    public void setBackTaskAndExecute(MainActivity a){
        backTask=new SongListCompressBackTask(a);
        //backTask.setIniter((MainActivity)getActivity());
        backTask.execute();
        backTask.setMusicService(a.getMusicService());


    }
    public SongListCompressBackTask getBackTask(){return backTask;}



}
