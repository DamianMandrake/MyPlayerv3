package com.damian.myplayerv3.Fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.damian.myplayerv3.Streamer.ClientSocket;
import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.R;
import com.damian.myplayerv3.Streamer.StreamListAdapter;
import com.damian.myplayerv3.Streamer.StreamerRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by damianmandrake on 3/1/17.
 */
public class StreamerFragment extends Fragment implements StreamerFragmentConstants,View.OnClickListener,StreamListAdapter {


    private ClientSocket clientSocket;

    private EditText editText;
    private Button button;
    String ip="";
    private RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.streamer_frag,container,false);
        this.editText=(EditText)view.findViewById(R.id.streamIpAddress);
        this.recyclerView=(RecyclerView)view.findViewById(R.id.streamListRecycler);
        this.button=(Button)view.findViewById(R.id.streamButton);
        this.button.setOnClickListener(this);
        this.askPerm();


        return view;
    }

    private void askPerm(){
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.INTERNET)== PackageManager.PERMISSION_GRANTED ){

            //clientSocket=new ClientSocket(this.ip);
            System.out.println("PERMS WERE GRANTED");

        }else{
            MainActivity.toast("Require internet/permissions for proper functioning");
        }



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {

            case MY_INTERNET:
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    MainActivity.toast("Require perms for proper functioning");
                }else {
                    System.out.println("permission granted");
                    this.button.setVisibility(View.INVISIBLE);
                }


            default:
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }


    public void onClick(View view){
        this.clientSocket=new ClientSocket(this.editText.getText().toString().trim(),this);
    }

    //overriding steramlistAdapter

    @Override
    public void setStreamListAdapter(final ArrayList<String> source) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StreamerFragment.this.recyclerView.setAdapter(new StreamerRecyclerAdapter(getContext(), source));
                StreamerFragment.this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
    }
}
interface StreamerFragmentConstants{
    static final int MY_INTERNET=44;

}
