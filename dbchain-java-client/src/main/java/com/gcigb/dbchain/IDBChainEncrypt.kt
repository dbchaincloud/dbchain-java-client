package com.gcigb.dbchain

import org.bitcoinj.crypto.DeterministicKey

interface IDBChainEncrypt {

    val pubKeyType: String

    /**
     * ǩ��
     * @param privateByteArray ByteArray ˽Կ
     * @param data ByteArray ����
     */
    fun sign(privateByteArray: ByteArray, data: ByteArray): ByteArray

    /**
     * ��֤ǩ��
     * @param publicKeyByteArray ByteArray ��Կ
     * @param data ByteArray ����
     * @param sign ByteArray ǩ��
     * @return Boolean true ͨ��������ʧ��
     */
    fun verify(publicKeyByteArray: ByteArray, data: ByteArray, sign: ByteArray): Boolean

    /**
     * ����
     * @param publicKeyByteArray ByteArray ��Կ
     * @param data ByteArray ����
     * @return ByteArray ����
     */
    fun encrypt(publicKeyByteArray: ByteArray, data: ByteArray): ByteArray

    /**
     * ����
     * @param privateByteArray ByteArray ˽Կ
     * @param data ByteArray ����
     * @return ByteArray ���ܺ������
     */
    fun decrypt(privateByteArray: ByteArray, data: ByteArray): ByteArray

    /**
     * ��Կ���ɵ�ַ
     * @param publicKeyByteArray33 ByteArray 33 ���ֽڵĹ�Կ��ѹ�����ģ�
     * @return String ��ַ
     */
    fun generateAddressByPublicKeyByteArray33(publicKeyByteArray33: ByteArray): String

    /**
     * ����˽Կ���ɹ�Կ
     * @param privateByteArray ByteArray
     * @param dkKey DeterministicKey
     * @return ByteArray
     */
    fun generatePublicKey33ByPrivateKey(
        privateByteArray: ByteArray,
        dkKey: DeterministicKey?
    ): ByteArray

    /**
     * ����˽Կ���ɹ�Կ
     * @param privateByteArray ByteArray
     * @param dkKey DeterministicKey
     * @return ByteArray
     */
    fun generatePublicKey64ByPrivateKey(
        privateByteArray: ByteArray,
        dkKey: DeterministicKey?
    ): ByteArray
}