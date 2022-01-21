package com.midea.cabinet.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.LogUtils
import com.midea.cabinet.R
import kotlinx.android.synthetic.main.fragment_door_open_fail.*

/**
 *@author:wgsun
 *2022/1/18
 *desc:
 */
class DoorOpenFailFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_door_open_fail, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCountDown(tvReturnMediaLauncher, 5)
    }

    private var mCountdownTimer: CountDownTimer? = null
    private fun startCountDown(tvReturnHome: TextView?, secondsInFuture: Long) {
        LogUtils.v("startCountDown开始倒计时,总时间${secondsInFuture}")
        mCountdownTimer = object : CountDownTimer(secondsInFuture * 1000, 1000) {
            override fun onFinish() {
                LogUtils.v("startCountDown倒计时结束")
                finishV1()
            }

            override fun onTick(millisUntilFinished: Long) {
                LogUtils.v("startCountDown开始倒计时${millisUntilFinished}")
                val secondsEnd = Math.round(millisUntilFinished.toDouble() / 1000)
                if (tvReturnHome != null) {
                    tvReturnHome.text = Html.fromHtml("<u>返回首页（${secondsEnd}s）</u>")
                }
            }

        }.start()
    }

    fun stopCountDown() {
        if (mCountdownTimer != null) {
            mCountdownTimer?.cancel()
            mCountdownTimer = null
        }
    }

    fun finishV1() {
        LogUtils.v("调用finishV1结束倒计时返回上个页面")
        if (mCountdownTimer != null) {
            mCountdownTimer?.cancel()
        }
        activity?.finish()
    }
}