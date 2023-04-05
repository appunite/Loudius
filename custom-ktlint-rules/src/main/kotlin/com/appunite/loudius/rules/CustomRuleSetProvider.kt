package com.appunite.loudius.rules

import com.pinterest.ktlint.core.RuleProvider
import com.pinterest.ktlint.core.RuleSetProviderV2

internal const val RULE_SET_ID = "loudius-rule-set-id"

class CustomRuleSetProvider : RuleSetProviderV2(id = RULE_SET_ID, about = NO_ABOUT) {
    override fun getRuleProviders(): Set<RuleProvider> =
        setOf(
            RuleProvider { UseStriktAssertionLibrary() },
        )
}