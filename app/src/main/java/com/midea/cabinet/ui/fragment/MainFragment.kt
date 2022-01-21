package com.midea.cabinet.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.GsonBuilder
import com.midea.cabinet.R
import com.midea.cabinet.base.https.BaseObserver
import com.midea.cabinet.base.retrofit.ApiClient
import com.midea.cabinet.base.retrofit.NetworkScheduler
import com.midea.cabinet.business.bean.BaseEntity
import com.midea.cabinet.business.control.FacepayUtils
import com.midea.cabinet.business.control.NoCabinetFaceControl
import com.midea.cabinet.business.control.NoCabinetPreferences
import com.midea.cabinet.business.model.*
import com.midea.cabinet.constants.Constants
import com.midea.cabinet.utils.ClickProxy
import com.midea.cabinet.utils.Utils
import kotlinx.android.synthetic.main.fragment_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *@author:wgsun
 *2022/1/18
 *desc:
 */
class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.v("onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogUtils.v("onCreateView")
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        startCountDown(30)
        noCabinetBind.setOnClickListener {
            NoCabinetFaceControl.toNoCabinetLogin(this)
        }
        iv_face_open.setOnClickListener(ClickProxy(View.OnClickListener {
            stopCountDown()
            faceOpenRequest()
        }))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DoorOpenEvent) {
        jumpToDoorOpenSuccess()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LockOpenFailEvent) {
        LogUtils.v("onMessageEvent LockOpenFailEvent")
        ToastUtils.showShort("开门失败")
        finishV1()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DoorOpenTimeOutEvent) {
        LogUtils.v("onMessageEvent DoorOpenTimeOutEvent")
        ToastUtils.showShort("开门查询超时")
        finishV1()
    }


    fun jumpToDoorOpenSuccess() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_mainFragment_to_doorOpenSuccessFragment)
    }

    fun jumpToDoorOpenFail() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_mainFragment_to_doorOpenFailFragment)
    }

    fun jumpToNoCabinetBindImeiFragment() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_mainFragment_to_noCabinetBindImeiFragment)
    }

    fun faceOpenRequest() {
        if (FacepayUtils.isBindDualSystem()){
            val deviceCode = NoCabinetPreferences.getSP().bindIMEI
            val age = "19"
            val time = System.currentTimeMillis().toString()
            val signCode = Utils.hash(deviceCode + time + Constants.MDE_KEY)
            val faceReportBean = FaceReportBean(deviceCode, age, time, signCode)
            val json = GsonBuilder().disableHtmlEscaping().create().toJson(faceReportBean)
            LogUtils.v("bindImei请求入参 $json")
            val requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
            ApiClient.service?.faceReport(requestBody)
                ?.compose(NetworkScheduler.compose())
                ?.subscribe(object : BaseObserver<FaceReportResuleBean>() {

                    override fun onSuccess(result: FaceReportResuleBean?) {
                        LogUtils.v("开门onSuccess：${result}")
                        NoCabinetFaceControl.serviceOpenDoorSuccess()
                    }

                    override fun onCodeError(t: BaseEntity<FaceReportResuleBean>) {
                        LogUtils.v("开门onCodeError：${t}")
                        if (t.data.isAdult == "0") {
                            jumpToDoorOpenFail()
                        } else {
                            ToastUtils.showShort(t.msg)
                        }
                    }

                    override fun onFailure(e: Throwable, isNetWorkError: Boolean) {
                        LogUtils.v("开门onFailure：${e.message}")
                        ToastUtils.showShort("绑定失败: 请检查网络")
                    }


                })
        } else {
            ToastUtils.showShort("设备未绑定，请绑定后再试")
        }
    }


    private fun finishV1() {
        LogUtils.v("调用finishV1结束倒计时返回上个页面")
        if (mCountdownTimer != null) {
            mCountdownTimer?.cancel()
        }
        activity?.finish()
    }


    private var mCountdownTimer: CountDownTimer? = null
    private fun startCountDown(secondsInFuture: Long) {
        LogUtils.v("startCountDown开始倒计时,总时间${secondsInFuture}")
        mCountdownTimer = object : CountDownTimer(secondsInFuture * 1000, 1000) {
            override fun onFinish() {
                finishV1()
            }

            override fun onTick(millisUntilFinished: Long) {
            }

        }.start()
    }

    fun stopCountDown() {
        LogUtils.v("stopCountDown")
        if (mCountdownTimer != null) {
            mCountdownTimer?.cancel()
            mCountdownTimer = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.v("onDestroyView")
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.v("onDestroy")
    }

}