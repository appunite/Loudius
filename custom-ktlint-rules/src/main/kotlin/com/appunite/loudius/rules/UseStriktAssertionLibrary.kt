/*
 * Copyright 2023 AppUnite S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appunite.loudius.rules

import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class UseStriktAssertionLibrary : Rule(RuleId("custom:use-strikt-assertion-library"), about = About()) {
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
