package br.com.composesample.structural.permissions

import androidx.activity.ComponentActivity
import android.content.Context

fun createPermissionsProvider(activity: ComponentActivity): PermissionsProvider = PermissionsProviderImpl(activity)

fun createPermissionsProvider(context: Context): PermissionsProvider = PermissionsProviderImpl(context)
