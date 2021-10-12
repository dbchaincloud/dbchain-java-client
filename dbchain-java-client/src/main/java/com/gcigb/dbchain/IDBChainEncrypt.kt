package com.gcigb.dbchain

import org.bitcoinj.crypto.DeterministicKey

interface IDBChainEncrypt {

    val pubKeyType: String

    /**
     * 签名
     * @param privateByteArray ByteArray 私钥
     * @param data ByteArray 明文
     */
    fun sign(privateByteArray: ByteArray, data: ByteArray): ByteArray

    /**
     * 验证签名
     * @param publicKeyByteArray ByteArray 公钥
     * @param data ByteArray 明文
     * @param sign ByteArray 签名
     * @return Boolean true 通过，否则失败
     */
    fun verify(publicKeyByteArray: ByteArray, data: ByteArray, sign: ByteArray): Boolean

    /**
     * 加密
     * @param publicKeyByteArray ByteArray 公钥
     * @param data ByteArray 明文
     * @return ByteArray 密文
     */
    fun encrypt(publicKeyByteArray: ByteArray, data: ByteArray): ByteArray

    /**
     * 解密
     * @param privateByteArray ByteArray 私钥
     * @param data ByteArray 密文
     * @return ByteArray 解密后的明文
     */
    fun decrypt(privateByteArray: ByteArray, data: ByteArray): ByteArray

    /**
     * 公钥生成地址
     * @param publicKeyByteArray33 ByteArray 33 个字节的公钥（压缩过的）
     * @return String 地址
     */
    fun generateAddressByPublicKeyByteArray33(publicKeyByteArray33: ByteArray): String

    /**
     * 根据私钥生成公钥
     * @param privateByteArray ByteArray
     * @param dkKey DeterministicKey
     * @return ByteArray
     */
    fun generatePublicKey33ByPrivateKey(
        privateByteArray: ByteArray,
        dkKey: DeterministicKey?
    ): ByteArray

    /**
     * 根据私钥生成公钥
     * @param privateByteArray ByteArray
     * @param dkKey DeterministicKey
     * @return ByteArray
     */
    fun generatePublicKey64ByPrivateKey(
        privateByteArray: ByteArray,
        dkKey: DeterministicKey?
    ): ByteArray
}