package com.electrocardio.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanghzeng on 2016/3/28.
 * 手机号正则验证
 */
public  class MobileNo {

    public static boolean isMobileNO(String mobiles){

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }




    public static boolean isMobileCode(String mobiles){

        Pattern pattern = Pattern.compile("(\\d{6})");

        Matcher m = pattern.matcher(mobiles);

        return m.matches();

    }
}
