package com.gcigb.network.util

import com.gcigb.dbchain.DBChain.Companion.iLog
import com.gcigb.network.NetworkLib.Companion.DEBUG
import com.gcigb.network.NetworkLib.Companion.TAG_ERROR
import com.gcigb.network.NetworkLib.Companion.TAG_TEST
import com.google.gson.Gson

/**
 * @author: Xiao Bo
 * @date: 18/7/2020
 */


fun logHttp(msg: String) {
    if (DEBUG) {
        iLog.logHttp(msg)
    }
}

fun logV(tag: String, msg: String) {
    if (DEBUG) {
        iLog.logV(tag, msg)
    }
}

fun logD(tag: String, msg: String) {
    if (DEBUG) {
        iLog.logD(tag, msg)
    }
}

fun logI(msg: String) {
    logI(TAG_TEST, msg)
}

fun logI(any: Any) {
    logI(TAG_TEST, Gson().toJson(any))
}

fun logI(tag: String, msg: String) {
    if (DEBUG) {
        iLog.logI(tag, msg)
    }
}

fun logW(tag: String, msg: String) {
    if (DEBUG) {
        iLog.logW(tag, msg)
    }
}

fun logE(msg: String) {
    logE(TAG_ERROR, msg)
}

fun logE(tag: String, msg: String) {
    if (DEBUG) {
        iLog.logE(tag, msg)
    }
}