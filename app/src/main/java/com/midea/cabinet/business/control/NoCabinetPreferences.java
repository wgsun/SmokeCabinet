package com.midea.cabinet.business.control;

import android.content.Context;
import android.content.SharedPreferences;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

public class NoCabinetPreferences {


    private static NoCabinetPreferences info;

    private SharedPreferences preferences;

    public synchronized static NoCabinetPreferences getSP() {
        if (info == null) {
            Context context = Utils.getApp();
            if (context != null) {
                info = new NoCabinetPreferences(context, "NoCabinetInfo");
                return info;
            } else {
                throw new IllegalArgumentException("BaseApplication.getInstance() == null");
            }
        } else {
            return info;
        }
    }

    private NoCabinetPreferences(Context context, final String name) {
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }


    public void putBindIMEI(String bindDeviceCode) {
        LogUtils.v("putBindIMEI bindDeviceCode = $bindDeviceCode");
        FacepayUtils.INSTANCE.putBindDevice(bindDeviceCode);
    }

    public String getBindIMEI() {
        return FacepayUtils.INSTANCE.getBindDeviceCode().getBindDeviceCode();
    }
}
