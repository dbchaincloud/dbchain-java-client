package com.gcigb.dbchain

import com.gcigb.dbchain.util.addByteArray
import com.gcigb.dbchain.util.coding.hash256
import com.gcigb.dbchain.util.encrypt.AESEncrypt
import com.gcigb.dbchain.util.subBefore

object KeyEscrow {

    private const val SY_KEY_SIZE = 16
    private val HASH_SUFFIX_SECRET = "secret".toByteArray()
    private val HASH_SUFFIX_PRIVATE = "private".toByteArray()

    fun createAndSavePrivateKeyWithPassword(userName: String, password: String, privateKeyDB: IPrivateKeyDB): Boolean {
        return savePrivateKey(
            userName = userName.toByteArray(),
            passwordOrRecoverWord = password.toByteArray(),
            privateKey = generateMnemonic().privateKeyBytes,
            privateKeyDB = privateKeyDB
        )
    }

    fun loadPrivateKeyByPassword(userName: String, password: String, privateKeyDB: IPrivateKeyDB): ByteArray? {
        return loadPrivateKey(
            userName = userName.toByteArray(),
            passwordOrRecoverWord = password.toByteArray(),
            privateKeyDB = privateKeyDB
        )
    }

    fun savePrivateKeyWithRecoverWord(userName: String, recoverWord: String, privateKey: ByteArray, privateKeyDB: IPrivateKeyDB): Boolean {
        return savePrivateKey(
            userName = userName.toByteArray(),
            passwordOrRecoverWord = recoverWord.toByteArray(),
            privateKey = privateKey,
            privateKeyDB = privateKeyDB
        )
    }

    fun loadPrivateKeyByRecoverWord(userName: String, recoverWord: String, privateKeyDB: IPrivateKeyDB): ByteArray? {
        return loadPrivateKey(
            userName = userName.toByteArray(),
            passwordOrRecoverWord = recoverWord.toByteArray(),
            privateKeyDB = privateKeyDB
        )
    }

    fun resetPasswordFromRecoverWord(userName: String, recoverWord: String, newPassword: String, privateKeyDB: IPrivateKeyDB): Boolean {
        val privateKey = loadPrivateKey(userName.toByteArray(), recoverWord.toByteArray(), privateKeyDB)
            ?: throw NullPointerException("Invalid recoverWord !!!")
        return savePrivateKey(
            userName = userName.toByteArray(),
            passwordOrRecoverWord = newPassword.toByteArray(),
            privateKey = privateKey,
            privateKeyDB = privateKeyDB
        )
    }

    fun resetPasswordFromOld(userName: String, oldPassword: String, newPassword: String, privateKeyDB: IPrivateKeyDB): Boolean {
        val privateKey = loadPrivateKey(userName.toByteArray(), oldPassword.toByteArray(), privateKeyDB)
            ?: throw NullPointerException("Invalid oldPassword !!!")
        return savePrivateKey(
            userName = userName.toByteArray(),
            passwordOrRecoverWord = newPassword.toByteArray(),
            privateKey = privateKey,
            privateKeyDB = privateKeyDB
        )
    }

    private fun savePrivateKey(
        userName: ByteArray,
        passwordOrRecoverWord: ByteArray,
        privateKey: ByteArray,
        privateKeyDB: IPrivateKeyDB
    ): Boolean {
        val seed = randomSeed()
        val encryptedPrivateKey = AESEncrypt.encrypt(f1(seed, passwordOrRecoverWord), privateKey)
        val secret = AESEncrypt.encrypt(f2(userName, passwordOrRecoverWord), seed)
        val keyOfSecret = hash1(userName, passwordOrRecoverWord)
        val keyOfPrivate = hash2(userName, passwordOrRecoverWord)
        return privateKeyDB.saveEncryptedPrivateKey(keyOfPrivate, encryptedPrivateKey) && privateKeyDB.saveSecret(keyOfSecret, secret)
    }

    private fun loadPrivateKey(userName: ByteArray, passwordOrRecoverWord: ByteArray, privateKeyDB: IPrivateKeyDB): ByteArray? {
        val keyOfSecret = hash1(userName, passwordOrRecoverWord)
        val keyOfPrivate = hash2(userName, passwordOrRecoverWord)
        val secret = privateKeyDB.loadSecret(keyOfSecret) ?: return null
        val encryptedPrivateKey = privateKeyDB.loadEncryptedPrivateKey(keyOfPrivate) ?: return null
        val seed = AESEncrypt.decrypt(f2(userName, passwordOrRecoverWord), secret)
        return AESEncrypt.decrypt(f1(seed, passwordOrRecoverWord), encryptedPrivateKey)
    }

    private fun f1(var1: ByteArray, var2: ByteArray): ByteArray = hash256(var1.addByteArray(var2)).subBefore(SY_KEY_SIZE)

    private fun f2(var1: ByteArray, var2: ByteArray): ByteArray = f1(var1, var2)

    private fun hash1(var1: ByteArray, var2: ByteArray): ByteArray = hash256(var1.addByteArray(var2).addByteArray(HASH_SUFFIX_SECRET))

    private fun hash2(var1: ByteArray, var2: ByteArray): ByteArray = hash256(var1.addByteArray(var2).addByteArray(HASH_SUFFIX_PRIVATE))

    private fun randomSeed() = AESEncrypt.randomSeed(SY_KEY_SIZE)
}

interface IPrivateKeyDB {
    fun saveEncryptedPrivateKey(key: ByteArray, encryptedPrivateKeyByteArray: ByteArray): Boolean
    fun loadEncryptedPrivateKey(key: ByteArray): ByteArray?

    fun saveSecret(key: ByteArray, secret: ByteArray): Boolean
    fun loadSecret(key: ByteArray): ByteArray?
}