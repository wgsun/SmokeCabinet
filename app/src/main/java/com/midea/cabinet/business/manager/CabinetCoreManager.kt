package com.midea.cabinet.business.manager

import android.annotation.SuppressLint
import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils

/**
 *@author:wgsun
 *2022/1/18
 *desc:
 */
object CabinetCoreManager {

    @SuppressLint("SdCardPath")
    const val LOG_DIR = "/sdcard/midea/appLog"

    fun init(context: Application) {
        Utils.init(context)
        LogUtils.getConfig()
            .setLogHeadSwitch(false)
            .setDir(LOG_DIR)
            .setBorderSwitch(false)
            .setFilePrefix("cabinet")
            .setLog2FileSwitch(true)
    }
}