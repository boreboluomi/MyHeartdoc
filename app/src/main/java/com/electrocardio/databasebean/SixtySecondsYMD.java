package com.electrocardio.databasebean;

/**
 * Created by ZhangBo on 2016/03/22.
 */
public class SixtySecondsYMD {

    private int yearAndMonth = -1;// 年和月
    private int day = -1;// 日

    public int getYearAndMonth() {
        return yearAndMonth;
    }

    public void setYearAndMonth(int yearAndMonth) {
        this.yearAndMonth = yearAndMonth;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "SixtySecondsYMD{" +
                "yearAndMonth=" + yearAndMonth +
                ", day=" + day +
                '}';
    }

    public SixtySecondsYMD() {
    }
}
