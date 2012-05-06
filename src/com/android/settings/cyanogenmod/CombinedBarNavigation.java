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

public class CombinedBarNavigation extends SettingsPreferenceFragment {

    private static final String COMBINED_BAR_NAVIGATION_BACK = "combined_bar_navigation_back";
    private static final String COMBINED_BAR_NAVIGATION_HOME = "combined_bar_navigation_home";
    private static final String COMBINED_BAR_NAVIGATION_RECENT = "combined_bar_navigation_recent";
    private static final String COMBINED_BAR_NAVIGATION_MENU = "combined_bar_navigation_menu";
    private static final String COMBINED_BAR_NAVIGATION_COLOR = "combined_bar_navigation_color";
    private static final String COMBINED_BAR_NAVIGATION_FORCE_MENU =
            "combined_bar_navigation_force_menu";
    private static final String BACK = "back";
    private static final String HOME = "home";
    private static final String RECENT = "recent";
    private static final String MENU = "menu";
    private static final String SPLIT = "|";
    private static final String DEFAULT_BUTTONS =
            BACK + SPLIT + HOME + SPLIT + RECENT + SPLIT + MENU + SPLIT;

    private CheckBoxPreference mCombinedBarNavigationBack;
    private CheckBoxPreference mCombinedBarNavigationHome;
    private CheckBoxPreference mCombinedBarNavigationRecent;
    private CheckBoxPreference mCombinedBarNavigationMenu;
    private CheckBoxPreference mCombinedBarNavigationForceMenu;
    private Preference mCombinedBarNavigationColor;

    private ContentResolver mContentResolver;

    private String mNavButtons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContentResolver = getActivity().getApplicationContext().getContentResolver();

        addPreferencesFromResource(R.xml.combined_bar_navigation);

        PreferenceScreen prefSet = getPreferenceScreen();

        mCombinedBarNavigationBack =
                (CheckBoxPreference) prefSet.findPreference(COMBINED_BAR_NAVIGATION_BACK);
        mCombinedBarNavigationHome =
                (CheckBoxPreference) prefSet.findPreference(COMBINED_BAR_NAVIGATION_HOME);
        mCombinedBarNavigationRecent =
                (CheckBoxPreference) prefSet.findPreference(COMBINED_BAR_NAVIGATION_RECENT);
        mCombinedBarNavigationMenu =
                (CheckBoxPreference) prefSet.findPreference(COMBINED_BAR_NAVIGATION_MENU);
        mCombinedBarNavigationForceMenu =
                (CheckBoxPreference) prefSet.findPreference(COMBINED_BAR_NAVIGATION_FORCE_MENU);
        mCombinedBarNavigationColor =
                (Preference) prefSet.findPreference(COMBINED_BAR_NAVIGATION_COLOR);


        mNavButtons = getNavButtons();

        if (mNavButtons == null) {
            mNavButtons = DEFAULT_BUTTONS;
        }

        mCombinedBarNavigationBack.setChecked(mNavButtons.contains(BACK));
        mCombinedBarNavigationHome.setChecked(mNavButtons.contains(HOME));
        mCombinedBarNavigationRecent.setChecked(mNavButtons.contains(RECENT));
        mCombinedBarNavigationMenu.setChecked(mNavButtons.contains(MENU));
        mCombinedBarNavigationForceMenu.setChecked((Settings.System.getInt(mContentResolver,
                Settings.System.FORCE_SOFT_MENU_BUTTON, 0) == 1));
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mCombinedBarNavigationBack) {
            value = mCombinedBarNavigationBack.isChecked();
            setNavButtons(BACK, value);
            return true;
        } else if (preference == mCombinedBarNavigationHome) {
            value = mCombinedBarNavigationHome.isChecked();
            setNavButtons(HOME, value);
            return true;
        } else if (preference == mCombinedBarNavigationRecent) {
            value = mCombinedBarNavigationRecent.isChecked();
            setNavButtons(RECENT, value);
            return true;
        } else if (preference == mCombinedBarNavigationMenu) {
            value = mCombinedBarNavigationMenu.isChecked();
            setNavButtons(MENU, value);
            return true;
        } else if (preference == mCombinedBarNavigationForceMenu) {
            value = mCombinedBarNavigationForceMenu.isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.FORCE_SOFT_MENU_BUTTON, value ? 1 : 0);
            return true;
        } else if (preference == mCombinedBarNavigationColor) {
            ColorPickerDialog cp = new ColorPickerDialog(getActivity(),
                    mColorListener, Settings.System.getInt(getActivity().getApplicationContext()
                    .getContentResolver(), Settings.System.COMBINED_BAR_NAVIGATION_COLOR, 0x00000000));
            cp.setDefaultColor(0x00000000);
            cp.show();
            return true;
        }
        return false;
    }

    private String getNavButtons() {
        return Settings.System.getString(getActivity().getApplicationContext()
                .getContentResolver(), Settings.System.COMBINED_BAR_NAVIGATION);
    }

    private void setNavButtons(String button, boolean newButton) {
        if (newButton && !mNavButtons.contains(button)) {
            mNavButtons = mNavButtons + button + SPLIT;
        } else if (mNavButtons.contains(button)) {
            mNavButtons = mNavButtons.replace(button + SPLIT, "");
        }

        Settings.System.putString(getActivity().getApplicationContext()
                .getContentResolver(), Settings.System.COMBINED_BAR_NAVIGATION, mNavButtons);
    }

    ColorPickerDialog.OnColorChangedListener mColorListener =
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.COMBINED_BAR_NAVIGATION_COLOR, color);
            }
            public void colorUpdate(int color) {
            }
    };
}
