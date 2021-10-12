package com.gcigb.dbchain.util.coding

import org.bitcoinj.core.Base58

/**
 * @author: Xiao Bo
 * @date: 26/10/2020
 */
fun base58Encode(bytes: ByteArray): String {
    return Base58.encode(bytes)
}