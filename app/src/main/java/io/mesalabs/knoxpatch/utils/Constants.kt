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

package io.mesalabs.knoxpatch.utils

object Constants {

    const val ONEUI_1_0: Int = 100000
    const val ONEUI_1_1: Int = 100100
    const val ONEUI_1_5: Int = 100500
    const val ONEUI_2_0: Int = 110000
    const val ONEUI_2_1: Int = 110100
    const val ONEUI_2_5: Int = 110500
    const val ONEUI_3_0: Int = 120000
    const val ONEUI_3_1: Int = 120100
    const val ONEUI_3_1_1: Int = 120500
    const val ONEUI_4_0: Int = 130000
    const val ONEUI_4_1: Int = 130100
    const val ONEUI_4_1_1: Int = 130500
    const val ONEUI_5_0: Int = 140000
    const val ONEUI_5_1: Int = 140100
    const val ONEUI_5_1_1: Int = 140500
    const val ONEUI_6_0: Int = 150000
    const val ONEUI_6_1: Int = 150100

    enum class EnterpriseKnoxSdkVersion(val internalVersion: String) {
        KNOX_ENTERPRISE_SDK_VERSION_NONE("N/A"),
        KNOX_ENTERPRISE_SDK_VERSION_1_0 ("1.0.0"),
        KNOX_ENTERPRISE_SDK_VERSION_1_0_1("1.0.1"),
        KNOX_ENTERPRISE_SDK_VERSION_1_0_2("1.0.2"),
        KNOX_ENTERPRISE_SDK_VERSION_1_1_0("1.1.0"),
        KNOX_ENTERPRISE_SDK_VERSION_1_2_0("1.2.0"),
        KNOX_ENTERPRISE_SDK_VERSION_2_0("2.0.0"),
        KNOX_ENTERPRISE_SDK_VERSION_2_1("2.1.0"),
        KNOX_ENTERPRISE_SDK_VERSION_2_2("2.2.0"),
        KNOX_ENTERPRISE_SDK_VERSION_2_3("2.3.0"),
        KNOX_ENTERPRISE_SDK_VERSION_2_4("2.4.0"),
        KNOX_ENTERPRISE_SDK_VERSION_2_4_1("2.4.1"),
        KNOX_ENTERPRISE_SDK_VERSION_2_5("2.5.0"),
        KNOX_ENTERPRISE_SDK_VERSION_2_5_1("2.5.1"),
        KNOX_ENTERPRISE_SDK_VERSION_2_6("2.6.0"),
        KNOX_ENTERPRISE_SDK_VERSION_2_7("2.7.0"),
        KNOX_ENTERPRISE_SDK_VERSION_2_7_1("2.7.1"),
        KNOX_ENTERPRISE_SDK_VERSION_2_8("2.8.0"),
        KNOX_ENTERPRISE_SDK_VERSION_2_9("2.9.0"),
        KNOX_ENTERPRISE_SDK_VERSION_3_0("3.0.0"),
        KNOX_ENTERPRISE_SDK_VERSION_3_1("3.1.0"),
        KNOX_ENTERPRISE_SDK_VERSION_3_2("3.2.0"),
        KNOX_ENTERPRISE_SDK_VERSION_3_2_1("3.2.1"),
        KNOX_ENTERPRISE_SDK_VERSION_3_3("3.3.0"),
        KNOX_ENTERPRISE_SDK_VERSION_3_4("3.4.0"),
        KNOX_ENTERPRISE_SDK_VERSION_3_4_1("3.4.1"),
        KNOX_ENTERPRISE_SDK_VERSION_3_5("3.5"),
        KNOX_ENTERPRISE_SDK_VERSION_3_6("3.6"),
        KNOX_ENTERPRISE_SDK_VERSION_3_7("3.7"),
        KNOX_ENTERPRISE_SDK_VERSION_3_7_1("3.7.1"),
        KNOX_ENTERPRISE_SDK_VERSION_3_8("3.8"),
        KNOX_ENTERPRISE_SDK_VERSION_3_9("3.9"),
        KNOX_ENTERPRISE_SDK_VERSION_3_10("3.10")
    }

    const val ENHANCER_SYSTEM_FEATURE: String = "io.mesalabs.knoxpatch_enhancer"

    const val ACTIVATION_PACKAGE_NAME: String = "com.samsung.android.activation"
    const val AUTO_BLOCKER_PACKAGE_NAME: String = "com.samsung.android.rampart"
    const val FIND_MY_MOBILE_PACKAGE_NAME: String = "com.samsung.android.fmm"
    const val PRIVATE_SHARE_PACKAGE_NAME: String = "com.samsung.android.privateshare"
    const val QUICK_SHARE_PACKAGE_NAME: String = "com.samsung.android.app.sharelive"
    const val SAMSUNG_ACCOUNT_PACKAGE_NAME: String = "com.osp.app.signin"
    const val SAMSUNG_CHECKOUT_PACKAGE_NAME: String = "com.sec.android.app.billing"
    const val SAMSUNG_CLOUD_PACKAGE_NAME: String = "com.samsung.android.scloud"
    const val SAMSUNG_CLOUD_PLATFORM_MANAGER_PACKAGE_NAME: String = "com.samsung.android.scpm"
    const val SAMSUNG_FLOW_PACKAGE_NAME: String = "com.samsung.android.galaxycontinuity"
    const val SAMSUNG_HEALTH_PACKAGE_NAME: String = "com.sec.android.app.shealth"
    const val SAMSUNG_HEALTH_MONITOR_PACKAGE_NAME: String = "com.samsung.android.shealthmonitor"
    const val SAMSUNG_WALLET_PACKAGE_NAME: String = "com.samsung.android.spay"
    const val SECURE_FOLDER_PACKAGE_NAME: String = "com.samsung.knox.securefolder"
    const val SECURE_WIFI_PACKAGE_NAME: String = "com.samsung.android.fast"

}
