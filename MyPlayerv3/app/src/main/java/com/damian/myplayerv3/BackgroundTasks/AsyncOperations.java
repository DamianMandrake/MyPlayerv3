package com.damian.myplayerv3.BackgroundTasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;

import com.damian.myplayerv3.MainActivity;

/**
 * Created by damianmandrake on 3/4/17.
 */
public class AsyncOperations extends AsyncTask<Void,Void,Object> {


    //op of doingBackground becomes ip of postexecute
    //params of doinbg is the first parameter in the generic
    //params of onProgressUpdate is second parameter in generic type
    //return type of doInBackground is the third parameter in the async operation
    /*
    function publishProgress can be used to call progress onProgressUpdate(Progress... varargs)


     */
    private DoStuffInBg doStuffInBackground=null;
    private DoStuffInPost doStuffInPostExecute=null;
    private DoStuffInPre doStuffInPreExecute=null;
    private Handler ref;
    //int handlerDelay;


    //private boolean showProgress;wont need showProgress ... if the message string is null it means that the progress dialog doesnt have to beshown



    private ProgressDialog progressDialog;

    public AsyncOperations(DoStuffInPre doStuffInPreExecute,DoStuffInPost doStuffInPostExecute,DoStuffInBg doStuffInBackground,Runnable handlerRunnable,int handlerDelay,String progressDialogMessage){
        this.doStuffInPreExecute=doStuffInPreExecute;
        this.doStuffInPostExecute=doStuffInPostExecute;
        this.doStuffInBackground=doStuffInBackground;

        if(handlerRunnable!=null) {
            this.ref = new Handler();
            this.ref.postDelayed(handlerRunnable, handlerDelay);
        }

        if(progressDialogMessage!=null){
            this.progressDialog=new ProgressDialog(MainActivity.context);
            this.progressDialog.setInverseBackgroundForced(false);
            this.progressDialog.setCancelable(false);
            this.progressDialog.setMessage(progressDialogMessage);
        }else
            this.progressDialog=null;




    }





    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(this.progressDialog!=null)this.progressDialog.show();
        if(this.doStuffInPreExecute!=null)this.doStuffInPreExecute.doStuffInPre();
    }



    @Override
    protected Object doInBackground(Void... paramses) {

        Object x=null;
        if(this.doStuffInBackground!=null)x=this.doStuffInBackground.doStuff();



        return x;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if(this.progressDialog!=null)this.progressDialog.dismiss();

        if(this.doStuffInPostExecute!=null)this.doStuffInPostExecute.doInPost(result);
    }
}
