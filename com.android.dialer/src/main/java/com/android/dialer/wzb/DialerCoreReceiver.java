package com.android.dialer.wzb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.dialer.DialtactsActivity;

import org.linphone.CallActivity;
import org.linphone.CallIncomingActivity;
import org.linphone.CallOutgoingActivity;
import org.linphone.LinphoneApplication;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.core.LinphoneCall;
import org.linphone.wzb.CommonAction;
import org.linphone.wzb.util.ToastUtil;

/**
 * Created by Administrator on 2018-01-29.
 */

public class DialerCoreReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        android.util.Log.e("wzb","DialerCoreReceiver action="+action);
        if(action.equals("com.custom.lp.CALLINCOMING_ANSWER")){
            if(!DialtactsActivity.isInstanciated()){
                return;
            }else{
                if(LinphoneApplication.curCall==null){
                    return;
                }else {
                    LinphoneManager.getInstance().routeAudioToReceiver();
                    DialtactsActivity.instance().startIncallActivity(LinphoneApplication.curCall);
                }
            }
        }else if(action.equals("com.custom.lp.CALLOUTGOING")){
            if(!DialtactsActivity.isInstanciated()){
                return;
            }else{
                if(LinphoneApplication.curCall==null){
                    return;
                }else {
                    LinphoneManager.getInstance().routeAudioToReceiver();
                    DialtactsActivity.instance().startIncallActivity(LinphoneApplication.curCall);
                }
            }
        }else if(action.equals("com.custom.lp.NetWork_err")){
            if(DialtactsActivity.isInstanciated()){
                ToastUtil.showLongToast(DialtactsActivity.instance(),"Error network unreachable");
            }
        }else if(action.equals("com.custom.lp.IncomingReceived")){
            Intent mintent=new Intent(context,DialtactsActivity.class);
            mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mintent);
        }
        //delete account
        else if(action.equals("com.custom.lp.DEL_ACCOUNT")){
            String url=intent.getStringExtra("sipurl");
            if(!TextUtils.isEmpty(url)){
                LinphonePreferences.instance().deleteAccountBySipUrl(url);
            }
        }
        //set default account
        else if(action.equals("com.custom.lp.SET_DEFAULT")){
            String defaultsip=intent.getStringExtra("defaultsip");
            if(!TextUtils.isEmpty(defaultsip)){
                LinphonePreferences.instance().setDefaultAccountBySipUrl(defaultsip);
            }
        }
    }


}
