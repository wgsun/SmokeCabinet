package com.midea.cabinet.business.control

import android.os.Environment
import android.text.TextUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.midea.cabinet.business.model.NoCabinetBindDevice
import com.midea.cabinet.utils.FileUtil
import java.io.File
import com.google.gson.Gson
import com.midea.cabinet.utils.fromJson

/**
 *@author:wgsun
 *2021/12/9
 *desc: 单双系统工具类
 */
object FacepayUtils {

    var mGson = Gson()
    var bindImeiPath = "${Environment.getExternalStorageDirectory().path}/midea/bindImeiConfig"

    /**
     * 判断设备是否绑定
     */
    fun isBindDualSystem() : Boolean{
        return !TextUtils.isEmpty(NoCabinetPreferences.getSP().bindIMEI)
    }

    /*@SuppressLint("MissingPermission")
    fun getDeviceId(): String {
        ///先判断是否绑定双系统
        val bindIMEI = NoCabinetPreferences.getSP().bindIMEI
        return if (TextUtils.isEmpty(bindIMEI)) {
            if (TextUtils.isEmpty(InfoPreferences.getSP().jPushAlias)) {
                PhoneUtils.getIMEI()
            } else {
                InfoPreferences.getSP().jPushAlias
            }
        } else {
            bindIMEI
        }
    }*/

    fun getBindDeviceCode(): NoCabinetBindDevice {
        return try {
            val other = File(bindImeiPath).readText()
            val cameraConfigBean = mGson.fromJson<NoCabinetBindDevice>(other)
            cameraConfigBean
        } catch (e: Exception) {
            NoCabinetBindDevice()
        }
    }

    fun putBindDevice(content: String) {
        val noCabinetBindDevice = NoCabinetBindDevice()
        noCabinetBindDevice.bindDeviceCode = content
        val bindDeviceJson = mGson.toJson(noCabinetBindDevice)
        LogUtils.v("bindDeviceJson:$bindDeviceJson")
        if (bindDeviceJson.isNotEmpty()) {
            FileUtil.string2File(bindDeviceJson, bindImeiPath)
        }
    }

    ///删除双系统设备号缓存文件
    fun deleteNoCabinetCache() {
        val config = File(bindImeiPath)
        if (config.exists()) {
            LogUtils.v("删除双系统设备号缓存文件")
            FileUtils.deleteFile(config)
        }else{
            LogUtils.v("不存在双系统设备号缓存文件")
        }
    }
}