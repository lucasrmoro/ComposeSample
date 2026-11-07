package br.com.composesample.modules

object Modules {
    object Structural {
        val Analytics = StructuralModules.Analytics
        val AssetsCache = StructuralModules.AssetsCache
        val Biometric = StructuralModules.Biometric
        val Camera = StructuralModules.Camera
        val Core = StructuralModules.Core
        val DI = StructuralModules.DI
        val DesignSystem = StructuralModules.DesignSystem
        val LocalStorage = StructuralModules.LocalStorage
        val Localization = StructuralModules.Localization
        val Logger = StructuralModules.Logger
        val Media = StructuralModules.Media
        val MediaCompression = StructuralModules.MediaCompression
        val Navigation = StructuralModules.Navigation
        val Network = StructuralModules.Network
        val Notifications = StructuralModules.Notifications
        val Permissions = StructuralModules.Permissions
        val PushNotifications = StructuralModules.PushNotifications
        val RemoteConfig = StructuralModules.RemoteConfig
        val Security = StructuralModules.Security
        val Session = StructuralModules.Session
        val VideoPlayer = StructuralModules.VideoPlayer
    }

    object Feature {
        val FeatureA = FeatureModules.FeatureA
        val Login = FeatureModules.Login
        val Stream = FeatureModules.Stream
    }
}
