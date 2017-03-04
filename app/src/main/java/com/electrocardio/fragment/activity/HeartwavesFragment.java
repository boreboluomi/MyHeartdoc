package com.electrocardio.fragment.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.electrocardio.R;
import com.electrocardio.custom.elec.DrawBackground;
import com.electrocardio.custom.elec.ScrollHorizontalView;
import com.electrocardio.custom.elec.SeekBarHint;
import com.electrocardio.fragment.base.AbsBaseFragment;

import java.util.ArrayList;

/**
 * Created by yangzheng on 2016/3/21.
 * 心电波界面
 */
public class HeartwavesFragment extends AbsBaseFragment implements ScrollHorizontalView.onScrollChangedDataListener, SeekBar.OnSeekBarChangeListener {
    private ScrollHorizontalView mroot;
    private RelativeLayout mHrartwaves;
    private int s = 0;
    private ArrayList<Float> data = new ArrayList<>();
    private float[] con = new float[1240];
    private SeekBarHint mHeartseekBar;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_heartwaves;
    }

    @Override
    protected void init(Bundle savedInstanceState, View contentView) {
        mHrartwaves = (RelativeLayout) contentView.findViewById(R.id.mhrartwaves_title);
        mroot = (ScrollHorizontalView) contentView.findViewById(R.id.root);
        mHeartseekBar = (SeekBarHint) contentView.findViewById(R.id.exce_seekbar);
        initdata();
    }

    @Override
    public void setFragmentType(int fragmentType) {

    }

    private void initdata() {
        DrawBackground bgView = new DrawBackground(getActivity());
        mHrartwaves.addView(bgView);
        mroot.setonScrollChangedBlackListener(this);
        mHeartseekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * 设置数据
     *
     * @param list
     */
    public void setCoustomDareData(ArrayList<Float> list) {
        if (list.size() < 1239) {
            setFragmentStatus(AbsBaseFragment.FRAGMENT_STATUS_SUCCESS);
            return;
        }
        data = list;
        for (int d = 0; d < data.size(); d++) {
            con[s] = data.get(d);
            s++;
            if (s == 1239) {
                mroot.initdata1(getActivity(), con);
                s = 0;
            }
            if (d == data.size() - 1) {
                setFragmentStatus(AbsBaseFragment.FRAGMENT_STATUS_SUCCESS);
            }
        }
    }

    @Override
    public void onScrollChangedData(int l, int t, int oldl, int s) {
        mHeartseekBar.setProgress(l);
        mHeartseekBar.invalidate();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        mroot.scrollTo(progress, 0);
        mroot.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
