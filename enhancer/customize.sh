#!/sbin/sh
#
# KnoxPatch Enhancer
# Copyright (C) 2023 BlackMesa123
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
SKIPUNZIP=1

enforce_sem() {
  ONEUI_VERSION=$(grep_prop 'ro.build.version.oneui')

  if [ -z "$ONEUI_VERSION" ]; then
    SEP_VERSION=$(grep_prop 'ro.build.version.sep')

    if [ -z "$SEP_VERSION" ]; then
      abort "E: This module only supports One UI devices"
    fi

    # The OneUI version is determined by the SEP version
    # (SEP 14.0 == OneUI 5.0 | [SEP - 9.0 = OneUI] )
    ONEUI_VERSION=$((SEP_VERSION - 90000))
  fi

  if [ ! "$ONEUI_VERSION" -ge "10000" ]; then
    abort "E: This module only supports One UI 1.x and above"
  fi

  SEP_LITE=false
  [ -f "/system/etc/floating_feature.xml" ] && FEATURE_PATH="/system/etc" || FEATURE_PATH="/vendor/etc"
  grep -q 'sep_lite' "$FEATURE_PATH/floating_feature.xml" && SEP_LITE=true
  [ -f "/system/etc/permissions/com.samsung.feature.samsung_experience_mobile_lite.xml" ] && SEP_LITE=true
  if $SEP_LITE; then
    LABEL="One UI Core"
  else
    LABEL="One UI"
  fi

  MAJOR=$((ONEUI_VERSION / 10000))
  MINOR=$((ONEUI_VERSION % 10000 / 100))
  PATCH=$((ONEUI_VERSION % 100))

  if [ $PATCH != "0" ]; then
    ui_print "I: $LABEL version: $MAJOR.$MINOR.$PATCH"
  else
    ui_print "I: $LABEL version: $MAJOR.$MINOR"
  fi
}

VERSION=$(grep_prop version "${TMPDIR}/module.prop")
ui_print "I: KnoxPatch Enhancer: ${VERSION}"

# Extract verify.sh
ui_print "- Extracting verify.sh"
unzip -o "$ZIPFILE" 'verify.sh' -d "$TMPDIR" >&2
if [ ! -f "$TMPDIR/verify.sh" ]; then
  ui_print "*********************************************************"
  ui_print "! Unable to extract verify.sh!"
  ui_print "! This zip may be corrupted, please try downloading again"
  abort    "*********************************************************"
fi
. "$TMPDIR/verify.sh"

extract "$ZIPFILE" 'customize.sh' "$TMPDIR"
extract "$ZIPFILE" 'verify.sh' "$TMPDIR"

if $BOOTMODE; then
  ui_print "- Installing from Magisk / KernelSU app"

  enforce_sem

  if pm list packages | grep -q knoxpatch; then
    KP_VERSION=$(dumpsys package io.mesalabs.knoxpatch | grep versionName | sed 's/versionName=//g' | sed 's/ //g')
    ui_print "I: KnoxPatch installed: v$KP_VERSION"
  else
    ui_print "W: KnoxPatch not installed!"
  fi

  ui_print "I: Extracting module files..."
  extract "$ZIPFILE" 'module.prop' "$MODPATH"
  extract "$ZIPFILE" 'system.prop' "$MODPATH"
  mkdir -p "$MODPATH/system/etc/permissions"
  extract "$ZIPFILE" 'system/etc/permissions/knoxpatch_enhancer.xml' "$MODPATH/system/etc/permissions" true

  ui_print "I: Applying WSM fix..."
  mkdir -p "$MODPATH/system/lib"
  touch "$MODPATH/system/lib/libhal.wsm.samsung.so"
  if $IS64BIT; then
    mkdir -p "$MODPATH/system/lib64"
    touch "$MODPATH/system/lib64/libhal.wsm.samsung.so"
  fi

  if [ -f "/vendor/lib/hw/camera.qcom.so" ]; then
    if grep -q 'ro.boot.flash.locked' /vendor/lib/hw/camera.qcom.so; then
      ui_print "I: Applying camera fix..."
      mkdir -p "$MODPATH/system/vendor/lib/hw"
      sed 's/ro.boot.flash.locked/ro.camera.notify_nfc/g' "/vendor/lib/hw/camera.qcom.so" > "$MODPATH/system/vendor/lib/hw/camera.qcom.so"
      [ -f "/vendor/lib/hw/com.qti.chi.override.so" ] && sed 's/ro.boot.flash.locked/ro.camera.notify_nfc/g' "/vendor/lib/hw/com.qti.chi.override.so" > "$MODPATH/system/vendor/lib/hw/com.qti.chi.override.so"
      if $IS64BIT; then
        mkdir -p "$MODPATH/system/vendor/lib64/hw"
        sed 's/ro.boot.flash.locked/ro.camera.notify_nfc/g' "/vendor/lib64/hw/camera.qcom.so" > "$MODPATH/system/vendor/lib64/hw/camera.qcom.so"
        [ -f "/vendor/lib64/hw/com.qti.chi.override.so" ] && sed 's/ro.boot.flash.locked/ro.camera.notify_nfc/g' "/vendor/lib64/hw/com.qti.chi.override.so" > "$MODPATH/system/vendor/lib64/hw/com.qti.chi.override.so"
      fi
    fi
  fi

  ui_print "I: Fixing file permissions..."
  set_perm_recursive "$MODPATH" 0 0 0755 0644
  set_perm_recursive "$MODPATH/system/lib" 0 0 0755 0644 "u:object_r:system_lib_file:s0"
  $IS64BIT && set_perm_recursive "$MODPATH/system/lib64" 0 0 0755 0644 "u:object_r:system_lib_file:s0"
  if [ -e "$MODPATH/system/vendor" ]; then
    set_perm_recursive "$MODPATH/system/vendor" 0 2000 0755 0644 "u:object_r:vendor_file:s0"
    set_perm "$MODPATH/system/vendor/lib/hw" 0 2000 0755 "u:object_r:vendor_hal_file:s0"
    set_perm "$MODPATH/system/vendor/lib/hw/camera.qcom.so" 0 0 0644 "u:object_r:vendor_file:s0"
    [ -f "$MODPATH/system/vendor/lib/hw/com.qti.chi.override.so" ] && set_perm "$MODPATH/system/vendor/lib/hw/com.qti.chi.override.so" 0 0 0644 "u:object_r:vendor_file:s0"
    if $IS64BIT; then
      set_perm "$MODPATH/system/vendor/lib64/hw" 0 2000 0755 "u:object_r:vendor_hal_file:s0"
      set_perm "$MODPATH/system/vendor/lib64/hw/camera.qcom.so" 0 0 0644 "u:object_r:vendor_file:s0"
      [ -f "$MODPATH/system/vendor/lib64/hw/com.qti.chi.override.so" ] && set_perm "$MODPATH/system/vendor/lib64/hw/com.qti.chi.override.so" 0 0 0644 "u:object_r:vendor_file:s0"
    fi
  fi
else
  ui_print "- Installing from recovery"

  enforce_sem

  mount -o remount,rw /system || abort "E: Could not mount system as rw"

  if [ -f "/system/bin/vold.bak" ]; then
    ui_print "I: Restoring backup files..."
    mv -f "/system/bin/vold.bak" "/system/bin/vold"
    [ -f "/system/lib/libepm.so.bak" ] && mv -f "/system/lib/libepm.so.bak" "/system/lib/libepm.so"
    [ -f "/system/lib64/libepm.so.bak" ] && mv -f "/system/lib64/libepm.so.bak" "/system/lib64/libepm.so"
  elif [ "$API" == "29" ] || [ "$API" == "30" ]; then
    if grep -q 'fileencryption' /vendor/etc/fstab.*; then
      if grep -q 'Knox protection required' /system/bin/vold; then
        ui_print "I: Applying Secure Folder fix..."
        cp --preserve=all "/system/bin/vold" "/system/bin/vold.bak"
        PATCHED=false
        $PATCHED || hex_patch "/system/bin/vold" 00e4006fea861a11 00e4006feabe0451 && PATCHED=true
        $PATCHED || hex_patch "/system/bin/vold" 08fa805200e4006f 0800805200e4006f && PATCHED=true
        $PATCHED || hex_patch "/system/bin/vold" 08fa80520800ae72 080080520800ae72 && PATCHED=true
        $PATCHED || hex_patch "/system/bin/vold" 09fa80520900ae72 090080520900ae72 && PATCHED=true
        $PATCHED || abort "E: Failed to apply patch"
        set_perm "/system/bin/vold" 0 2000 0755 "u:object_r:vold_exec:s0"
      else
        ui_print "W: No patches required for this device"
      fi
    elif [ "$API" == "29" ] && [ "$ARCH" == "arm64" ]; then
      if grep -q 'Device supports FBE!' /system/lib/libepm.so; then
        ui_print "I: Applying Secure Folder fix..."
        mv "/system/bin/vold" "/system/bin/vold.bak"
        mv "/system/lib/libepm.so" "/system/lib/libepm.so.bak"
        mv "/system/lib64/libepm.so" "/system/lib64/libepm.so.bak"
        extract "$ZIPFILE" 'system/bin/vold' "/system/bin" true
        extract "$ZIPFILE" 'system/lib/libepm.so' "/system/lib" true
        extract "$ZIPFILE" 'system/lib64/libepm.so' "/system/lib64" true
        set_perm "/system/bin/vold" 0 2000 0755 "u:object_r:vold_exec:s0"
        set_perm "/system/lib/libepm.so" 0 0 0644
        set_perm "/system/lib64/libepm.so" 0 0 0644
      else
        ui_print "W: No patches required for this device"
      fi
    fi
  else
    ui_print "W: Nothing to do here :/"
  fi

  mount -o remount,ro /system
fi
