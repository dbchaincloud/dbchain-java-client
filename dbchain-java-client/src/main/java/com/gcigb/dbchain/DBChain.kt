package com.gcigb.dbchain

import com.gcigb.network.initNetworkModule
import dbchain.java.client.base.IDBChainEncrypt
import okhttp3.Interceptor


/**
 * @author: Xiao Bo
 * @date: 13/10/2020
 */

lateinit var appCode: String
    private set
internal lateinit var baseUrl: String
internal lateinit var chainId: String
lateinit var dbChainKey: DbChainKey
lateinit var dbChainEncrypt: IDBChainEncrypt
lateinit var iLog: ILog
var defaultGasNumber: Long = 200000

fun init(
    appCodeParameter: String, baseUrlParameter: String, chainIdParameter: String,
    dbChainEncryptParameter: IDBChainEncrypt,
    iLogParameter: ILog,
    defaultGasNumberParameter: Long = 200000,
    isDebug: Boolean = false,
    testLogTag: String = "tag_test",
    errorLogTag: String = "tag_error",
    httpLogTag: String = "tag_http",
    interceptors: List<Interceptor>? = null
) {
    appCode = appCodeParameter
    baseUrl = baseUrlParameter
    chainId = chainIdParameter
    dbChainEncrypt = dbChainEncryptParameter
    iLog = iLogParameter
    defaultGasNumber = defaultGasNumberParameter
    initNetworkModule(isDebug, testLogTag, errorLogTag, httpLogTag, baseUrl, interceptors)
}

fun withDBChainKey(dbChainKeyParameter: DbChainKey) {
    dbChainKey = dbChainKeyParameter
}

fun withAppCode(appCodeParameter: String) {
    appCode = appCodeParameter
}
