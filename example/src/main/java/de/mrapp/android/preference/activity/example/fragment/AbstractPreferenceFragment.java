/*
 * Copyright 2014 - 2017 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.android.preference.activity.example.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import de.mrapp.android.preference.activity.PreferenceFragment;
import de.mrapp.android.preference.activity.example.R;

/**
 * An abstract base class for all preference fragments.
 *
 * @author Michael Rapp
 */
public abstract class AbstractPreferenceFragment extends PreferenceFragment {

    /**
     * Initializes the elevation of the button bar.
     *
     * @param sharedPreferences
     *         The shared preferences, which should be used, as an instance of the type {@link
     *         SharedPreferences}
     */
    private void initializeButtonBarElevation(final SharedPreferences sharedPreferences) {
        String key = getString(R.string.preference_fragment_button_bar_elevation_preference_key);
        String defaultValue = getString(
                R.string.preference_fragment_button_bar_elevation_preference_default_value);
        int elevation = Integer.valueOf(sharedPreferences.getString(key, defaultValue));
        setButtonBarElevation(elevation);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        initializeButtonBarElevation(sharedPreferences);
    }

}