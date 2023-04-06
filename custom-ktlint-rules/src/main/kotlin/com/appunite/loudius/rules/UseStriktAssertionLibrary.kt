package com.appunite.loudius.rules

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class UseStriktAssertionLibrary : Rule("use-strikt-assertion-library") {
    private val forbiddenPackageNames = listOf(
        "junit.framework.TestCase",
        "org.junit.jupiter.api.Assertions",
        "org.junit.Assert",
        "junit.framework.Assert",
    )
    override fun beforeVisitChildNodes(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit,
    ) {
        if (node.elementType == KtStubElementTypes.IMPORT_DIRECTIVE) {
            val importDirective = node.psi as KtImportDirective
            val path = importDirective.importPath?.pathStr ?: return

            forbiddenPackageNames.forEach { forbiddenPackage ->
                if (path.contains(forbiddenPackage)) {
                    emit(
                        node.startOffset,
                        "Instead of using $forbiddenPackage use strikt.api.expectThat",
                        false,
                    )
                }
            }
        }
    }
}
