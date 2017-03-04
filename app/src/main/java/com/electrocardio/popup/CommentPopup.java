package com.electrocardio.popup;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.base.BasePopupWindow;
import com.electrocardio.util.DimensUtils;


/**
 * Created byyangzhengcopy on 2016/2/27.
 *
 */
public class CommentPopup extends BasePopupWindow implements View.OnClickListener {

    //private ImageView mLikeAnimaView;
    private TextView mLikeText;

    private RelativeLayout mLikeClikcLayout;
    private RelativeLayout mCommentClickLayout;

    private int[] viewLocation;

    private OnCommentPopupClickListener mOnCommentPopupClickListener;

    private Handler mHandler;
    public CommentPopup(Activity context) {
        this(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public CommentPopup(Activity context, int w, int h) {
        super(context, w, h);

        viewLocation = new int[2];
        mHandler=new Handler();

       // mLikeAnimaView = (ImageView) mPopupView.findViewById(R.id.iv_like);
        mLikeText = (TextView) mPopupView.findViewById(R.id.tv_like);

        mLikeClikcLayout = (RelativeLayout) mPopupView.findViewById(R.id.item_one);
        mCommentClickLayout = (RelativeLayout) mPopupView.findViewById(R.id.item_two);
        RelativeLayout mitem_three =(RelativeLayout) mPopupView.findViewById(R.id.item_three);
        RelativeLayout mitem_four=(RelativeLayout) mPopupView.findViewById(R.id.item_four);
        mLikeClikcLayout.setOnClickListener(this);
        mCommentClickLayout.setOnClickListener(this);
        mitem_three.setOnClickListener(this);
        mitem_four.setOnClickListener(this);
        buildAnima();
    }

    private AnimationSet mAnimationSet;

    private void buildAnima() {
        ScaleAnimation mScaleAnimation = new ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleAnimation.setDuration(200);
        mScaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mScaleAnimation.setFillAfter(false);

        AlphaAnimation mAlphaAnimation = new AlphaAnimation(1, .2f);
        mAlphaAnimation.setDuration(400);
        mAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAlphaAnimation.setFillAfter(false);

        mAnimationSet=new AnimationSet(false);
        mAnimationSet.setDuration(400);
        mAnimationSet.addAnimation(mScaleAnimation);
        mAnimationSet.addAnimation(mAlphaAnimation);
        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 150);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void showPopupWindow(View v) {
        try {
            //得到v的位置
            v.getLocationOnScreen(viewLocation);
            //展示位置：
            //参照点为view的右上角，偏移值为：x方向距离参照view的一定倍数距离
            //垂直方向自身减去popup自身高度的一半（确保在中间）
           /* mPopupWindow.showAtLocation(v, Gravity.RIGHT | Gravity.TOP, (int) (v.getWidth() * 1.8),
                    viewLocation[1] - DimensUtils.dipToPx(mContext,15f));*/
            //mPopupWindow.showAsDropDown(v);
            mPopupWindow.showAsDropDown(v,-DimensUtils.dipToPx(mContext, 160f),20);
            if (getShowAnimation() != null && getAnimaView() != null) {
                getAnimaView().startAnimation(getShowAnimation());
            }
        } catch (Exception e) {
            Log.w("error","error");
        }
    }

    @Override
    protected Animation getShowAnimation() {
        return getScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
    }

    @Override
    public Animation getExitAnimation() {
        return getScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
    }

    @Override
    public View getPopupView() {
        return LayoutInflater.from(mContext).inflate(R.layout.popup_comment, null);
    }

    @Override
    public View getAnimaView() {
        return mPopupView.findViewById(R.id.comment_popup_contianer);
    }
    //=============================================================Getter/Setter

    public OnCommentPopupClickListener getOnCommentPopupClickListener() {
        return mOnCommentPopupClickListener;
    }

    public void setOnCommentPopupClickListener(OnCommentPopupClickListener onCommentPopupClickListener) {
        mOnCommentPopupClickListener = onCommentPopupClickListener;
    }

    //=============================================================clickEvent
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_one:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onItem1(v);
                //    mLikeAnimaView.clearAnimation();
                 //   mLikeAnimaView.startAnimation(mAnimationSet);
                    dismiss();
                }
                break;
            case R.id.item_two:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onItem2(v);
                    dismiss();
                }
                break;

            case R.id.item_three:
                mOnCommentPopupClickListener.onItem3(v);
                dismiss();
                break;
            case R.id.item_four:
                mOnCommentPopupClickListener.onItem4(v);
                dismiss();
                break;

        }
    }

    //=============================================================InterFace
    public interface OnCommentPopupClickListener {
        void onItem1(View v);
        void onItem2(View v);
        void onItem3(View v);
        void onItem4(View v);
    }
    //=============================================================abortMethods

    @Override
    protected View getClickToDismissView() {
        return null;
    }

}
