package com.gcigb.dbchain.net

/**
 * @author: Xiao Bo
 * @date: 14/10/2020
 */
data class BaseResponseDbChain<T>(val height: String, val result: T)