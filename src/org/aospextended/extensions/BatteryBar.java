/*
 * Copyright (C) 2016 AospExtended ROM Project
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


package org.aospextended.extensions;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import org.aospextended.extensions.preference.CustomSeekBarPreference;
import org.aospextended.extensions.R;

public class BatteryBar extends SettingsPreferenceFragment
            implements Preference.OnPreferenceChangeListener  {

    private static final String PREF_BATT_BAR = "statusbar_battery_bar_list";
    private static final String PREF_BATT_BAR_STYLE = "statusbar_battery_bar_style";
    private static final String PREF_BATT_BAR_COLOR = "statusbar_battery_bar_color";
    private static final String PREF_BATT_BAR_CHARGING_COLOR = "statusbar_battery_bar_charging_color";
    private static final String PREF_BATT_BAR_BATTERY_LOW_COLOR = "statusbar_battery_bar_battery_low_color";
    private static final String PREF_BATT_BAR_WIDTH = "statusbar_battery_bar_thickness";
    private static final String PREF_BATT_ANIMATE = "statusbar_battery_bar_animate";
    private static final String PREF_BATT_USE_CHARGING_COLOR = "statusbar_battery_bar_enable_charging_color";
    private static final String PREF_BATT_BLEND_COLOR = "statusbar_battery_bar_blend_color";
    private static final String PREF_BATT_BLEND_COLOR_REVERSE = "statusbar_battery_bar_blend_color_reverse";
    private static final String PREF_BATT_SHOW_ON_LOCK_SCREEN = "statusbar_battery_bar_show_on_lock_screen";

    private ListPreference mBatteryBar;    
    private ListPreference mBatteryBarStyle;
    private CustomSeekBarPreference mBatteryBarThickness;
    private SwitchPreference mBatteryBarChargingAnimation;
    private SwitchPreference mBatteryBarUseChargingColor;
    private SwitchPreference mBatteryBarBlendColor;
    private SwitchPreference mBatteryBarBlendColorReverse;
    private SwitchPreference mBatteryBarShowOnLockScreen;
    private ColorPickerPreference mBatteryBarColor;
    private ColorPickerPreference mBatteryBarChargingColor;
    private ColorPickerPreference mBatteryBarBatteryLowColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.battery_bar);

        ContentResolver resolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        int barVal = Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR, 0);
        mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
        mBatteryBar.setValue(String.valueOf(barVal));
        mBatteryBar.setSummary(mBatteryBar.getEntry());
        mBatteryBar.setOnPreferenceChangeListener(this);

        int barStyle = Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0);
        mBatteryBarStyle = (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
        mBatteryBarStyle.setValue(String.valueOf(barStyle));
        mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntry());
        mBatteryBarStyle.setOnPreferenceChangeListener(this);

        int battcolor = Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_COLOR, Color.WHITE);
        mBatteryBarColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);        
        mBatteryBarColor.setNewPreviewColor(battcolor);
        mBatteryBarColor.setOnPreferenceChangeListener(this);

        int battchcolor = Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, Color.WHITE);
        mBatteryBarChargingColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_CHARGING_COLOR);        
        mBatteryBarChargingColor.setNewPreviewColor(battchcolor);
        mBatteryBarChargingColor.setOnPreferenceChangeListener(this);        

        int battlowcolor = Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR, Color.WHITE);
        mBatteryBarBatteryLowColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_BATTERY_LOW_COLOR);
        mBatteryBarBatteryLowColor.setNewPreviewColor(battlowcolor);
        mBatteryBarBatteryLowColor.setOnPreferenceChangeListener(this);

        mBatteryBarThickness = (CustomSeekBarPreference) findPreference(PREF_BATT_BAR_WIDTH);
        mBatteryBarThickness.setValue(Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1));
        mBatteryBarThickness.setOnPreferenceChangeListener(this);        

        mBatteryBarUseChargingColor = (SwitchPreference) findPreference(PREF_BATT_USE_CHARGING_COLOR);
        mBatteryBarBlendColor = (SwitchPreference) findPreference(PREF_BATT_BLEND_COLOR);
        mBatteryBarBlendColorReverse = (SwitchPreference) findPreference(PREF_BATT_BLEND_COLOR_REVERSE);

        mBatteryBarChargingAnimation = (SwitchPreference) findPreference(PREF_BATT_ANIMATE);
        mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);

        mBatteryBarShowOnLockScreen = (SwitchPreference) findPreference(PREF_BATT_SHOW_ON_LOCK_SCREEN);
        mBatteryBarShowOnLockScreen.setChecked(Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_SHOW_ON_LOCK_SCREEN, 0) == 5);
        
        updateBatteryBarOptions();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBatteryBarColor) {
            int color = ((Integer) newValue).intValue();
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_COLOR, color);
            return true;
        } else if (preference == mBatteryBarChargingColor) {
            int color = ((Integer) newValue).intValue();
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, color);
            return true;
        } else if (preference == mBatteryBarBatteryLowColor) {
            int color = ((Integer) newValue).intValue();
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR, color);
            return true;
        } else if (preference == mBatteryBar) {
            int val = Integer.parseInt((String) newValue);
            int index = mBatteryBar.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR, val);
            mBatteryBar.setSummary(mBatteryBar.getEntries()[index]);
            updateBatteryBarOptions();
            return true;
        } else if (preference == mBatteryBarStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mBatteryBarStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_STYLE, val);
            mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarThickness) {
            int val =  (Integer) newValue;
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, val);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;
        if (preference == mBatteryBarChargingAnimation) {
            value = mBatteryBarChargingAnimation.isChecked();
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, value ? 1 : 0);
            return true;
        } else if (preference == mBatteryBarShowOnLockScreen) {
            value = mBatteryBarShowOnLockScreen.isChecked();
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_SHOW_ON_LOCK_SCREEN, value ? 5 : 0);
            return true;
        }
        return false;
    }

    private void updateBatteryBarOptions() {
        if (Settings.System.getInt(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR, 0) == 0) {
            mBatteryBarStyle.setEnabled(false);
            mBatteryBarThickness.setEnabled(false);
            mBatteryBarChargingAnimation.setEnabled(false);
            mBatteryBarColor.setEnabled(false);
            mBatteryBarChargingColor.setEnabled(false);
            mBatteryBarBatteryLowColor.setEnabled(false);
            mBatteryBarUseChargingColor.setEnabled(false);
            mBatteryBarBlendColor.setEnabled(false);
            mBatteryBarBlendColorReverse.setEnabled(false);
            mBatteryBarShowOnLockScreen.setEnabled(false);
        } else {
            mBatteryBarStyle.setEnabled(true);
            mBatteryBarThickness.setEnabled(true);
            mBatteryBarChargingAnimation.setEnabled(true);
            mBatteryBarColor.setEnabled(true);
            mBatteryBarChargingColor.setEnabled(true);
            mBatteryBarBatteryLowColor.setEnabled(true);
            mBatteryBarUseChargingColor.setEnabled(true);
            mBatteryBarBlendColor.setEnabled(true);
            mBatteryBarBlendColorReverse.setEnabled(true);
            mBatteryBarShowOnLockScreen.setEnabled(true);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.EXTENSIONS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}