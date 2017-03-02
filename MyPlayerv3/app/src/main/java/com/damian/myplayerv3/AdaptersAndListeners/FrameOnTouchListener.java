package com.damian.myplayerv3.AdaptersAndListeners;

import android.view.MotionEvent;
import android.view.View;

import com.damian.myplayerv3.Fragments.MusicControllerFragment;
import com.damian.myplayerv3.MainActivity;

/**
 * Created by damianmandrake on 2/24/17.
 */
public class FrameOnTouchListener implements View.OnTouchListener{
    private MusicControllerFragment musicControllerFragment;
    private float previousY,trans,diff;
    ToggleNavDrawerDrawable navDrawerDrawable;

    public FrameOnTouchListener(MusicControllerFragment musicControllerFragment){
        this.musicControllerFragment=musicControllerFragment;
        this.trans=this.diff=0;

    }
    public void setNavDrawerDrawable(ToggleNavDrawerDrawable tog){
        this.navDrawerDrawable=tog;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                previousY=motionEvent.getY();

                return true;

            case MotionEvent.ACTION_MOVE:
                this.diff=previousY-motionEvent.getY();
                this.trans=view.getTranslationY();

                this.trans-=this.diff;

                this.trans= this.trans<0?0:(this.trans> MainActivity.MAX_TRANSLATION? MainActivity.MAX_TRANSLATION: this.trans);

                view.setTranslationY(trans);



                return true;

            case MotionEvent.ACTION_UP:
                if(this.diff<0){
                    System.out.println("Diff is less than 0");
                    view.setTranslationY(MainActivity.MAX_TRANSLATION);
                    musicControllerFragment.smallPlayPause.setVisibility(View.VISIBLE);
                    this.navDrawerDrawable.toggleNavDrawerDrawable(true);
                }else if(this.diff>0){
                    System.out.println("Diff is greater than 0");

                    view.setTranslationY(0);
                    musicControllerFragment.smallPlayPause.setVisibility(View.INVISIBLE);
                    this.navDrawerDrawable.toggleNavDrawerDrawable(false);


                }
                return true;


        }



        return view.onTouchEvent(motionEvent);
    }



    public interface ToggleNavDrawerDrawable{
        public void toggleNavDrawerDrawable(boolean b);

    }
}
