package com.appunite.loudius.rules

import com.pinterest.ktlint.core.RuleProvider
import com.pinterest.ktlint.core.RuleSetProviderV2
import com.twitter.compose.rules.ktlint.ComposeCompositionLocalAllowlistCheck
import com.twitter.compose.rules.ktlint.ComposeCompositionLocalNamingCheck
import com.twitter.compose.rules.ktlint.ComposeContentEmitterReturningValuesCheck
import com.twitter.compose.rules.ktlint.ComposeModifierComposableCheck
import com.twitter.compose.rules.ktlint.ComposeModifierMissingCheck
import com.twitter.compose.rules.ktlint.ComposeModifierReusedCheck
import com.twitter.compose.rules.ktlint.ComposeModifierWithoutDefaultCheck
import com.twitter.compose.rules.ktlint.ComposeMultipleContentEmittersCheck
import com.twitter.compose.rules.ktlint.ComposeMutableParametersCheck
import com.twitter.compose.rules.ktlint.ComposeNamingCheck
import com.twitter.compose.rules.ktlint.ComposeParameterOrderCheck
import com.twitter.compose.rules.ktlint.ComposePreviewNamingCheck
import com.twitter.compose.rules.ktlint.ComposePreviewPublicCheck
import com.twitter.compose.rules.ktlint.ComposeRememberMissingCheck
import com.twitter.compose.rules.ktlint.ComposeUnstableCollectionsCheck
import com.twitter.compose.rules.ktlint.ComposeViewModelForwardingCheck
import com.twitter.compose.rules.ktlint.ComposeViewModelInjectionCheck

internal const val RULE_SET_ID = "loudius-rule-set-id"

class CustomRuleSetProvider : RuleSetProviderV2(id = RULE_SET_ID, about = NO_ABOUT) {
    override fun getRuleProviders(): Set<RuleProvider> =
        setOf(
            RuleProvider { UseStriktAssertionLibrary() }
        )
}
