package br.com.composesample.structural.assets_cache

/** Groups an asset in a caller-selected cache subdirectory, without assigning feature meaning. */
data class AssetRequest(val url: String, val group: String) {

    init {
        require(group.matches(GROUP_PATTERN))
    }

    private companion object {
        val GROUP_PATTERN = Regex("[a-z][a-z0-9-]*")
    }
}
