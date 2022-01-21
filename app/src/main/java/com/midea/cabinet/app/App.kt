package com.midea.cabinet.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.midea.cabinet.business.manager.CabinetCoreManager

/**
 *@author:wgsun
 *2022/1/18
 *desc:
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        CabinetCoreManager.init(this)
        val processName = getProcessName(applicationContext)

        LogUtils.v("\n\n\n\n")
        LogUtils.v("小卖柜软件重启了!!!\nBaseApplication getProcessName() $processName  pid: ${android.os.Process.myPid()}")
        if ("com.midea.cabinet" == processName) {

        }
    }

    private fun getProcessName(context: Context): String? {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        return runningApps.firstOrNull { it.pid == android.os.Process.myPid() && it.processName != null }
            ?.processName
    }
}