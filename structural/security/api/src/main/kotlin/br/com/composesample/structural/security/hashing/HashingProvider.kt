package br.com.composesample.structural.security.hashing

/** Produces deterministic digests without exposing the cryptographic implementation. */
interface HashingProvider {

    /** Returns lowercase hexadecimal SHA-256 over the UTF-8 bytes of [value]. */
    fun sha256(value: String): String

}
