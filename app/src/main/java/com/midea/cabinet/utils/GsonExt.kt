package com.midea.cabinet.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson(json, T::class.java)
        ?: throw Exception("json:$json \n parse json failed")

inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type