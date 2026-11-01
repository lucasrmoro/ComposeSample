-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

-keep,allowobfuscation class br.com.composesample.structural.network.http.HttpResponseType
-keep,allowobfuscation class * extends br.com.composesample.structural.network.http.HttpResponseType

-keepclasseswithmembers,allowobfuscation class * {
    @br.com.composesample.structural.network.http.JsonName <fields>;
}

# Optional JVM TLS providers are not bundled with this Android client.
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
