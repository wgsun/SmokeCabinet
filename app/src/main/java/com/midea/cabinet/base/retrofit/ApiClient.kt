package com.midea.cabinet.base.retrofit
import com.blankj.utilcode.util.LogUtils
import com.google.gson.GsonBuilder
import com.midea.cabinet.business.manager.UrlManager
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object ApiClient {

    var service: ApiService?
    private const val HTTP_TIME = 30000L

    init {
        val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            //打印retrofit日志
            LogUtils.v("retrofitBack = $message")
        })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(HTTP_TIME, TimeUnit.MILLISECONDS)
            .readTimeout(HTTP_TIME, TimeUnit.MILLISECONDS)
            .writeTimeout(HTTP_TIME, TimeUnit.MILLISECONDS)
            //输入http连接时的log，也可添加更多的Interceptor
            .addInterceptor(loggingInterceptor)
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build()

        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(UrlManager.SERVER_HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        service = retrofit.create(ApiService::class.java)
    }
}
/*
class ApiClient private constructor() {
    lateinit var service: ApiService
    private var HTTP_TIME = 30000L

    private object Holder {
        val INSTANCE = ApiClient()
    }

    companion object {
        @JvmStatic
        val instance by lazy { Holder.INSTANCE }
    }

    fun init() {   //在Application的onCreate中调用一次即可
        val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            //打印retrofit日志
            LogUtils.v("retrofitBack = $message")
        })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

        val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(HTTP_TIME, TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_TIME, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_TIME, TimeUnit.MILLISECONDS)
                //输入http连接时的log，也可添加更多的Interceptor
                .addInterceptor(loggingInterceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
                .baseUrl(UrlManager.SERVER_HOST)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()

        service = retrofit.create(ApiService::class.java)
    }
}*/
