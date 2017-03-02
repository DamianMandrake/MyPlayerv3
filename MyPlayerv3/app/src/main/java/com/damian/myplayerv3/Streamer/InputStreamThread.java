package com.damian.myplayerv3.Streamer;

import com.damian.myplayerv3.MainActivity;

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
    private DataInputStream dataInputStream;
    private String clickFileName;
    private StreamListAdapter streamListAdapter;
    ArrayList<String> arr;
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
        ArrayList <String>arrayList=new ArrayList<>();
        for(int i=0;i<size;i++){
            arrayList.add(dataInputStream.readUTF());
        }
        return arrayList;
    }

    private void readFile()throws IOException{
        File readFile;

        //BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        File f=new File(MainActivity.DOWNLOADS_DIRECTORY,"audfm/"+this.clickFileName.trim());

        if(!f.exists())
            System.out.println("file could be made "+f.createNewFile());

        int arraySize=this.dataInputStream.readInt();
        byte arr[]=new byte[arraySize];
        dataInputStream.readFully(arr);

        FileOutputStream fout= new FileOutputStream((f));//todo get file name from onClick
        fout.write(arr);//hoping this is enough

        MainActivity.toast("file downloaded");




    }


    @Override
    public void setClickFileName(int pos){
        if(this.arr!=null)this.clickFileName=this.arr.get(pos);
    }


}
