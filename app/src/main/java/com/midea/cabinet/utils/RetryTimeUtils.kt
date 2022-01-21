package com.midea.cabinet.utils

/**
 *@author:wgsun
 *2021/5/21
 *desc:
 */
object RetryTimeUtils {

    private val times = arrayListOf(3, 4, 7, 11, 15, 9999)
    private val waitTimes = arrayListOf(5_000L, 30_000L, 60_000L, 600_000L, 900_000L, 1800_000L)

    fun getSleepTime(currentTimes: Int): Long {
        val currentLevel = when {
            currentTimes <= times[0] -> 0
            currentTimes <= times[1] -> 1
            currentTimes <= times[2] -> 2
            currentTimes <= times[3] -> 3
            currentTimes <= times[4] -> 4
            else -> 4
        }
        return waitTimes[currentLevel]
    }
}