/*
 * Copyright 2014 - 2018 Michael Rapp
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
package de.mrapp.android.preference.activity.example;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragmentCompat;
import de.mrapp.android.preference.activity.NavigationPreference;
import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.PreferenceFragment;

/**
 * An activity, which is used to demonstrate the default appearance of a {@link
 * PreferenceActivity}.
 *
 * @author Michael Rapp
 */
public class SettingsActivity extends AbstractPreferenceActivity {

    /**
     * Initializes the navigation preference, which allows to navigate to the appearance settings.
     *
     * @param fragment
     *         The fragment, which contains the preference, as an instance of the class {@link
     *         PreferenceFragmentCompat}. The fragment may not be null
     */
    private void initializeAppearanceNavigationPreference(
            @NonNull final PreferenceFragmentCompat fragment) {
        String key = getString(R.string.appearance_navigation_preference_key);
        NavigationPreference navigationPreference =
                (NavigationPreference) fragment.findPreference(key);
        Bundle extras = new Bundle();
        extras.putBoolean(PreferenceFragment.EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON, true);
        navigationPreference.setExtras(extras);

    }

    /**
     * Initializes the navigation preference, which allows to navigate to the behavior settings.
     *
     * @param fragment
     *         The fragment, which contains the preference, as an instance of the class {@link
     *         PreferenceFragmentCompat}. The fragment may not be null
     */
    private void initializeBehaviorNavigationPreference(
            @NonNull final PreferenceFragmentCompat fragment) {
        String key = getString(R.string.behavior_navigation_preference_key);
        NavigationPreference navigationPreference =
                (NavigationPreference) fragment.findPreference(key);
        Bundle extras = new Bundle();
        extras.putBoolean(PreferenceFragment.EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON, true);
        navigationPreference.setExtras(extras);
    }

    @Override
    public final void onCreateNavigation(@NonNull final PreferenceFragmentCompat fragment) {
        fragment.addPreferencesFromResource(R.xml.navigation);
        initializeAppearanceNavigationPreference(fragment);
        initializeBehaviorNavigationPreference(fragment);
    }

}