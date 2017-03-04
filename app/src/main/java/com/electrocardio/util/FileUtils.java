package com.electrocardio.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzheng on 2016/2/25.
 */
public class FileUtils {
    private static ArrayList<String> list = new ArrayList<String>();
    private static File file = null;
    public static void writeFileToSD(float[] array, String fileName) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return;
        }
        try {
            String pathName = "/sdcard/test/";
            // String fileName="file2.txt";
            File path = new File(pathName);
            File file = new File(pathName + fileName + ".txt");
            if (!path.exists()) {
                Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(file);
            // String s = "this is a test string writing to file11111.";
            for (int i = 0; i < array.length; i++) {
                String s = String.valueOf(array[i]);
                byte[] buf = s.getBytes();
                stream.write(buf);
                byte[] newLine = "\r\n".getBytes();
                stream.write(newLine);
            }

            stream.close();

        } catch (Exception e) {
            Log.e("TestFile", "Error on writeFilToSD.");
            e.printStackTrace();
        }
    }

    public static List<String> getFile(String path) {
        try {
            file = new File(path);
            if (file.exists() && file.isDirectory()) {// 检查path是否存在,并且是一个目录
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        list.add(f.getPath());

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

//遍历集合
    public  static void writeArrayToSD(List mlist, String fileName) {
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return;
        }
        try {
            String pathName="/sdcard/test1/";
            // String fileName="file2.txt";
            File path = new File(pathName);
            File file = new File(pathName + fileName+".txt");
            if( !path.exists()) {
                Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if( !file.exists()) {
                Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(file);
            // String s = "this is a test string writing to file11111.";
            for(int i=0;i<mlist.size();i++){
                String s = String.valueOf(mlist.get(i));
                byte[] buf =s.getBytes();
                stream.write(buf);
                byte []newLine="\r\n".getBytes();
                stream.write(newLine);
            }

            stream.close();

        } catch(Exception e) {
            Log.e("TestFile", "Error on writeFilToSD.");
            e.printStackTrace();
        }
    }
}
