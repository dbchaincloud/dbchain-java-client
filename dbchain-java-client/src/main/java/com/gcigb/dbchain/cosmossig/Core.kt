package com.gcigb.dbchain.cosmossig

import com.gcigb.dbchain.DBChain.Companion.dbChainEncrypt
import com.gcigb.dbchain.DbChainKey
import com.gcigb.dbchain.bean.*
import com.gcigb.dbchain.ktx.toJsonSort
import com.gcigb.dbchain.util.coding.base64Encode
import com.gcigb.network.util.logHttp


/**
 * @author: Xiao Bo
 * @date: 9/10/2020
 */

internal fun signTx(tx: TxBean, meta: SignMetaBean, dbChainKey: DbChainKey): TxBean {
    val signMsg = createSignMsg(tx, meta)
    val signature = createSignature(signMsg, dbChainKey)
    tx.signatures.add(signature)
    return tx
}

/**
 * 创建需要签名的消息
 */
private fun createSignMsg(tx: TxBean, meta: SignMetaBean): SignMsgBean {
    return SignMsgBean(
        meta.account_number,
        meta.chain_id,
        tx.fee,
        tx.memo,
        tx.msg,
        meta.sequence
    )
}

/**
 * 创建签名实体
 */
private fun createSignature(signMsg: SignMsgBean, dbChainKey: DbChainKey): SignatureBean {
    val signatureObj = createSignatureBytes(signMsg, dbChainKey.privateKeyBytes)
    val signature = base64Encode(signatureObj)
    val value = base64Encode(dbChainKey.publicKeyBytes33)
    return SignatureBean(
        signature,
        PublicKeyBean(value = value)
    )
}

private fun createSignatureBytes(signMsg: SignMsgBean, privateKeyByteArray: ByteArray): ByteArray {
    val bytes = toCanonicalJSONBytes(signMsg)
    return sign(bytes, privateKeyByteArray)
}

private fun toCanonicalJSONBytes(any: Any): ByteArray {
    val sortJson = any.toJsonSort()
    logHttp("json sort: $sortJson")
    return sortJson.toByteArray()
}

/**
 * 先哈希，再 secp256k1 签名
 */
internal fun sign(bytes: ByteArray, privateKeyByteArray: ByteArray): ByteArray {
    return dbChainEncrypt.sign(privateKeyByteArray,bytes)
}
