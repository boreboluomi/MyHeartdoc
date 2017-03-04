package com.electrocardio.activity;


import android.graphics.Color;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.electrocardio.Adapter.BaseAdapterHelper;
import com.electrocardio.Adapter.QuickAdapter;
import com.electrocardio.Bean.loadfileBean;
import com.electrocardio.Callback.UserCallback;
import com.electrocardio.R;
import com.electrocardio.base.BaseActivity;
import com.electrocardio.util.FileUtils;
import com.electrocardio.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.internal.http.HttpEngine;


/**
 * Created by yangzheng on 2016/3/9.
 */
public class UploadFileActivity extends BaseActivity {

    private ListView mListView;
    private String mBaseUrl ="http://api.heartdoc.cn:89/v/uploadtest/ecgtest";
    private List<String> file1;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_uploadfile;
    }

    @Override
    protected void Initialize() {
        initView();
        initdata();
    }

    private void initView() {
        onTitle();
        mListView = (ListView) findViewById(R.id.listView);

    }



    private void initdata() {
        file1 = FileUtils.getFile("/sdcard/test/");
        if(file1.size()>0)
            file1.clear();
        file1 = FileUtils.getFile("/sdcard/test/");
     //  ToastUtils.ToastMessage(this, file1.get(0).toString() + "");

        QuickAdapter<String> quickAdapter = new QuickAdapter<String>(UploadFileActivity.this, R.layout.uploaddfile_item, file1) {

            @Override
            protected void convert(final BaseAdapterHelper helper, final String item) {
                final int position = helper.getPosition();
                helper.setText(R.id.tv_timeSpace, item.substring(13,item.length()-4));
                helper.setText(R.id.m_dayOfMonth,item.substring(21,23));
                Button mButton = helper.getView(R.id.button_upload);

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // ToastUtils.ToastMessage(UploadFileActivity.this,position+"点击了");
                        String fileAddress = file1.get(position);
                        System.out.println("fileAddress" + file1.size());
                       uploadFile(fileAddress, fileAddress.substring(13, fileAddress.length()), helper);
                    }
                });

            }
        };
        mListView.setAdapter(quickAdapter);

    }

    private void onTitle() {
        View view = findViewById(R.id.in_title);
        TextView mTextView = (TextView) view.findViewById(R.id.tv_title);
        ImageView mImageView= (ImageView) view.findViewById(R.id.iv_titleLeft);
        mTextView.setText("数据上传");
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void uploadFile(String pathName, String fileName, BaseAdapterHelper helper)
    {

        File file = new File(pathName);
        if (!file.exists())
        {
            Toast.makeText(this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userId", "17");

        String url = mBaseUrl ;

        OkHttpUtils.post()
                .addFile("tfile", fileName, file)
                .url(url)
                .params(params)
                .build()
                .execute(new myUserCallback(helper));
    }

    public class myUserCallback extends UserCallback{

            private final BaseAdapterHelper helper;

            public myUserCallback(BaseAdapterHelper helper) {

                this.helper=helper;
            }
            @Override
            public void onBefore(Request request)
            {
                super.onBefore(request);
                // setTitle("loading...");
            }

            @Override
            public void onAfter()
            {
                super.onAfter();
            }

            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(loadfileBean loadfileBean) {
                String code = loadfileBean.getCode();
                String msg = loadfileBean.getMsg();
                String data = loadfileBean.getData();
                if(code.equals("200")) {
                    Button view = helper.getView(R.id.button_upload);
                    view.setText("成功");
                    view.setTextColor(Color.RED);
                    view.setEnabled(false);
                    ToastUtils.ToastMessage(UploadFileActivity.this, "上传成功");
                }else{
                    ToastUtils.ToastMessage(UploadFileActivity.this,"请重新上传");
                }
            }


        @Override
        public void inProgress(float progress)
        {
            System.out.println("progress"+progress);
            helper.setProgress(R.id.pb_uploadProgress, (int) progress * 100);
            helper.setText(R.id.tv_uploadDataState,progress * 100+"");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
