package com.damian.myplayerv3.Streamer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damian.myplayerv3.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by damianmandrake on 3/1/17.
 */
public class StreamerRecyclerAdapter extends RecyclerView.Adapter<StreamerRecyclerAdapter.StreamerViewHolder> {

    private ArrayList<String> arr;
    private LayoutInflater layoutInflater;

    public StreamerRecyclerAdapter(Context context,ArrayList<String> ar){
        this.arr=ar;
        this.layoutInflater=LayoutInflater.from(context);
    }


    @Override
    public int getItemCount() {
        return arr.size();
    }

    @Override
    public StreamerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.recycler_list_selection,parent,false);
        return new StreamerViewHolder(view);


    }



    @Override
    public void onBindViewHolder(StreamerViewHolder holder, int position) {

        holder.textView.setText(this.arr.get(position));

    }



    static class StreamerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        public static RequestWriter requestWriter;
        public static SetClickedFileName clickedFileName;
        public StreamerViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
            textView=(TextView)view.findViewById(R.id.recyclerSongName);
        }

        public void onClick(View view){
            new Thread(new Runnable(){//since networking cant be done on main thread
                public void run(){
                    int pos=getAdapterPosition();
                    clickedFileName.setClickFileName(pos);
                    requestWriter.writeRequest("song :"+pos);

                }

            }).start();


        }




    }




}
