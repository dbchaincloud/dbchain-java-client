package com.gcigb.dbchain.util

import com.gcigb.dbchain.bean.result.DBChainListResult
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author: Xiao Bo
 * @date: 10/6/2021
 */
fun Any.toMap(): Map<String, String> {
    val json = GsonBuilder().serializeNulls().create().toJson(this)
    val type = object : TypeToken<Map<String, String>>() {}.type
    return GsonBuilder().serializeNulls().create().fromJson(json, type)
}

fun Any.toJsonString(): String {
    return GsonBuilder().serializeNulls().create().toJson(this)
}

fun <T> String.json2Any(clazz: Class<T>): T {
    return GsonBuilder().serializeNulls().create().fromJson(this, clazz)
}

fun <T> String.toBaseQueryResult(clazz: Class<T>): DBChainListResult<T> {
    val array = Array<Type>(1) { clazz }
    val ty = GsonTypeTokenObject(DBChainListResult::class.java, array)
    return GsonBuilder().serializeNulls().create().fromJson(this, ty)
}

private class GsonTypeTokenObject(private val raw: Class<*>, private val args: Array<Type>) :
    ParameterizedType {

    override fun getActualTypeArguments(): Array<Type> {
        return args
    }

    override fun getRawType(): Type {
        return raw
    }

    override fun getOwnerType(): Type? {
        return null
    }

}