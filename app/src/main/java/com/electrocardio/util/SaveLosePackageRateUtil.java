package com.electrocardio.util;

import android.os.Environment;

import com.electrocardio.base.BaseApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/3/10.
 */
public class SaveLosePackageRateUtil {

    private static long currentTimemi = 0;
    private String currentFile = "";

    private boolean isFirst = true;
    private ArrayList<String> listOne;// 第一个存数据的list
    private ArrayList<String> listTwo;// 第二个存数据的list
    private static SaveLosePackageRateUtil instance;

    private SaveLosePackageRateUtil() {
        listOne = new ArrayList<>();
        listTwo = new ArrayList<>();
    }

    public static SaveLosePackageRateUtil getInstance() {
        if (instance == null)
            instance = new SaveLosePackageRateUtil();
        return instance;
    }

    /**
     * 添加数据
     *
     * @param number
     */
    public void addNumber(String number) {
        if ((System.currentTimeMillis() - currentTimemi) > 60000) {
            currentFile = System.currentTimeMillis() + ".txt";
        }
        currentTimemi = System.currentTimeMillis();
        if (isFirst) {
            listOne.add(number);
            if (listOne.size() > 15) {
                isFirst = false;
                saveToFile(listOne);
            }
        } else {
            listTwo.add(number);
            if (listTwo.size() > 15) {
                isFirst = true;
                saveToFile(listTwo);
            }
        }
    }

    /**
     * 保存到文件
     *
     * @param list
     */
    private void saveToFile(final ArrayList<String> list) {
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String pathSrc = Environment.getExternalStorageDirectory() + "/AHeartDoc";
                File pathFile = new File(pathSrc);
                if (!pathFile.exists())
                    pathFile.mkdirs();
                File saveFile = new File(pathSrc, currentFile);
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(saveFile, true);
                    for (String data : list)
                        outputStream.write((data + "\t").getBytes());
                    if (isFirst)
                        listTwo.clear();
                    else
                        listOne.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (outputStream != null)
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        });
    }
}
