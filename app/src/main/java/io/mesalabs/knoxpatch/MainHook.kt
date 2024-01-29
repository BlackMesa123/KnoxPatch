/*
 * KnoxPatch
 * Copyright (C) 2023 BlackMesa123
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.mesalabs.knoxpatch

import com.highcapable.yukihookapi.YukiHookAPI.encase
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

import io.mesalabs.knoxpatch.hooks.KnoxSDKHooks
import io.mesalabs.knoxpatch.hooks.PropSpoofHooks
import io.mesalabs.knoxpatch.hooks.RootDetectionHooks
import io.mesalabs.knoxpatch.hooks.SamsungKeystoreHooks
import io.mesalabs.knoxpatch.hooks.SystemHooks
import io.mesalabs.knoxpatch.utils.BuildUtils
import io.mesalabs.knoxpatch.utils.Constants

@InjectYukiHookWithXposed(isUsingResourcesHook = false)
object MainHook : IYukiHookXposedInit {
    private const val TAG: String = "MainHook"

    override fun onInit() = configs {
        debugLog {
            tag = "KnoxPatch"
            isEnable = true
            elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
        }
        isDebug = BuildConfig.DEBUG
    }

    override fun onHook() = encase {
        // The OneUI version is determined by the SEP version
        // (SEP 14.0 == OneUI 5.0 | [SEP - 9.0 = OneUI] )
        val sepVersion: Int = BuildUtils.getSEPVersion()

        /*
         * Currently supported versions:
         * - Android 9 (One UI 1.x)
         * - Android 10 (One UI 2.x)
         * - Android 11 (One UI 3.x)
         * - Android 12/12.1 (One UI 4.x)
         * - Android 13 (One UI 5.x)
         * - Android 14 (One UI 6.x)
         */
        when {
            sepVersion == -1 -> {
                YLog.error(msg = "$TAG: onHook: This module only supports One UI running devices.")
                return@encase
            }
            sepVersion < Constants.ONEUI_1_0 -> {
                YLog.error(msg = "$TAG: onHook: unknown SEP version: $sepVersion")
                return@encase
            }
            sepVersion > Constants.ONEUI_6_1 -> {
                val oneUiVersion: String = BuildUtils.getFormattedOneUIVersion()
                YLog.error(msg = "$TAG: onHook: One UI $oneUiVersion is not yet supported.")
                return@encase
            }
        }

        loadSystem(SystemHooks)

        loadApp(Constants.AUTO_BLOCKER_PACKAGE_NAME, PropSpoofHooks)
        loadApp(Constants.SAMSUNG_CLOUD_PLATFORM_MANAGER_PACKAGE_NAME, PropSpoofHooks)
        loadApp(Constants.SECURE_FOLDER_PACKAGE_NAME, PropSpoofHooks)
        loadApp(Constants.SECURE_WIFI_PACKAGE_NAME, PropSpoofHooks)

        if (sepVersion >= Constants.ONEUI_1_5) {
            loadApp(Constants.ACTIVATION_PACKAGE_NAME, SamsungKeystoreHooks)
            loadApp(Constants.FIND_MY_MOBILE_PACKAGE_NAME, SamsungKeystoreHooks)
            loadApp(Constants.SAMSUNG_ACCOUNT_PACKAGE_NAME, SamsungKeystoreHooks)
            loadApp(Constants.SAMSUNG_CLOUD_PACKAGE_NAME, SamsungKeystoreHooks)
            loadApp(Constants.SAMSUNG_CLOUD_PLATFORM_MANAGER_PACKAGE_NAME, SamsungKeystoreHooks)
            loadApp(Constants.SAMSUNG_WALLET_PACKAGE_NAME, SamsungKeystoreHooks)
            loadApp(Constants.SECURE_WIFI_PACKAGE_NAME, SamsungKeystoreHooks)
            if (sepVersion >= Constants.ONEUI_5_1_1) {
                loadApp(Constants.QUICK_SHARE_PACKAGE_NAME, SamsungKeystoreHooks)
            } else {
                loadApp(Constants.PRIVATE_SHARE_PACKAGE_NAME, SamsungKeystoreHooks)
            }
        }

        loadApp(Constants.SAMSUNG_HEALTH_PACKAGE_NAME, KnoxSDKHooks)

        loadApp(Constants.SAMSUNG_CHECKOUT_PACKAGE_NAME, RootDetectionHooks)
        loadApp(Constants.SAMSUNG_FLOW_PACKAGE_NAME, RootDetectionHooks)
        loadApp(Constants.SAMSUNG_HEALTH_MONITOR_PACKAGE_NAME, RootDetectionHooks)
    }

}
