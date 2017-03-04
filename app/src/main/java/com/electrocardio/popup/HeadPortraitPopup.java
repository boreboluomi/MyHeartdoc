package com.electrocardio.popup;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.CheckBox;

import com.electrocardio.R;
import com.electrocardio.base.BasePopupWindow;
import com.electrocardio.util.ToastUtils;


/**
 * Created by 大灯泡 on 2016/1/15.
 * 从底部滑上来的popup
 */
public class HeadPortraitPopup extends BasePopupWindow implements View.OnClickListener{

    private View popupView;
    private final Activity mcontext;

    public HeadPortraitPopup(Activity context) {
        super(context);
        mcontext = context;
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
        popupView= LayoutInflater.from(mContext).inflate(R.layout.head_portrait_popup,null);
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }

    private void bindEvent() {
        if (popupView!=null){
            popupView.findViewById(R.id.photograph).setOnClickListener(this);
            popupView.findViewById(R.id.photo).setOnClickListener(this);
            popupView.findViewById(R.id.cancel).setOnClickListener(this);

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.photograph:
                listener.Onphotograph();
                break;
            case R.id.photo:

                listener.Onphoto();
                break;
            case R.id.cancel:
                listener.OnClickCancel();
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
        void OnClickCancel();
        void Onphotograph();
        void Onphoto();
    }
}
