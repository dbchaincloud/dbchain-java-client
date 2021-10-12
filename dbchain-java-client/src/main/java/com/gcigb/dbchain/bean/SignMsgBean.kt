package com.gcigb.dbchain.bean

import com.gcigb.dbchain.DBChain.Companion.dbChainEncrypt


/**
 * @author: Xiao Bo
 * @date: 9/10/2020
 */
data class SignMsgBean(
    val account_number: String,
    val chain_id: String,
    val fee: FeeBean,
    val memo: String,
    val msgs: List<Message>,
    val sequence: String
)

data class SignatureBean(val signature: String, val pub_key: PublicKeyBean)

data class PublicKeyBean(val type: String = dbChainEncrypt.pubKeyType, val value: String)

data class SignMetaBean(val chain_id: String, val account_number: String, val sequence: String)