package com.gcigb.dbchain.net

import com.gcigb.dbchain.bean.AccountBean
import com.gcigb.dbchain.bean.result.DBChainResult
import com.gcigb.dbchain.bean.result.DenomAmount
import com.gcigb.dbchain.bean.result.QueryOperationResultBean
import com.gcigb.dbchain.bean.result.TxHashBean
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @author: Xiao Bo
 * @date: 12/10/2020
 */
interface ApiService {
    /**
     * 获取account
     */
    @GET("auth/accounts/{address}")
    fun getAccountAsync(@Path("address") address: String): Call<BaseResponseDbChain<AccountBean>>

    /**
     * 获取哈希
     */
    @POST("txs")
    fun insertAsync(@Body body: RequestBody): Call<TxHashBean>

    /**
     * 插入数据之后，几秒后请求这个接口查看数据
     */
    @GET("txs/{txHash}")
    fun restGetAsync(@Path("txHash") txHash: String): Call<QueryOperationResultBean>

    /**
     * 查询
     */
    @GET("dbchain/querier/{token}/{appCode}/{query}")
    fun querier(@Path("token") token: String, @Path("appCode") appCode: String, @Path("query") query: String): Call<ResponseBody>

    /**
     * 自定义的查询函数
     */
    @GET("dbchain/call-custom-querier/{token}/{appCode}/{querierName}/{params}")
    fun querierFunction(
        @Path("token") token: String,
        @Path("appCode") appCode: String,
        @Path("querierName") querierName: String,
        @Path("params") params: String
    ): Call<ResponseBody>

    /**
     * 请求打一个积分
     */
    @GET("dbchain/oracle/new_app_user/{token}")
    fun requestAppUser(@Path("token") token: String): Call<Response<DBChainResult>?>

    /**
     * 获取积分数量
     */
    @GET("auth/accounts/{address}")
    fun getToken(@Path("address") address: String): Call<BaseResponseDbChain<AccountBean>>

    @GET("dbchain/min_gas_prices/{token}")
    fun getMinGasPrices(@Path("token") token: String):Call<BaseResponseDbChain<List<DenomAmount>?>>

    @GET("dbchain/application/{token}")
    fun queryApplication(@Path("token") token: String):Call<BaseResponseDbChain<List<String>>>
}