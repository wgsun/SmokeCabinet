package com.midea.cabinet.business.control

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.google.gson.GsonBuilder
import com.midea.cabinet.R
import com.midea.cabinet.base.https.BaseObserver
import com.midea.cabinet.base.retrofit.ApiClient
import com.midea.cabinet.base.retrofit.NetworkScheduler
import com.midea.cabinet.business.bean.BaseEntity
import com.midea.cabinet.business.model.*
import com.midea.cabinet.constants.Constants
import com.midea.cabinet.utils.RetryTimeUtils
import com.midea.cabinet.utils.Utils
import okhttp3.MediaType
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * 双系统
 */
object NoCabinetFaceControl {

    private var checkCount = 0

    private var mOpenStatus = false
    private const val DISPLAY_LOADING = 0
    private const val DISPLAY_TOTAL_TIME = 2
    private var loadCount = 0

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                DISPLAY_LOADING -> {
                    loadCount++
                    getDoorState()
                    sendEmptyMessageDelayed(DISPLAY_LOADING, 1000)
                    if (loadCount > 30) {
                        removeMessage()
                        EventBus.getDefault().post(LockOpenFailEvent())
                    }
                }
                DISPLAY_TOTAL_TIME -> {
                    LogUtils.v("达到总时间3分钟")
                    removeMessage()
                    EventBus.getDefault().post(DoorOpenTimeOutEvent())
                }
            }
        }
    }

    /**
     * 进入系统绑定页面
     */
    private var lastTime: Long = 0L
    private var clickIndex = 0//点击次数
    fun toNoCabinetLogin(fragment: Fragment) {
        val currentTime: Long = SystemClock.elapsedRealtime()
        val timeInterval = 1000//间隔时间
        if (currentTime - lastTime < timeInterval || lastTime == 0L) {
            clickIndex++
            lastTime = currentTime
        } else {
            clickIndex = 0
            lastTime = 0L
        }
        if (clickIndex == 10) {
            clickIndex = 0
            NavHostFragment.findNavController(fragment).navigate(R.id.action_mainFragment_to_noCabinetBindImeiFragment)
        }
    }


    fun serviceOpenDoorSuccess() {
        LogUtils.v("调用服务器开门成功")
        loadCount = 0
        mOpenStatus = false
        removeMessage()
        mHandler.sendEmptyMessage(DISPLAY_LOADING)
        ///总时间倒计时
        if (mHandler.hasMessages(DISPLAY_TOTAL_TIME)) {
            mHandler.removeMessages(DISPLAY_TOTAL_TIME)
        }
        mHandler.sendEmptyMessageDelayed(DISPLAY_TOTAL_TIME, 180000)
    }

    private fun removeMessage() {
        if (mHandler.hasMessages(DISPLAY_LOADING)) {
            mHandler.removeMessages(DISPLAY_LOADING)
        }
    }

    fun getDoorState() {
        val deviceCode = NoCabinetPreferences.getSP().bindIMEI
        val time = System.currentTimeMillis().toString()
        val signCode = Utils.hash(deviceCode + time + Constants.MDE_KEY)
        val doorStateRequestBean = DoorStateRequestBean(deviceCode, time, signCode)
        val json = GsonBuilder().disableHtmlEscaping().create().toJson(doorStateRequestBean)
        LogUtils.v("bindImei请求入参 $json")
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        ApiClient.service?.getDoorState(requestBody)
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : BaseObserver<DoorStateBean>() {
                override fun onSuccess(result: DoorStateBean?) {
                    result ?: return
                    LogUtils.v("获取门状态onSuccess：${result}")
                    if (result.doorStatus == "2" && !mOpenStatus) {
                        LogUtils.v("门已开")
                        mOpenStatus = true
                        removeMessage()
                        EventBus.getDefault().post(DoorOpenEvent())
                    }
                }

                override fun onCodeError(t: BaseEntity<DoorStateBean>) {
                    LogUtils.v("获取门状态onCodeError：${t}")
                }

                override fun onFailure(e: Throwable, isNetWorkError: Boolean) {
                    LogUtils.v("获取门状态onCodeError：${e.message}")
                }

            })

    }


    /**
     * 检测是否绑定
     */
    fun checkDeviceBinded() {
        val deviceCode = Constants.DEVICE_ID
        val bindImeiCheckBean = BindImeiCheckBean(deviceCode)
        val json = GsonBuilder().disableHtmlEscaping().create().toJson(bindImeiCheckBean)
        LogUtils.v("bindImei请求入参 $json")
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        ApiClient.service?.checkImeiBinded("http://10.173.54.173:81/aiot/device/device/getBindFaceImeiStatus", requestBody)
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object :BaseObserver<CheckScreenBindDeviceBean>(){
                override fun onSuccess(result: CheckScreenBindDeviceBean?) {
                    checkCount = 0
                    LogUtils.v("检测双系统屏幕绑定onSuccess：${result}")
                    if (result?.status == "1") {
                        if (!FacepayUtils.isBindDualSystem() && result.faceImei.isNotEmpty()) {
                            NoCabinetPreferences.getSP().putBindIMEI(result.faceImei)
                        }
                    } else {
                        if (result?.status == "0") {
                            FacepayUtils.deleteNoCabinetCache()
                        }
                    }
                }

                override fun onCodeError(t: BaseEntity<CheckScreenBindDeviceBean>) {
                    LogUtils.v("检测屏幕双系统绑定onCodeError：${t}")
                    rebind()
                }

                override fun onFailure(e: Throwable, isNetWorkError: Boolean) {
                    LogUtils.v("检测双系统屏幕绑定onFailure：${e.message}")
                    rebind()
                }

            })
    }

    fun rebind() {
        LogUtils.v("checkScreenBindDevice rebind")
        checkCount++
        if (checkCount < 5) {
            ThreadUtils.executeByCachedWithDelay(object : com.blankj.utilcode.util.ThreadUtils.SimpleTask<String>() {
                override fun doInBackground(): String? {
                    checkDeviceBinded()
                    return "true"
                }

                override fun onSuccess(result: String?) {}
            },  RetryTimeUtils.getSleepTime(checkCount), TimeUnit.MILLISECONDS)
        }
    }

}