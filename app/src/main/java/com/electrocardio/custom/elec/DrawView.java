package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.LinkedList;

/**
 * ͼ������� Created by Meteoral.Liu On MacOS User: Meteoral Date: 14-10-10 WebSite:
 * http://www.liuqingwei.com QQ: 120599662
 */
public class DrawView extends View {

    /**
     * ��ȡ��ǰҳ������
     */
    public static final int GET_THIS_PAGE = 0;
    /**
     * ��ȡ��һҳ������
     */
    public static final int GET_PRE_PAGE = 1;
    /**
     * ��ȡ��һҳ������
     */
    public static final int GET_NEXT_PAGE = 2;
    /**
     * �ؼ��Ŀ�
     */
    private int width;
    /**
     * �ؼ��ĸ�
     */
    private int height;
    /**
     * ��Ⱦ������
     */
    private Renderer render;
    /**
     * �ؼ���Rect
     */
    private Rect rect;
    /**
     * �ؼ�Context
     */
    private Context con;
    /**
     * �����¼���������
     */
    private float eventX;
    /**
     * ���ݼ�
     */
    // private float[] drawData;
    private float[] drawData;
    /**
     * ���ݼ����Ƿ��������
     */
    private boolean hasData;

    private boolean run = true;
    /**
     * �����͵�URLֵ(��������)
     */
    private String _url = "http://www.bit-health.com/ecg/drawEcg.php";
    /**
     * �������¼�EventId
     */
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
                    int one = (Integer)msg.obj;
                    oneAddTwoJian(one);
                    break;
                case 5:
                    int three = (Integer)msg.obj;
                    oneJianTwoAdd(three);
                    break;

            }
        }
    };



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        run = false;
    }

    private void oneJianTwoAdd(int i) {
        stack.push2(drawData[i * 4 + 1]);
        stack.push2(drawData[i * 4 + 3]);
        if (stack.getSize() != 0) {
            stack.pop();
            stack.pop();
        }

        if(stack.getSize2()==i){
            stack.push2(0);

        }
        invalidate();
    }



    private void oneAddTwoJian(int i) {
        stack.push(drawData[i * 4 + 1]);
        stack.push(drawData[i * 4 + 3]);
        if (stack.getSize2() != 0) {
            stack.pop2();
            stack.pop2();
        }
        if(stack.getSize()==i){
            stack.push(0);
        }
        invalidate();
    }


    private Paint p;
    private Paint p1;
    private float[] as;
    private StackL stack = new StackL();

    public DrawView(Context context) {
        super(context);
        con = context;
        render = new Renderer();
    }

    public DrawView(Context context, Renderer renderer) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawBackgroud(canvas);
        if (drawData == null) {
            getDrawData(GET_THIS_PAGE, canvas);
        }
        if (hasData) {
            float x = 0;

            if (!state) {
                for (int i = 0; i < stack.getSize()-2; i++) {
                    canvas.drawLine(x, (Float) stack.getp(i), (float)(x + 0.5), (Float) stack.getp(i + 1), p);
                    x  += 0.5;
                }
                if (stack.getSize2() !=0) {
                   // x += add;
                  //  canvas.drawRect(x-10,0,x,getHeight(),p);
                    for (int j = 0; j < stack.getSize2()-2; j++) {
                       canvas.drawLine(x, (Float) stack.getp2(j), (float) (x + 0.5), (Float) stack.getp2(j + 1), p1);
                        x +=0.5;
                     }

                }
            } else {
                for (int j = 0; j < stack.getSize2()-2; j++) {
                    canvas.drawLine(x, (Float) stack.getp2(j), (float) (x + 0.5), (Float) stack.getp2(j + 1), p1);
                    x += 0.5;
                }
                if (stack.getSize() !=0) {
                  //  x +=add;
                   // canvas.drawRect(x-10,0,x,getHeight(),p);
                    for (int i = 0; i < stack.getSize() - 2; i++) {

                        canvas.drawLine(x, (Float) stack.getp(i),(float)(x + 0.5), (Float) stack.getp(i + 1), p);
                        x += 0.5;
                    }
                }
            }


        }
    }
    public void DrawChart(final Canvas canvas) {

        p = new Paint();
        canvas.saveLayerAlpha(new RectF(rect), 0xFF, Canvas.MATRIX_SAVE_FLAG);
        p.setColor(Color.BLACK);
        p.setStrokeWidth(3);
        p1= new Paint();
        p1.setColor(Color.RED);
        //p1.setStroke(3);
        as = new float[drawData.length];
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
             /*   for (int i = 0; i < drawData.length - 3; i += 4) {
                    System.out.println("i:" + i);
                    as[i] = drawData[i];
                    as[i + 1] = drawData[i + 1];
                    as[i + 2] = drawData[i + 2];
                    as[i + 3] = drawData[i + 3];
                    myHandler.obtainMessage(1).sendToTarget();
                    if(drawData.length-4==i){
                       // getDrawData(GET_NEXT_PAGE, mCanvas);
                    }
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
                for(int z=0;z<5;z++){
                    if (!state) {
                        for (int i = 0; i < drawData.length / 4; i++) {
                            myHandler.obtainMessage(3,i).sendToTarget();

                            try {
                                Thread.sleep(9);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                     // add = 20;
                        state = true;
                    } else {
                        for (int i = 0; i < drawData.length / 4; i++) {
                            myHandler.obtainMessage(5,i).sendToTarget();
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

    public boolean getDrawData(int dataType, Canvas canvas) {
        switch (dataType) {
            case GET_THIS_PAGE: {
                new PostRequest(canvas).execute(GET_THIS_PAGE);
            }
            break;
            case GET_PRE_PAGE: {
                new PostRequest(canvas).execute(GET_PRE_PAGE);
            }
            break;
            case GET_NEXT_PAGE: {
                new PostRequest(canvas).execute(GET_NEXT_PAGE);
            }
            break;
            default:
                new PostRequest(canvas).execute(GET_THIS_PAGE);
        }

        return hasData;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (render.isSiatScrollable()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    eventX = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    if (eventX - event.getX() > width / 4) {
                        getDrawData(GET_PRE_PAGE, mCanvas);
                        if (hasData) {
                            Toast.makeText(con, "��һҳ", Toast.LENGTH_SHORT).show();
                        } else {
                            setPage(getPage() + 1);
                        }
                    } else if (event.getX() - eventX > width / 4) {
                        getDrawData(GET_NEXT_PAGE, mCanvas);
                        if (hasData) {
                            Toast.makeText(con, "��һҳ", Toast.LENGTH_SHORT).show();
                        } else {
                            setPage(getPage() - 1);
                        }
                    }
                    break;
            }
        }
        return true;
    }

    class PostRequest extends AsyncTask<Integer, String, String> {

        private Canvas mCanvas;

        /**
         * �޲������췢�������� Ĭ�Ϲ���urlΪhttp://www.bit-health.com/ecg/drawEcg.php
         * Ĭ����ʼҳ��0ҳ
         */
        public PostRequest(Canvas canvas) {
            mCanvas = canvas;
        }

        public PostRequest(String url, String eventId, Integer page) {
            _eventId = eventId;
            _page = page;
            _url = url;
        }

        @Override
        protected String doInBackground(Integer... type) {
            String responseString = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            try {
                String urlParm = null;
                if (type[0] == GET_THIS_PAGE) {
                    urlParm = "?type=" + _page + "&EventId=" + _eventId;
                } else if (type[0] == GET_NEXT_PAGE) {
                    _page++;
                    urlParm = "?type=" + _page + "&EventId=" + _eventId;
                } else if (type[0] == GET_PRE_PAGE) {
                    _page--;
                    if (_page < 0)
                        _page = 0;// ҳ������С��0
                    urlParm = "?type=" + _page + "&EventId=" + _eventId;
                }
                response = httpclient.execute(new HttpGet(getUrl() + urlParm));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    // Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                // TODO Handle problems..
            } catch (IOException e) {
                // TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject obj = new JSONObject(result);
                // if (0 == obj.getInt("offset") || 1 ==
                // obj.getInt("offset")){
                int a = 0;
                if (0 == a) {
                    JSONArray data = obj.getJSONArray("data");
                    hasData = true;
                    // int step =
                    // render.getSiatLineStep();
                    int step = 1;
                    final int counts = (data.length() / width) * step;
                    final int halfHeight = height / 2;
                    drawData = new float[(width / step) * 4];
                    for (int i = 0; i < width / step; i++) {
                        drawData[i * 4] = i * step;
                        drawData[i * 4 + 1] = (-data.getInt(i * counts) * 0.5f)
                                + halfHeight;
                        drawData[i * 4 + 2] = (i + 1) * step;
                        drawData[i * 4 + 3] = (-data.getInt((i + 1) * counts) * 0.5f)
                                + halfHeight;

                        // myHandler.sendMessage(null);

                    }
                    myHandler.obtainMessage(2).sendToTarget();
                } else if (2 == obj.getInt("flag"))// û��������
                {
                    hasData = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // invalidate();
        }
    }

}
