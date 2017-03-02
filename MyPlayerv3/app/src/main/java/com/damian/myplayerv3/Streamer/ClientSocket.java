package com.damian.myplayerv3.Streamer;

import com.damian.myplayerv3.MainActivity;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by damianmandrake on 3/1/17.
 */
public class ClientSocket implements Runnable {

    //// TODO: 3/1/17 this class handles all interaction with the clients data source
    // todo have to add an interface which will send request to the server




    private String ip;
    static int port=4444;
    private Thread thread;
    private InputStreamThread inputStreamThread;
    private OutputStreamThread outputStreamThread;
    private StreamListAdapter streamListAdapter;
    public ClientSocket(String ip,StreamListAdapter listAdapter){
        this.thread=new Thread(this);
        this.thread.start();
        this.ip=ip;
        this.streamListAdapter=listAdapter;
        System.out.println("IP IS " + ip);
        System.out.println("192.168.1.104".compareTo(ip));

    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(this.ip, port);
            System.out.println("Established");
            outputStreamThread=new OutputStreamThread(socket);
            outputStreamThread.writeRequest("arraylist");

            this.inputStreamThread=new InputStreamThread(socket,this.streamListAdapter);

            System.out.println("leaving run");
        }catch (IOException ukw){
            MainActivity.toast("there was some issue in your input");
            ukw.printStackTrace();
        }
    }
}
