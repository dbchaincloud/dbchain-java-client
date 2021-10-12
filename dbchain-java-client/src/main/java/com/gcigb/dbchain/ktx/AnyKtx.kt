package com.gcigb.dbchain.ktx

import com.google.gson.GsonBuilder


fun Any.toJsonSort(): String {
    return GsonBuilder().disableHtmlEscaping().create().toJson(this)
}
