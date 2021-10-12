package com.gcigb.dbchain.bean

/**
 * @author: Xiao Bo
 * @date: 22/10/2020
 */
data class TxBean(
    val fee: FeeBean = FeeBean(),
    val memo: String = "",
    val msg: List<Message>,
    val signatures: MutableList<SignatureBean> = mutableListOf()
)
//gas 越大越爽
data class FeeBean(val amount: List<String> = mutableListOf(), val gas: String = "99999999")