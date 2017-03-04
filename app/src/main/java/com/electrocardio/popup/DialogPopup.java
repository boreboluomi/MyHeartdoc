package com.electrocardio.popup;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;

import com.electrocardio.R;
import com.electrocardio.base.BasePopupWindow;


/**
 * Created by yangzheng on 2016/1/23.
 *
 */
public  class DialogPopup extends BasePopupWindow {

    private View popupView;
    private  Activity mContext;

    public DialogPopup(Activity context) {
        super(context);
        this.mContext = context;
        bindEvent();
    }




    @Override
    protected Animation getShowAnimation() {
        AnimationSet set=new AnimationSet(false);
        Animation shakeAnima=new RotateAnimation(0,0,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);//数值越大越快
        shakeAnima.setInterpolator(new CycleInterpolator(5));
        shakeAnima.setDuration(400);
        set.addAnimation(getDefaultAlphaAnimation());
        set.addAnimation(shakeAnima);
        return set;
    }

    @Override
    protected View getClickToDismissView() {
        return null;
    }



    @Override
    public View getPopupView() {
        popupView = getPopupViewById(R.layout.popup_dialog);
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return mPopupView.findViewById(R.id.popup_anima);
    }

    private void bindEvent() {
        if (popupView!=null){
            popupView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.cancelButton();
                }
            });
            popupView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.okButton();
                }
            });
        }
    }


    OnDialogListener listener;

    public void setOnDialogListener(OnDialogListener listener) {
        this.listener = listener;
    }
    public interface OnDialogListener {

        void cancelButton();
        void okButton();
    }
}
