package com.android.dialer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
//import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;

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
        String str=data.substring(4);
        TextView sip1Title =(TextView)view.findViewById(R.id.siptitle1);
        TextView sip2Title =(TextView)view.findViewById(R.id.siptitle2);
        TextView sip3Title =(TextView)view.findViewById(R.id.siptitle3);
        TextView sip4Title =(TextView)view.findViewById(R.id.siptitle4);

        TextView sip1name =(TextView)view.findViewById(R.id.sipname1);
        TextView sip2name =(TextView)view.findViewById(R.id.sipname2);
        TextView sip3name =(TextView)view.findViewById(R.id.sipname3);
        TextView sip4name =(TextView)view.findViewById(R.id.sipname4);

        Log.e("sip","default data = "+data);
        if(isDefaultAccount(data,SIP1,context))
        {
            sip1Title.setText(R.string.sip1default);
            sip2Title.setText(R.string.sip2);
            sip3Title.setText(R.string.sip3);
            sip4Title.setText(R.string.sip4);
          //  sip1name.setText(str);
        }
        else if(isDefaultAccount(data,SIP2,context))
        {
            sip2Title.setText(R.string.sip2default);
           // sip2name.setText(str);
            sip1Title.setText(R.string.sip1);
            sip3Title.setText(R.string.sip3);
            sip4Title.setText(R.string.sip4);
        }
        else if(isDefaultAccount(data,SIP3,context))
        {
            sip3Title.setText(R.string.sip3default);
           // sip3name.setText(str);
            sip1Title.setText(R.string.sip1);
            sip2Title.setText(R.string.sip2);
            sip4Title.setText(R.string.sip4);
        }
        else if(isDefaultAccount(data,SIP4,context))
        {
            sip4Title.setText(R.string.sip4default);
           // sip4name.setText(str);
            sip1Title.setText(R.string.sip1);
            sip2Title.setText(R.string.sip2);
            sip3Title.setText(R.string.sip3);
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
    public  void updateImageView(ImageView image,Context context,int state,String userAccount,TextView textview)
    {
        String str = userAccount.substring(4);
        if(image!=null&&textview!=null) {
            Drawable drawable = context.getResources().getDrawable(STATE_DRAWABLE[state]);
            image.setImageDrawable(drawable);
            textview.setText(str);
        }
    }
    public  int  getSipState(String stateStr)
    {
        int state;
        if(stateStr.equals(REG_NONE)||stateStr.equals(REG_CLEAR))
        {
            state=STATE_UNKNOWN;

        }
        else if(stateStr.equals(REG_OK))
        {
            state=STATE_ENABLED;
        }
        else
        {
            state = STATE_DISABLED;
        }
        return state;
    }
    public static  String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, "unknown" ));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return value;
        }
    }
    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value );
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        TextView sip1name =(TextView)view.findViewById(R.id.sipname1);
        TextView sip2name =(TextView)view.findViewById(R.id.sipname2);
        TextView sip3name =(TextView)view.findViewById(R.id.sipname3);
        TextView sip4name =(TextView)view.findViewById(R.id.sipname4);

        ImageView sip1AccountBtn =(ImageView)view.findViewById(R.id.sip1);
        ImageView sip2AccountBtn =(ImageView)view.findViewById(R.id.sip2);
        ImageView sip3AccountBtn =(ImageView)view.findViewById(R.id.sip3);
        ImageView sip4AccountBtn =(ImageView)view.findViewById(R.id.sip4);
        String sip1Account = getProperty("custom.lp.sip1","custom.lp.sip");
        String sip2Account = getProperty("custom.lp.sip2","custom.lp.sip");
        String sip3Account = getProperty("custom.lp.sip3","custom.lp.sip");
        String sip4Account = getProperty("custom.lp.sip4","");
        Log.e("sip","sip1Account = "+sip1Account);
        Log.e("sip","sip2Account = "+sip2Account);
        Log.e("sip","sip3Account = "+sip3Account);
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
                if(sip1Account.equals(userAccount[k]))
                {
                    storeSipAccount(context,SIP1,SIPACCOUNT,userAccount[k],SIPSTATE,state[k]);
                    updateImageView(sip1AccountBtn,context,getSipState(state[k]),userAccount[k],sip1name);
                }
                else if(sip2Account.equals(userAccount[k]))
                {
                    storeSipAccount(context,SIP2,SIPACCOUNT,userAccount[k],SIPSTATE,state[k]);
                    updateImageView(sip2AccountBtn,context,getSipState(state[k]),userAccount[k],sip2name);
                }
                else if(sip3Account.equals(userAccount[k]))
                {
                    storeSipAccount(context,SIP3,SIPACCOUNT,userAccount[k],SIPSTATE,state[k]);
                    updateImageView(sip3AccountBtn,context,getSipState(state[k]),userAccount[k],sip3name);
                }
                else if(sip4Account.equals(userAccount[k]))
                {
                    storeSipAccount(context,SIP4,SIPACCOUNT,userAccount[k],SIPSTATE,state[k]);
                    updateImageView(sip4AccountBtn,context,getSipState(state[k]),userAccount[k],sip4name);
                }

            }
        }
    }
}
