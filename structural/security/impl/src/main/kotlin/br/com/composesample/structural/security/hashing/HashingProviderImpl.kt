package br.com.composesample.structural.security.hashing

import java.security.MessageDigest

internal class HashingProviderImpl : HashingProvider {

    override fun sha256(value: String): String =
        MessageDigest.getInstance(ALGORITHM)
            .digest(value.toByteArray(Charsets.UTF_8))
            .joinToString(separator = "") {
                HEX_BYTE_FORMAT.format(it)
            }

    private companion object {
        const val ALGORITHM = "SHA-256"
        const val HEX_BYTE_FORMAT = "%02x"
    }
}

fun createHashingProvider(): HashingProvider = HashingProviderImpl()
