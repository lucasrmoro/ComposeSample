package br.com.composesample.modules

object Modules {
    const val COMMON_DOMAIN = ":common:domain"

    object Structural {
        val Analytics = StructuralModules.Analytics
        val Biometric = StructuralModules.Biometric
        val Camera = StructuralModules.Camera
        val Media = StructuralModules.Media
        val MediaCompression = StructuralModules.MediaCompression
        val Core = StructuralModules.Core
        val DesignSystem = StructuralModules.DesignSystem
        val DI = StructuralModules.DI
        val LocalStorage = StructuralModules.LocalStorage
        val Localization = StructuralModules.Localization
        val Logger = StructuralModules.Logger
        val Navigation = StructuralModules.Navigation
        val Network = StructuralModules.Network
        val Notifications = StructuralModules.Notifications
        val Permissions = StructuralModules.Permissions
        val PushNotifications = StructuralModules.PushNotifications
        val RemoteConfig = StructuralModules.RemoteConfig
        val Session = StructuralModules.Session
    }

    object Feature {
        val FeatureA = FeatureModules.FeatureA
        val Login = FeatureModules.Login
    }
}
