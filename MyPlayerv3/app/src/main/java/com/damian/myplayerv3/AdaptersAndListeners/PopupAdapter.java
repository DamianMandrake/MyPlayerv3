package com.damian.myplayerv3.AdaptersAndListeners;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.Playlist;
import com.damian.myplayerv3.R;
import com.damian.myplayerv3.Song;

import java.util.ArrayList;

/**
 * Created by damianmandrake on 2/26/17.
 */
public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.PopupItemViewHolder>
{

    private ArrayList<Song> songArrayList;
    private LayoutInflater inflater;
    //private boolean boo[];
    public PopupAdapter(Context ctx,ArrayList <Song>arrayList){
        this.songArrayList=arrayList;
        //this.boo=new boolean[arrayList.size()];
        //this.songArrayList.addAll(arrayList);
        this.inflater=LayoutInflater.from(ctx);

    }


    @Override
    public void onBindViewHolder(final PopupItemViewHolder holder, final int position) {
        System.out.println("inside onbiind viewholder");
        final Song curr=PopupItemViewHolder.playlistArrayHandler.getSong(position);


        holder.textView.setText(this.songArrayList.get(position).getTitle());
        System.out.println("song "+curr.getTitle()+" IS "+curr.getIsSelected());
        holder.view.setBackgroundColor(curr.getIsSelected()? PopupItemViewHolder.checkedBg : PopupItemViewHolder.ogBg);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b= !curr.getIsSelected();

                curr.setSelected(b);
                int bg=PopupItemViewHolder.ogBg;
                Song c=PopupAdapter.this.songArrayList.get(position);
                if(b){
                    PopupItemViewHolder.playlistArrayHandler.addElement(c);
                    bg=PopupItemViewHolder.checkedBg;
                }else
                    PopupItemViewHolder.playlistArrayHandler.removeSong(c);
                holder.view.setBackgroundColor(bg);

            }
        });



    }

    @Override
    public PopupItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.recycler_list_selection,parent,false);
        return new PopupItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.songArrayList.size();
    }

    public static class PopupItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

         static int checkedBg;
         static int ogBg;
        View view;

        static{
            if( Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                checkedBg=MainActivity.context.getResources().getColor(R.color.darkTwitter, MainActivity.context.getTheme());
                ogBg=MainActivity.context.getResources().getColor(R.color.lightTwitter, MainActivity.context.getTheme());
            }else{
                checkedBg=MainActivity.context.getResources().getColor(R.color.darkTwitter);
                ogBg=MainActivity.context.getResources().getColor(R.color.lightTwitter);
            }

        }
        static PlaylistArrayHandler playlistArrayHandler;
        PopupItemViewHolder(View view){
            super(view);
            this.view=view;

            textView=(TextView)view.findViewById(R.id.recyclerSongName);

            //checkBox=(CheckBox)view.findViewById(R.id.recyclerCheckbox);
        }
        public static void setPlaylistArrayHandler(PlaylistArrayHandler pl){PopupItemViewHolder.playlistArrayHandler=pl;}



        //this interface is used to obtain/remove song from main list and
        public interface PlaylistArrayHandler{
            public void addElement(Song s);
            public boolean removeSong(Song s);
            public Song getSong(int pos);
        }


    }


    public void setFilter(ArrayList<Song> arrayList){
        this.songArrayList=new ArrayList<>();
        this.songArrayList.addAll(arrayList);
        this.notifyDataSetChanged();

    }

    //overriding flipper




}

