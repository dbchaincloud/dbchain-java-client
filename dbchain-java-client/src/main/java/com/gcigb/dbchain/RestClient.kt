package com.gcigb.dbchain

import com.gcigb.dbchain.bean.*
import com.gcigb.dbchain.bean.result.DBChainQueryResult
import com.gcigb.dbchain.bean.result.QueryOperationResultBean
import com.gcigb.dbchain.check.checkDBChainOperactionSuccess
import com.gcigb.dbchain.cosmossig.signTx
import com.gcigb.dbchain.net.ApiService
import com.gcigb.dbchain.net.BaseResponseDbChain
import com.gcigb.dbchain.util.coding.base58Encode
import com.gcigb.network.RetrofitClient
import com.gcigb.network.request.RequestClient
import com.google.gson.Gson

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
 * 查询数据
 */
suspend fun querier(queriedArray: QueriedArray): DBChainQueryResult {
    val body = loopHandleInCount({
        RetrofitClient.sendRequestForReturn {
            val json = queriedArray.toJson()
            return@sendRequestForReturn RetrofitClient.createService(DBChain.baseUrl, ApiService::class.java)
                .querier(createAccessToken(), DBChain.appCode, base58Encode(json.toByteArray()))
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
    appCode: String = DBChain.appCode,
    function_name: String,
    queriedArray: QueriedArray,
    vararg parmas: String
): DBChainQueryResult {
    val body = loopHandleInCount({
        RetrofitClient.sendRequestForReturn {
            val json = queriedArray.toJson()
            val sb = StringBuilder()
            val queriedArrayEncode = base58Encode(json.toByteArray())
            sb.append(queriedArrayEncode)
            parmas.forEach {
                sb.append("/")
                base58Encode(it.toByteArray())
            }
            return@sendRequestForReturn RetrofitClient.createService(DBChain.baseUrl, ApiService::class.java)
                .querierFunction(createAccessToken(), appCode, function_name, base58Encode(sb.toString().toByteArray()))
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
private fun formatRequestBodyJson(
    msgList: List<Message>,
    accountBean: BaseResponseDbChain<AccountBean>
): String {
    val tx = TxBean(msg = msgList)
    val account = accountBean.result.value
    val signMeta = SignMetaBean(DBChain.chainId, "${account.account_number}", "${account.sequence}")
    // 签名
    val signedTx = signTx(tx, signMeta, DBChain.dbChainKey)
    return Gson().toJson(BodyBean(tx = signedTx))
}

private suspend inline fun createTransaction(
    block: (accountBean: BaseResponseDbChain<AccountBean>) -> String
): QueryOperationResultBean? {
    return RetrofitClient.sendRequestForReturn {
        val apiService = RetrofitClient.createService(DBChain.baseUrl, ApiService::class.java)
        // 获取account
        val accountBean = apiService.getAccountAsync(DBChain.dbChainKey.address).await()
        // 组装请求数据
        val broadcastBody = block(accountBean)
        // 插入成功返回的哈希
        val txHashBean = apiService.insertAsync(RequestClient.getJsonRequestBody(broadcastBody)).await()
        return@sendRequestForReturn loopHandleInTime({
            RetrofitClient.sendRequestForReturn inner@{
                return@inner apiService.restGetAsync(txHashBean.txhash).await()
            }
        }, {
            it != null
        })
    }
}



