package com.electrocardio.databasebean;

/**
 * Created by ZhangBo on 2016/03/22.
 */
public class HeartRateStandard {

    public static final int FAST = 1;// 过快
    public static final int FASTNORMAL = 2;// 稍快
    public static final int NORMAL = 3;// 正常
    public static final int SLOWNORMAL = 4;// 稍慢
    public static final int SLOW = 5;// 过慢

    public HeartRateStandard() {
    }

    /**
     * 判断心率水平
     *
     * @param heartRate
     * @return
     */
    public int judgeState(int heartRate) {
        if (heartRate >= 120)
            return FAST;
        else if (heartRate >= 100)
            return FASTNORMAL;
        else if (heartRate >= 60)
            return NORMAL;
        else if (heartRate >= 50)
            return SLOWNORMAL;
        else
            return SLOW;
    }
}
