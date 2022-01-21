package com.midea.cabinet.business.model

/**
 *@author:wgsun
 *2022/1/19
 *desc:
 */
data class BindImeiBean(
    //设备唯一标识
    var deviceCode: String,
    //时间
    var time: String,
    //签名
    var signCode: String,
    //需要绑定的刷脸imei
    var bindImei: String
)

data class FaceReportBean(
    //设备唯一标识
    var deviceCode: String,
    //年龄
    var age: String,
    //时间
    var time: String,
    //签名
    var signCode: String
)

data class FaceReportResuleBean(
    //是否成年 1未成年 1成年
    var isAdult: String
)

data class DoorStateRequestBean(
    //设备唯一标识
    var deviceCode: String,
    //时间
    var time: String,
    //签名
    var signCode: String
)

data class BindImeiCheckBean(
    //设备唯一标识
    var deviceCode: String
)

data class CheckScreenBindDeviceBean(
    var deviceCode: String,
    var faceImei: String,
    //绑定状态 0.未绑定 1.绑定
    var status: String
)

data class DoorStateBean(
    /**
     * 0-关门（默认、关门上报、超时落锁上报）
     * 1-开门中（已发开门指令）
     * 2-已开门（开门上报）
     */
    var doorStatus: String
)



data class NoCabinetBindDevice(
    var bindDeviceCode: String = ""
)