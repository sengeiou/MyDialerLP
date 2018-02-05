package com.android.dialer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2018/2/3.
 */

public class SipAccountState {
    private static final int BUTTON_SIP1 = 0;
    private static final int BUTTON_SIP2 = 1;
    private static final int BUTTON_SIP3 = 2;
    private static final int BUTTON_SIP4 = 3;
    private static final int STATE_DISABLED = 0;
    private static final int STATE_ENABLED = 1;
    private static final int STATE_UNKNOWN = 2;
    private static final int STATE_DEFAULT = 10;
    private static final int STATE_NORMAL = 101;
    private static final String REG_NONE = "RegistrationNone";
    private static final String REG_FAIL = "RegistrationFailed";
    private static final String REG_PROGRESS = "RegistrationProgress";
    private static final String REG_OK = "RegistrationOk";
    private static final String REG_CLEAR = "RegistrationCleared";

    private static final String SIP1 = "sip1";
    private static final String SIP2 = "sip2";
    private static final String SIP3 = "sip3";
    private static final String SIP4 = "sip4";

    private static final String SIPACCOUNT = "account";
    private static final String SIPSTATE = "state";

    private static final int[] STATE_DRAWABLE = {
            R.drawable.offline,
            R.drawable.online,
            R.drawable.error
    };

    public  void handleSipDefaultAccount(String data,Context context,View view)
    {
        Button sip1AccountBtn =(Button)view.findViewById(R.id.sip1);
        Button sip2AccountBtn =(Button)view.findViewById(R.id.sip2);
        Button sip3AccountBtn =(Button)view.findViewById(R.id.sip3);
        Button sip4AccountBtn =(Button)view.findViewById(R.id.sip4);
        Log.e("sip","default data = "+data);
        if(isDefaultAccount(data,SIP1,context))
        {
            sip1AccountBtn.setText(R.string.sip1default);
        }
        else if(isDefaultAccount(data,SIP2,context))
        {
            sip2AccountBtn.setText(R.string.sip2default);
        }
        else if(isDefaultAccount(data,SIP3,context))
        {
            sip3AccountBtn.setText(R.string.sip3default);
        }
        else if(isDefaultAccount(data,SIP4,context))
        {
            sip4AccountBtn.setText(R.string.sip4default);
        }
    }
    public  boolean isDefaultAccount(String data,String sipKey,Context context)
    {
        SharedPreferences sip=context.getSharedPreferences(sipKey, 0);
        String sipAccount = sip.getString(SIPACCOUNT,"默认值");
        Log.e("sip","sipAccount = "+sipAccount);
        if(data!=null&&sipAccount!=null) {
            if (data.equals(sipAccount)) {
                return true;
            }
        }
        return false;
    }
    public  void storeSipAccount(Context context,String prName,String putKey1,String putKeyValue1,String putKey2,String putKeyValue2)
    {
        SharedPreferences padded =context.getSharedPreferences(prName, 0);
        SharedPreferences.Editor editor = padded.edit();
        editor.putString(putKey1,putKeyValue1);
        editor.putString(putKey2,putKeyValue2);
        editor.commit();
    }
    public  void handleSipAccount(String data, Context context, View view)
    {
        String[] temp = null;
        String[] userAccount= new String[10];
        String[] state =new String[10];;
        int sip1Sta=0;
        int sip2Sta=0;
        int sip3Sta=0;
        int sip4Sta=0;
        Button sip1AccountBtn =(Button)view.findViewById(R.id.sip1);
        Button sip2AccountBtn =(Button)view.findViewById(R.id.sip2);
        Button sip3AccountBtn =(Button)view.findViewById(R.id.sip3);
        Button sip4AccountBtn =(Button)view.findViewById(R.id.sip4);
        temp = data.split(";");
        int count=0;
        for(int i=0;i<temp.length;i++)
        {
            if(temp[i].startsWith("sip:"))
            {
                userAccount[count] = temp[i];
                state[count] =temp[i+1];
                count++;
            }
        }

        if(count>0)
        {
            for(int k=0;k<count;k++) {
                Log.e("sip","userAccount = "+k+userAccount[k]+"state = "+state[k]);
                switch (k) {
                    case 0:
                        if(state[k].equals(REG_NONE)||state[k].equals(REG_CLEAR))
                        {
                            sip1Sta=STATE_UNKNOWN;

                        }
                        else if(state[k].equals(REG_OK))
                        {
                            sip1Sta=STATE_ENABLED;
                        }
                        else
                        {
                            sip1Sta = STATE_DISABLED;
                        }
                        storeSipAccount(context,SIP1,SIPACCOUNT,userAccount[k],SIPSTATE,state[k]);
                        //sSip1.onActualStateChange(context, intent, sip1Sta);
                        Drawable drawable= context.getResources().getDrawable(STATE_DRAWABLE[sip1Sta]);
                        /// 这一步必须要做,否则不会显示.
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        sip1AccountBtn.setCompoundDrawables(drawable,null,null,null);
                        break;
                    case 1:
                        if(state[k].equals(REG_NONE)||state[k].equals(REG_CLEAR))
                        {
                            sip2Sta=STATE_UNKNOWN;
                        }
                        else if(state[k].equals(REG_OK))
                        {
                            sip2Sta=STATE_ENABLED;
                        }
                        else
                        {
                            sip2Sta = STATE_DISABLED;
                        }
                        storeSipAccount(context,SIP2,SIPACCOUNT,userAccount[k],SIPSTATE,state[k]);
                        Drawable drawable1= context.getResources().getDrawable(STATE_DRAWABLE[sip2Sta]);
                        /// 这一步必须要做,否则不会显示.
                        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                        sip2AccountBtn.setCompoundDrawables(drawable1,null,null,null);
                        break;
                    case 2:
                        if(state[k].equals(REG_NONE)||state[k].equals(REG_CLEAR))
                        {
                            sip3Sta=STATE_UNKNOWN;
                        }
                        else if(state[k].equals(REG_OK))
                        {
                            sip3Sta=STATE_ENABLED;
                        }
                        else
                        {
                            sip3Sta = STATE_DISABLED;
                        }
                        storeSipAccount(context,SIP3,SIPACCOUNT,userAccount[k],SIPSTATE,state[k]);
                        Drawable drawable2= context.getResources().getDrawable(STATE_DRAWABLE[sip3Sta]);
                        /// 这一步必须要做,否则不会显示.
                        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                        sip3AccountBtn.setCompoundDrawables(drawable2,null,null,null);
                        break;
                    case 3:
                        if(state[k].equals(REG_NONE)||state[k].equals(REG_CLEAR))
                        {
                            sip4Sta=STATE_UNKNOWN;
                        }
                        else if(state[k].equals(REG_OK))
                        {
                            sip4Sta=STATE_ENABLED;
                        }
                        else
                        {
                            sip4Sta = STATE_DISABLED;
                        }
                        storeSipAccount(context,SIP4,SIPACCOUNT,userAccount[k],SIPSTATE,state[k]);
                        Drawable drawable3= context.getResources().getDrawable(STATE_DRAWABLE[sip4Sta]);
                        /// 这一步必须要做,否则不会显示.
                        drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
                        sip4AccountBtn.setCompoundDrawables(drawable3,null,null,null);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
