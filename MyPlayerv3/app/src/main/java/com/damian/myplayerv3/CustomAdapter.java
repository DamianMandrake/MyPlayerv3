package com.damian.myplayerv3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by damianmandrake on 2/18/17.
 */
public class CustomAdapter extends BaseAdapter {

    private ArrayList<Song> arrayList;
    private int resourceId;
    private LayoutInflater layoutInflater;
    public CustomAdapter(ArrayList<Song> songs,Context context,int resId){
        this.arrayList=songs;
        this.resourceId=resId;
        this.layoutInflater=LayoutInflater.from(context);
        System.out.println("LEAVING CTOR OF CUSTOM ADAPTER");
    }



    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    @Override
    public Song getItem(int i) {
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

        viewHolder.textView.setText(this.getItem(i).getTitle());
        System.out.println(this.getItem(i).getTitle());

        return row;
    }

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
