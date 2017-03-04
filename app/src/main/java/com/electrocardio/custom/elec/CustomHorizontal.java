package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by yangzheng on 2016/3/10.
 */
public class CustomHorizontal extends View{
    private InputStream is;
    private Context con;
    private String content = ""; //文件内容字符串
    private float[] drawData;
    private int width;
    private int height;
    private int halfheight;
    private Rect rect;
    private ArrayList<String> hourLabel;// 小时标签集合
    private ArrayList<String> minuteLabel;// 分钟标签集合
    private Paint p;
    private Boolean state=false;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    invalidate();
                    break;

            }
        }
    };
    private Paint rect1;


    public CustomHorizontal(Context context){
        super(context);
        con = context;

    }

    public CustomHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
        con = context;


    }

    public CustomHorizontal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        con = context;


    }
    public CustomHorizontal(Context context, float[] drawData) {
        super(context);
        init();
        getCoustomDareData(drawData);
    }
    private void getCoustomDareData(float[] data){


            drawData = new float[4960];
            for (int i = 0; i <4960 / 4 - 1; i++) {

                drawData[i * 4] = i;
                drawData[i * 4 + 1] = 700 - data[i] - 360;
                drawData[i * 4 + 2] = (float) (i+1);
                drawData[i * 4 + 3 ] = 700 - data[i+1] - 360;
            }
            state=true;
            myHandler.obtainMessage(1).sendToTarget();

    }
    private void init() {
        p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.BLACK);
        p.setStrokeWidth(3);
        rect1 = new Paint();
        rect1.setAntiAlias(true);
        rect1.setColor(Color.RED);
        rect1.setStrokeWidth(4);
        hourLabel = new ArrayList<>();
        minuteLabel = new ArrayList<>();
        for (int i = 0; i < 60; ) {
            if (i < 10)
                minuteLabel.add("0" + i);
            else
                minuteLabel.add(i + "");
            i += 10;
        }
        for (int i = 0; i < 24; i++) {
            if (i < 10)
                hourLabel.add("0" + i);
            else
                hourLabel.add(i + "");
        }

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawBackgroud(canvas);
        if (state) {
            canvas.drawLines(drawData, p);
        }
        drawBottomTimeLabel(canvas);
        drawSectionLine(canvas);
    }


    public void DrawBackgroud(Canvas canvas) {
        Paint background = new Paint();
        background.setColor(Color.alpha(0));
        Paint backP = new Paint();
       // backP.setColor(render.getSiatAxesColor());
        backP.setAlpha(100);
        rect = canvas.getClipBounds();
        canvas.drawRect(0, 0, width, height, background);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height=getMeasuredHeight();
        halfheight = getMeasuredHeight()/2;
    }

    private void drawBottomTimeLabel(Canvas canvas) {
        int textX = 185;
        int textY = height;
        p.setColor(Color.parseColor("#666666"));
        p.setTextSize(40);

            for (int i=0;i<3;i++) {
                canvas.drawText("100", textX-15, textY-halfheight+230, p);
                canvas.drawRect(textX+20,textY-halfheight+170,textX+25,textY-halfheight+180,rect1);
                textX += getMeasuredWidth()/3 ;
            }


    }

    private void drawSectionLine(Canvas canvas) {
        int startX = 0;
        int startY = height;
                canvas.drawLine(startX, startY-halfheight+180, width, startY-halfheight+180,rect1);

    }
}
