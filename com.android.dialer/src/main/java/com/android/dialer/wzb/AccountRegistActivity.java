package com.android.dialer.wzb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import org.linphone.R;

import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneUtils;
import org.linphone.core.LinphoneAccountCreator;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.LinphonePreferences.AccountBuilder;
import org.linphone.core.LinphoneAddress.TransportType;
import org.linphone.wzb.Wlog;
import org.linphone.wzb.util.CustomDialog;

/**
 * Created by Administrator on 2018-02-02.
 */

public class AccountRegistActivity extends Activity implements LinphoneAccountCreator.LinphoneAccountCreatorListener{

    private LinphoneCoreListenerBase mListener;
    private LinphoneAddress address;
    private boolean accountCreated = false, newAccount = false, isLink = false, fromPref = false;
    private LinphoneAccountCreator accountCreator;
    private LinphonePreferences mPrefs;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accountregist);

        mPrefs = LinphonePreferences.instance();
        accountCreator = LinphoneCoreFactory.instance().createAccountCreator(LinphoneManager.getLc(), LinphonePreferences.instance().getXmlrpcUrl());
        accountCreator.setDomain(getResources().getString(R.string.default_domain));
        accountCreator.setListener(this);

        mListener = new LinphoneCoreListenerBase() {

            @Override
            public void configuringStatus(LinphoneCore lc, final LinphoneCore.RemoteProvisioningState state, String message) {
               // if (progress != null) progress.dismiss();
                if (state == LinphoneCore.RemoteProvisioningState.ConfiguringSuccessful) {
                    //goToLinphoneActivity(); //del by wzb for test
                } else if (state == LinphoneCore.RemoteProvisioningState.ConfiguringFailed) {
                   // Toast.makeText(AssistantActivity.instance(), getString(R.string.remote_provisioning_failure), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {
                Wlog.e("AccountRegistActivity registrationState accountCreated="+accountCreated+" newAccount="+newAccount+" address="+address.asString()+" :"+cfg.getAddress().asString());
                if (accountCreated && !newAccount){
                    if (address != null && address.asString().equals(cfg.getAddress().asStringUriOnly()) ) {
                        if (state == LinphoneCore.RegistrationState.RegistrationOk) {
                            Wlog.e("AccountRegistActivity RegistrationOk");

                            if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
                                accountCreator.isAccountUsed();
                            }
                           // CustomDialog.dismissDialog();
                           // completed("OK");
                            mHandler.sendEmptyMessageDelayed(1000,2000);
                        } else if (state == LinphoneCore.RegistrationState.RegistrationFailed) {

                            Wlog.e("AccountRegistActivity RegistrationFailed");
                           // CustomDialog.dismissDialog();
                          //  completed("FAIL");
                            mHandler.sendEmptyMessageDelayed(1001,2000);
                        } else if(!(state == LinphoneCore.RegistrationState.RegistrationProgress)) {

                            Wlog.e("AccountRegistActivity RegistrationProgress");
                           // CustomDialog.dismissDialog();
                          //  completed("EXP");
                            mHandler.sendEmptyMessageDelayed(1003,2000);
                        }
                    }
                }
            }
        };
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1000://ok
                    completed("OK");
                    break;
                case 1001://fail
                    completed("FAIL");
                    break;
                case 1002://exp
                    completed("EXP");
                    break;
                case 1006:
                    completed("EXSIT");
                    break;
                case 1009://15s timeout
                    completed("TIMEOUT");
                    break;
                default:
                    completed("EXP");
                    break;

            }
        }
    };

    @Override
    public void onBackPressed() {

       // super.onBackPressed();
    }

    private void completed(String result){
        CustomDialog.dismissDialog();
        Intent intent=new Intent();
        intent.putExtra("result",result);
        setResult(1823,intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            Wlog.e("AccountRegistActivity add listener");
            lc.addListener(mListener);
        }

        CustomDialog.showWaitDialog(AccountRegistActivity.this,"正在注册到服务器...",false);
        String username=getIntent().getStringExtra("username");
        String password=getIntent().getStringExtra("password");
        String domain=getIntent().getStringExtra("domain");
        String transports=getIntent().getStringExtra("transport");
        String displayname=getIntent().getStringExtra("displayename");
        Wlog.e("AccountRegistActivity onResume");
        Wlog.e("username="+username+" password="+password+" domain="+domain+" transports="+transports+" displayname="+displayname);
        if(displayname==null){
            displayname=username;
        }
        if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)||TextUtils.isEmpty(transports)||TextUtils.isEmpty(domain)){
            completed("EXP");
        }
        TransportType transport;
        if(transports.equals("UDP")){
            transport=TransportType.LinphoneTransportUdp;
        }else if(transports.equals("TLS")){
            transport = TransportType.LinphoneTransportTls;
        }else{
            transport = TransportType.LinphoneTransportTcp;
        }
        //check sip url
        String identity = "sip:" + username + "@" + domain;
        if(checkSipAcc(identity)) {
            genericLogIn(username, password, displayname, null, domain, transport);
            mHandler.sendEmptyMessageDelayed(1009, 15 * 1000);
        }else{
            mHandler.sendEmptyMessageDelayed(1006, 2000);
        }

    }

    private boolean checkSipAcc(String url){
        int accountNum=LinphoneManager.getLc().getProxyConfigList().length;

        if(accountNum>0) {
            for (LinphoneProxyConfig proxyConfig : LinphoneManager.getLc().getProxyConfigList()) {
                if(proxyConfig.getAddress().asStringUriOnly().equals(url)){

                    return false;
                }

            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.removeListener(mListener);
        }
        super.onPause();
        CustomDialog.dismissDialog();
    }

    private void logIn(String username, String password, String displayname,String ha1, String prefix, String domain, LinphoneAddress.TransportType transport, boolean sendEcCalibrationResult) {
        saveCreatedAccount(username, password,displayname, ha1, prefix, domain, transport);
    }

    public void retryLogin(String username, String password,String displayname, String prefix, String domain, LinphoneAddress.TransportType transport) {
        accountCreated = false;
        saveCreatedAccount(username, password,displayname, null, prefix, domain, transport);
    }

    public void genericLogIn(String username, String password,String displayname, String prefix, String domain, LinphoneAddress.TransportType transport) {
        if (accountCreated) {
            retryLogin(username, password,displayname, prefix, domain, transport);
        } else {
            logIn(username, password, displayname,null, prefix, domain, transport, false);
        }
    }

    public void saveCreatedAccount(String username, String password,String displayname, String prefix, String ha1, String domain, LinphoneAddress.TransportType transport) {
        Wlog.e("saveCreatedAccount accountCreated="+accountCreated);
        if (accountCreated)
            return;

        username = LinphoneUtils.getDisplayableUsernameFromAddress(username);
        domain = LinphoneUtils.getDisplayableUsernameFromAddress(domain);

        String identity = "sip:" + username + "@" + domain;
        try {
            address = LinphoneCoreFactory.instance().createLinphoneAddress(identity);
        } catch (LinphoneCoreException e) {
            Wlog.e(e.toString());
        }

        boolean isMainAccountLinphoneDotOrg = domain.equals(getString(R.string.default_domain));
        AccountBuilder builder = new AccountBuilder(LinphoneManager.getLc())
                .setUsername(username)
                .setDomain(domain)
                .setHa1(ha1)
                .setDisplayName(displayname)
                .setPassword(password);

        if(prefix != null){
            builder.setPrefix(prefix);
        }

        if (isMainAccountLinphoneDotOrg) {
            if (getResources().getBoolean(R.bool.disable_all_security_features_for_markets)) {
                builder.setProxy(domain)
                        .setTransport(TransportType.LinphoneTransportTcp);
            }
            else {
                builder.setProxy(domain)
                        .setTransport(TransportType.LinphoneTransportTls);
            }

            builder.setExpires("604800")
                    .setAvpfEnabled(true)
                    .setAvpfRRInterval(3)
                    .setQualityReportingCollector("sip:voip-metrics@sip.linphone.org")
                    .setQualityReportingEnabled(true)
                    .setQualityReportingInterval(180)
                    .setRealm("sip.linphone.org")
                    .setNoDefault(false);

            mPrefs.enabledFriendlistSubscription(getResources().getBoolean(R.bool.use_friendlist_subscription));

            mPrefs.setStunServer(getString(R.string.default_stun));
            mPrefs.setIceEnabled(true);

            accountCreator.setPassword(password);
            accountCreator.setHa1(ha1);
            accountCreator.setUsername(username);
        } else {
            String forcedProxy = "";
            if (!TextUtils.isEmpty(forcedProxy)) {
                builder.setProxy(forcedProxy)
                        .setOutboundProxyEnabled(true)
                        .setAvpfRRInterval(5);
            }

            if(transport != null) {
                builder.setTransport(transport);
            }
        }

        if (getResources().getBoolean(R.bool.enable_push_id)) {
            String regId = mPrefs.getPushNotificationRegistrationID();
            String appId = getString(R.string.push_sender_id);
            if (regId != null && mPrefs.isPushNotificationEnabled()) {
                String contactInfos = "app-id=" + appId + ";pn-type=google;pn-tok=" + regId;
                builder.setContactParameters(contactInfos);
            }
        }

        try {
            builder.saveNewAccount();
            if(!newAccount) {
               // displayRegistrationInProgressDialog();
               // CustomDialog.showWaitDialog(AccountRegistActivity.this,"正在注册到服务器...");
            }
            accountCreated = true;
        } catch (LinphoneCoreException e) {
            Wlog.e("LinphoneCoreException"+e.toString());
        }

    }



    @Override
    public void onAccountCreatorIsAccountUsed(LinphoneAccountCreator linphoneAccountCreator, LinphoneAccountCreator.Status status) {

    }

    @Override
    public void onAccountCreatorAccountCreated(LinphoneAccountCreator linphoneAccountCreator, LinphoneAccountCreator.Status status) {

    }

    @Override
    public void onAccountCreatorAccountActivated(LinphoneAccountCreator linphoneAccountCreator, LinphoneAccountCreator.Status status) {

    }

    @Override
    public void onAccountCreatorAccountLinkedWithPhoneNumber(LinphoneAccountCreator linphoneAccountCreator, LinphoneAccountCreator.Status status) {

    }

    @Override
    public void onAccountCreatorPhoneNumberLinkActivated(LinphoneAccountCreator linphoneAccountCreator, LinphoneAccountCreator.Status status) {

    }

    @Override
    public void onAccountCreatorIsAccountActivated(LinphoneAccountCreator linphoneAccountCreator, LinphoneAccountCreator.Status status) {

    }

    @Override
    public void onAccountCreatorPhoneAccountRecovered(LinphoneAccountCreator linphoneAccountCreator, LinphoneAccountCreator.Status status) {

    }

    @Override
    public void onAccountCreatorIsAccountLinked(LinphoneAccountCreator linphoneAccountCreator, LinphoneAccountCreator.Status status) {

    }

    @Override
    public void onAccountCreatorIsPhoneNumberUsed(LinphoneAccountCreator linphoneAccountCreator, LinphoneAccountCreator.Status status) {

    }

    @Override
    public void onAccountCreatorPasswordUpdated(LinphoneAccountCreator linphoneAccountCreator, LinphoneAccountCreator.Status status) {

    }
}
