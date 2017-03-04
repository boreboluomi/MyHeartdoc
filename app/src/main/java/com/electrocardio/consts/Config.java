package com.electrocardio.consts;

/**
 * @author seven
 * @date 2014年6月6日
 * @time 下午2:37:26
 * @contact seven.com
 */
public class Config {

    /**
     * 发布模式
     * <p/>
     * 设置true，将环境设置为产品发布打包模式
     */
    public static boolean RELEASE = true;

    /**
     * HaodfApplication  oncreate() 中设置了false
     *
     * @param rELEASE （true为线上发布版本、false线下开发版本）
     */
    public static void setRELEASE(boolean rELEASE) {
        RELEASE = rELEASE;
    }


}
