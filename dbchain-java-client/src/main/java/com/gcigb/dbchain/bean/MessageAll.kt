package com.gcigb.dbchain.bean

import com.gcigb.dbchain.DBChain

sealed class BaseMessageValue

internal data class CreateApplicationMessageValue(
    // 库描述
    val description: String,
    // 库名称
    val name: String,
    // 创建者地址
    val owner: String,
    // 权限
    val permission_required: Boolean
) : BaseMessageValue()

internal data class CreateTableMessageValue(
    //固定，测试和生产环境区分
    val app_code: String = DBChain.appCode,
    //插入字段
    val fields: List<String>,
    //用户地址
    val owner: String = DBChain.dbChainKey.address,
    //插入数据的表名称
    val table_name: String
) : BaseMessageValue()

internal data class InsertMessageValue(
    //固定，测试和生产环境区分
    val app_code: String = DBChain.appCode,
    //插入字段
    val fields: String,
    //用户地址
    val owner: String = DBChain.dbChainKey.address,
    //插入数据的表名称
    val table_name: String
) : BaseMessageValue()

internal data class FreezeMessageValue(
    //固定，测试和生产环境区分
    val app_code: String = DBChain.appCode,
    //插入字段
    val id: String,
    //用户地址
    val owner: String = DBChain.dbChainKey.address,
    //插入数据的表名称
    val table_name: String
) : BaseMessageValue()

internal data class AddFunctionMessageValue(
    //固定，测试和生产环境区分
    val app_code: String = DBChain.appCode,
    //函数体
    val body: String,
    //参数：["table_a_tableName","table_a_value","table_b_tableName","table_b_value"]
    val description: String,
    //创建的函数名称
    val function_name: String,
    //用户地址
    val owner: String = DBChain.dbChainKey.address
) : BaseMessageValue()

internal data class CallFunctionMessageValue(
    //固定，测试和生产环境区分
    val app_code: String = DBChain.appCode,
    //参数：["table_a_tableName","table_a_value","table_b_tableName","table_b_value"]
    val argument: String,
    //调用的函数名称
    val function_name: String,
    //用户地址
    val owner: String = DBChain.dbChainKey.address
) : BaseMessageValue()

internal data class DropFunctionMessageValue(
    //固定，测试和生产环境区分
    val app_code: String = DBChain.appCode,
    //函数名称
    val function_name: String,
    //用户地址
    val owner: String = DBChain.dbChainKey.address
) : BaseMessageValue()