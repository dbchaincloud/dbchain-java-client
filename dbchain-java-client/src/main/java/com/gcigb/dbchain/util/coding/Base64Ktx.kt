package com.gcigb.dbchain.util.coding

import java.util.*

/**
 * @author: Xiao Bo
 * @date: 24/10/2020
 */

fun base64Encode(bytes: ByteArray): String {
    return Base64.getEncoder().encodeToString(bytes)
}

fun base64Decode(str: String): ByteArray {
    return Base64.getDecoder().decode(str)
}

fun base64EncodeByHexString(str: String): String {
    return base64Encode(HexUtil.decode(str))
}
