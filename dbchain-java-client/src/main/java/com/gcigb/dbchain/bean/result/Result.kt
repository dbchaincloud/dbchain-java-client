package com.gcigb.dbchain.bean.result

data class DBChainQueryResult(
    val isSuccess: Boolean,
    val content: String?
)

data class DBChainResult(
    val height: String,
    val result: String
)

data class DBChainListResult<T>(
    val height: String,
    val result: List<T>
)

data class TxHashBean(
    val height: String,
    val txhash: String
)