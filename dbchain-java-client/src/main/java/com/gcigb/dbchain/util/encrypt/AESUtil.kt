package com.gcigb.dbchain.util.encrypt

import com.gcigb.dbchain.util.coding.base64Decode
import com.gcigb.dbchain.util.coding.base64Encode
import com.gcigb.dbchain.util.coding.HexUtil
import com.gcigb.network.util.logI
import java.io.*
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

/**
 * @author: Xiao Bo
 * @date: 10/12/2020
 */
class AESUtil {
    companion object {
        private const val ALGORITHM = "AES"
        private const val KEY_SIZE = 256
        private const val CACHE_SIZE = 1024

        /**
         * 生成随机密钥
         */
        @Throws(Exception::class)
        fun getSecretKey(): String? {
            return getSecretKey(null)
        }

        /**
         * 生成密钥
         */
        @Throws(Exception::class)
        fun getSecretKey(seed: String?): String? {
            val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
            val secureRandom: SecureRandom = if (seed != null) {
                SecureRandom(seed.toByteArray())
            } else {
                SecureRandom()
            }
            keyGenerator.init(KEY_SIZE, secureRandom)
            val secretKey = keyGenerator.generateKey()
            return HexUtil.encodeHexString(secretKey.encoded)
        }

        fun encrypt(keyHex: String, data: String): String {
            val secretKeySpec =
                toKey(HexUtil.decode(keyHex))
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            return try {
                val byteArray = cipher.doFinal(data.toByteArray())
                base64Encode(byteArray)
            } catch (e: java.lang.Exception) {
                data
            }
        }

        fun decrypt(keyHex: String, data: String): String {
            return try {
                val secretKeySpec = toKey(HexUtil.decode(keyHex))
                val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
                val byteArray = cipher.doFinal(base64Decode(data))
                String(byteArray)
            } catch (e: java.lang.Exception) {
                data
            }

        }


        /**
         * 文件加密
         */
        @Throws(Exception::class)
        fun encryptFile(
            privateKey: String,
            sourceFilePath: String,
            destFilePath: String,
            defaultSuffix: String = ".aes"
        ): File {
            val startTime = System.currentTimeMillis()
            val sourceFile = File(sourceFilePath)
            val destFile = File("$destFilePath$defaultSuffix")
            if (sourceFile.exists() && sourceFile.isFile) {
                if (!destFile.parentFile.exists()) {
                    destFile.parentFile.mkdirs()
                }
                destFile.createNewFile()
                val inputStream: InputStream = FileInputStream(sourceFile)
                val out: OutputStream = FileOutputStream(destFile)
                val secretKeySpec = toKey(
                    HexUtil.decode(privateKey)
                )
                val cipher = Cipher.getInstance(ALGORITHM)
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
                val cin = CipherInputStream(inputStream, cipher)
                val cache = ByteArray(CACHE_SIZE)
                var nRead: Int
                while (cin.read(cache).also { nRead = it } != -1) {
                    out.write(cache, 0, nRead)
                    out.flush()
                }
                out.close()
                cin.close()
                inputStream.close()
            }
            return destFile
        }

        /**
         * 文件解密
         */
        @Throws(Exception::class)
        fun decryptFile(
            privateKey: String,
            sourceFilePath: String,
            destFilePath: String
        ): File {
            val startTime = System.currentTimeMillis()
            val sourceFile = File(sourceFilePath)
            val destFile = File(destFilePath)
            if (sourceFile.exists() && sourceFile.isFile) {
                if (!destFile.parentFile.exists()) {
                    destFile.parentFile.mkdirs()
                }
                destFile.createNewFile()
                val inputStream = FileInputStream(sourceFile)
                val out = FileOutputStream(destFile)
                val secretKeySpec = toKey(
                    HexUtil.decode(privateKey)
                )
                val cipher = Cipher.getInstance(ALGORITHM)
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
                val cout = CipherOutputStream(out, cipher)
                val cache = ByteArray(CACHE_SIZE)
                var nRead = 0
                while (inputStream.read(cache).also { nRead = it } != -1) {
                    cout.write(cache, 0, nRead)
                    cout.flush()
                }
                cout.close()
                out.close()
                inputStream.close()

            }
            return destFile
        }


        /**
         * 文件解密
         */
        @Throws(Exception::class)
        fun decryptFileAndOutput(
            key: String,
            inputStream: InputStream,
            out: OutputStream
        ) {
            val k =
                toKey(
                    base64Decode(key)
                )
            val raw = k.encoded
            val secretKeySpec =
                SecretKeySpec(
                    raw,
                    ALGORITHM
                )
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            val cout = CipherOutputStream(out, cipher)
            val cache = ByteArray(CACHE_SIZE)
            var nRead = 0
            while (inputStream.read(cache).also { nRead = it } != -1) {
                cout.write(cache, 0, nRead)
                cout.flush()
            }
            cout.close()
            out.close()
            inputStream.close()
        }

        /**
         * 转换密钥
         */
        @Throws(Exception::class)
        private fun toKey(key: ByteArray): Key {
            return SecretKeySpec(key, ALGORITHM)
        }
    }
}