package com.electrocardio.databasebean;

/**
 * Created by ZhangBo on 2016/03/22.
 */
public class BreathRateStandard {

    public static final int FAST = 1;// 过快
    public static final int NORMAL = 2;// 正常
    public static final int SLOW = 3;// 过慢

    public BreathRateStandard() {
    }

    /**
     * 判断呼吸率率水平
     *
     * @param breathRate
     * @return
     */
    public int judgeState(int breathRate) {
        if (breathRate >= 24)
            return FAST;
        else if (breathRate >= 12)
            return NORMAL;
        else
            return SLOW;
    }

}
