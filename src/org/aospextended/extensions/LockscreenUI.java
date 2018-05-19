/*
 * Copyright (C) 2017 AospExtended ROM Project
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

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.util.Log;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

public class LockscreenUI extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private ListPreference mAmbientTicker;

    private ListPreference mLockscreenClockSelection;
    private ListPreference mLockscreenDateSelection;

    private SwitchPreference mLockMenuKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_ui);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();

        mAmbientTicker = (ListPreference) findPreference("force_ambient_for_media");
        int mode = Settings.System.getIntForUser(resolver,
                Settings.System.FORCE_AMBIENT_FOR_MEDIA, 0, UserHandle.USER_CURRENT);
        mAmbientTicker.setValue(Integer.toString(mode));
        mAmbientTicker.setSummary(mAmbientTicker.getEntry());
        mAmbientTicker.setOnPreferenceChangeListener(this);

        Resources resources = getResources();

        mLockMenuKey = (SwitchPreference) findPreference("menukey_lockscreen");
        if (!resources.getBoolean(R.bool.LockMenuKey)) {
	    ((PreferenceCategory) findPreference("lockscreen_ui_gestures_category")).removePreference(mLockMenuKey);
	} else {
        mLockMenuKey.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.HW_MENU_KEY_LOCKSCREEN, 0) == 1));
        mLockMenuKey.setOnPreferenceChangeListener(this);
        }

	mLockscreenClockSelection = (ListPreference) findPreference("lockscreen_clock_selection");
        int clockSelection = Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_CLOCK_SELECTION, 0, UserHandle.USER_CURRENT);
        mLockscreenClockSelection.setValue(String.valueOf(clockSelection));
        mLockscreenClockSelection.setSummary(mLockscreenClockSelection.getEntry());
        mLockscreenClockSelection.setOnPreferenceChangeListener(this);

	mLockscreenDateSelection = (ListPreference) findPreference("lockscreen_date_selection");
        int dateSelection = Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_DATE_SELECTION, 0, UserHandle.USER_CURRENT);
        mLockscreenDateSelection.setValue(String.valueOf(dateSelection));
        mLockscreenDateSelection.setSummary(mLockscreenDateSelection.getEntry());
        mLockscreenDateSelection.setOnPreferenceChangeListener(this);

    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.EXTENSIONS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mAmbientTicker) {
    	    int mode = Integer.valueOf((String) objValue);
    	    int index = mAmbientTicker.findIndexOfValue((String) objValue);
    	    mAmbientTicker.setSummary(
                mAmbientTicker.getEntries()[index]);
    	    Settings.System.putIntForUser(resolver, Settings.System.FORCE_AMBIENT_FOR_MEDIA,
                mode, UserHandle.USER_CURRENT);
    	    return true;
	} else if (preference == mLockscreenClockSelection) {
            int clockSelection = Integer.valueOf((String) objValue);
            int index = mLockscreenClockSelection.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.LOCKSCREEN_CLOCK_SELECTION, clockSelection, UserHandle.USER_CURRENT);
            mLockscreenClockSelection.setSummary(mLockscreenClockSelection.getEntries()[index]);
            return true;
	} else if (preference == mLockscreenDateSelection) {
            int dateSelection = Integer.valueOf((String) objValue);
            int index = mLockscreenDateSelection.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.LOCKSCREEN_DATE_SELECTION, dateSelection, UserHandle.USER_CURRENT);
            mLockscreenDateSelection.setSummary(mLockscreenDateSelection.getEntries()[index]);
            return true;
        } else if (preference == mLockMenuKey) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    Settings.System.HW_MENU_KEY_LOCKSCREEN, value ? 1 : 0);
            return true;
	    }
        return false;
    }
}
