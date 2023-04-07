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

package com.appunite.loudius.ui.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Github app currently have a bug on Xiaomi devices where
 * "Display pup-up windows while running in the background" permission needs to be granted on those
 * devices to allow log-in if the app is installed on those devices. This class helps you find those
 * situations and ask for the permissions.
 *
 * Those steps are necessary to reproduce the Github App issue that is fixed by the class:
 * - Use Xiaomi device
 * - Install github app
 * - Kill the app
 * - Open the Loudius app
 * - Click Log-in
 * - If nothing happens, or you can't continue to log-in the issue persist.
 *
 * We've checked 1.107.0 version of Github from 2023-04-06.
 * If you won't be able to reproduce the issue without the fix, it can be removed.
 */
class GithubHelper @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private const val GITHUB_APP_PACKAGE_NAME = "com.github.android"

        fun xiaomiPermissionManagerForGithub(): Intent =
            Intent("miui.intent.action.APP_PERM_EDITOR")
                .setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity",
                )
                .putExtra("extra_pkgname", GITHUB_APP_PACKAGE_NAME)
    }

    fun shouldAskForXiaomiIntent(): Boolean = when {
        !Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true) -> false
        !isGithubAppInstalled() -> false
        else -> true
    }

    private fun isGithubAppInstalled(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    GITHUB_APP_PACKAGE_NAME,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_META_DATA.toLong()),
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    GITHUB_APP_PACKAGE_NAME,
                    PackageManager.GET_META_DATA,
                )
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
