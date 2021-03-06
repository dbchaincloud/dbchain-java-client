package com.gcigb.dbchain.util

fun ByteArray.addByteArray(byteArray: ByteArray): ByteArray {
    val result = ByteArray(this.size + byteArray.size)
    System.arraycopy(this, 0, result, 0, this.size)
    System.arraycopy(byteArray, 0, result, this.size, byteArray.size)
    return result
}

fun ByteArray.subBefore(length: Int): ByteArray {
    if (this.size <= length) return this
    val result = ByteArray(length)
    System.arraycopy(this, 0, result, 0, length)
    return result
}

fun ByteArray.subAfter(afterIndex: Int): ByteArray {
    if (this.size <= afterIndex) return this
    val result = ByteArray(size - afterIndex)
    System.arraycopy(this, afterIndex, result, 0, result.size)
    return result
}