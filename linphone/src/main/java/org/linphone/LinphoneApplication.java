package org.linphone;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import org.linphone.core.LinphoneCall;

import java.util.ArrayList;
import java.util.List;





/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 8, 2017 2:54:00 PM
 */
public class LinphoneApplication  {

	/**
	 * 全局上下文环境.
	 */
	public static Context CONTEXT;

	// public static UserBean user;
	/**
	 * 文件根目录
	 */
	//public static final String BASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
	//		+ "/setting/";
	/**
	 * 图片文件目录
	 */
	//public static final String IMAGE_FILE_PATH = BASE_FILE_PATH + "image/";

	/**
	 * 保存全部activity,便于管理
	 */
	public static List<Activity> activityList = new ArrayList<Activity>();

	public static boolean isNetWork = true;

	/**
	 * SP文件名.
	 */
	private final String SP_NAME = "setting";

	public static SQLiteDatabase db;

	public static int USER_LEVEL = 0;

	public static LinphoneCall curCall;

    public static void init(Context context){
        CONTEXT=context;
        curCall=null;
    }
	



}
