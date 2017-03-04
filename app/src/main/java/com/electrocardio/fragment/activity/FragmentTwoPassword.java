package com.electrocardio.fragment.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.electrocardio.Bean.UserInformation;
import com.electrocardio.Bean.UserNumberBean;
import com.electrocardio.Callback.UserNumberCallBack;
import com.electrocardio.R;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.Md5;
import com.electrocardio.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * Created by yangzheng on 2016/5/24.
 * 填写密码
 */
public class FragmentTwoPassword extends Fragment implements View.OnClickListener {

    private UserInformation userInformation;
    private EditText mp;
    private EditText smp;
    private Button mButtonLogin;
    private RelativeLayout mStatefinish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.change_password_layout2, container, false);
        mp = (EditText) view.findViewById(R.id.password);
        smp = (EditText) view.findViewById(R.id.mpassword);
        mStatefinish = (RelativeLayout) view.findViewById(R.id.activity_finish);
        mButtonLogin = (Button)view.findViewById(R.id.button_login);
        initdata();

        return view;
    }

    private void initdata() {

        userInformation = UserDao.getInstance(getActivity()).getUser();
        mp.setFocusable(true);
        mp.setFocusableInTouchMode(true);
        mp.requestFocus();
        mButtonLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login:
                String spassword = mp.getText().toString();
                String sspassword = smp.getText().toString();

                //ToastUtils.ToastMessage(getActivity(), "密码:"+getPassword(spassword));
                if (spassword.equals("") || sspassword.equals("")) {
                    ToastUtils.ToastMessage(getActivity(), "请填写密码");
                    break;
                }
                if(spassword.length()<6||sspassword.length()<6){
                    ToastUtils.ToastMessage(getActivity(), "密码必须大于6位");
                    break;
                }
                if(!getPassword(spassword)||!getPassword(sspassword)){
                    ToastUtils.ToastMessage(getActivity(), "密码格式不正确");
                    break;
                }
                if (!spassword.toLowerCase().equals(sspassword.toLowerCase())) {
                    ToastUtils.ToastMessage(getActivity(), "两次密码输入不一致");
                    break;
                }
                String mphone = userInformation.getPhoneNumber();
                postPhoneCade(mphone, spassword.toLowerCase());
                break;
        }

    }
/*    private boolean getPassword(String userName){
        String regEx = "^[a-zA-Z][a-zA-Z0-9_]{5,19}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(userName);
        return matcher.matches();
    }*/

 /*   private boolean getPassword(String userName) {
        String regEx = "^[a-zA-Z0-9]{6,19}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(userName);
        return matcher.matches();
    }*/
 public static boolean getPassword(String password) {
     String string = "^[Za-z0-9`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]+$";
     Pattern mPattern = Pattern.compile(string);
     Matcher matcher = mPattern.matcher(password);
     return matcher.matches();
 }

    /*public static boolean getPassword(String password) {
        String string = "^[\u4E00-\u9FA5A-Za-z0-9`~!@#$%^&*()+=|{}':;',.<>/?~@#￥%……&;*——+|{}【】‘；：”“’。，、？|-]+$";
        Pattern mPattern = Pattern.compile(string);
        Matcher matcher = mPattern.matcher(password);
        return matcher.matches();
    }*/
    public void postPhoneCade(String mphone, String password)
    {

        String url = "http://realtime.ddduo.com/realtime/AppServiceController/appLoginPassword";
        // System.out.println("password:"+password);
        Map<String, String> params = new HashMap<>();
        params.put("phone", mphone);
        params.put("password",  Md5.getMD5Str(password));
        params.put("findType", "02");
        params.put("userCode", "");

        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new mpasswordCallBack());
           /*     .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String s) {
                        System.out.println("s:" + s);
                    }
                });*/
    }

    private class mpasswordCallBack extends UserNumberCallBack {


        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(UserNumberBean userNumberBean) {
            String Code = userNumberBean.getStatusCode();
            if(Code.equals("010")){
                FragmentThreePassword fThree = new FragmentThreePassword() {
                    @Override
                    public boolean onBackPressed() {
                        return true;
                    }
                };
                FragmentManager fm = getFragmentManager();
                FragmentTransaction tx = fm.beginTransaction();
                tx.replace(R.id.id_content, fThree, "Three");
                // tx.addToBackStack(null);
                tx.commit();
            }


        }
    }

}
