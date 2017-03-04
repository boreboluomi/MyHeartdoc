package com.electrocardio.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Md5 {
    /**
     * MD5
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    /**
     * 计算使用MD5加密的签名值
     *
     * @param pwd
     * @return
     */
    public static String MD5(String pwd) {
        StringBuffer signatureData = new StringBuffer(pwd);
        byte[] byteMD5 = encryptToMD5(signatureData.toString());
        //System.out.println("pwd:。。。。。。。。。。。。。。。。。。。"+pwd);
        String result = toHexString(byteMD5);
        //System.out.println("result:......"+result);
        return result;

    }

    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String toHexString(byte[] b) { // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static byte[] encryptToMD5(String data) {

        byte[] digestdata = null;
        try {
            // 得到一个md5的消息摘要
            MessageDigest alga = MessageDigest.getInstance(KEY_MD5);
            alga.update(data.getBytes());
            digestdata = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return digestdata;
    }

    private static final String KEY_MD5 = "MD5";

}
