package com.ztesoft.sca.common;

import org.apache.log4j.Logger;

import java.io.*;

public class Byte2ObjectUtil {
    protected static final Logger log = Logger.getLogger(Byte2ObjectUtil.class);

    public static Object byteToObject(byte[] bytes) {
        Object object = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            object = ois.readObject();
            return object;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static byte[] objectToByte(Object obj) {

        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos =null;;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }finally{
            if(oos!=null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bos!=null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args){
        String str = "abc";
        byte[] a = Byte2ObjectUtil.objectToByte(str);
        System.out.println(Byte2ObjectUtil.byteToObject(a));

        System.out.println(Integer.MAX_VALUE);
    }

}
