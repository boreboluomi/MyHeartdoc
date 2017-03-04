package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.util.ConstantUtils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/2/24.
 */
public class SelectBirthdayPopupWindow extends PopupWindow {
    private TextView tv_confirm;
    private View mMenuView;
    private SelectListener mSelectListener;
    private ArrayList<String> bigMonth;// 大月份
    private ArrayList<String> smallMonth;// 小月份
    private boolean Chiness = false;

    public void setSelectListener(SelectListener selectListener) {
        mSelectListener = selectListener;
    }

    public void setChiness(boolean flag) {
        Chiness = flag;
    }

    public SelectBirthdayPopupWindow(final Context context) {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
        this(context, -1, -1, -1);
    }

    public SelectBirthdayPopupWindow(final Context context, int selectYear, int selectMonth, int selectDay) {
        bigMonth = new ArrayList<>();
        bigMonth.add("01");
        bigMonth.add("03");
        bigMonth.add("05");
        bigMonth.add("07");
        bigMonth.add("08");
        bigMonth.add("10");
        bigMonth.add("12");
        smallMonth = new ArrayList<>();
        smallMonth.add("04");
        smallMonth.add("06");
        smallMonth.add("09");
        smallMonth.add("11");

        mMenuView = View.inflate(context, R.layout.birthdayselectrow, null);
        tv_confirm = (TextView) mMenuView.findViewById(R.id.tv_sure);
        // 设置view
        this.setContentView(mMenuView);
        // 设置窗体弹出的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置窗体弹出的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置窗体可点击
        this.setFocusable(true);
        // 设置窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置窗体的背景
        this.setBackgroundDrawable(dw);
        // 年集合
        ArrayList<String> yearList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        if (selectYear == -1)
            selectYear = calendar.get(Calendar.YEAR);
        if (selectMonth == -1)
            selectMonth = calendar.get(Calendar.MONTH) + 1;
        if (selectDay == -1)
            selectDay = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1900; i <= calendar.get(Calendar.YEAR); i++) {
            yearList.add(i + "");
        }
        // 月集合
        ArrayList<String> monthList = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            if (i < 10)
                monthList.add("0" + i);
            else
                monthList.add(i + "");
        }
        // 大月天数集合
        final ArrayList<String> bigMonthDayList = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            if (i < 10)
                bigMonthDayList.add("0" + i);
            else
                bigMonthDayList.add(i + "");
        }
        // 小月天数集合
        final ArrayList<String> smallMonthDayList = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            if (i < 10)
                smallMonthDayList.add("0" + i);
            else
                smallMonthDayList.add(i + "");
        }
        // 平二月天数集合
        final ArrayList<String> erPMonthDayList = new ArrayList<>();
        for (int i = 1; i <= 28; i++) {
            if (i < 10)
                erPMonthDayList.add("0" + i);
            else
                erPMonthDayList.add(i + "");
        }
        // 闰二月天数集合
        final ArrayList<String> erRMonthDayList = new ArrayList<>();
        for (int i = 1; i <= 29; i++) {
            if (i < 10)
                erRMonthDayList.add("0" + i);
            else
                erRMonthDayList.add(i + "");
        }

        final PapersPicker yearPapersPicker = (PapersPicker) mMenuView
                .findViewById(R.id.year_paperspicker);
        final PapersPicker monthPapersPicker = (PapersPicker) mMenuView
                .findViewById(R.id.month_paperspicker);
        final PapersPicker dayPapersPicker = (PapersPicker) mMenuView
                .findViewById(R.id.day_paperspicker);

        yearPapersPicker.setList(yearList, selectYear - 1900);
        monthPapersPicker.setList(monthList, selectMonth);
        dayPapersPicker.setList(bigMonthDayList, selectDay);

        // 年变化时的监听
        yearPapersPicker
                .setPapersPickerTextChangeListener(new PapersPicker.PapersPickerTextChangeListener() {
                    @Override
                    public void onTextChanged(String text) {
                        Integer year = Integer.parseInt(text);
                        if (ConstantUtils.isRunNian(year)) {
                            if ("02".equals(monthPapersPicker.getPapers())) {
                                int currentId = dayPapersPicker
                                        .getCurrentPapersIndex();
                                dayPapersPicker.setList(erRMonthDayList,
                                        currentId);
                            }
                        } else {
                            if ("02".equals(monthPapersPicker.getPapers())) {
                                int currentId = dayPapersPicker
                                        .getCurrentPapersIndex();
                                if (currentId == 28) {
                                    currentId = 27;
                                }
                                dayPapersPicker.setList(erPMonthDayList,
                                        currentId);
                            }
                        }
                    }
                });
        // 月变化时的监听
        monthPapersPicker
                .setPapersPickerTextChangeListener(new PapersPicker.PapersPickerTextChangeListener() {
                    @Override
                    public void onTextChanged(String text) {
                        if (bigMonth.contains(text)) {
                            int currentId = dayPapersPicker
                                    .getCurrentPapersIndex();
                            dayPapersPicker.setList(bigMonthDayList, currentId);
                        } else {
                            if (smallMonth.contains(text)) {
                                int currentId = dayPapersPicker
                                        .getCurrentPapersIndex();
                                if (currentId == 30)
                                    currentId = 29;
                                dayPapersPicker.setList(smallMonthDayList,
                                        currentId);
                            } else {
                                int currentId = dayPapersPicker
                                        .getCurrentPapersIndex();
                                if (currentId >= 27) {
                                    Integer year = Integer
                                            .parseInt(yearPapersPicker
                                                    .getPapers());
                                    if (ConstantUtils.isRunNian(year)) {
                                        dayPapersPicker.setList(
                                                erRMonthDayList, 28);
                                    } else {
                                        dayPapersPicker.setList(
                                                erPMonthDayList, 27);
                                    }
                                }

                            }
                        }
                    }
                });

        tv_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSelectListener != null) {
                    String selectString;
                    if (Chiness) {
                        selectString = yearPapersPicker.getPapers() + "年"
                                + monthPapersPicker.getPapers() + "月"
                                + dayPapersPicker.getPapers() + "日";
                    } else {
                        selectString = yearPapersPicker.getPapers() + "-"
                                + monthPapersPicker.getPapers() + "-"
                                + dayPapersPicker.getPapers();
                    }
                    mSelectListener.onSelectItem(selectString);
                    dismiss();
                }

            }
        });

        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public interface SelectListener {
        void onSelectItem(String str);
    }
}
