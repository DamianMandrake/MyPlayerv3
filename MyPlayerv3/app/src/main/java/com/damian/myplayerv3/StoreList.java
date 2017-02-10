package com.damian.myplayerv3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by damianmandrake on 2/10/17.
 */
public class StoreList {
    private ArrayList arrayList;

    private ObjectInputStream oin;
    private ObjectOutputStream oout;
    private File file;
    private boolean b;

    public StoreList(ArrayList a,String fileName){
        this(fileName);
        b=true;
        this.arrayList=a;
        System.out.println("The current storage dir is "+file.getAbsolutePath());

    }
    public StoreList(String fileName){
        this.file=new File(MainActivity.STORAGE_DIR,fileName);
        b=false;

    }

    public void writeArrayList(){
        if(b) {

            try {
                this.oout = new ObjectOutputStream(new FileOutputStream(this.file));

                this.oout.writeObject(this.arrayList);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (this.oout != null) {
                        oout.close();
                        oout = null;
                    }
                } catch (IOException in) {
                    in.printStackTrace();
                }
            }
        }
    }
    public ArrayList readArrayList() {
        if (!b) {
            try {
                this.oin = new ObjectInputStream(new FileInputStream(this.file));

                arrayList = (ArrayList) this.oin.readObject();

            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            } catch (IOException ii) {
                ii.printStackTrace();
            } finally {
                try {
                    if (this.oin != null) {
                        oin.close();
                        oin = null;
                    }
                } catch (IOException in) {
                    in.printStackTrace();
                }
            }
            return arrayList;


        }
        return null;

    }




}
