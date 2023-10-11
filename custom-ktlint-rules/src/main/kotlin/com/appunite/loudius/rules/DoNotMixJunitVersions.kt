package com.appunite.loudius.rules

import com.pinterest.ktlint.core.Rule
import com.pinterest.ktlint.core.ast.ElementType
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtImportDirective

class DoNotMixJunitVersions : Rule("do-not-mix-junit-versions") {
    val junit4Annotations = listOf(
        "org.junit.Test",
        "org.junit.Before",
        "org.junit.After",
        "org.junit.Ignore",
        "org.junit.runner.RunWith",
        "org.junit.runners.Parameterized",
        "org.junit.runners.Theory"
    )
    val junit5Annotations = listOf(
        "org.junit.jupiter.api.Test",
        "org.junit.jupiter.api.BeforeEach",
        "org.junit.jupiter.api.AfterEach",
        "org.junit.jupiter.api.Disabled",
        "org.junit.jupiter.api.extension.ExtendWith",
        "org.junit.jupiter.params.ParameterizedTest",
        "org.junit.jupiter.params.provider.ValueSource"
    )


    //    private val forbiddenPackageNames = mapOf(
//        "org.junit.Test" to "org.junit.jupiter.api.Test",
//    )
    override fun beforeVisitChildNodes(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit,
    ) {
        if (node.elementType == ElementType.IMPORT_LIST) {
            val children = node.getChildren(null)
            if (children.isNotEmpty()) {
                val imports = children
                    .filter { it.elementType == ElementType.IMPORT_DIRECTIVE }
                    .mapNotNull { it.psi as? KtImportDirective }
                    .mapNotNull { it.importPath?.pathStr }

                val junit4Imports = imports.filter { junit4Annotations.contains(it) }
                val junit5Imports = imports.filter { junit5Annotations.contains(it) }

                if (junit4Imports.isNotEmpty() && junit5Imports.isNotEmpty()) {
                    val errorMessage = "${junit4Imports.joinToString(separator = ",")} " +
                            "and ${junit5Imports.joinToString(separator = ",")} " +
                            "packages are from different JUnit versions. Don't mix Junit4 with Junit5 in a single test."
                    emit(
                        node.startOffset,
                        errorMessage,
                        false,
                    )
                }
            }
        }
    }


}
