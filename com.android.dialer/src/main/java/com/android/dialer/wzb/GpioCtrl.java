package com.android.dialer.wzb;

import java.io.IOException;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date 2018-02-23	15:05
 */

public class GpioCtrl {

    public static void gpioSet(int pin,int state){
        try {
            Runtime.getRuntime().exec("/system/bin/sh /system/bin/pstnctrl.sh" + " " + pin + " " + state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
