package br.com.composesample.structural.analytics

/** An event with textual name/value pairs; names follow Firebase Analytics rules. */
class AnalyticsEvent(
    val name: String,
    vararg parameters: Pair<String, String>
) {

    val parameters: Map<String, String> = parameters.toMap()

    init {
        requireValidName(name)
        require(parameters.size <= 25) { "An event supports at most 25 parameters" }
        require(parameters.size == this.parameters.size) { "Duplicate parameter names" }
        this.parameters.keys.forEach(::requireValidName)
    }

    private companion object {

        val NAME_PATTERN = Regex("[A-Za-z][A-Za-z0-9_]{0,39}")

        val RESERVED_PREFIXES = listOf("firebase_", "google_", "ga_")

        fun requireValidName(name: String) {
            require(name.matches(NAME_PATTERN)) { "Invalid analytics name: $name" }
            require(RESERVED_PREFIXES.none(name::startsWith)) { "Reserved analytics prefix: $name" }
        }

    }

}
