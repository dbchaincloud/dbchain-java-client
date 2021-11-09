package com.gcigb.dbchain

import com.gcigb.dbchain.bean.*
import com.gcigb.dbchain.bean.result.DBChainQueryResult
import com.gcigb.dbchain.bean.result.QueryOperationResultBean
import com.gcigb.dbchain.check.checkDBChainOperactionSuccess
import com.gcigb.dbchain.cosmossig.signTx
import com.gcigb.dbchain.net.ApiService
import com.gcigb.dbchain.net.BaseResponseDbChain
import com.gcigb.dbchain.util.coding.base58Encode
import com.gcigb.network.createService
import com.gcigb.network.sendRequestForReturn
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.math.BigDecimal
import kotlin.math.ceil

fun newMessageList(): MutableList<Message> = mutableListOf()

/**
 * 批量操作
 */
suspend fun handleBatchMessage(msgList: List<Message>): Boolean {
    if (msgList.isEmpty()) return true
    val result = loopHandleInCount({
        createTransaction {
            formatRequestBodyJson(msgList, it)
        }
    }, {
        checkDBChainOperactionSuccess(it)
    })
    return checkDBChainOperactionSuccess(result)
}

suspend fun createApplication(
    // 库名称
    name: String,
    // 库描述
    description: String,
    // 权限
    permission_required: Boolean,
    // 创建者地址
    owner: String
): Boolean {
    val message = createApplicationMessage(name, description, permission_required, owner)
    return handleBatchMessage(listOf(message))
}


suspend fun queryApplication(): List<String> {
    val result = loopHandleInCount({
        sendRequestForReturn {
            return@sendRequestForReturn createService(baseUrl, ApiService::class.java)
                .queryApplication(createAccessToken())
                .await().result
        }
    }, {
        it != null
    })
    return result ?: listOf()
}

suspend fun createTable(tableName: String, fields: List<String>): Boolean {
    val message = createTableMessage(tableName, fields)
    return handleBatchMessage(listOf(message))
}

/**
 * 插入一条数据
 */
suspend fun insertRow(tableName: String, fields: Map<String, String>): Boolean {
    val message = createInsertMessage(tableName, fields)
    return handleBatchMessage(listOf(message))
}

/**
 * 冻结一条数据
 */
suspend fun freezeRow(tableName: String, id: String): Boolean {
    val message = createFreezeMessage(tableName, id)
    return handleBatchMessage(listOf(message))
}

/**
 * 调用函数
 */
suspend fun callFunction(functionName: String, argument: String): Boolean {
    val message = createCallFunctionMessage(functionName, argument)
    return handleBatchMessage(listOf(message))
}

/**
 * 添加函数
 */
suspend fun addFunction(functionName: String, description: String, body: String): Boolean {
    val message = createAddFunctionMessage(functionName, description, body)
    return handleBatchMessage(listOf(message))
}

/**
 * 删除函数
 */
suspend fun dropFunction(functionName: String): Boolean {
    val message = createDropFunctionMessage(functionName)
    return handleBatchMessage(listOf(message))
}

/**
 * 查询数据
 */
suspend fun querier(queriedArray: QueriedArray): DBChainQueryResult {
    val body = loopHandleInCount({
        sendRequestForReturn {
            val json = queriedArray.toJson()
            return@sendRequestForReturn createService(baseUrl, ApiService::class.java)
                .querier(createAccessToken(), appCode, base58Encode(json.toByteArray()))
                .await()
        }
    }, {
        it != null
    })
    return DBChainQueryResult(body != null, body?.string())
}

/**
 * 查询数据的自定义函数
 */
suspend fun querierFunction(
    apC: String = appCode,
    function_name: String,
    queriedArray: QueriedArray,
    vararg parmas: String
): DBChainQueryResult {
    val body = loopHandleInCount({
        sendRequestForReturn {
            val json = queriedArray.toJson()
            val sb = StringBuilder()
            val queriedArrayEncode = base58Encode(json.toByteArray())
            sb.append(queriedArrayEncode)
            parmas.forEach {
                sb.append("/")
                base58Encode(it.toByteArray())
            }
            return@sendRequestForReturn createService(baseUrl, ApiService::class.java)
                .querierFunction(createAccessToken(), apC, function_name, base58Encode(sb.toString().toByteArray()))
                .await()
        }
    }, {
        it != null
    })
    return DBChainQueryResult(body != null, body?.string())
}

/**
 * 格式化请求体
 */
private suspend fun formatRequestBodyJson(
    msgList: List<Message>,
    accountBean: BaseResponseDbChain<AccountBean>
): String {
    val denomAmountList = sendRequestForReturn {
        createService(baseUrl, ApiService::class.java).getMinGasPrices(createAccessToken()).await().result
    }
    val gas = msgList.size * defaultGasNumber
    if (denomAmountList != null && denomAmountList.isNotEmpty()) {
        val amount = BigDecimal(denomAmountList[0].amount)
        val multiply = amount.multiply(BigDecimal(gas)).toDouble()
        val ceil = ceil(multiply).toLong().toString()
        denomAmountList[0].amount = ceil
    }
    val fee = FeeBean(amount = denomAmountList ?: listOf(), gas = "$gas")
    val tx = TxBean(fee = fee, msg = msgList)
    val account = accountBean.result.value
    val signMeta = SignMetaBean(chainId, "${account.account_number}", "${account.sequence}")
    // 签名
    val signedTx = signTx(tx, signMeta, dbChainKey)
    return Gson().toJson(BodyBean(tx = signedTx))
}

private suspend inline fun createTransaction(
    block: (accountBean: BaseResponseDbChain<AccountBean>) -> String
): QueryOperationResultBean? {
    return sendRequestForReturn {
        val apiService = createService(baseUrl, ApiService::class.java)
        // 获取account
        val accountBean = apiService.getAccountAsync(dbChainKey.address).await()
        // 组装请求数据
        val broadcastBody = block(accountBean)
        // 插入成功返回的哈希
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), broadcastBody)
        val txHashBean = apiService.insertAsync(requestBody).await()
        return@sendRequestForReturn loopHandleInTime({
            sendRequestForReturn inner@{
                return@inner apiService.restGetAsync(txHashBean.txhash).await()
            }
        }, {
            it != null
        })
    }
}



