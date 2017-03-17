package com.damian.myplayerv3.Streamer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.damian.myplayerv3.Fragments.PlaylistFrag;
import com.damian.myplayerv3.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by damianmandrake on 3/1/17.
 * This class handles all inputs
 */
public class InputStreamThread implements Runnable,SetClickedFileName {


    //todo will have to write an interface whose callback inits a list or a file

    //todo- will have to write a class?/interface which will store the downloaded files


    private Socket socket;
    private Thread t;
    private static final String path="http://192.168.1.107/aud/";
    private DataInputStream dataInputStream;
    private String clickFileName;
    private StreamListAdapter streamListAdapter;
    ArrayList<String> arr;

    public static PlayFromUrl playFromUrl;

    public InputStreamThread(Socket source,StreamListAdapter streamListAdapter){
        this.socket=source;
        this.arr=new ArrayList<>();
        this.streamListAdapter=streamListAdapter;
        StreamerRecyclerAdapter.StreamerViewHolder.clickedFileName=this;
        this.t=new Thread(this);
        t.start();
    }



    @Override
    public void run(){


        try {
            this.dataInputStream = new DataInputStream(this.socket.getInputStream());
            System.out.println("isr started");
            while (true) {

                switch (dataInputStream.readInt()) {
                    case 0://receiving list to be read
                        System.out.println("receiveed 0");
                        this.arr=this.readList();
                        this.streamListAdapter.setStreamListAdapter(arr);
                        System.out.println("list received size is "+arr.size());
                        //todo call interface which sets recycler adapter's list

                        break;

                    case 1://receiving file to be stored
                        System.out.println("receiveed 1");

                        this.readFile();
                        System.out.println("file received");//called only when a file is clicked and server responds in kind

                        break;


                }
            }


            }catch(IOException ioe){
                ioe.printStackTrace();
            }




    }

    private ArrayList<String> readList()throws IOException{
        int size=this.dataInputStream.readInt();
         //this.jsonObject=new JSONObject[size];
        ArrayList <String>arrayList=new ArrayList<>();
        for(int i=0;i<size;i++){
            arrayList.add(dataInputStream.readUTF());


            /*try{
                 this.jsonObject[i]=new JSONObject(this.dataInputStream.readUTF());


            }catch (JSONException js){
                js.printStackTrace();
                System.out.println("********>cant read the json being sent<**********");
            }*/


        }
        return arrayList;
    }

    private void readFile()throws IOException{
        File readFile;

        //BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        System.out.println("DOWNLOADS DIR "+MainActivity.DOWNLOADS_DIRECTORY);
        File f=new File(MainActivity.DOWNLOADS_DIRECTORY,this.clickFileName.trim());

        if(!f.exists()) {

            System.out.println("file could be made " + f.createNewFile());
            System.out.println("can execute "+f.setExecutable(true,false));
            System.out.println("can read "+f.setReadable(true,false));
            System.out.println("can write "+f.setWritable(true,false));
        }

        int arraySize=this.dataInputStream.readInt();
        byte arr[]=new byte[arraySize];
        this.dataInputStream.readFully(arr);

        FileOutputStream fout= new FileOutputStream((f));//todo get file name from onClick
        fout.write(arr);
        fout.flush();
        fout.close();


        /*System.out.println("canonical path is " + f.getCanonicalPath());
        System.out.println("absolute path is " + f.getAbsolutePath());
        try {
            Process p = Runtime.getRuntime().exec("chmod 755 " + MainActivity.DOWNLOADS_DIRECTORY +"" + f.getName());
            p.waitFor();
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
        System.out.println("rename was " + f.renameTo(f));*/

        MainActivity.toast("file " + f.getName() + "downloaded");
        MediaScannerConnection.scanFile(MainActivity.context, new String[]{f.getPath()}, new String[]{"mp3/*", "flac/*", "wav/*"}, null);//adds the song to the contentResolver db

        /*try {
            MediaPlayer mediaPlayer=new MediaPlayer();
            this.clickFileName=this.clickFileName.replace(" ","%20");
            String t=InputStreamThread.path + "" + this.clickFileName;
            System.out.println(t);

            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    System.out.println("buffer progress " + i);
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    System.out.println("starting ");
                    mediaPlayer.start();


                }
            });


            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(t);
            mediaPlayer.reset();

            mediaPlayer.prepareAsync();

            System.out.println("leaving func");


        }catch (Exception e){
            e.printStackTrace();

        }*/


    }


    @Override
    public void setClickFileName(int pos){
        if(this.arr!=null) {
            this.clickFileName = this.arr.get(pos);
            /*byte arr[]=new byte[this.clickFileName.length()];
            for(int j=0;j<arr.length;j++){
                char ch= clickFileName.charAt(j);
                arr[j]= ch<=255?(byte)ch: (byte)'?';

            }

            try {
                this.clickFileName = new String(arr);
                MediaPlayer mediaPlayer =MediaPlayer.create(MainActivity.context, Uri.parse("https://"+ClientSocket.ip+""+this.jsonObject[pos].get("path").toString()));
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
            }//catch(IOException ioe){
               // ioe.printStackTrace();System.out.println("ioExcpetion was generated");}
            catch(JSONException js){
                js.printStackTrace();System.out.println("json couldnt be read for some reason");
            }*/


        }
    }


}
