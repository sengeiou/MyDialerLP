package com.android.dialer.wzb;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.Reason;
import org.linphone.wzb.Wlog;

/**
 * Created by Administrator on 2018-02-01.
 */

public class RegisterListenService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    private LinphoneCoreListenerBase mListener=new LinphoneCoreListenerBase(){


        @Override
        public void registrationState(LinphoneCore lc, LinphoneProxyConfig proxy, LinphoneCore.RegistrationState state, String smessage) {
            if (state.equals(LinphoneCore.RegistrationState.RegistrationCleared)) {
                if (lc != null) {
                    LinphoneAuthInfo authInfo = lc.findAuthInfo(proxy.getIdentity(), proxy.getRealm(), proxy.getDomain());
                    if (authInfo != null)
                        lc.removeAuthInfo(authInfo);
                }
            }

            refreshAccounts();

            if(getResources().getBoolean(org.linphone.R.bool.use_phone_number_validation)) {
                if (state.equals(LinphoneCore.RegistrationState.RegistrationOk)) {
                    LinphoneManager.getInstance().isAccountWithAlias();
                }
            }

        }

    };

    private void refreshAccounts(){
        int accountNum=LinphoneManager.getLc().getProxyConfigList().length;
        Wlog.e("RegisterListenService accountNum="+accountNum);
        if(accountNum>0) {
            String allAccount = "sip;sip";
            for (LinphoneProxyConfig proxyConfig : LinphoneManager.getLc().getProxyConfigList()) {

               // Wlog.e("RegisterListenService account:" + proxyConfig.getAddress().asStringUriOnly());
               // Wlog.e("RegisterListenService account state:" + proxyConfig.getState());

                allAccount = allAccount + ";" + proxyConfig.getAddress().asStringUriOnly() + ";" + proxyConfig.getState();

            }

            String defaultAccount = LinphoneManager.getLc().getDefaultProxyConfig().getAddress().asStringUriOnly();
            //Wlog.e("RegisterListenService defaultAccount=" + defaultAccount);

            Intent allAccountIntent=new Intent("com.custom.lp.ALL_ACCOUNT");
            allAccountIntent.putExtra("all_account",allAccount);
            sendBroadcast(allAccountIntent);

            Intent defaultAccountIntent=new Intent("com.custom.lp.DEFAULT_ACCOUNT");
            defaultAccountIntent.putExtra("default_account",defaultAccount);
            sendBroadcast(defaultAccountIntent);
        }
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        Wlog.e("RegisterListenService onStartCommand");
        try {
            LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
            if (lc != null) {
                lc.removeListener(mListener);
                lc.addListener(mListener);
            }
        }catch (Exception e){
            Wlog.e("RegisterListenService onStartCommand addListener err");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Wlog.e("RegisterListenService onDestroy");
        try{
            LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
            if (lc != null) {
                lc.removeListener(mListener);
            }
        }catch (Exception e){
            Wlog.e("RegisterListenService onDestroy removeListener err");
        }
    }
}
