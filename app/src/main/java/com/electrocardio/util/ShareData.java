package com.electrocardio.util;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yangzheng on 13-10-14.
 */
public class ShareData {
    // 存储文件名称
    public static final String SHARE_FILE_USER = "SHARE_FILE_USER";
    
    // 存储是否启动app
    public static final String SHARE_FILE_START_GUIDE = "SHARE_FILE_START_GUIDE";

    //存储Token

    public  static final String SHARE_FILE_TOLEN="SHARE_FILE_TOLEN";



    /**
     * 存储手机ID
     *
     * @param context
     * @param value
     */
    public static void savePHONEDATE(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_USER, Context.MODE_PRIVATE);
        // 保存单个
        sp.edit().putString(key, value).commit();
    }

    public static void savePHONEDATE(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_USER, Context.MODE_PRIVATE);
        // 保存单个
        sp.edit().putInt(key, value).commit();
    }

    public static String getPHONEDATE(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_USER, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static int getPHONEDATEInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_USER, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public static void saveStartGuide(Context context, String key, Integer value)
    {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_START_GUIDE, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static int getStartGuide(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_START_GUIDE, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    /**
     * 删除
     */
    public static void removePHONEDATE(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_USER, Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

    /**
     * 清空
     */
    public static void clearPHONEDATE(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_USER, Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }




    public static void saveRegister(Context context, String key, Integer value)
    {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_START_GUIDE, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static int getRegister(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_START_GUIDE, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }



    public static void saveToken(Context context, String key, String value)
    {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_TOLEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();

    }

    public static String getToken(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_TOLEN, Context.MODE_PRIVATE);
        return sp.getString(key,null);
    }
    /**
     * 清空
     */
    public static void clearToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARE_FILE_TOLEN, Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }


}
