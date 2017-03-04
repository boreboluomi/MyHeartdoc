package com.electrocardio.Callback;


import com.electrocardio.Bean.loadfileBean;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yangzheng on 2016/3/21.
 * 文件上传回调类
 */
public abstract class UserCallback extends Callback<loadfileBean> {
    @Override
    public loadfileBean parseNetworkResponse(Response response) throws Exception {

        String string = response.body().string();
        loadfileBean mloadfileBean = new Gson().fromJson(string, loadfileBean.class);
        return mloadfileBean;

    }


}
