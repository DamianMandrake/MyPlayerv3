package com.damian.myplayerv3.Streamer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by damianmandrake on 3/1/17.
 */
public class OutputStreamThread implements RequestWriter {

    private Thread t;
    private PrintWriter printWriter;
    public OutputStreamThread(Socket sokcet)throws IOException{
        this.printWriter=new PrintWriter(sokcet.getOutputStream(),true);
        StreamerRecyclerAdapter.StreamerViewHolder.requestWriter=this;

    }
    @Override
    public void writeRequest(String a){
        printWriter.println("{"+a+"}");
        System.out.println("req sent");
    }



}
