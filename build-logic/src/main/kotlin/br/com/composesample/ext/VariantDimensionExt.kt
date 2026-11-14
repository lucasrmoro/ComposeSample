package br.com.composesample.ext

import com.android.build.api.dsl.VariantDimension

private const val STRING = "String"

fun VariantDimension.stringBuildConfigField(name: String, value: String) {
    buildConfigField(type = STRING, name = name, value = "\"$value\"")
}
