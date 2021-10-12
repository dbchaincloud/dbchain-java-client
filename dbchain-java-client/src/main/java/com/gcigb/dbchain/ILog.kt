package com.gcigb.dbchain

interface ILog {

    fun logHttp(msg: String)

    fun logV(tag: String, msg: String)

    fun logD(tag: String, msg: String)

    fun logI(msg: String)

    fun logI(any: Any)

    fun logI(tag: String, msg: String)

    fun logW(tag: String, msg: String)

    fun logE(msg: String)

    fun logE(tag: String, msg: String)
}