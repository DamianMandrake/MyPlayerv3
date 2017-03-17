package com.damian.myplayerv3.AdaptersAndListeners;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.damian.myplayerv3.R;
import com.damian.myplayerv3.Song;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by damianmandrake on 2/18/17.
 */
public class CustomAdapter extends BaseAdapter implements FilterSongs {

    private ArrayList arrayList;
    private int resourceId;
    private LayoutInflater layoutInflater;
    private boolean textColorWhite=false;//tells me which ctor was called since im using this in both navddrawer and listpopup window

    public CustomAdapter(ArrayList<Song> songs,Context context,int resId){
        this(context,resId);
        this.arrayList=new ArrayList<>();
        this.arrayList=songs;
        this.textColorWhite=false;


    }

    public CustomAdapter(String arr[],Context context,int resourceId){
        this(context,resourceId);
        this.arrayList=new ArrayList<String>(Arrays.asList(arr));
        this.textColorWhite=true;


    }
    private CustomAdapter(Context context,int resourceId){
        this.resourceId=resourceId;
        this.layoutInflater=LayoutInflater.from(context);
        System.out.println("LEAVING CTOR OF CUSTOM ADAPTER");
    }


    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row=view;
        ViewHolder viewHolder=null;
        System.out.println("INSIDE GET VIEW");

        if(row==null||row.getTag()==null){
            System.out.println("INSIDE IF OF GET VIEW IE row is NULL");
            row=this.layoutInflater.inflate(resourceId,viewGroup,false);
            viewHolder=new ViewHolder(row);

            row.setTag(viewHolder);
        }else
        viewHolder=(ViewHolder)row.getTag();

        Object obj=this.getItem(i);
        String x="";
        if(textColorWhite){
            viewHolder.textView.setTextColor(Color.WHITE);
            x = obj.toString();

        }else
            x= ((Song)obj).getTitle();






        viewHolder.textView.setText(x);
        System.out.println(x);

        return row;
    }

    @Override
    public void setFilter(ArrayList<Song> array){
        System.out.println("INSIDE SET FILTER");
        this.arrayList=new ArrayList<>();
        this.arrayList.addAll(array);
        this.notifyDataSetChanged();
    }






    static class ViewHolder{
        TextView textView;
        public ViewHolder(View view){
            textView=(TextView)view.findViewById(R.id.suggestionText);
        }
    }


}
