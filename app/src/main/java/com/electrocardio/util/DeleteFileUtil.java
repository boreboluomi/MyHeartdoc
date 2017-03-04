package com.electrocardio.util;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by ZhangBo on 2016/03/25.删除文件的工具类
 */
public class DeleteFileUtil {

    /**
     * 根据路径删除单个文件
     *
     * @param fileSrc
     * @return
     */
    public boolean deleteFile(String fileSrc) {
        boolean flag = false;
        File file = new File(fileSrc);
        if (file.exists() && file.isFile()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除文件夹以及文件夹中的所有文件
     *
     * @param fileSrc
     * @return
     */
    public boolean deleteDirectory(String fileSrc) {
        // 如果fileSrc不宜文件分隔符结尾，自动添加文件分隔符
        if (!fileSrc.endsWith(File.separator)) {
            fileSrc += File.separator;
        }
        File dirFile = new File(fileSrc);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下色所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } else { // 删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        if (dirFile.delete())
            return true;
        else
            return false;
    }

    /**
     * 获取指定文件大小
     *
     * @param fileSrc
     * @return
     */
    public static int getFileSize(String fileSrc) {
        File file = new File(fileSrc);
        if (file.exists() && file.isFile())
            return (int) (file.length() / 1024);
        else
            return 0;
    }

    /**
     * 获取指定文件大小单位为byte
     *
     * @param fileSrc
     * @return
     */
    public static long getFileSizeByByte(String fileSrc) {
        File file = new File(fileSrc);
        long length = file.length();
        return length;
//        int length = 0;
//        if (file.exists() && file.isFile()) {
//            InputStream inputStream = null;
//            try {
//                inputStream = new FileInputStream(file);
//                length = inputStream.available();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (inputStream != null)
//                    try {
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//            }
//            return length;
//        } else
//            return length;
    }

    /**
     * 对文件进行重命名
     *
     * @param path
     * @param fromName
     * @param toName
     * @return
     */
    public static boolean renameFile(String path, String fromName, String toName) {
        File fromFile = new File(path, fromName);
        File toFile = new File(path, toName);
        return fromFile.renameTo(toFile);
    }

    /**
     * 获取可用空间尺寸
     *
     * @return
     */
    public static String getAvaiableSize() {
        String size;
        File path = Environment.getExternalStorageDirectory();
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        StatFs stat = new StatFs(path.getPath());
        long availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            availableBlocks = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        else
            availableBlocks = stat.getAvailableBlocks() * stat.getBlockSize();
        if (availableBlocks < 1048576) {
            size = decimalFormat.format(availableBlocks / 1024d) + "K";
        } else {
            if (availableBlocks < 1073741824)
                size = decimalFormat.format(availableBlocks / 1048576d) + "M";
            else {
                size = decimalFormat.format(availableBlocks / 1073741824d) + "G";
            }
        }
        return size;
    }

    /**
     * 获取可用空间尺寸
     *
     * @return
     */
    public static float getAvaiableSizeByF() {
        float size = 0;
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            availableBlocks = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        else
            availableBlocks = stat.getAvailableBlocks() * stat.getBlockSize();
        size = availableBlocks / 1024;
        return size;
    }

    /**
     * 获取已经占用的空间的大小
     *
     * @param sizeK
     * @return
     */
    public static String getOccupySize(float sizeK) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String size;
        if (sizeK < 1024f)
            size = sizeK + "K";
        else if (sizeK < 1048576f)
            size = decimalFormat.format(sizeK / 1024f) + "M";
        else
            size = decimalFormat.format(sizeK / 1048576f) + "G";
        return size;
    }
}
