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
import org.linphone.LinphoneService;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.wzb.CommonAction;
import org.linphone.wzb.Wlog;
import org.linphone.wzb.util.ToastUtil;

import java.text.SimpleDateFormat;

import static android.content.Intent.ACTION_MAIN;

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
        else if(action.equals("com.custom.lp_GOTO_CALL")){
            String number=intent.getStringExtra("number");
            String displayname=intent.getStringExtra("name");
            if(!TextUtils.isEmpty(number))DialtactsActivity.instance().gotoCall(number,displayname);
        }else if(action.equals("com.custom.lp_DIRECT_CALL")){
            String number=intent.getStringExtra("number");
            String displayname=intent.getStringExtra("name");
            if(!TextUtils.isEmpty(number))callOutgoing(context,number,displayname);
        }else if(action.equals("com.android.custom.hall_up")){
            Wlog.e("######app"+MyLifecycleHandler.isApplicationInForeground());
            if(!MyLifecycleHandler.isApplicationInForeground()){
                context.startActivity(new Intent()
                        .setClass(context, DialtactsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            hall_up();
            //test
            String temp_date=new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(System.currentTimeMillis());
            final LinphoneCall call = LinphoneManager.getLc().getCurrentCall();
           // LinphoneCallParams params = LinphoneManager.getLc().createCallParams(call);
           // params.setRecordFile("/sdcard/"+temp_date+"_lprecord.wav");
            //int ret=LinphoneManager.getLc().updateCall(call, params);
            //Wlog.e("set params ret="+ret);
            call.startRecording();
            //end
        }else if(action.equals("com.android.custom.hall_down")){
            hall_down();
            LinphoneManager.getLc().getCurrentCall().stopRecording();
        }else if(action.equals("com.android.custom.boot_completed")){
            context.startService(new Intent(ACTION_MAIN).setClass(context, LinphoneService.class));
        }
    }


    private void hall_up(){
        GpioCtrl.gpioSet(87,1);
        GpioCtrl.gpioSet(95,0);

        GpioCtrl.gpioSet(98,0);
        GpioCtrl.gpioSet(1,1);
    }

    private void hall_down(){
        GpioCtrl.gpioSet(1,0);


        GpioCtrl.gpioSet(60,1);
        GpioCtrl.gpioSet(95,1);
    }


    private void callOutgoing(Context context,String number,String name) {
        try {
            if (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) {

                LinphoneManager.getInstance().newOutgoingCall(number, name);

                context.startActivity(new Intent()
                        .setClass(context, DialtactsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }
        } catch (LinphoneCoreException e) {
            LinphoneManager.getInstance().terminateCall();
        }

        try {

        } catch (Exception e) {

        }
    }

}
