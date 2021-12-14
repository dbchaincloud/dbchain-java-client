package com.gcigb.dbchain.util.encrypt

import com.gcigb.dbchain.util.addByteArray
import com.gcigb.dbchain.util.subAfter
import com.gcigb.dbchain.util.subBefore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESEncrypt {

    private const val SEED_BYTES_SIZE = 16

    fun randomSeed(numBytes: Int): ByteArray {
        return SecureRandom().generateSeed(numBytes)
    }

    fun generateKey(seed: ByteArray): ByteArray {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(SecureRandom(seed))
        return keyGen.generateKey().encoded
    }

    fun encrypt(key: ByteArray, input: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        val iv = SecureRandom.getInstanceStrong().generateSeed(SEED_BYTES_SIZE)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(iv))
        val encryptedData = cipher.doFinal(input)
        return iv.addByteArray(encryptedData)
    }

    fun decrypt(key: ByteArray, input: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        val iv = input.subBefore(SEED_BYTES_SIZE)
        val encryptedData = input.subAfter(SEED_BYTES_SIZE)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv))
        return cipher.doFinal(encryptedData)
    }
}