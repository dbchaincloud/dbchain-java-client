package com.gcigb.dbchain

import kotlinx.coroutines.delay

suspend inline fun <T> loopHandleInTime(handleBlock: () -> T?, checkCompleteBlock: (T?) -> Boolean, maxHandleSecond: Int = 10): T? {
    var second = 0
    var data: T?
    var complete: Boolean
    do {
        // 需要循环处理的事务
        data = handleBlock()
        // 是否完成由钩子决定
        complete = checkCompleteBlock(data)
        // 如果没有成功，则多等待一秒
        if (!complete) {
            second++
            delay(1000)
        }
    } while (!complete && second < maxHandleSecond)
    return data
}

inline fun <T> loopHandleInCount(handleBlock: () -> T?, checkCompleteBlock: (T?) -> Boolean, maxHandleCount: Int = 3): T? {
    var count = 0
    var data: T?
    var complete: Boolean
    do {
        // 需要循环处理的事务
        data = handleBlock()
        // 是否完成由钩子决定
        complete = checkCompleteBlock(data)
        // 如果没有成功
        if (!complete) count++
    } while (!complete && count < maxHandleCount)
    return data
}