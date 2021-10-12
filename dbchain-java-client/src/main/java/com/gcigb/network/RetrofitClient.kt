package com.gcigb.network

import com.gcigb.network.adapter.CoroutineCallAdapterFactory
import com.gcigb.network.util.isNetworkAvailable
import com.gcigb.network.util.logE
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException

/**
 * @author: Xiao Bo
 * @date: 18/7/2020
 */

/**
 * 存放 Api 实例
 */
private val apiMap: HashMap<String, Any> = HashMap()

class RetrofitClient {
    companion object {
        /**
         * 获取 Api 实例，如果缓存里没有，则创建
         */
        fun <S> createService(baseUrl: String = NetworkLib.BASE_URL, cls: Class<S>, client: OkHttpClient = okHttpClient): S {
            val key = cls.name + baseUrl
            return if (apiMap.containsKey(key)) {
                apiMap[key] as S
            } else {
                val api = createRetrofit(baseUrl, client).create(cls)
                apiMap[key] = api as Any
                api
            }
        }

        inline fun <T> sendRequestForReturn(block: () -> T): T? {
            return try {
                if (isNetworkAvailable()) {
                    block()
                } else {
                    null
                }
            } catch (e: HttpException) {
                logE("Error Code: ${e.code()}  Error message: ${e.message()}")
                null
            } catch (e: ConnectException) {
                logE("Error cache, Error message: ${e.message}")
                null
            } catch (e: Exception) {
                logE("Error cache, Error message: ${e.message}")
                null
            }
        }
    }
}

/**
 * 创建 Retrofit
 */
private fun createRetrofit(baseUrl: String, client: OkHttpClient) = Retrofit.Builder().apply {
    baseUrl(baseUrl)  // 设置服务器路径
    client(client)  // 设置okhttp的网络请求
    addConverterFactory(GsonConverterFactory.create())// 添加转化库,默认是Gson
    addCallAdapterFactory(CoroutineCallAdapterFactory())//添加会调库，适用kotlin协程
}.build()

