package com.electrocardio.Callback;

import com.electrocardio.Bean.UserNumberBean;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by yangzheng on 2016/3/29.
 * <p/>
 * 用户注册验证手机是否注册成功
 */
public abstract class UserNumberCallBack extends Callback<UserNumberBean> {

    @Override
    public UserNumberBean parseNetworkResponse(Response response) throws Exception {
        String string = response.body().string();
        UserNumberBean mUserNumberBean = new Gson().fromJson(string, UserNumberBean.class);
        return mUserNumberBean;

    }
}
