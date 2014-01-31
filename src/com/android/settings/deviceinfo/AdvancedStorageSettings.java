/*
 * Copyright (C) 2013 The CyanogenMod Project
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
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.os.UserManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.io.File;

/**
 * Advanced storage settings.
 */
public class AdvancedStorageSettings extends SettingsPreferenceFragment {

    private static final String TAG = "AdvancedStorageSettings";

    private static final String KEY_SYS_SWAP = "as_sys_swap";
    private static final String SYSVOLD_SWITCH_PERSIST_PROP = "persist.sys.vold.switchexternal";
    private static final String VOLD_SWITCHABLEPAIR_PROP = "persist.sys.vold.switchablepair";
    private static boolean mSwitchablePairFound = false;
    private CheckBoxPreference mSysSwap;

    private PreferenceScreen createPreferenceHierarchy() {
        PreferenceScreen root = getPreferenceScreen();
        if (root != null) {
            root.removeAll();
        }
        addPreferencesFromResource(R.xml.advanced_storage_settings);
        root = getPreferenceScreen();

        mSysSwap = (CheckBoxPreference)root.findPreference(KEY_SYS_SWAP);
        updateToggles();

        return root;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mSwitchablePairFound = setSwitchablePair();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        createPreferenceHierarchy();
    }

    private boolean setSwitchablePair() {
        String[] primaryPath = System.getenv("EXTERNAL_STORAGE").split(File.separator);
        String[] secondaryPath = System.getenv("SECONDARY_STORAGE").split(File.separator);
        String primaryDir = primaryPath[primaryPath.length-1];
        String secondaryDir = secondaryPath[secondaryPath.length-1];

        if (TextUtils.isEmpty(primaryDir)) {
            Log.e(TAG,"Unable to find primary storage device for vold swap");
            return false;
        }
        if (Environment.isExternalStorageEmulated()) {
            Log.e(TAG,"Vold swap does not support emulated primary storage");
            return false;
        }
        if (!TextUtils.equals(secondaryDir, "sdcard1")) {
            Log.e(TAG,"Unable to find secondary storage device sdcard1 for vold swap");
            return false;
        }

        SystemProperties.set(VOLD_SWITCHABLEPAIR_PROP, primaryDir + ','
            + secondaryDir);
        Log.i(TAG, "System property set: " + VOLD_SWITCHABLEPAIR_PROP + "="
            + primaryDir + ',' + secondaryDir);
        return true;
    }

    private void updateToggles() {
        if (mSwitchablePairFound) {
            if(SystemProperties.get(SYSVOLD_SWITCH_PERSIST_PROP).equals("1")) {
                mSysSwap.setChecked(true);
            } else {
                mSysSwap.setChecked(false);
            }
            mSysSwap.setEnabled(true);
        } else {
            mSysSwap.setChecked(false);
            mSysSwap.setSummary(R.string.as_swap_unavailable);
            mSysSwap.setEnabled(false);
        }
    }

    private void showRebootPrompt() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.as_swap_reboot_prompt_title)
                .setMessage(R.string.as_swap_reboot_prompt_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        pm.reboot(null);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create();

        dialog.show();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        // Don't allow any changes to take effect as the USB host will be disconnected, killing
        // the monkeys
        if (Utils.isMonkeyRunning()) {
            return true;
        }

        if (preference == mSysSwap) {
            SystemProperties.set(SYSVOLD_SWITCH_PERSIST_PROP,
                mSysSwap.isChecked() ? "1" : "0");
            Log.i(TAG, "System property set: " + SYSVOLD_SWITCH_PERSIST_PROP
                  + "=" + SystemProperties.get(SYSVOLD_SWITCH_PERSIST_PROP));
        }
        updateToggles();
        showRebootPrompt();
        return true;
    }
}
