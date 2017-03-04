package com.electrocardio.databasebean;

/**
 * Created by ZhangBo on 2016/03/28.
 */
public class AbnormalSignInfor {

    public static final int UNIT_LENGTH = 325;// 单位时间数据长度

    public static final int MARK = 0;// 手动截取
    public static final int SIN_TACHY = 1;// 窦性心动过速
    public static final int SIN_BRADY = 2;// 窦性心动过缓
    public static final int SINGLE_VEN = 3;// 单发室早
    public static final int PAIR_VEN = 4;// 成对室早
    public static final int VEN_TACHY = 5;// 室速
    public static final int VEB_BI = 6;// 室早二联律
    public static final int VEB_TRI = 7;// 室早三联律
    public static final int SINGLE_ATR = 8;// 单发房早
    public static final int PAIR_ATR = 9;// 成对房早
    public static final int ATR_TACHY = 10;// 房速
    public static final int APB_BI = 11;// 房早二联律
    public static final int APB_TRI = 12;// 房早三联律
    public static final int PAUSE = 13;// 漏搏
    public static final int AFRIL = 14;// 房颤
    public static final int START = 15;// 开始测量
    public static final int TIMERCUT = 20;// 定时截取

    public static int MARK_CACHE = 1625;// 截取数据缓存长度
    public static int SIN_TACHY_CACHE = 1625;// 窦性心动过速缓存长度
    public static int SIN_BRADY_CACHE = 1625;// 窦性心动过缓缓存长度
    public static int SINGLE_VEN_CACHE = 650;// 单发室早缓存长度
    public static int PAIR_VEN_CACHE = 1625;// 成对室早缓存长度
    public static int VEN_TACHY_CACHE = 1625;// 室速缓存长度
    public static int VEB_BI_CACHE = 1625;// 室早二联律缓存长度
    public static int VEB_TRI_CACHE = 1625;// 室早三联律缓存长度
    public static int SINGLE_ATR_CACHE = 650;// 单发房早缓存长度
    public static int PAIR_ATR_CACHE = 1625;// 成对房早缓存长度
    public static int ATR_TACHY_CACHE = 1625;// 房速缓存长度
    public static int APB_BI_CACHE = 1625;// 房早二联律缓存长度
    public static int APB_TRI_CACHE = 1625;// 房早三联律缓存长度
    public static int PAUSE_CACHE = 1625;// 漏搏缓存长度
    public static int AFRIL_CACHE = 1625;// 房颤缓存长度
    public static int START_CACHE = 0;// 开始测量缓存长度
    public static int TIMERCUT_CACHE = 1625;// 定时截取缓存长度

    public static int MARK_LENGTH = 6500;// 截取数据长度
    public static int SIN_TACHY_LENGTH = 6500;// 窦性心动过速长度
    public static int SIN_BRADY_LENGTH = 6500;// 窦性心动过缓长度
    public static int SINGLE_VEN_LENGTH = 1300;// 单发室早长度
    public static int PAIR_VEN_LENGTH = 6500;// 成对室早长度
    public static int VEN_TACHY_LENGTH = 6500;// 室速长度
    public static int VEB_BI_LENGTH = 6500;// 室早二联律长度
    public static int VEB_TRI_LENGTH = 6500;// 室早三联律长度
    public static int SINGLE_ATR_LENGTH = 1300;// 单发房早长度
    public static int PAIR_ATR_LENGTH = 6500;// 成对房早长度
    public static int ATR_TACHY_LENGTH = 6500;// 房速长度
    public static int APB_BI_LENGTH = 6500;// 房早二联律长度
    public static int APB_TRI_LENGTH = 6500;// 房早三联律长度
    public static int PAUSE_LENGTH = 6500;// 漏搏长度
    public static int AFRIL_LENGTH = 6500;// 房颤长度
    public static int START_LENGTH = 6500;// 开始测量时截取数据长度
    public static int TIMERCUT_LENGTH = 6500;// 定时截取数据长度

    /**
     * 获取缓存长度
     *
     * @param type
     * @return
     */
    public static int getCacheLength(int type) {
        int length = -1;
        switch (type) {
            case MARK:// 手动截取
                length = MARK_CACHE;
                break;
            case SIN_TACHY:// 窦性心动过速
                length = SIN_TACHY_CACHE;
                break;
            case SIN_BRADY:// 窦性心动过缓
                length = SIN_BRADY_CACHE;
                break;
            case SINGLE_VEN:// 单发室早
                length = SINGLE_VEN_CACHE;
                break;
            case PAIR_VEN:// 成对室早
                length = PAIR_VEN_CACHE;
                break;
            case VEN_TACHY:// 室速
                length = VEN_TACHY_CACHE;
                break;
            case VEB_BI:// 室早二联律
                length = VEB_BI_CACHE;
                break;
            case VEB_TRI:// 室早三联律
                length = VEB_TRI_CACHE;
                break;
            case SINGLE_ATR:// 单发房早
                length = SINGLE_ATR_CACHE;
                break;
            case PAIR_ATR:// 成对房早
                length = PAIR_ATR_CACHE;
                break;
            case ATR_TACHY:// 房速
                length = ATR_TACHY_CACHE;
                break;
            case APB_BI:// 房早二联律
                length = APB_BI_CACHE;
                break;
            case APB_TRI:// 房早三联律
                length = APB_TRI_CACHE;
                break;
            case PAUSE:// 漏搏
                length = PAUSE_CACHE;
                break;
            case AFRIL:// 房颤
                length = AFRIL_CACHE;
                break;
            case START:// 开始测量
                length = START_CACHE;
                break;
            case TIMERCUT:// 定时截取
                length = TIMERCUT_CACHE;
                break;
        }
        return length;
    }

    /**
     * 获取所有数据长度
     *
     * @param type
     * @return
     */
    public static int getTotalLength(int type) {
        int length = -1;
        switch (type) {
            case MARK:// 手动截取
                length = MARK_LENGTH;
                break;
            case SIN_TACHY:// 窦性心动过速
                length = SIN_TACHY_LENGTH;
                break;
            case SIN_BRADY:// 窦性心动过缓
                length = SIN_BRADY_LENGTH;
                break;
            case SINGLE_VEN:// 单发室早
                length = SINGLE_VEN_LENGTH;
                break;
            case PAIR_VEN:// 成对室早
                length = PAIR_VEN_LENGTH;
                break;
            case VEN_TACHY:// 室速
                length = VEN_TACHY_LENGTH;
                break;
            case VEB_BI:// 室早二联律
                length = VEB_BI_LENGTH;
                break;
            case VEB_TRI:// 室早三联律
                length = VEB_TRI_LENGTH;
                break;
            case SINGLE_ATR:// 单发房早
                length = SINGLE_ATR_LENGTH;
                break;
            case PAIR_ATR:// 成对房早
                length = PAIR_ATR_LENGTH;
                break;
            case ATR_TACHY:// 房速
                length = ATR_TACHY_LENGTH;
                break;
            case APB_BI:// 房早二联律
                length = APB_BI_LENGTH;
                break;
            case APB_TRI:// 房早三联律
                length = APB_TRI_LENGTH;
                break;
            case PAUSE:// 漏搏
                length = PAUSE_LENGTH;
                break;
            case AFRIL:// 房颤
                length = AFRIL_LENGTH;
                break;
            case START:// 开始测量
                length = START_LENGTH;
                break;
            case TIMERCUT:// 定时截取
                length = TIMERCUT_LENGTH;
                break;
        }
        return length;
    }

    /**
     * 获取异常数据描述
     *
     * @param type
     * @return
     */
    public static String getStateDescription(int type) {
        String description = "";
        switch (type) {
            case MARK:// 手动截取
                description = "手动截取";
                break;
            case SIN_TACHY:// 窦性心动过速
                description = "窦性心动过速";
                break;
            case SIN_BRADY:// 窦性心动过缓
                description = "窦性心动过缓";
                break;
            case SINGLE_VEN:// 单发室早
                description = "单发室早";
                break;
            case PAIR_VEN:// 成对室早
                description = "成对室早";
                break;
            case VEN_TACHY:// 室速
                description = "室速";
                break;
            case VEB_BI:// 室早二联律
                description = "室早二联律";
                break;
            case VEB_TRI:// 室早三联律
                description = "室早三联律";
                break;
            case SINGLE_ATR:// 单发房早
                description = "单发房早";
                break;
            case PAIR_ATR:// 成对房早
                description = "成对房早";
                break;
            case ATR_TACHY:// 房速
                description = "房速";
                break;
            case APB_BI:// 房早二联律
                description = "房早二联律";
                break;
            case APB_TRI:// 房早三联律
                description = "房早三联律";
                break;
            case PAUSE:// 漏搏
                description = "漏搏";
                break;
            case AFRIL:// 房颤
                description = "房颤";
                break;
            case START:// 开始测量
                description = "开始截取";
                break;
            case TIMERCUT:// 定时截取
                description = "定时截取";
                break;
        }
        return description;
    }
}
