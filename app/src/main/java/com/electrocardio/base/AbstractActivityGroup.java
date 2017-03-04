package com.electrocardio.base;

import android.app.ActivityGroup;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public abstract class AbstractActivityGroup extends ActivityGroup {
	public String TAG = null;
	
	public AbstractActivityGroup() {
		TAG = this.getClass().getName();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//去掉title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//强制竖屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//设置软键盘不弹出
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}
	
	protected abstract void setChildContentView(int layoutResID);

	protected abstract String onTitle() ;
	
	/**
	 * 设置right button text
	 * @param
	 */

	protected abstract void setChildContentView(View view);
	
	/**
	 * 从上一个Activity intent中取出需要的数据
	 */
	protected abstract void onUpperActivityargs();
		
	/**
	 * 初始化 (变量，对象)
	 */
	protected abstract void onInitialize();	
	
	/**
	 * find 并 setup view event
	 */
	protected abstract void onFindViews();
	
	protected abstract void onRequest();	
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
			
	//activity stop or destroy  call onUserLeaveHint();
	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		return super.onCreateDialog(id, args);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		super.onPrepareDialog(id, dialog, args);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onPostResume() {
		super.onPostResume();
	}

	/**
	 * back键 event 
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 显示loading
	 */
	protected abstract void showProgress();
	
	/**
	 * 移除loading
	 */
	protected abstract void removeProgress();
//	
//	/**
//	 * 提示框，默认半秒钟消失
//	 * @param tip 提示信息
//	 */
//	protected abstract void showTip(String tip);
//	
//	/**
//	 * 展示提示框
//	 * @param tip  提示信息
//	 * @param displayTime 提示框展示时间
//	 */
//	protected abstract void showTip(String tip, long displayTime);
	
//	private OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener() {
//		@Override
//		public void onGlobalLayout() {
//			View contentView = getWindow().getDecorView();
//			Rect r = new Rect();
//			contentView.getWindowVisibleDisplayFrame(r);
//		}
//	};
	
	public String getStringFromBundle(Bundle bundle) {
		if (bundle == null || bundle.size() == 0) return " null or size zero";
		StringBuilder builder = new StringBuilder();
		builder.append("bundle:[");
		for (String key : bundle.keySet()) builder.append(key + ":" + bundle.get(key) + ";");
		builder.append("]");
		return builder.toString();
	}
	
	
	
	//需要忽略 监听dispatchTouchEvent 的方法
	String classNames = "CaseDetailActivity,SearchAllListActivity,TeamDynamicList,PatientsFromMenZhenActivity,PatientsDetailTabActivity";
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		String className = this.getClass().getSimpleName();
		int index = classNames.indexOf(className);
			if (index ==-1 && ev.getAction() == MotionEvent.ACTION_DOWN) {
			 
             // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
             View v = getCurrentFocus();
             if (isShouldHideInput(v, ev)) {
            	 InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                 imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
             }
            
         }
		return super.dispatchTouchEvent(ev);
	}
	
	
	 private boolean isShouldHideInput(View v, MotionEvent event) {
         if (v != null && (v instanceof EditText)) {
             int[] l = { 0, 0 };
             v.getLocationInWindow(l);
             int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
             if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                 // 点击EditText的事件，忽略它。
                 return false;
             } else {
                 return true;
             }
         }
         // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
         return false;
     }
	
}