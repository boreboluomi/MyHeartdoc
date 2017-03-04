package com.electrocardio.Callback;


import com.electrocardio.Bean.UploadImageBean;
import com.electrocardio.Bean.loadfileBean;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by yangzheng on 2016/3/21.
 * 文件上传回调类
 */
public abstract class UploadImageback extends Callback<UploadImageBean> {




    @Override
    public UploadImageBean parseNetworkResponse(Response response) throws Exception {

        String string = response.body().string();
        UploadImageBean mUploadImageblack = new Gson().fromJson(string, UploadImageBean.class);
        return mUploadImageblack;

    }


}
