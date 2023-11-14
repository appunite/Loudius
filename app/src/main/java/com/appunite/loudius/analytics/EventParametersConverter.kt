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

package com.appunite.loudius.analytics

import android.os.Bundle

class EventParametersConverter {

    fun convert(parameters: List<EventParameter>): Bundle {
        val bundle = Bundle()
        for (parameter in parameters) {
            when (parameter) {
                is EventParameter.String -> bundle.putString(parameter.name, parameter.value)
                is EventParameter.Boolean -> bundle.putBoolean(parameter.name, parameter.value)
            }
        }
        return bundle
    }
}
