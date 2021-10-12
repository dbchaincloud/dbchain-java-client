package com.gcigb.dbchain.bean

import com.gcigb.dbchain.bean.result.DenomAmount

/**
 * @author: Xiao Bo
 * @date: 22/10/2020
 */
data class TxBean(
    val fee: FeeBean,
    val memo: String = "",
    val msg: List<Message>,
    val signatures: MutableList<SignatureBean> = mutableListOf()
)

//gas 越大越爽
data class FeeBean(val amount: List<DenomAmount>, val gas: String)