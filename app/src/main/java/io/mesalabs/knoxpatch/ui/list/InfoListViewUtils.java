/*
 * KnoxPatch
 * Copyright (C) 2022 BlackMesa123
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

package io.mesalabs.knoxpatch.ui.list;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SemBuild;
import android.os.SemSystemProperties;

import com.samsung.android.feature.SemFloatingFeature;
import com.samsung.android.knox.SemPersonaManager;
import com.samsung.android.knox.SemPersonaManager.KnoxContainerVersion;
import com.samsung.android.knox.ddar.DualDARPolicy;
import com.samsung.android.knox.hdm.HdmManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import io.mesalabs.knoxpatch.R;
import io.mesalabs.knoxpatch.utils.BuildUtils;
import io.mesalabs.knoxpatch.utils.Constants;

class InfoListViewUtils {
    private static final int ONE_UI_VERSION_SEP_VERSION_GAP = 90000;

    static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    static String getBuildNumber() {
        String buildDisplay = "";

        String buildId = SemSystemProperties.get("ro.build.id", "");
        if (!buildId.isEmpty()) {
            buildDisplay += buildId + ".";
        }
        buildDisplay += Build.VERSION.INCREMENTAL;

        return buildDisplay;
    }

    static boolean isKnoxAvailable() {
        final KnoxContainerVersion currentVersion = SemPersonaManager.getKnoxContainerVersion();
        return currentVersion.compareTo(KnoxContainerVersion.KNOX_CONTAINER_VERSION_2_2_0) >= 0;
    }

    @SuppressWarnings("deprecation")
    static String getKnoxComponentsVersion(@NonNull Context context) {
        String knoxVersion = "";

        // Knox version
        knoxVersion += context.getString(R.string.knox_version_knox) + " ";

        final String knoxVersionName = BuildUtils.getEnterpriseKnoxSdkVersion().getInternalVersion();
        int lastInt = Integer.parseInt(Character.toString(knoxVersionName.charAt(knoxVersionName.length() - 1)));
        if (lastInt > 0) {
            knoxVersion += knoxVersionName;
        } else {
            final int index = knoxVersionName.lastIndexOf('.');
            knoxVersion += knoxVersionName.substring(0, index);
        }

        // Knox API level
        knoxVersion += "\n" + context.getString(R.string.knox_version_knox_api) + " ";
        knoxVersion += BuildUtils.getKnoxAPIVersion();

        // TIMA version
        final String timaProp = SemSystemProperties.get("ro.config.tima", "");
        final boolean isTimaSupported = !timaProp.isEmpty() && timaProp.equals("1");

        if (isTimaSupported) {
            knoxVersion += "\n" + context.getString(R.string.knox_version_knox_tima) + " ";
            if (SemSystemProperties.get("ro.config.timaversion", "").equals("3.0")) {

                if (BuildUtils.getSEPVersion() >= Constants.ONEUI_1_1) {
                    knoxVersion += "4.1.0";
                } else {
                    if (SemPersonaManager.getKnoxContainerVersion()
                            .compareTo(KnoxContainerVersion.KNOX_CONTAINER_VERSION_2_7_0) >= 0) {
                        knoxVersion += "3.3.0";
                    } else {
                        knoxVersion += "3.2.0";
                    }
                }
            } else {
                knoxVersion += SemSystemProperties.get("ro.config.timaversion", "No Policy Version");
            }
        }

        // Knox ML version
        try {
            PackageInfo knoxMLApp = context.getPackageManager().getPackageInfo(
                    "com.samsung.android.app.kfa", PackageManager.GET_META_DATA);
            if (knoxMLApp != null) {
                knoxVersion += "\n" + context.getString(R.string.knox_version_knox_ml) + " ";

                String knoxMLVersion = knoxMLApp.versionName;
                knoxVersion += knoxMLVersion.substring(0, knoxMLVersion.length() - 3);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // no-op
        }

        // DualDAR version
        String dualDARVersion = DualDARPolicy.getDualDARVersion();
        if (dualDARVersion != null) {
            knoxVersion += "\n" + context.getString(R.string.knox_version_knox_dualdar) + " ";
            knoxVersion += dualDARVersion;
        }

        // HDM version
        if (Build.VERSION.SDK_INT >= 30) {
            String hdmVersion = HdmManager.getHdmVersion();
            if (hdmVersion != null) {
                knoxVersion += "\n" + context.getString(R.string.knox_version_knox_hdm) + " ";
                knoxVersion += hdmVersion;
            }
        }

        return knoxVersion;
    }

    @SuppressWarnings("unchecked")
    static String getKnoxFeatures() {
        List<String> features = new ArrayList<>();

        if (BuildUtils.getSEPVersion() >= Constants.ONEUI_5_0) {
            try {
                Class<?> cls = Class.forName("com.samsung.android.knox.dar.DarRune");
                if (cls != null) {
                    List<Field> fields = HiddenApiBypass.getStaticFields(cls);

                    // KNOX_SUPPORT_*
                    for (Field field : fields) {
                        if (field.getName().contains("KNOX_SUPPORT_")) {
                            features.add(field.getName() + " = " + field.get(null));
                        }
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Class<?> cls = Class.forName("com.samsung.android.rune.CoreRune");
            if (cls != null) {
                List<Field> fields = HiddenApiBypass.getStaticFields(cls);

                // KNOX_SUPPORT_*
                for (Field field : fields) {
                    if (field.getName().contains("KNOX_SUPPORT_")) {
                        features.add(field.getName() + " = " + field.get(null));
                    }
                }

                Collections.sort(features);

                // SUPPORT_KNOX
                for (Field field : fields) {
                    if (field.getName().contains("SUPPORT_KNOX")) {
                        features.add(0, field.getName() + " = " + field.get(null));
                    }
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return String.join("\n", features);
    }

    static boolean isSepLiteAvailable(@NonNull Context context) {
        final String sepCategoryFeature = SemFloatingFeature.getInstance()
                .getString("SEC_FLOATING_FEATURE_COMMON_CONFIG_SEP_CATEGORY");
        if (sepCategoryFeature.equals("sep_lite") || sepCategoryFeature.equals("sep_lite_new")) {
            return true;
        } else {
            return context.getPackageManager()
                    .hasSystemFeature("com.samsung.feature.samsung_experience_mobile_lite");
        }
    }

    static String getOneUIVersion() {
        final int oneUiOwnVersion = SemSystemProperties.getInt("ro.build.version.oneui", 0);

        if (oneUiOwnVersion > 0) {
            final int major = oneUiOwnVersion / 10000;
            final int minor = (oneUiOwnVersion % 10000) / 100;
            final int patch = oneUiOwnVersion % 100;

            if (patch == 0) {
                return major + "." + minor;
            } else {
                return major + "." + minor + "." + patch;
            }
        } else {
            try {
                final int sepVersion = SemBuild.VERSION.SEM_PLATFORM_INT - ONE_UI_VERSION_SEP_VERSION_GAP;
                return sepVersion / 10000 + "." + (sepVersion % 10000) / 100;
            } catch (NoSuchFieldError e) {
                return "Unknown";
            }
        }
    }
}
