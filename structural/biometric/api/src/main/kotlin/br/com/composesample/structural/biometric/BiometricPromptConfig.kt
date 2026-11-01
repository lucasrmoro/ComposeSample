package br.com.composesample.structural.biometric

data class BiometricPromptConfig(
    val title: String,
    val cancelText: String,
    val subtitle: String? = null
) {

    init {
        require(title.isNotBlank()) { "Biometric prompt title must not be blank" }
        require(cancelText.isNotBlank()) { "Biometric prompt cancel text must not be blank" }
    }

}
