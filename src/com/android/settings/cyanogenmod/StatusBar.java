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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String STATUS_BAR_AM_PM = "status_bar_am_pm";

    private static final String STATUS_BAR_BATTERY = "status_bar_battery";

    private static final String STATUS_BAR_CLOCK = "status_bar_show_clock";

    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";

    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";

    private static final String COMBINED_BAR_AUTO_HIDE = "combined_bar_auto_hide";

    private static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";

    private static final String STATUS_BAR_CATEGORY_GENERAL = "status_bar_general";

    private static final String KEY_TABLET_MODE = "tablet_mode";
    private static final String KEY_TABLET_FLIPPED = "tablet_flipped";
    private static final String KEY_NAVIGATION_CONTROLS = "navigation_controls";
    private static final String COMBINED_BAR_NAVIGATION_FORCE_MENU =
            "combined_bar_navigation_force_menu";
    private static final String STATUS_BAR_CLOCK_COLOR = "status_bar_clock_color";

    private ListPreference mStatusBarAmPm;

    private ListPreference mStatusBarBattery;

    private ListPreference mStatusBarCmSignal;

    private CheckBoxPreference mStatusBarClock;

    private CheckBoxPreference mStatusBarBrightnessControl;

    private CheckBoxPreference mCombinedBarAutoHide;

    private CheckBoxPreference mStatusBarNotifCount;

    private PreferenceCategory mPrefCategoryGeneral;

    private CheckBoxPreference mTabletMode;
    private CheckBoxPreference mTabletFlipped;
    private CheckBoxPreference mNavigationControls;
    private CheckBoxPreference mCombinedBarNavigationForceMenu;
    private Preference mStatusBarClockColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarClock = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_CLOCK);
        mStatusBarBrightnessControl = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarAmPm = (ListPreference) prefSet.findPreference(STATUS_BAR_AM_PM);
        mStatusBarBattery = (ListPreference) prefSet.findPreference(STATUS_BAR_BATTERY);
        mCombinedBarAutoHide = (CheckBoxPreference) prefSet.findPreference(COMBINED_BAR_AUTO_HIDE);
        mStatusBarCmSignal = (ListPreference) prefSet.findPreference(STATUS_BAR_SIGNAL);
        mCombinedBarNavigationForceMenu =
                (CheckBoxPreference) prefSet.findPreference(COMBINED_BAR_NAVIGATION_FORCE_MENU);
        mStatusBarClockColor = (Preference) prefSet.findPreference(STATUS_BAR_CLOCK_COLOR);

        mStatusBarClock.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, 1) == 1));
        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));

        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(), 
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.TIME_12_24) == 24) {
                mStatusBarAmPm.setEnabled(false);
                mStatusBarAmPm.setSummary(R.string.status_bar_am_pm_info);
            }
        } catch (SettingNotFoundException e ) {
        }

        int statusBarAmPm = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_AM_PM, 2);
        mStatusBarAmPm.setValue(String.valueOf(statusBarAmPm));
        mStatusBarAmPm.setOnPreferenceChangeListener(this);

        int statusBarBattery = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY, 0);
        mStatusBarBattery.setValue(String.valueOf(statusBarBattery));
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        int signalStyle = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        mCombinedBarAutoHide.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.COMBINED_BAR_AUTO_HIDE, 0) == 1));

        mStatusBarNotifCount = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_NOTIF_COUNT, 0) == 1));

        mTabletMode = (CheckBoxPreference) findPreference(KEY_TABLET_MODE);
        mTabletMode.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.TABLET_MODE, 0) == 1);

        mTabletFlipped = (CheckBoxPreference) findPreference(KEY_TABLET_FLIPPED);
        mTabletFlipped.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.TABLET_FLIPPED, 0) == 1);

        mNavigationControls = (CheckBoxPreference) findPreference(KEY_NAVIGATION_CONTROLS);
        mNavigationControls.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.NAVIGATION_CONTROLS, 1) == 1);

        mCombinedBarNavigationForceMenu.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.FORCE_SOFT_MENU_BUTTON, 0) == 1));

        if (getResources().getConfiguration().smallestScreenWidthDp != 600) {
            getPreferenceScreen().removePreference(mTabletMode);
        } else {
            mTabletFlipped.setEnabled(mTabletMode.isChecked());
        }
        if (getResources().getConfiguration().smallestScreenWidthDp < 600) {
            getPreferenceScreen().removePreference(mTabletFlipped);
        }

        mPrefCategoryGeneral = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_GENERAL);

        if (Utils.isScreenLarge()) {
            mPrefCategoryGeneral.removePreference(mStatusBarBrightnessControl);
            mPrefCategoryGeneral.removePreference(mStatusBarCmSignal);
        } else {
            mPrefCategoryGeneral.removePreference(mCombinedBarAutoHide);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarAmPm) {
            int statusBarAmPm = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_AM_PM, statusBarAmPm);
            return true;
        } else if (preference == mStatusBarBattery) {
            int statusBarBattery = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY, statusBarBattery);
            return true;
        } else if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mStatusBarClock) {
            value = mStatusBarClock.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarBrightnessControl) {
            value = mStatusBarBrightnessControl.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mCombinedBarAutoHide) {
            value = mCombinedBarAutoHide.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.COMBINED_BAR_AUTO_HIDE, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarNotifCount) {
            value = mStatusBarNotifCount.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_NOTIF_COUNT, value ? 1 : 0);
            return true;
        } else if (preference == mTabletMode) {
            value = mTabletMode.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.TABLET_MODE,
                    value ? 1 : 0);
            mTabletFlipped.setEnabled(value);
            return true;
        } else if (preference == mTabletFlipped) {
            value = mTabletFlipped.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.TABLET_FLIPPED,
                    value ? 1 : 0);
            return true;
        } else if (preference == mNavigationControls) {
            value = mNavigationControls.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_CONTROLS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mCombinedBarNavigationForceMenu) {
            value = mCombinedBarNavigationForceMenu.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.FORCE_SOFT_MENU_BUTTON, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarClockColor) {
            ColorPickerDialog cp = new ColorPickerDialog(getActivity(),
                    mColorListener, Settings.System.getInt(getActivity().getApplicationContext()
                    .getContentResolver(), Settings.System.STATUS_BAR_CLOCK_COLOR, 0xFF33B5E5));
            cp.setDefaultColor(0xFF33B5E5);
            cp.show();
            return true;
        }
        return false;
    }

    ColorPickerDialog.OnColorChangedListener mColorListener =
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.STATUS_BAR_CLOCK_COLOR, color);
            }
            public void colorUpdate(int color) {
            }
    };
}
