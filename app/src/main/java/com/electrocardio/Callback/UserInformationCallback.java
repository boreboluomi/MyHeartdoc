package com.electrocardio.Callback;


import com.electrocardio.Bean.UserInformationBean;
import com.electrocardio.Bean.loadfileBean;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by yangzheng on 2016/3/21.
 * 用户详情
 */
public abstract class UserInformationCallback extends Callback<UserInformationBean> {
    @Override
    public UserInformationBean parseNetworkResponse(Response response) throws Exception {

        String string = response.body().string();
        UserInformationBean mUserInformationBean = new Gson().fromJson(string, UserInformationBean.class);
        return mUserInformationBean;

    }


}
