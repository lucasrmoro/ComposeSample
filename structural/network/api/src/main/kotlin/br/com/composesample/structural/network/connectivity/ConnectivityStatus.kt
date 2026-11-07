package br.com.composesample.structural.network.connectivity

/** Validation state of the default network; it does not guarantee endpoint reachability. */
enum class ConnectivityStatus {
    /** Android has validated Internet access on the default network. */
    ONLINE,
    /** No default network, no Internet capability, or a detected captive portal. */
    OFFLINE,
    /** A default network exists, but Internet validation has not been confirmed. */
    CHECKING
}
