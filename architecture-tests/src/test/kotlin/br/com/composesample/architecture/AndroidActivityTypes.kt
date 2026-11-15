package br.com.composesample.architecture

import io.github.baole.konture.ClassDeclaration

internal class AndroidActivityTypes(classes: List<ClassDeclaration>) {

    private val classesByName = classes.associateBy { it.fqName }

    fun isActivity(declaration: ClassDeclaration): Boolean =
        inheritsActivity(declaration = declaration, visited = mutableSetOf())

    private fun inheritsActivity(declaration: ClassDeclaration, visited: MutableSet<String>): Boolean {
        if (!visited.add(declaration.fqName)) return false
        return declaration.supertypes.any { supertype ->
            val qualifiedType = declaration.qualifiedSupertype(supertype)
            qualifiedType in androidActivityClasses || classesByName[qualifiedType]?.let { parent ->
                inheritsActivity(declaration = parent, visited = visited)
            } == true
        }
    }

    private fun ClassDeclaration.qualifiedSupertype(supertype: String): String {
        val type = supertype.substringBefore('<').removeSuffix("?")
        if ('.' in type) return type
        return imports.firstOrNull { import -> import.substringAfterLast('.') == type } ?: "$packageName.$type"
    }

    private companion object {
        val androidActivityClasses = setOf(
            "android.app.Activity",
            "android.app.ActivityGroup",
            "android.app.AliasActivity",
            "android.app.ExpandableListActivity",
            "android.app.LauncherActivity",
            "android.app.ListActivity",
            "android.app.NativeActivity",
            "android.app.TabActivity",
            "android.accounts.AccountAuthenticatorActivity",
            "android.preference.PreferenceActivity",
            "androidx.activity.ComponentActivity",
            "androidx.core.app.ComponentActivity",
            "androidx.fragment.app.FragmentActivity",
            "androidx.appcompat.app.AppCompatActivity"
        )
    }

}
