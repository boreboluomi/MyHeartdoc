package com.electrocardio.custom.elec;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by yangzheng on 2016/1/13.
 */
public class MergeECG extends RelativeLayout {
    private final Context context;

    public MergeECG(Context context) {
        super(context);
        this.context=context;
    }

    public MergeECG(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
    }

    public MergeECG(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        Initialize();
    }

    private void Initialize() {
        Renderer renderer = new Renderer();
        renderer.setSiatShowLabel(true);
        renderer.setSiatLineStep(3);
        renderer.setSiatScrollable(true);
        renderer.getSiatBackgroundColor();
        DrawViewCopy view=new DrawViewCopy(context,renderer);
        view.invalidate();
        addView(view);
    }
}
