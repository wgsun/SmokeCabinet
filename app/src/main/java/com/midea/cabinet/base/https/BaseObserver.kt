package com.midea.cabinet.base.https

import android.accounts.NetworkErrorException
import com.blankj.utilcode.util.LogUtils
import com.midea.cabinet.business.bean.BaseEntity
import com.midea.cabinet.utils.isAvailableByPingDomain
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import kotlin.concurrent.thread

abstract class BaseObserver<T> : Observer<BaseEntity<T>> {
    private val SUCCESS_CODE = "0"

    override fun onSubscribe(d: Disposable) {
        onRequestStart()
    }

    override fun onNext(response: BaseEntity<T>) {
        val tBaseEntity: BaseEntity<T> = response
        if (SUCCESS_CODE == tBaseEntity.code) {
            try {
                LogUtils.v(" 返回的event: " + response.eventForm+" ,data: " + response.data)
                onSuccess(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtils.v("数据解析错误1 ${e.message}")
            }
        } else {
            try {
                onCodeError(tBaseEntity)
                LogUtils.v("数据解析错误2 $response")
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtils.v("数据解析错误3 $response")
            }
        }
        onRequestEnd()
    }

    override fun onError(e: Throwable) {
        try {
            LogUtils.v("网络请求失败，失败类型： error : ${e.javaClass.name}")
            if (e is ConnectException
                    || e is TimeoutException
                    || e is SocketTimeoutException
                    || e is NetworkErrorException
                    || e is UnknownError
                    || e is UnknownHostException) {
                thread(true) {
                    isAvailableByPingDomain()
                    isAvailableByPingDomain("baidu.com")
                }
                onFailure(e, true)
            } else {
                onFailure(e, false)
                if (e.message?.contains("timeout") != false) {
                    thread(true) {
                        isAvailableByPingDomain()
                        isAvailableByPingDomain("baidu.com")
                    }
                }
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        onRequestEnd()
    }

    override fun onComplete() {}

    /**
     * 返回成功
     *
     * @param t
     * @throws Exception
     */
    @Throws(Exception::class)
    protected abstract fun onSuccess(result: T?)

    /**
     * 返回成功了,但是code错误
     *
     * @param t
     * @throws Exception
     */
    @Throws(Exception::class)
    protected abstract fun onCodeError(t: BaseEntity<T>)

    /**
     * 返回失败
     *
     * @param e
     * @param isNetWorkError 是否是网络错误
     * @throws Exception
     */
    @Throws(Exception::class)
    protected abstract fun onFailure(e: Throwable, isNetWorkError: Boolean)

    protected open fun onRequestStart() {}

    protected open fun onRequestEnd() {}

}
