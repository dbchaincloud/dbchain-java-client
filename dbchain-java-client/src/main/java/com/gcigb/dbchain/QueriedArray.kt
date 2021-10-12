package com.gcigb.dbchain

import androidx.annotation.StringDef
import com.google.gson.Gson

/**
 * @author: Xiao Bo
 * @date: 16/10/2020
 * 组装查询语句
 */
class QueriedArray(method: String = "table", table: String) :
    ArrayList<BaseQuerier>() {

    init {
        add(TableQuerier(method, table))
    }

    /** @hide
     */
    @StringDef(value = [OPERATOR_EQUAL, OPERATOR_LESS, OPERATOR_LESS_AND_EQUAL, OPERATOR_LARGER, OPERATOR_LARGER_AND_EQUAL])
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Operator

    fun findCreatedBy(address: String): QueriedArray {
        return findEqual("created_by", address)
    }

    /**
     * 操作符：=、<、>、<=、>=
     */
    fun findWhere(field: String, value: String, @Operator operator: String): QueriedArray {
        val queried = FieldQuerier("where", field, value, operator)
        add(queried)
        return this
    }

    fun findEqual(field: String, value: String): QueriedArray {
        return findWhere(field, value, OPERATOR_EQUAL)
    }

    fun select(vararg columns: String): QueriedArray {
        val fields = columns.contentToString()
            .replace("[", "")
            .replace("]", "")
            .replace(" ", "")
        val queried = SelectQuerier(fields = fields)
        add(queried)
        return this
    }

    /**
     * 查询指定 id 的数据
     */
    fun findById(id: String): QueriedArray {
        val queried = FindQuerier(id = id)
        add(queried)
        return this
    }

    /**
     * 查询第一条数据
     */
    fun findFirst(): QueriedArray {
        val queried = CommonQuerier("first")
        add(queried)
        return this
    }

    /**
     * 查询最后一条数据
     */
    fun findLast(): QueriedArray {
        val queried = CommonQuerier("last")
        add(queried)
        return this
    }

    fun order(field: String, direction: String = "desc"): QueriedArray {
        val queried = OrderQuerier(field = field, direction = direction)
        add(queried)
        return this
    }

    fun toJson(): String = Gson().toJson(this)

    companion object {
        const val OPERATOR_EQUAL = "="
        const val OPERATOR_LESS = "<"
        const val OPERATOR_LARGER = ">"
        const val OPERATOR_LESS_AND_EQUAL = "<="
        const val OPERATOR_LARGER_AND_EQUAL = ">="
    }
}

abstract class BaseQuerier {
    abstract val method: String
}

data class CommonQuerier(override val method: String) : BaseQuerier()

data class TableQuerier(override val method: String, val table: String) : BaseQuerier()

data class FieldQuerier(
    override val method: String,
    val field: String,
    val value: String,
    val operator: String
) :
    BaseQuerier()

data class FindQuerier(override val method: String = "find", val id: String) : BaseQuerier()

data class SelectQuerier(override val method: String = "select", val fields: String) : BaseQuerier()

data class OrderQuerier(
    override val method: String = "order",
    val field: String,
    val direction: String = "desc"
) : BaseQuerier()
