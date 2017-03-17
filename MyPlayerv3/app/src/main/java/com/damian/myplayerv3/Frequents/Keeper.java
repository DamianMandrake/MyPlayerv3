package com.damian.myplayerv3.Frequents;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by damianmandrake on 3/17/17.
 */
public class Keeper {

    public static String getMd5(Object x){
        try {
            byte arr[] = x.toString().getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte out[]=messageDigest.digest(arr);
            String bigInteger=new String(out);
            System.out.println("HASH I S "+bigInteger.toString());
            return bigInteger;

        }catch (NoSuchAlgorithmException no){
            no.printStackTrace();
        }
        return null;
    }



}
