package com.midea.cabinet.business.manager

import com.midea.cabinet.BuildConfig

/**
 *@author:wgsun
 *2022/1/18
 *desc:
 */
object UrlManager {

    const val MD5KEY = "NBbae403a06"

    //const val SERVER_HOST = ""
    var SERVER_HOST = if(BuildConfig.DEBUG) {
        "http://10.173.54.173:8003"
    } else {
        "https://show-api.mideazn.com"
    }
}