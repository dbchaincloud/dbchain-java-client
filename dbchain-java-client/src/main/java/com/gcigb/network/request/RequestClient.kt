package com.gcigb.network.request

import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * @author: Xiao Bo
 * @date: 10/12/2020
 */
class RequestClient {
    companion object {
        fun getJsonRequestBody(json: String): RequestBody {
            return RequestBody.create(MediaType.parse("application/json"), json)
        }
    }
}