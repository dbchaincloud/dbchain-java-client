package com.gcigb.dbchain.bean.result

/**
 * @author: Xiao Bo
 * @date: 14/12/2020
 */
data class QueryOperationResultBean(
    val gas_used: String,
    val gas_wanted: String,
    val height: String,
    val logs: List<Log>,
    val raw_log: String?,
    val timestamp: String,
    val tx: Tx,
    val txhash: String
)

data class Log(
    val events: List<Event>,
    val log: String,
    val msg_index: Int
)

data class Tx(
    val type: String,
    val value: Value
)

data class Event(
    val attributes: List<Attribute>,
    val type: String
)

data class Attribute(
    val key: String,
    val value: String
)

data class Value(
    val fee: Fee,
    val memo: String,
    val msg: List<Msg>,
    val signatures: List<Signature>
)

data class Fee(
    val amount: List<DenomAmount>,
    val gas: String
)

data class DenomAmount(
    var amount: String,
    val denom: String
)

data class Msg(
    val type: String,
    val value: ValueX
)

data class Signature(
    val pub_key: PubKey,
    val signature: String
)

data class ValueX(
    val app_code: String,
    val fields: Any,
    val owner: String,
    val table_name: String
)

data class PubKey(
    val type: String,
    val value: String
)