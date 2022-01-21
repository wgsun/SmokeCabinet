package com.midea.cabinet.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.midea.cabinet.business.control.NoCabinetPreferences
import com.midea.cabinet.business.model.BindImeiBean
import com.midea.cabinet.constants.Constants
import com.midea.cabinet.utils.ClickProxy
import com.midea.cabinet.utils.Utils
import kotlinx.android.synthetic.main.fragment_no_cabinet_bind_imei.*
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 *@author:wgsun
 *2022/1/19
 *desc:
 */
class NoCabinetBindImeiFragment : Fragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_no_cabinet_bind_imei, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener(ClickProxy(View.OnClickListener {
            back()
        }))
        tv_bind.setOnClickListener(ClickProxy(View.OnClickListener {
            clickBindImei()
        }, 6000))
    }

    @SuppressLint("MissingPermission")
    private fun clickBindImei() {
        val etBindDeviceCode = etImei.text.toString().trim()
        if (TextUtils.isEmpty(etBindDeviceCode)) {
            ToastUtils.showShort("请输入正确的绑定imei号")
            return
        }

        //val deviceCode = PhoneUtils.getIMEI()
        val deviceCode = Constants.DEVICE_ID
        val time = System.currentTimeMillis().toString()
        val signCode = Utils.hash(deviceCode + time + Constants.MDE_KEY)
        val bindImei = etImei.text.toString()
        val bindImeiBean = BindImeiBean(deviceCode, time, signCode = signCode, bindImei)
        val json = GsonBuilder().disableHtmlEscaping().create().toJson(bindImeiBean)
        LogUtils.v("bindImei请求入参 $json")
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        ApiClient.service?.bindImei(requestBody)
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : BaseObserver<Any>() {
                override fun onCodeError(t: BaseEntity<Any>) {
                    if (!isDetached) {
                        LogUtils.v("绑定失败onCodeError：${t}")
                        ToastUtils.showShort(t.msg)
                    }
                }

                override fun onFailure(e: Throwable, isNetWorkError: Boolean) {
                    if (!isDetached) {
                        LogUtils.v("绑定失败onFailure：${e.message}")
                        ToastUtils.showShort("绑定失败: 请检查网络")
                    }
                }

                override fun onSuccess(result: Any?) {
                    if (!isDetached) {
                        ToastUtils.showShort("设备绑定成功")
                        NoCabinetPreferences.getSP().putBindIMEI(etImei.text.toString().trim())
                        back()
                    }
                }
            })
    }

    private fun back() {
        NavHostFragment.findNavController(this).navigateUp()
    }
}