package com.damian.myplayerv3.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.damian.myplayerv3.AdaptersAndListeners.PopupAdapter;
import com.damian.myplayerv3.AdaptersAndListeners.SongRecycler;
import com.damian.myplayerv3.Frequents.Keeper;
import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.Playlist;
import com.damian.myplayerv3.R;
import com.damian.myplayerv3.Song;

import java.util.ArrayList;

/**
 * Created by damianmandrake on 2/26/17.
 * going to use this to create new playlists.... managing playlists will have to be done later
 * DialogFragment doesnt inflate the layout since its being inflated twice once intertnally and other in onCraeteView
 *
 */
public class PopupDialogFrag extends DialogFragment implements PopupAdapter.PopupItemViewHolder.PlaylistArrayHandler {

    private EditText playlistName,searchField;
    private static boolean isInEditMode;
    private RecyclerView recyclerView;//todo make a new adapter which has a textview and a checkbox to select songs

    private static ArrayList<Song> playlistSongList;
    //private static ArrayList<Song> tempList;
    public PopupDialogFrag(){
        //this is required by DialogFra base to instantiate this obj
        //to create your own obj of dialog frag use a static method which returns an instance of this class
    }

    public static PopupDialogFrag getInstance(String title){
        PopupDialogFrag popupDialogFrag=new PopupDialogFrag();
        Bundle bundle =new Bundle();
        bundle.putString("title", title);
        popupDialogFrag.setArguments(bundle);
        PopupDialogFrag.playlistSongList=new ArrayList<>();
        System.out.println("leaving getInstace of dialog");
        PopupDialogFrag.isInEditMode=false;

        return popupDialogFrag;

    }
    //for editing playlists
    public static PopupDialogFrag getInstance(String title, String playlistName,ArrayList<Song> arrayList){
        PopupDialogFrag.isInEditMode=true;
        PopupDialogFrag popupDialogFrag=new PopupDialogFrag();
        Bundle bundle=new Bundle();
        PopupDialogFrag.playlistSongList=arrayList;
        bundle.putString("title", title);
        bundle.putString("playlistName",playlistName);
        popupDialogFrag.setArguments(bundle);
        //todo add the arraylist to the bundle

        return popupDialogFrag;
    }

    @Nullable
    //DONT DO THIS SINCE ITS BEING INFLATED TWICE
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //dont init views here... instead do it in the onViewCreated callback
        System.out.println("leaving onCreateView of dialog");
        if(!isInLayout())
            return super.onCreateView(inflater,container,savedInstanceState);

        View view=inflater.inflate(R.layout.popup_window,container,false);
        initViews(view);

        return view;
    }*/
    /*
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        System.out.println("leaving onViewCreated of dialog");

    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        //inflating stuff her
        PopupAdapter.PopupItemViewHolder.setPlaylistArrayHandler(this);
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.popup_window,null);
        //building alert dialog

        AlertDialog.Builder alertDialog= new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getArguments().getString("title"));


        alertDialog.setView(view);
        initViews(view);
        if(isInEditMode)
            this.playlistName.setText(getArguments().getString("playlistName"));

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                MainActivity.toast("SUCCESS");
                //todo create and store playlist retreived from NEW recycleradapter
                //need all playlist names.... will have to read it before hand
                //todo add validations
                String name = playlistName.getText().toString();
                if (name.length() > 0 && playlistSongList.size() > 0) {//&& notExists()
                    //for(Song s:playlistSongList)
                    //s.setSelected(false);
                    new Playlist(name, PopupDialogFrag.playlistSongList);

                } else
                    MainActivity.toast("Please enter a valid name or atleast 1 song in list");

                PopupDialogFrag.this.cleanupMainList();

            }

        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                MainActivity.toast("DISMISSING ");
                dialogInterface.dismiss();
               PopupDialogFrag.this.cleanupMainList();

            }

        });





        return alertDialog.create();



    }


    private void cleanupMainList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(Song s:MainActivity.songList){
                    s.setSelected(false);
                }
            }
        }).start();

    }


    private void initViews(View view){
        //this.playlistSongList=new ArrayList<>();


        this.playlistName=(EditText)view.findViewById(R.id.dialogPlaylistName);
        //todo add a textListener to search for songs
        this.searchField=(EditText)view.findViewById(R.id.dialogSearch);
        //todo set A NEW custom adapter
        this.recyclerView=(RecyclerView)view.findViewById(R.id.dialogRecyclerView);


        if(PopupDialogFrag.isInEditMode){
            System.out.println("setting seelcted songs... ");
            for(Song s:PopupDialogFrag.playlistSongList){
                System.out.println("curr song to be set is "+s.getTitle());
                System.out.println("curr song is selected "+s.getIsSelected());
                int i=0;
                //using this method since java's hashcode function generates different values for the same object if theyre serialized
                for( ;i<MainActivity.songList.size(); i++) {
                    if(Keeper.getMd5(MainActivity.songList.get(i)).equals(Keeper.getMd5(s)))
                        break;

                }
                Song sa=MainActivity.songList.get(i - 1);
                System.out.println("TITLE "+sa.getTitle());
                MainActivity.songList.get(i - 1).setSelected(true);

            }
        }

        final PopupAdapter popupAdapter=new PopupAdapter(getContext(),MainActivity.songList);


        this.recyclerView.setAdapter(popupAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        this.searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<Song> tempList=new ArrayList<Song>();
                for(Song s:MainActivity.songList){
                    if(s.getArtist().toUpperCase().contains(charSequence.toString().toUpperCase()) || s.getTitle().toUpperCase().contains(charSequence.toString().toUpperCase()))
                        tempList.add(s);

                }






                popupAdapter.setFilter(tempList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }


    //making hash kinda




    //Overriding playlist arrayhandler funcs

    @Override
    public void addElement(Song s){
        PopupDialogFrag.playlistSongList.add(s);
        System.out.println("arr list size is "+this.playlistSongList.size());
    }
    @Override
    public boolean removeSong(Song s){
        return PopupDialogFrag.playlistSongList.remove(s);
    }
    @Override
    public Song getSong(int pos){
        return MainActivity.songList.get(pos);
    }


}
