package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.electrocardio.util.DimensUtils;
import com.electrocardio.util.SysinfoHelper;
import java.util.LinkedList;
/**
 * Created by yangzheng on 2015/12/31.
 * @心电图形
 */
public class ECGDrawView extends View {
    private int width;
    private int height;
    private Renderer render;
    private Rect rect;
    private Context con;
    private boolean hasData;
    private Canvas mCanvas;
    private Boolean state = false;
    private int halfheight;
    private Handler myHandler = new Handler() {
        public  void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    invalidate();
                    break;
                case 2:

                        float mLineArray1 = (float )msg.obj;
                        stack.addelement1(mLineArray1);
                    if(stack.getSize1()>1079){
                        stack.removeFrist1();
                    }
                        if (stack.getSize1()== (getWidth() / 13 + 1) * 13 ) {
                            state = true;
                        }
                        if (stack.getSize2() >0) {
                            stack.removeFrist2();
                        }
                        invalidate();


                    break;
                case 3:
                    float mLineArray2 = (float )msg.obj;
                    stack.addelement2(mLineArray2);
                    if(stack.getSize2()>1079){
                        stack.removeFrist2();
                    }
                    if (stack.getSize2() == (getWidth() / 13 + 1) * 13) {
                        state = false;

                    }

                    if (stack.getSize1() >0) {
                        stack.removeFrist1();
                    }

                    invalidate();
                    break;


            }
        }
    };



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private Paint p;
    private StackL stack = new StackL();

    public ECGDrawView(Context context) {
        super(context);
        con = context;
        render = new Renderer();

    }
    public ECGDrawView(Context context, Renderer renderer) {
        super(context);
        con = context;
        render = renderer;
        init();
    }
    private void init() {
        p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.BLACK);
        p.setStrokeWidth(DimensUtils.dipToPx(con, 1));
        disableHardwareAccelerated();
    }
    public void setDrawArray(float[] drawArray) {
        hasData = true;
            if (!state) {

                for (int i = 0; i < 13; i++) {
                    float mLine = height - (drawArray[i] + halfheight);
                    myHandler.obtainMessage(2, mLine).sendToTarget();
                }
        }
            if (state) {

                for (int i = 0; i < 13; i++) {
                    float mLine = height - (drawArray[i] + halfheight);
                    myHandler.obtainMessage(3, mLine).sendToTarget();
                }
            }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

            if (hasData) {
                float x = 0;
                DrawBackgroud(canvas);
                if (!state) {

                    for (int i = 0; i < stack.getSize1()-1; i++) {

                        canvas.drawLine(x, (float) stack.getposition1(i), (float) (x + 1.0), (float) stack.getposition1(i + 1), p);
                        x += 1.0;

                    }
                    if (stack.getSize2() >0) {
                        for (int j = 0; j < stack.getSize2()-1; j++) {
                            canvas.drawLine(x, (float) stack.getposition2(j), (float) (x + 1.0), (float) stack.getposition2(j + 1), p);
                            x += 1.0;
                        }

                    }
                } else {
                    for (int j = 0; j < stack.getSize2()-1; j++) {
                        canvas.drawLine(x, (float) stack.getposition2(j), (float) (x + 1.0), (float) stack.getposition2(j + 1), p);
                        x += 1.0;
                    }
                    if (stack.getSize1()>0) {
                        for (int i = 0; i < stack.getSize1()-1; i++) {
                            canvas.drawLine(x, (float) stack.getposition1(i), (float) (x + 1.0), (float) stack.getposition1(i + 1), p);
                            x += 1.0;
                        }
                    }
                }


            }


    }


    private void DrawTitlecanvas(Canvas canvas) {
        Paint TextPaint = new Paint();
        // canvas.saveLayerAlpha(new RectF(rect), 0xFF, Canvas.MATRIX_SAVE_FLAG);
        TextPaint.setAntiAlias(true);
        TextPaint.setColor(Color.BLACK);
        TextPaint.setStrokeWidth(5);
        TextPaint.setTextSize(40);
        canvas.drawText("25", 40, 60, TextPaint);
        canvas.drawText("mm/mv", 90, 60, TextPaint);
        canvas.drawText("10", 240, 60, TextPaint);
        canvas.drawText("mm/s", 290, 60, TextPaint);

    }

    public void DrawBackgroud(Canvas canvas) {
        Paint background = new Paint();
        background.setColor(Color.alpha(0));
        Paint backP = new Paint();
        backP.setColor(render.getSiatAxesColor());
        backP.setAlpha(100);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        halfheight = getMeasuredHeight()/2;
        rect = canvas.getClipBounds();
        canvas.drawRect(0, 0, width, height, background);
    }
    class StackL {
        private LinkedList list = new LinkedList();
        private LinkedList list2 = new LinkedList();

        public void addelement1(Object v) {
            list.add(v);
        }

        public Object getFirst1() {
            return list.getFirst();
        }

        public Object removeFrist1() {
            return list.removeFirst();
        }

        public void clear1() {
            list.clear();
        }

        public void clear2() {list2.clear();}

        public Object getposition1(int i) {
            return list.get(i);
        }

        public int getSize1() {
            return list.size();
        }

        public void addelement2(Object v) {
            list2.add(v);
        }

        public Object getposition2(int i) {
            return list2.get(i);
        }

        public int getSize2() {
            return list2.size();
        }

        public Object removeFrist2() {
            return list2.removeFirst();
        }
    }

    /**
     * 禁用硬件加速
     */
    private void disableHardwareAccelerated() {
        if (SysinfoHelper.getInstance().supportHardwareAccelerated()) {
            // 是否开启了硬件加速，如开启将其禁掉
            if (!isHardwareAccelerated()) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }
    }
}
