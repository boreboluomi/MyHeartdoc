package com.electrocardio.Callback;

import com.electrocardio.Bean.UserLoginBean;
import com.electrocardio.Bean.UserNumberBean;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by yangzheng on 2016/3/29.
 *
 * 用户注册验证手机是否注册成功
 */
public abstract class UserLoginCallBack extends Callback<UserLoginBean> {

    @Override
    public UserLoginBean parseNetworkResponse(Response response) throws Exception {

        String string = response.body().string();
        UserLoginBean mUserLoginBean = new Gson().fromJson(string, UserLoginBean.class);
        return mUserLoginBean;

    }
}
