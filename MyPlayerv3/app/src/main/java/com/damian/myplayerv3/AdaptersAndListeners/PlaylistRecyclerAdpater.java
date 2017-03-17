package com.damian.myplayerv3.AdaptersAndListeners;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.damian.myplayerv3.MainActivity;
import com.damian.myplayerv3.Playlist;
import com.damian.myplayerv3.R;

import java.util.ArrayList;

/**
 * Created by damianmandrake on 2/25/17.
 */
public class PlaylistRecyclerAdpater extends RecyclerView.Adapter<PlaylistRecyclerAdpater.PlaylistViewHolder> {

    private ArrayList<Playlist> playlists=new ArrayList<>();
    private Context context;
    private static int oldPos;
    public PlaylistRecyclerAdpater(Context ctx,ArrayList<Playlist> arr){
        this.context=ctx;
        this.playlists=arr;
        this.oldPos=-1;

    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(this.context).inflate(R.layout.playlist_item,parent,false);

        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {

        Playlist ref=this.playlists.get(position);
        holder.textView.setText(ref.getName());
        System.out.println("setting album art to "+ref.getName()+" playlist album art is "+ref.getLargeImgPath());
        String path= ref.getLargeImgPath();
        if(path==null)
        holder.imageView.setImageResource(R.drawable.notfound);
        else
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(path));



    }

    @Override
    public int getItemCount() {
        return this.playlists.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener,View.OnCreateContextMenuListener{
        TextView textView;
        ImageView imageView;
        View view;
        private static PlayListSetter playListSetter;





        PlaylistViewHolder(View view){
            super(view);
            this.view=view;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setOnCreateContextMenuListener(this);
            textView=(TextView)view.findViewById(R.id.playlistText);
            textView.setTextColor(Color.WHITE);
            imageView=(ImageView)view.findViewById(R.id.playlistImageView);



        }

        public static void setPlayListSetter(PlayListSetter playListSetter){
            PlaylistViewHolder.playListSetter=playListSetter;
        }

        @Override
        public void onClick(View view){
            System.out.println("old pos" + PlaylistRecyclerAdpater.oldPos);



            //changes right now
            PlaylistRecyclerAdpater.oldPos=getAdapterPosition();
            System.out.println("curr pos"+PlaylistRecyclerAdpater.oldPos);
            playListSetter.setPlaylistAndInitSongFragment(PlaylistRecyclerAdpater.oldPos);//passes the index of the playlist object to be retreived
        }

        @Override
        public boolean onLongClick(View view){
            playListSetter.setTempPlaylist(getAdapterPosition());
            view.showContextMenu();


            System.out.println("in longclik");
            return true;
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu,View view,ContextMenu.ContextMenuInfo menuInfo){

            MenuInflater menuInflater= MainActivity.menuInflater;
            menuInflater.inflate(R.menu.item_options,contextMenu);

        }



    }


    public interface PlayListSetter
    {
        public void setPlaylistAndInitSongFragment(int i);
        public void setTempPlaylist(int pos);

    }


}
