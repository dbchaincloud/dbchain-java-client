package com.gcigb.dbchain

import com.gcigb.network.NetworkLib
import okhttp3.Interceptor
import dbchain.java.client.base.IDBChainEncrypt


/**
 * @author: Xiao Bo
 * @date: 13/10/2020
 */
class DBChain {

    companion object {
        lateinit var appCode: String
            private set
        internal lateinit var baseUrl: String
        internal lateinit var chainId: String
        lateinit var dbChainKey: DbChainKey
        lateinit var dbChainEncrypt: IDBChainEncrypt
        lateinit var iLog: ILog
        var defaultGasNumber: Long = 200000

        fun init(
            appCode: String, baseUrl: String, chainId: String,
            dbChainEncrypt: IDBChainEncrypt,
            iLog: ILog,
            defaultGasNumber: Long = 200000,
            isDebug: Boolean = false,
            testLogTag: String = "tag_test",
            errorLogTag: String = "tag_error",
            httpLogTag: String = "tag_http",
            interceptors: List<Interceptor>? = null
        ) {
            this.appCode = appCode
            this.baseUrl = baseUrl
            this.chainId = chainId
            this.dbChainEncrypt = dbChainEncrypt
            this.iLog = iLog
            NetworkLib.initNetworkModule(isDebug, testLogTag, errorLogTag, httpLogTag, baseUrl, interceptors)
        }

        fun withDBChainKey(dbChainKey: DbChainKey) {
            this.dbChainKey = dbChainKey
        }

        fun withAppCode(appCode: String) {
            this.appCode = appCode
        }
    }
}