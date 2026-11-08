package br.com.composesample.structural.biometric

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal class BiometricCipher {

    fun forEncryption(key: SecretKey): Cipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, key)
    }

    fun forDecryption(key: SecretKey, data: BiometricEncryptedData): Cipher {
        val payload = decode(data)
        val iv = payload.copyOfRange(VERSION_SIZE, HEADER_SIZE)
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))
        }
    }

    fun encrypt(cipher: Cipher, data: ByteArray): BiometricEncryptedData {
        val encrypted = cipher.doFinal(data)
        val iv = cipher.iv
        require(iv.size == IV_SIZE)
        val payload = byteArrayOf(FORMAT_VERSION) + iv + encrypted
        return BiometricEncryptedData(Base64.getEncoder().encodeToString(payload))
    }

    fun decrypt(cipher: Cipher, data: BiometricEncryptedData): ByteArray {
        val payload = decode(data)
        val encrypted = payload.copyOfRange(HEADER_SIZE, payload.size)
        return cipher.doFinal(encrypted)
    }

    private fun decode(data: BiometricEncryptedData): ByteArray {
        val payload = Base64.getDecoder().decode(data.value)
        require(payload.size >= HEADER_SIZE + TAG_SIZE / Byte.SIZE_BITS)
        require(payload.first() == FORMAT_VERSION)
        return payload
    }

    private companion object {
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val IV_SIZE = 12
        const val TAG_SIZE = 128
        const val FORMAT_VERSION: Byte = 1
        const val VERSION_SIZE = 1
        const val HEADER_SIZE = VERSION_SIZE + IV_SIZE
    }

}
