package com.gcigb.network

import com.gcigb.network.NetworkLib.Companion.DEBUG
import com.gcigb.network.NetworkLib.Companion.interceptorList
import com.gcigb.network.interceptor.LogInterceptor
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

internal val okHttpClient: OkHttpClient by lazy {

    OkHttpClient.Builder().apply {
        //设置超时时间
        connectTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        //Header参数拦截器
        interceptorList?.let {
            for (interceptor in it) {
                addInterceptor(interceptor)
            }
        }
        //日志拦截器
        if (DEBUG) {
            addInterceptor(LogInterceptor())
        }
        //设置连接池
        connectionPool(ConnectionPool(5, 1, TimeUnit.SECONDS))
        //默认重试一次，若需要重试N次，则要实现拦截器
        retryOnConnectionFailure(true)
    }.build()
}

val okHttpClientUpload: OkHttpClient by lazy {

    OkHttpClient.Builder().apply {
        //设置超时时间
        connectTimeout(30, TimeUnit.MINUTES)
        writeTimeout(30, TimeUnit.MINUTES)
        readTimeout(30, TimeUnit.MINUTES)
        //Header参数拦截器
        interceptorList?.let {
            for (interceptor in it) {
                addInterceptor(interceptor)
            }
        }
        //日志拦截器
        if (DEBUG) {
            addInterceptor(LogInterceptor())
        }
        //设置连接池
        connectionPool(ConnectionPool(5, 1, TimeUnit.SECONDS))
        //默认重试一次，若需要重试N次，则要实现拦截器
        retryOnConnectionFailure(true)
    }.build()
}
