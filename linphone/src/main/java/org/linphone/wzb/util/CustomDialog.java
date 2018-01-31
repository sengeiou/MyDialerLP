package org.linphone.wzb.util;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.linphone.R;
import org.linphone.wzb.widget.FlyDialog;


/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 8, 2017 3:34:34 PM
 */
public class CustomDialog {

	private static FlyDialog dialog;
	private static View view;

	/**
	 * 显示默认对话框.
	 *
	 * @param context
	 *            上下文环境.
	 * @param view
	 *            视图.
	 * @param isCancel
	 *            是否可取消.
	 */
	public static void showDefaultDialog(Context context, View view, boolean isCancel) {
		if (context == null) {
			return;
		}
		dialog = new FlyDialog(context, R.style.DefalutDialog);

		dialog.setCancelable(isCancel);
		dialog.setCanceledOnTouchOutside(isCancel);
		dialog.setContentView(view);
		dialog.showBounceTopEenter(view);
		/*
		 * 将对话框的大小按屏幕大小的百分比设置
		 */
		Window dialogWindow = dialog.getWindow();
		WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.7
		dialogWindow.setAttributes(p);
		// dialogWindow.setGravity(Gravity.BOTTOM);
		dialog.show();
	}

	/**
	 * 显示默认对话框.
	 *
	 * @param context
	 *            上下文环境.
	 * @param view
	 *            视图.
	 * @param isCancel
	 *            是否可取消.
	 */
	public static void showNoSizeDefaultDialog(Context context, View view, boolean isCancel) {
		if (context == null) {
			return;
		}
		dialog = new FlyDialog(context, R.style.WaitDialog);
		dialog.setCancelable(isCancel);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		// dialog.showBounceEenter(view);
		dialog.show();
	}

	/**
	 * 显示等待对话框.
	 *
	 * @param context
	 *            上下文环境.
	 */
	public static void showWaitDialog(Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.dialog_wait, null);
		showNoSizeDefaultDialog(context, view, true);
	}
	
	//add by wzb
	private static TextView waitAndCancelTxt=null;
	public static void showWaitAndCancelDialog(Context context, String msg,View.OnClickListener cancleListener){
		view = LayoutInflater.from(context).inflate(R.layout.dialog_wait2, null);
		waitAndCancelTxt = (TextView) view.findViewById(R.id.txt_dialog_msg);
		waitAndCancelTxt.setText(msg);
		Button bt_cancel = (Button) view.findViewById(R.id.txt_dialog_btn_cancle);
		bt_cancel.setOnClickListener(cancleListener);
		showNoSizeDefaultDialog(context, view, true);
	}
	
	public static void setWaitAndCancelTxt(String txt){
		if(waitAndCancelTxt!=null){
			waitAndCancelTxt.setText(txt);
		}
	}

	/**
	 * 显示等待对话框.
	 *
	 * @param context
	 *            上下文环境.
	 */
	public static void showWaitDialog(Context context, String msg) {
		view = LayoutInflater.from(context).inflate(R.layout.dialog_wait, null);
		TextView txt_dialog_msg = (TextView) view.findViewById(R.id.txt_dialog_msg);
		txt_dialog_msg.setText(msg);
		showNoSizeDefaultDialog(context, view, true);
	}

	/**
	 * 显示等待对话框.
	 *
	 * @param context
	 *            上下文环境.
	 */
	public static void showWaitDialog(Context context, int msg) {
		view = LayoutInflater.from(context).inflate(R.layout.dialog_wait, null);
		TextView txt_dialog_msg = (TextView) view.findViewById(R.id.txt_dialog_msg);
		txt_dialog_msg.setText(context.getString(msg));
		showNoSizeDefaultDialog(context, view, true);
	}

	/**
	 * 普通对话框
	 *
	 * @param context
	 *            上下文环境.
	 */
	public static void showOkAndCalcelDialog(Context context, String title, String msg, boolean isShowCancel,
			View.OnClickListener okListenter, View.OnClickListener cancleListener) {
		if (context == null) {
			return;
		}
		view = LayoutInflater.from(context).inflate(R.layout.dialog_defalut, null);
		TextView tv_title, tv_msg, tv_line;
		Button bt_ok, bt_cancel;
		tv_title = (TextView) view.findViewById(R.id.txt_dialog_title);
		tv_msg = (TextView) view.findViewById(R.id.txt_dialog_msg);
		bt_ok = (Button) view.findViewById(R.id.btn_ok);
		bt_cancel = (Button) view.findViewById(R.id.btn_cancle);
		tv_line = (TextView) view.findViewById(R.id.txt_line);
		if (isShowCancel) {
			bt_cancel.setVisibility(View.GONE);
			bt_ok.setBackgroundResource(R.drawable.default_btn_selector1);
			tv_line.setVisibility(View.GONE);
		}
		tv_title.setText(title);
		tv_msg.setText(msg);
		bt_ok.setOnClickListener(okListenter);
		bt_cancel.setOnClickListener(cancleListener);
		showDefaultDialog(context, view, false);
	}
	
	public static void showOkAndCalcelDialog(Context context, String title, String msg, boolean isShowCancel,
			View.OnClickListener okListenter, View.OnClickListener cancleListener,String ok,String cancel) {
		if (context == null) {
			return;
		}
		view = LayoutInflater.from(context).inflate(R.layout.dialog_defalut, null);
		TextView tv_title, tv_msg, tv_line;
		Button bt_ok, bt_cancel;
		tv_title = (TextView) view.findViewById(R.id.txt_dialog_title);
		tv_msg = (TextView) view.findViewById(R.id.txt_dialog_msg);
		bt_ok = (Button) view.findViewById(R.id.btn_ok);
		bt_cancel = (Button) view.findViewById(R.id.btn_cancle);
		tv_line = (TextView) view.findViewById(R.id.txt_line);
		if (isShowCancel) {
			bt_cancel.setVisibility(View.GONE);
			bt_ok.setBackgroundResource(R.drawable.default_btn_selector1);
			tv_line.setVisibility(View.GONE);
		}
		tv_title.setText(title);
		tv_msg.setText(msg);
		bt_ok.setText(ok);
		bt_cancel.setText(cancel);
		bt_ok.setOnClickListener(okListenter);
		bt_cancel.setOnClickListener(cancleListener);
		showDefaultDialog(context, view, false);
	}


	/**
	 * 普通对话框
	 *
	 * @param context
	 *            上下文环境.
	 */
	public static void showOkAndCalcelDialog(Context context, String title, String msg,
			View.OnClickListener okListenter, View.OnClickListener cancleListener) {
		showOkAndCalcelDialog(context, title, msg, false, okListenter, cancleListener);
	}

	/**
	 * 关闭对话框.
	 */
	public static void dismissDialog() {
		if (null != dialog) {
			dialog.dismiss();
		}
	}

}
