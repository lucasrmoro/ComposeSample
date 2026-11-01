package br.com.composesample.structural.network.http

/**
 * Required on every serialized request/response DTO field, including nested DTOs.
 * The JSON name remains stable after obfuscation; names must not be blank.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonName(val value: String)
