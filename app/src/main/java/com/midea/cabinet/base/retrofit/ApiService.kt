package com.midea.cabinet.base.retrofit

import com.google.gson.Gson
import com.midea.cabinet.business.bean.BaseEntity
import com.midea.cabinet.business.model.CheckScreenBindDeviceBean
import com.midea.cabinet.business.model.DoorStateBean
import com.midea.cabinet.business.model.FaceReportResuleBean
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    //绑定设备
    @Streaming
    @POST("/terminal/bindIemi")
    fun bindImei(@Body requestBody: RequestBody): Observable<BaseEntity<Any>>

    //获取刷脸imei绑定状态
    @Streaming
    @POST
    fun checkImeiBinded(@Url api: String, @Body requestBody: RequestBody): Observable<BaseEntity<CheckScreenBindDeviceBean>>

    //获取门状态
    @Streaming
    @POST("/terminal/getDoorStatus")
    fun getDoorState(@Body requestBody: RequestBody):Observable<BaseEntity<DoorStateBean>>

    //刷脸上报
    @Streaming
    @POST("/terminal/faceReport")
    fun faceReport(@Body requestBody: RequestBody): Observable<BaseEntity<FaceReportResuleBean>>

}
