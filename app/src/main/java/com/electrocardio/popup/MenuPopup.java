package com.electrocardio.popup;

import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.base.BasePopupWindow;
import com.nineoldandroids.view.ViewHelper;



/**
 * Created by yangzhengcopy on 2016/1/22.
 * 菜单。
 */
public class MenuPopup extends BasePopupWindow implements View.OnClickListener {

    private TextView tx1;


    private int[] viewLocation;

    public MenuPopup(Activity context) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewLocation=new int[2];
        mPopupView.findViewById(R.id.tx_1).setOnClickListener(this);

    }



    @Override
    protected Animation getShowAnimation() {
        AnimationSet set=new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(getScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0));
        set.addAnimation(getDefaultAlphaAnimation());
        return set;
        //return null;
    }

    @Override
    public Animator getShowAnimator() {
       /* AnimatorSet set=new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mAnimaView,"scaleX",0.0f,1.0f).setDuration(300),
                ObjectAnimator.ofFloat(mAnimaView,"scaleY",0.0f,1.0f).setDuration(300),
                ObjectAnimator.ofFloat(mAnimaView,"alpha",0.0f,1.0f).setDuration(300*3/2));*/
        return null;
    }

    @Override
    public void showPopupWindow(View v) {
        try {
            v.getLocationOnScreen(viewLocation);
           /* mPopupWindow.showAtLocation(v, Gravity.RIGHT | Gravity.TOP, (int)(v.getWidth()*1.5),
                    viewLocation[1]+((v.getHeight()*2/3)));*/
            mPopupWindow.showAsDropDown(v);
            if (getShowAnimation() != null && mAnimaView != null) {
                mAnimaView.clearAnimation();
                mAnimaView.startAnimation(getShowAnimation());
            }
            if (getShowAnimation() == null && getShowAnimator() != null && mAnimaView != null &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ViewHelper.setPivotX(mAnimaView, 1);
                ViewHelper.setPivotY(mAnimaView, 0);
                getShowAnimator().start();
            }
        } catch (Exception e) {
            Log.w("error","error");
        }
    }
    @Override
    protected View getClickToDismissView() {

        return null;
    }


    @Override
    public View getPopupView() {
        return getPopupViewById(R.layout.popup_menu);
    }

    @Override
    public View getAnimaView() {
        return mPopupView.findViewById(R.id.popup_contianer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tx_1:
                listener.OnClickConfirm();
                break;

            default:
                break;

        }

    }

    OnMenuClickListener listener;

    public void setOnMenuListener(OnMenuClickListener listener) {
        this.listener = listener;
    }
    public interface OnMenuClickListener {

        public void OnClickConfirm();

    }
}
