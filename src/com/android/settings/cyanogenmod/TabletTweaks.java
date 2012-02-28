/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.io.DataOutputStream;
import java.io.IOException;

public class TabletTweaks extends SettingsPreferenceFragment {

    private static final String TABLET_TWEAKS_HIDE_HOME = "tablet_tweaks_hide_home";

    private static final String TABLET_TWEAKS_HIDE_RECENT = "tablet_tweaks_hide_recent";

    private static final String TABLET_TWEAKS_HIDE_BACK = "tablet_tweaks_hide_back";

    private static final String TABLET_TWEAKS_HIDE_MENU = "tablet_tweaks_hide_menu";

    public static final String TABLET_TWEAKS_DISABLE_HARDWARE_BUTTONS =
            "tablet_tweaks_disable_hardware_buttons";

    private static final String TABLET_TWEAKS_RECENT_THUMBNAILS = "tablet_tweaks_recent_thumbnails";

    private static final String TABLET_TWEAKS_STORAGE_SWITCH = "tablet_tweaks_storage_switch";

    private static final String TABLET_TWEAKS_STORAGE_AUTOMOUNT = "tablet_tweaks_storage_automount";

    private static final String TABLET_TWEAKS_RIGHT_BUTTONS = "tablet_tweaks_right_buttons";

    private static final String TABLET_TWEAKS_PEEK_NOTIFICATIONS = "tablet_tweaks_peek_notifications";

    private static final String TABLET_TWEAKS_SCREENSHOTS_JPEG = "tablet_tweaks_screenshots_jpeg";

    private static final String TABLET_TWEAKS_HIDE_STATUSBAR = "tablet_tweaks_hide_statusbar";

    public static final String BUTTONS_ENABLED_COMMAND = "echo ";

    public static final String BUTTONS_ENABLED_PATH =
            " > /sys/devices/platform/s3c2440-i2c.2/i2c-2/2-004a/buttons_enabled";

    public static final String BUTTONS_ENABLED_SHELL = "/system/bin/sh";

    private CheckBoxPreference mTabletTweaksHideHome;

    private CheckBoxPreference mTabletTweaksHideRecent;

    private CheckBoxPreference mTabletTweaksHideBack;

    private CheckBoxPreference mTabletTweaksHideMenu;

    private CheckBoxPreference mTabletTweaksDisableHardwareButtons;

    private CheckBoxPreference mTabletTweaksRecentThumbnails;

    private CheckBoxPreference mTabletTweaksStorageSwitch;

    private CheckBoxPreference mTabletTweaksStorageAutomount;

    private CheckBoxPreference mTabletTweaksRightButtons;

    private CheckBoxPreference mTabletTweaksPeekNotifications;

    private CheckBoxPreference mTabletTweaksScreenshotsJpeg;

    private CheckBoxPreference mTabletTweaksHideStatusbar;

    private ContentResolver mContentResolver;

    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        addPreferencesFromResource(R.xml.tablet_tweaks);

        PreferenceScreen prefSet = getPreferenceScreen();

        mContentResolver = getActivity().getApplicationContext().getContentResolver();

        mTabletTweaksHideHome =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_HIDE_HOME);
        mTabletTweaksHideRecent =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_HIDE_RECENT);
        mTabletTweaksHideBack =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_HIDE_BACK);
        mTabletTweaksHideMenu =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_HIDE_MENU);
        mTabletTweaksDisableHardwareButtons =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_DISABLE_HARDWARE_BUTTONS);
        mTabletTweaksRecentThumbnails =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_RECENT_THUMBNAILS);
        mTabletTweaksStorageSwitch =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_STORAGE_SWITCH);
        mTabletTweaksStorageAutomount =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_STORAGE_AUTOMOUNT);
        mTabletTweaksRightButtons =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_RIGHT_BUTTONS);
        mTabletTweaksPeekNotifications =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_PEEK_NOTIFICATIONS);
        mTabletTweaksScreenshotsJpeg =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_SCREENSHOTS_JPEG);
        mTabletTweaksHideStatusbar =
                (CheckBoxPreference) prefSet.findPreference(TABLET_TWEAKS_HIDE_STATUSBAR);

        mTabletTweaksHideHome.setChecked((Settings.System.getInt(mContentResolver,
                Settings.System.HIDE_SOFT_HOME_BUTTON, 0) == 1));
        mTabletTweaksHideRecent.setChecked((Settings.System.getInt(mContentResolver,
                Settings.System.HIDE_SOFT_RECENT_BUTTON, 0) == 1));
        mTabletTweaksHideBack.setChecked((Settings.System.getInt(mContentResolver,
                Settings.System.HIDE_SOFT_BACK_BUTTON, 0) == 1));
        mTabletTweaksHideMenu.setChecked((Settings.System.getInt(mContentResolver,
                Settings.System.HIDE_SOFT_MENU_BUTTON, 0) == 1));
        mTabletTweaksRecentThumbnails.setChecked((Settings.System.getInt(mContentResolver,
                Settings.System.LARGE_RECENT_THUMBNAILS, 0) == 1));
        mTabletTweaksStorageSwitch.setChecked(
                SystemProperties.getInt("persist.sys.vold.switchexternal", 0) == 1);
        mTabletTweaksStorageAutomount.setChecked((Settings.Secure.getInt(mContentResolver,
                Settings.Secure.MOUNT_UMS_AUTOSTART, 0) == 1));
        mTabletTweaksRightButtons.setChecked((Settings.System.getInt(mContentResolver,
                Settings.System.RIGHT_SOFT_BUTTONS, 0) == 1));
        mTabletTweaksPeekNotifications.setChecked((Settings.System.getInt(mContentResolver,
                Settings.System.SHOW_NOTIFICATION_PEEK, 0) == 1));
        mTabletTweaksScreenshotsJpeg.setChecked((Settings.System.getInt(mContentResolver,
                Settings.System.JPEG_SCREENSHOTS, 0) == 1));
        mTabletTweaksHideStatusbar.setChecked((Settings.System.getInt(mContentResolver,
                Settings.System.HIDE_STATUSBAR, 0) == 1));
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mTabletTweaksHideHome) {
            value = mTabletTweaksHideHome.isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.HIDE_SOFT_HOME_BUTTON, value ? 1 : 0);
            return true;
        } else if (preference == mTabletTweaksHideRecent) {
            value = mTabletTweaksHideRecent.isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.HIDE_SOFT_RECENT_BUTTON, value ? 1 : 0);
            return true;
        } else if (preference == mTabletTweaksHideBack) {
            value = mTabletTweaksHideBack.isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.HIDE_SOFT_BACK_BUTTON, value ? 1 : 0);
            return true;
        } else if (preference == mTabletTweaksHideMenu) {
            value = mTabletTweaksHideMenu.isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.HIDE_SOFT_MENU_BUTTON, value ? 1 : 0);
            return true;
        } else if (preference == mTabletTweaksDisableHardwareButtons) {
            value = mTabletTweaksDisableHardwareButtons.isChecked();
            try {
                String[] cmds = {BUTTONS_ENABLED_SHELL, "-c",
                        BUTTONS_ENABLED_COMMAND + (value ? "0" : "1") + BUTTONS_ENABLED_PATH};
                Runtime.getRuntime().exec(cmds);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else if (preference == mTabletTweaksRecentThumbnails) {
            value = mTabletTweaksRecentThumbnails.isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.LARGE_RECENT_THUMBNAILS, value ? 1 : 0);
            return true;
        } else if (preference == mTabletTweaksStorageSwitch) {
            value = mTabletTweaksStorageSwitch.isChecked();
            SystemProperties.set("persist.sys.vold.switchexternal", value ? "1" : "0");
            return true;
        } else if (preference == mTabletTweaksStorageAutomount) {
            value = mTabletTweaksStorageAutomount.isChecked();
            Settings.Secure.putInt(mContentResolver,
                    Settings.Secure.MOUNT_UMS_AUTOSTART, value ? 1 : 0);
            return true;
        } else if (preference == mTabletTweaksRightButtons) {
            value = mTabletTweaksRightButtons.isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.RIGHT_SOFT_BUTTONS, value ? 1 : 0);
            return true;
        } else if (preference == mTabletTweaksPeekNotifications) {
            value = mTabletTweaksPeekNotifications.isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.SHOW_NOTIFICATION_PEEK, value ? 1 : 0);
            return true;
        } else if (preference == mTabletTweaksScreenshotsJpeg) {
            value = mTabletTweaksScreenshotsJpeg.isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.JPEG_SCREENSHOTS, value ? 1 : 0);
            return true;
        } else if (preference == mTabletTweaksHideStatusbar) {
            value = mTabletTweaksHideStatusbar.isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.HIDE_STATUSBAR, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
