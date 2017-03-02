package com.damian.myplayerv3.AdaptersAndListeners;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.damian.myplayerv3.MainActivity;

/**
 * Created by damianmandrake on 2/28/17.
 */
public class RecyclerScrollListener extends RecyclerView.OnScrollListener {

    private RecyclerView targetView;
    public RecyclerScrollListener(RecyclerView recyclerView){
    this.targetView=recyclerView;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        System.out.println("dx dy" + dx + "" + dy);
        int t=-recyclerView.getTop();
        System.out.println("translation is "+t);
        this.targetView.setTranslationY(MainActivity.SCREEN_HEIGHT-dy);

        //negative dy is scroll upwards
        //postive dy is scroll downwards

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        System.out.println("in on scrollstatechanged");
     }
}
