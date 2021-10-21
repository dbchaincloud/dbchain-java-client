package com.gcigb.dbchain

import com.gcigb.dbchain.bean.result.DBChainQueryResult
import com.gcigb.dbchain.cosmossig.sign
import com.gcigb.dbchain.net.ApiService
import com.gcigb.dbchain.util.coding.base58Encode
import com.gcigb.network.createService
import com.gcigb.network.sendRequestForReturn

fun createAccessToken(): String {
    val currentTimeMillis = "${System.currentTimeMillis()}"
    val signObj = sign(currentTimeMillis.toByteArray(), dbChainKey.privateKeyBytes)
    val encodedPubKey = base58Encode(dbChainKey.publicKeyBytes33)
    val encodedSig = base58Encode(signObj)
    return "${encodedPubKey}:${currentTimeMillis}:${encodedSig}"
}

/**
 * 打一个积分
 */
suspend fun requestAppUser(): DBChainQueryResult {
    val result = sendRequestForReturn {
        return@sendRequestForReturn createService(baseUrl, ApiService::class.java)
            .requestAppUser(createAccessToken())
            .await()
    }
    return DBChainQueryResult(result?.isSuccessful ?: false, null)
}

suspend fun getToken(address: String): Int {
    return sendRequestForReturn {
        val result = createService(baseUrl, ApiService::class.java)
            .getToken(address)
            .await()

        val coins = result.result.value.coins
        return run breaking@{
            coins.forEach {
                if (it.denom == "dbctoken") return@breaking it.amount.toInt()
            }
            return@breaking 0
        }
    } ?: return 0
}

suspend fun checkTokenAvailable(dbChainKey: DbChainKey): Boolean {
    withDBChainKey(dbChainKey)
    //先查询是否有积分
    if (getToken(dbChainKey.address) > 0) return true
    // 先去获取积分
    val requestUser = loopHandleInCount({
        requestAppUser().isSuccess
    }, { it ?: false }) ?: return false
    if (!requestUser) return false
    // 查询积分是不是真的到账了
    val token =
        loopHandleInTime({ getToken(dbChainKey.address) }, { it != null && it.toInt() > 0 })
    if (token == null || token.toInt() < 1) return false
    return true
}

