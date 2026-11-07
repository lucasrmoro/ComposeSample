package br.com.composesample.structural.core.text

/** Defers resource resolution to the UI so state does not retain a Context or a resolved locale. */
sealed class UiText {

    /** Runtime content, such as user input, that is not application-owned translatable copy. */
    data class Raw(val value: String) : UiText()

    /** A string resource identifier and a snapshot of its positional formatting arguments. */
    class StringResource(val id: Int, vararg params: Any) : UiText() {

        val params: List<Any> = params.toList()

    }

}
