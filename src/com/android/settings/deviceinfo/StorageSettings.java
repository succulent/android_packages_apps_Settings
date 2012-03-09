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

package com.android.settings.deviceinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentQueryMap;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

/**
 * Generic storage settings.
 */
public class StorageSettings extends SettingsPreferenceFragment {

    private static final String TAG = "StorageSettings";

    private static final String SWITCH_STORAGE_PREF = "pref_switch_storage";

    private static final String STORAGE_AUTOMOUNT_PREF = "pref_storage_automount";

    private CheckBoxPreference mSwitchStoragePref;

    private CheckBoxPreference mStorageAutomountPref;

    private ContentResolver mContentResolver;

    private PreferenceScreen createPreferenceHierarchy() {
        PreferenceScreen root = getPreferenceScreen();
        if (root != null) {
            root.removeAll();
        }
        addPreferencesFromResource(R.xml.storage_settings);
        root = getPreferenceScreen();

        mContentResolver = getActivity().getApplicationContext().getContentResolver();

        mSwitchStoragePref = (CheckBoxPreference) root.findPreference(SWITCH_STORAGE_PREF);
        mSwitchStoragePref.setChecked((SystemProperties.getInt("persist.sys.vold.switchexternal", 0) == 0));

        if (SystemProperties.get("ro.vold.switchablepair","").equals("")) {
            mSwitchStoragePref.setSummary(R.string.storage_switch_unavailable);
            mSwitchStoragePref.setEnabled(false);
        }

        mStorageAutomountPref = (CheckBoxPreference) root.findPreference(STORAGE_AUTOMOUNT_PREF);
        mStorageAutomountPref.setChecked((Settings.Secure.getInt(mContentResolver,
                Settings.Secure.MOUNT_UMS_AUTOSTART, 0) == 1));

        return root;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Make sure we reload the preference hierarchy since some of these settings
        // depend on others...
        createPreferenceHierarchy();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        // Don't allow any changes to take effect as the USB host will be disconnected, killing
        // the monkeys
        if (Utils.isMonkeyRunning()) {
            return true;
        }
        if (preference == mSwitchStoragePref) {
            SystemProperties.set("persist.sys.vold.switchexternal",
               mSwitchStoragePref.isChecked() ? "0" : "1");
        } else if (preference == mStorageAutomountPref) {
            boolean value = mStorageAutomountPref.isChecked();
            Settings.Secure.putInt(mContentResolver,
                    Settings.Secure.MOUNT_UMS_AUTOSTART, value ? 1 : 0);
            return true;
        }
        return true;
    }
}
