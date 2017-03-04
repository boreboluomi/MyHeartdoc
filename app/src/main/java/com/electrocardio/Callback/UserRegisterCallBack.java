package com.electrocardio.Callback;

import com.electrocardio.Bean.UserNumberBean;
import com.electrocardio.Bean.UserRegisterBean;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by yangzheng on 2016/3/29.
 *
 * 用户注册验证手机是否注册成功
 */
public abstract class UserRegisterCallBack extends Callback<UserRegisterBean> {

    @Override
    public UserRegisterBean parseNetworkResponse(Response response) throws Exception {

        String string = response.body().string();
        UserRegisterBean mUserRegisterBean = new Gson().fromJson(string, UserRegisterBean.class);
        return mUserRegisterBean;

    }
}
