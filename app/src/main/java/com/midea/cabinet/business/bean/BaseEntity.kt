package com.midea.cabinet.business.bean

data class BaseEntity<T>(
        var code: String = "-1",
        var msg: String = "null",
        var eventForm: String = "null",
        var data: T,
        var result: T
)