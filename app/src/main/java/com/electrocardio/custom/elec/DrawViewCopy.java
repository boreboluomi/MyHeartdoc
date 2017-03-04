package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.electrocardio.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * ???????? Created by Meteoral.Liu On MacOS User: Meteoral Date: 14-10-10 WebSite:
 * http://www.liuqingwei.com QQ: 120599662
 */
public class DrawViewCopy extends View {
    private String content = ""; //文件内容字符串



    public static final int GET_THIS_PAGE = 0;

    public static final int GET_PRE_PAGE = 1;

    public static final int GET_NEXT_PAGE = 2;

    private int width;

    private int height;

    private Renderer render;

    private Rect rect;

    private Context con;

    private float eventX;

    // private float[] drawData;
    private float[] drawData;

    private boolean hasData;

    private boolean run = true;

    private String _url = "http://www.bit-health.com/ecg/drawEcg.php";

    private String _eventId = "53b64a8611dbae4910000003";
    private Canvas mCanvas;
    private Boolean state = false;
    private int add = 0;
    private Integer _page = 0;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    invalidate();
                    break;
                case 2:
                    DrawChart(mCanvas);
                    break;
                case 3:
                    int one = (Integer) msg.obj;
                    oneAddTwoJian(one);
                    break;
                case 5:
                    int three = (Integer) msg.obj;
                    oneJianTwoAdd(three);
                    break;

            }
        }
    };
    private InputStream is;
    private ByteArrayOutputStream arrayOutputStream;
    private ArrayList<Float> drawArray;
    private Float[] drawArray1;


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        run = false;
    }

    private void oneJianTwoAdd(int i) {
        stack.push2(drawData[i * 4]);
        stack.push2(drawData[i * 4 + 1]);
        if (stack.getSize() != 0) {
            stack.pop();
            stack.pop();
        }

        if (stack.getSize2() == i) {
            stack.push2(0);
        }
        invalidate();
    }


    private void oneAddTwoJian(int i) {
        stack.push(drawData[i * 4]);
        stack.push(drawData[i * 4 + 1]);
        if (stack.getSize2() != 0) {
            stack.pop2();
            stack.pop2();
        }
        if (stack.getSize() == i) {
            stack.push(0);



        }
        invalidate();
    }


    private Paint p;
   // private Paint p1;
    private float[] as;
    private StackL stack = new StackL();

    public DrawViewCopy(Context context) {
        super(context);
        con = context;
        render = new Renderer();
    }

    public DrawViewCopy(Context context, Renderer renderer) {
        super(context);
        con = context;
        render = renderer;
    }


    public String getUrl() {
        return _url;
    }


    public String getEventId() {
        return _eventId;
    }

    public Integer getPage() {
        return _page;
    }


    public void setEventId(String _eventId) {
        this._eventId = _eventId;
    }


    public void setPage(Integer _page) {
        this._page = _page;
    }


    public void setUrl(String _url) {
        this._url = _url;
    }

    public void setDrawArray(ArrayList<Float> drawArray) {
        this.drawArray = drawArray;




    }



    public ArrayList<Float> getDrawArray() {
        return drawArray;
    }

    public void setDrawArray1(Float[] drawArray1) {
        this.drawArray1 = drawArray1;

        getData(drawArray1);
    }
   /* public void setDrawChart(){

    }*/
   private void getData(Float[] drawArray) {
    /*   drawData = new float[1400];
       for (int i = 0; i <drawArray.length / 2 - 1; i++) {
           drawData[i * 2] = drawArray[i*2];
           drawData[i * 2 + 1] =drawArray[i*2+1];
       }
       myHandler.obtainMessage(2).sendToTarget();*/
       System.out.println(drawArray.length+"--------");
   }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawBackgroud(canvas);
        DrawTitlecanvas(canvas);

       /* if (drawData == null) {
            // getDrawData(GET_THIS_PAGE, canvas);
            try {
                getCoustomDareData(canvas);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        if (hasData) {
            float x = 0;

            if (!state) {
                for (int i = 0; i < stack.getSize() - 2; i++) {
                    canvas.drawLine(x, (Float) stack.getp(i), (float) (x + 2), (Float) stack.getp(i + 1), p);
                    x += 2;
                }
                if (stack.getSize2() != 0) {
                    // x += add;
                    //  canvas.drawRect(x-10,0,x,getHeight(),p);
                    for (int j = 0; j < stack.getSize2() - 2; j++) {
                        canvas.drawLine(x, (Float) stack.getp2(j), (float) (x + 2), (Float) stack.getp2(j + 1), p);
                        x += 2;
                    }

                }
            } else {
                for (int j = 0; j < stack.getSize2() - 2; j++) {
                    canvas.drawLine(x, (Float) stack.getp2(j), (float) (x + 2), (Float) stack.getp2(j + 1), p);
                    x += 2;
                }
                if (stack.getSize() != 0) {
                    //  x +=add;
                    // canvas.drawRect(x-10,0,x,getHeight(),p);
                    for (int i = 0; i < stack.getSize() - 2; i++) {

                        canvas.drawLine(x, (Float) stack.getp(i), (float) (x + 2), (Float) stack.getp(i + 1), p);
                        x += 2;
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
        canvas.drawText("mm/s",290,60,TextPaint);

    }

    private void getCoustomDareData() throws IOException {


        // JSONArray data = obj.getJSONArray("data");
        is = con.getClass().getClassLoader().getResourceAsStream("assets/" + "data.txt");
        if (is != null) {
            float[] data = new float[is.available()];
            InputStreamReader inputreader = new InputStreamReader(is);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            //分行读取
            int z = 0;
            while ((line = buffreader.readLine()) != null) {
                content += line + "\n";
                //  System.out.println("content----" + content);
                if (line != null && !line.equals("")) {
                    Float number = Float.valueOf(content);
                    if(z<1400)
                        data[z] = number;
                    content = "";
                    z++;
                }

            }
            is.close();
            hasData = true;
            final int halfHeight = height / 2;
            final int Height = height;
            drawData = new float[1400];
            for (int i = 0; i <1400 / 2 - 1; i++) {
                drawData[i * 2] = (Height - ((data[i * 2]) - 12800) / 20) - halfHeight;
                drawData[i * 2 + 1] = (Height - ((data[i * 2 + 1]) - 12800) / 20) - halfHeight;
                }
                myHandler.obtainMessage(2).sendToTarget();

                //System.out.println(i * 2 + "----" + data[i * 2] + "-----" + (Height - ((data[i * 2]) - 12800)) + "-----" + drawData[i * 2]);
              //  System.out.println(i * 2 + 1 + "----" + data[i * 2 + 1] + "----" + (Height - ((data[i * 2 + 1]) - 12800)) + "-----" + drawData[i * 2 + 1]);

        }
    }

    public void DrawChart(final Canvas canvas) {
        p = new Paint();
      //  canvas.saveLayerAlpha(new RectF(rect), 0xFF, Canvas.MATRIX_SAVE_FLAG);

        p.setAntiAlias(true);
        p.setColor(Color.BLACK);
        p.setStrokeWidth(2);
       /* p1 = new Paint();
        p1.setColor(Color.RED);
        p1.setAntiAlias(true);
        p1.setStrokeWidth(2);*/
        as = new float[drawData.length];
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                    if (!state) {
                        for (int i = 0; i < drawData.length/4-1; i++) {
                            myHandler.obtainMessage(3, i).sendToTarget();

                            try {
                                Thread.sleep(9);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        // add = 20;
                        state = true;
                    } else {
                        for (int i = 0; i < drawData.length/4-1; i++) {
                            myHandler.obtainMessage(5, i).sendToTarget();
                            try {
                                Thread.sleep(9);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        state = false;
                        // add = 20;
                    }
                }

        }).start();


    }

    public void DrawBackgroud(Canvas canvas) {
        mCanvas = canvas;
        Paint background = new Paint();
        background.setColor(render.BACKGROUND_COLOR);

        Paint backP = new Paint();
        backP.setColor(render.getSiatAxesColor());
        backP.setAlpha(100);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        rect = canvas.getClipBounds();
        canvas.drawRect(0, 0, width, height, background);
        if (render.isSiatShowAxes()) {

            final int gridStep = width / 50;
            for (int k = 0; k < width / gridStep; k++) {
                if (k % 5 == 0) {
                    backP.setStrokeWidth(2);
                    canvas.drawLine(k * gridStep, 0, k * gridStep, height,
                            backP);
                } else {
                    backP.setStrokeWidth(1);
                    canvas.drawLine(k * gridStep, 0, k * gridStep, height,
                            backP);
                }
            }
            for (int g = 0; g < height / gridStep; g++) {
                if (g % 5 == 0) {
                    backP.setStrokeWidth(2);
                    canvas.drawLine(0, g * gridStep, width, g * gridStep, backP);
                } else {
                    backP.setStrokeWidth(1);
                    canvas.drawLine(0, g * gridStep, width, g * gridStep, backP);
                }
            }
        }
        if (render.isSiatShowLabel()) {
            Paint labelPaint = new Paint();
            labelPaint.setColor(render.TEXT_COLOR);
            labelPaint.setTypeface(render.getSiatTextTypeface());
            labelPaint.setTextSize(render.getSiatChartTextSize());
            labelPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(render.getSiatChartLabel(), width / 2, height - 10,
                    labelPaint);
        }
    }


    public static <T> T invertArray(T array) {
        int len = Array.getLength(array);

        Class<?> classz = array.getClass().getComponentType();

        Object dest = Array.newInstance(classz, len);

        System.arraycopy(array, 0, dest, 0, len);

        Object temp;

        for (int i = 0; i < (len / 2); i++) {
            temp = Array.get(dest, i);
            Array.set(dest, i, Array.get(dest, len - i - 1));
            Array.set(dest, len - i - 1, temp);
        }

        return (T) dest;
    }




    class StackL {
        private LinkedList list = new LinkedList();
        private LinkedList list2 = new LinkedList();

        public void push(Object v) {
            list.add(v);
        }

        public Object top() {

            return list.getFirst();
        }

        public Object pop() {
            return list.removeFirst();
        }


        public Object getp(int i) {
            return list.get(i);
        }

        public int getSize() {
            return list.size();
        }

        public void push2(Object v) {
            list2.add(v);
        }

        public Object getp2(int i) {
            return list2.get(i);
        }

        public int getSize2() {
            return list2.size();
        }

        public Object pop2() {
            return list2.removeFirst();
        }
    }


}
