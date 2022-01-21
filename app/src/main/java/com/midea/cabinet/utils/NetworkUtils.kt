package com.midea.cabinet.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ShellUtils.execCmd
import com.blankj.utilcode.util.Utils


/**
 * Created by ydh on 17-12-18.
 */
fun getActiveNetworkInfo(): NetworkInfo? {
    val cs = Utils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE)
    return if (cs is ConnectivityManager) {
        cs.activeNetworkInfo
    } else {
        null
    }
}

fun isConnected(): Boolean {
    val info = getActiveNetworkInfo()
    return info?.isConnected ?: false
}

/**
 * 判断网络是否可用
 */
fun isAvailableByPing(): Boolean = isAvailableByPing(null)

/**
 * 判断网络是否可用
 *
 * 如果ping不通就说明网络不可用
 *
 */
fun isAvailableByPing(ip: String?): Boolean {
    var temp = ip
    if (temp == null || temp.isEmpty()) {
        temp = "47.98.88.223"
    }
    val result = execCmd("ping -c 1 $temp", false)
    val ret = result.result == 0
    LogUtils.d("NetworkUtils", "isAvailableByPing() called $result")
    return ret
}

fun isAvailableByPingDomain(domain: String? = null): Boolean {
    var temp = domain
    if (temp == null || temp.isEmpty()) {
        temp = "api.xiaomaigui.com"
    }
    val result = execCmd("ping -c 1 $temp", false)
    val ret = result.result == 0
    LogUtils.d("NetworkUtils", "isAvailableByPingDomain() $temp called $result")
    return ret
}

/**
 * 获取网络运营商名称
 *
 * 中国移动、如中国联通、中国电信
 *
 * @return 运营商名称
 */
fun getNetworkOperatorName(): String {
    val tm = Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE)
    return if (tm is TelephonyManager) {
        tm.networkOperatorName
    } else {
        "unknow"
    }
}

private val NETWORK_TYPE_GSM = 16
private val NETWORK_TYPE_TD_SCDMA = 17
private val NETWORK_TYPE_IWLAN = 18

/**
 * 获取当前网络类型
 *
 */
fun getNetworkType(): NetworkType {
    var netType = NetworkType.NETWORK_NO
    val info = getActiveNetworkInfo()
    if (info != null && info.isAvailable) {

        when {
            info.type == ConnectivityManager.TYPE_WIFI -> netType = NetworkType.NETWORK_WIFI

            info.type == ConnectivityManager.TYPE_MOBILE ->
                when (info.subtype) {

                    NETWORK_TYPE_GSM,
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_1xRTT,
                    TelephonyManager.NETWORK_TYPE_IDEN -> netType = NetworkType.NETWORK_2G

                    NETWORK_TYPE_TD_SCDMA,
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_HSPAP -> netType = NetworkType.NETWORK_3G

                    NETWORK_TYPE_IWLAN,
                    TelephonyManager.NETWORK_TYPE_LTE -> netType = NetworkType.NETWORK_4G

                    else -> {
                        val subtypeName = info.subtypeName

                        netType = if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)) {
                            NetworkType.NETWORK_3G
                        } else {
                            NetworkType.NETWORK_UNKNOWN
                        }
                    }
                }
            else -> netType = NetworkType.NETWORK_UNKNOWN
        }
    }
    return netType
}

enum class NetworkType {
    NETWORK_WIFI,
    NETWORK_4G,
    NETWORK_3G,
    NETWORK_2G,
    NETWORK_UNKNOWN,
    NETWORK_NO
}