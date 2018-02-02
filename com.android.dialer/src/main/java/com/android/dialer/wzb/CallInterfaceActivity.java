package com.android.dialer.wzb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.dialer.DialtactsActivity;

import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCoreException;
import org.linphone.wzb.Wlog;

/**
 * Created by Administrator on 2018-01-31.
 */

public class CallInterfaceActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Wlog.e("CallInterfaceActivity onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wlog.e("CallInterfaceActivity onResume");
        Uri uri = getIntent().getData();
        if (uri != null) {
            String url = uri.toString();
            Wlog.e("url:" + url);
            String scheme = uri.getScheme();
            Wlog.e("scheme: " + scheme);
            String tel_number = url.substring(4);
            Wlog.e("tel_number=" + tel_number);
            callOutgoing(tel_number);
        }
    }

    private void callOutgoing(String number) {
        try {
            if (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) {
                String to = String.format("sip:%s@%s", number, "192.168.0.191");

                LinphoneManager.getInstance().newOutgoingCall(number, null);

                startActivity(new Intent()
                        .setClass(CallInterfaceActivity.this, DialtactsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                finish();
            }
        } catch (LinphoneCoreException e) {
            LinphoneManager.getInstance().terminateCall();
        }

        try {

        } catch (Exception e) {

        }
    }
}
