package com.midea.cabinet.utils

import android.view.View

class ClickProxy(private val origin: View.OnClickListener,private val times: Long = 1000L, private val again: IAgain? = null) : View.OnClickListener {

    private var lastclick: Long = 0

    override fun onClick(v: View) {
        if (System.currentTimeMillis() - lastclick >= times) {
            origin.onClick(v)
            lastclick = System.currentTimeMillis()
        } else {
            again?.onAgain()
        }
    }

    interface IAgain {
        fun onAgain() //重复点击
    }
}