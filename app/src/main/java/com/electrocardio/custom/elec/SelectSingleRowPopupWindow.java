package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.electrocardio.R;
import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class SelectSingleRowPopupWindow extends PopupWindow {
    private TextView tv_confirm;
    private View mMenuView;
    // private TextView tv_title;
    private SelectListener mSelectListener;

    public void setSelectListener(SelectListener selectListener) {
        mSelectListener = selectListener;
    }

    public SelectSingleRowPopupWindow(Context context, String title,
                                      ArrayList<String> arrayList, int selectId) {
        mMenuView = View.inflate(context, R.layout.singleselectrow, null);
        // tv_title = (TextView) mMenuView.findViewById(R.id.tv_title);
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
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        // 设置窗体的背景
        this.setBackgroundDrawable(dw);
        // tv_title.setText(title);
        final PapersPicker papersPicker = (PapersPicker) mMenuView
                .findViewById(R.id.paperspicker);
        papersPicker.setList(arrayList, selectId);

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectListener != null) {
                    String selectString = papersPicker.getPapers();
                    mSelectListener.onSelectItem(selectString);
                    dismiss();
                }
            }
        });

        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.ll_content).getTop();
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
