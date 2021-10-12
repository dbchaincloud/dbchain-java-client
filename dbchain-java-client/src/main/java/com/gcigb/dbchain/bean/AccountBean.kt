package com.gcigb.dbchain.bean

/**
 * @author: Xiao Bo
 * @date: 14/10/2020
 */
data class AccountBean(
    val type: String,
    val value: ValueBean
)

data class ValueBean(
    val account_number: Int,
    val address: String,
    val coins: List<CoinBean>,
    val public_key: Any,
    val sequence: Int
)

data class CoinBean(
    val amount: String,
    val denom: String
)

data class BodyBean(val mode: String = "async",val tx: TxBean)
