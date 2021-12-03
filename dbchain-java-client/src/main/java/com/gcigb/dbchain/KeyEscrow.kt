package com.gcigb.dbchain

import com.gcigb.dbchain.util.addByteArray
import com.gcigb.dbchain.util.coding.hash256
import com.gcigb.dbchain.util.encrypt.AESEncrypt
import com.gcigb.dbchain.util.subBefore

object KeyEscrow {

    private const val SY_KEY_SIZE = 16
    private val HASH_SUFFIX_SECRET = "secret".toByteArray()
    private val HASH_SUFFIX_PRIVATE = "private".toByteArray()

    fun createAndSave(userName: String, password: String, privateKeyDB: IPrivateKeyDB): Boolean {
        return save(
            seed = randomSeed(),
            userName = userName.toByteArray(),
            var2 = password.toByteArray(),
            privateKey = generateMnemonic().privateKeyBytes,
            privateKeyDB = privateKeyDB
        )
    }

    fun savePassword(userName: String, password: String, privateKey: ByteArray, privateKeyDB: IPrivateKeyDB): Boolean {
        return save(
            seed = randomSeed(),
            userName = userName.toByteArray(),
            var2 = password.toByteArray(),
            privateKey = privateKey,
            privateKeyDB = privateKeyDB
        )
    }

    fun saveRecoverWord(userName: String, recoverWord: String, privateKey: ByteArray, privateKeyDB: IPrivateKeyDB): Boolean {
        return save(
            seed = randomSeed(),
            userName = userName.toByteArray(),
            var2 = recoverWord.toByteArray(),
            privateKey = privateKey,
            privateKeyDB = privateKeyDB
        )
    }

    fun forgetPassword(userName: String, recoverWord: String, newPassword: String, privateKeyDB: IPrivateKeyDB): Boolean {
        val privateKey = loadPrivateKey(userName, recoverWord, privateKeyDB) ?: throw NullPointerException("Invalid recoverWord !!!")
        return savePassword(userName, newPassword, privateKey, privateKeyDB)
    }

    fun resetPassword(userName: String, oldPassword: String, newPassword: String, privateKeyDB: IPrivateKeyDB): Boolean {
        val privateKey = loadPrivateKey(userName, oldPassword, privateKeyDB) ?: throw NullPointerException("Invalid oldPassword !!!")
        return savePassword(userName, newPassword, privateKey, privateKeyDB)
    }

    fun loadPrivateKeyByPassword(userName: String, password: String, privateKeyDB: IPrivateKeyDB): ByteArray? {
        return loadPrivateKey(userName, password, privateKeyDB)
    }

    fun loadPrivateKeyByRecoverWord(userName: String, recoverWord: String, privateKeyDB: IPrivateKeyDB): ByteArray? {
        return loadPrivateKey(userName, recoverWord, privateKeyDB)
    }

    private fun loadPrivateKey(userName: String, var2: String, privateKeyDB: IPrivateKeyDB): ByteArray? {
        return load(
            userName = userName.toByteArray(),
            var2 = var2.toByteArray(),
            privateKeyDB = privateKeyDB
        )
    }

    private fun save(seed: ByteArray, userName: ByteArray, var2: ByteArray, privateKey: ByteArray, privateKeyDB: IPrivateKeyDB): Boolean {
        val encryptPrivateKey = AESEncrypt.encrypt(f1(seed, var2), privateKey)
        val secret = AESEncrypt.encrypt(f2(userName, var2), seed)
        val keySaveSecret = hash1(userName, var2)
        val keySavePrivate = hash2(userName, var2)
        return privateKeyDB.saveEncryptPrivateKey(keySavePrivate, encryptPrivateKey)
                &&
                privateKeyDB.saveSecret(keySaveSecret, secret)
    }

    private fun load(userName: ByteArray, var2: ByteArray, privateKeyDB: IPrivateKeyDB): ByteArray? {
        val keySaveSecret = hash1(userName, var2)
        val keySavePrivate = hash2(userName, var2)
        val secret = privateKeyDB.loadSecret(keySaveSecret) ?: return null
        val encryptPrivateKey = privateKeyDB.loadSecret(keySavePrivate) ?: return null
        val seed = AESEncrypt.decrypt(f2(userName, var2), secret)
        return AESEncrypt.decrypt(f1(seed, var2), encryptPrivateKey)
    }

    private fun f1(var1: ByteArray, var2: ByteArray): ByteArray = hash256(var1.addByteArray(var2)).subBefore(SY_KEY_SIZE)

    private fun f2(var1: ByteArray, var2: ByteArray): ByteArray = f1(var1, var2)

    private fun hash1(var1: ByteArray, var2: ByteArray): ByteArray = hash256(var1.addByteArray(var2).addByteArray(HASH_SUFFIX_SECRET))

    private fun hash2(var1: ByteArray, var2: ByteArray): ByteArray = hash256(var1.addByteArray(var2).addByteArray(HASH_SUFFIX_PRIVATE))

    private fun randomSeed() = AESEncrypt.randomSeed(SY_KEY_SIZE)
}

interface IPrivateKeyDB {
    fun saveEncryptPrivateKey(key: ByteArray, encryptPrivateKeyByteArray: ByteArray): Boolean

    fun saveSecret(key: ByteArray, secret: ByteArray): Boolean

    fun loadEncryptPrivateKey(key: ByteArray): ByteArray?

    fun loadSecret(key: ByteArray): ByteArray?
}