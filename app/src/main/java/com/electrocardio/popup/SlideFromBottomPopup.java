package com.electrocardio.popup;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.base.BasePopupWindow;


/**
 * Created by yangzheng on 2016/1/15.
 * 从底部滑上来的popup
 */
public class SlideFromBottomPopup extends BasePopupWindow implements View.OnClickListener {

    private View popupView;
    private CheckBox mOneMesure;
    private CheckBox mTwoMesure;
    private int STATE = 1001;

    public SlideFromBottomPopup(Activity context) {
        super(context);
        bindEvent();
    }

    @Override
    protected Animation getShowAnimation() {
        return getTranslateAnimation(250 * 2, 0, 300);
    }

    @Override
    protected View getClickToDismissView() {
        return popupView.findViewById(R.id.click_to_dismiss);
    }


    @Override
    public View getPopupView() {
        popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_slide_from_bottom, null);
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }

    private void bindEvent() {
        if (popupView != null) {
            popupView.findViewById(R.id.tx_1).setOnClickListener(this);
            popupView.findViewById(R.id.tx_2).setOnClickListener(this);
            popupView.findViewById(R.id.tx_3).setOnClickListener(this);
            popupView.findViewById(R.id.confirm_to_dismiss).setOnClickListener(this);
            popupView.findViewById(R.id.cancel_to_dismiss).setOnClickListener(this);
            mOneMesure = (CheckBox) popupView.findViewById(R.id.measure_one);
            mTwoMesure = (CheckBox) popupView.findViewById(R.id.measure_two);

            mOneMesure.setOnClickListener(this);
            mTwoMesure.setOnClickListener(this);

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tx_1:
                break;
            case R.id.tx_2:
                mOneMesure.setChecked(true);
                mTwoMesure.setChecked(false);
                break;
            case R.id.tx_3:
                mOneMesure.setChecked(false);
                mTwoMesure.setChecked(true);
                break;
            case R.id.confirm_to_dismiss:
                if (mOneMesure.isChecked()) {
                    STATE = 1001;
                }
                if (mTwoMesure.isChecked()) {
                    STATE = 1002;
                }
                listener.OnClickConfirm(STATE);
                break;
            case R.id.cancel_to_dismiss:
                listener.OnClickCancel();
                break;
            case R.id.measure_one:
                mOneMesure.setChecked(true);
                mTwoMesure.setChecked(false);
                STATE = 1001;
                break;

            case R.id.measure_two:
                mTwoMesure.setChecked(true);
                mOneMesure.setChecked(false);
                STATE = 1002;
                break;
            default:
                break;
        }

    }

    OnConfirmClickListener listener;

    public void setOnConfirmListener(OnConfirmClickListener listener) {
        this.listener = listener;
    }

    public interface OnConfirmClickListener {

        void OnClickConfirm(int STATE);

        void OnClickCancel();
    }
}
