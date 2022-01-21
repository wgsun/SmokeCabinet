package com.midea.cabinet.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.LogUtils
import com.midea.cabinet.R

/**
 *@author:wgsun
 *2022/1/18
 *desc:
 */
class DoorOpenSuccessFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_door_open_success, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCountDown(30)
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
}