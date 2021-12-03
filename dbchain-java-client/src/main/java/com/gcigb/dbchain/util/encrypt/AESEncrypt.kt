package com.gcigb.dbchain.util.encrypt

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object AESEncrypt {

    fun randomSeed(numBytes: Int): ByteArray {
        return SecureRandom().generateSeed(numBytes)
    }

    fun generateKey(seed: ByteArray): ByteArray {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(SecureRandom(seed))
        return keyGen.generateKey().encoded
    }

    fun encrypt(key: ByteArray, input: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        return cipher.doFinal(input)
    }

    fun decrypt(key: ByteArray, input: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        return cipher.doFinal(input)
    }
}