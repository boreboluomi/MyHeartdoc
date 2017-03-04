package com.electrocardio.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yangzheng on 2016/4/19.
 */
public  class TimeUtils {

    public static String getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");

        Date curDate = new Date(System.currentTimeMillis());//获取当前时间

        String str1 = formatter.format(curDate);

        return str1;
    }
}
