package com.gcigb.dbchain.check

import com.gcigb.dbchain.bean.result.QueryOperationResultBean

/**
 * @author: Xiao Bo
 * @date: 17/12/2020
 */
//检查库链操作是否成功，只要不为空就是成功
internal fun checkDBChainOperactionSuccess(queryOperationResultBean: QueryOperationResultBean?): Boolean {
    //如果等于空，直接返回失败
    if (queryOperationResultBean == null) return false
    //如果 raw_log 为空则说明失败
    if (queryOperationResultBean.raw_log == null) return false
    return when {
        queryOperationResultBean.raw_log.contains("unknown request") -> {
            false
        }
        queryOperationResultBean.raw_log.contains("failed") -> {
            false
        }
        queryOperationResultBean.raw_log.contains("panic") -> {
            false
        }
        queryOperationResultBean.raw_log.contains("internal") -> {
            false
        }
        else -> {

            true
        }
    }

}

fun checkIdValid(id: String?): Boolean {
    try {
        val toLong = id?.toLong() ?: return false
        return toLong > 0
    } catch (e: Exception) {
        return false
    }
}