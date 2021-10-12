package com.gcigb.dbchain.util.coding

import java.security.MessageDigest

fun hash256(byteArray: ByteArray): ByteArray {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(byteArray)
    return messageDigest.digest()
}



