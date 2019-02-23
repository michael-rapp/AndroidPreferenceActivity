/*
 * Copyright 2014 - 2019 Michael Rapp
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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.PreferenceFragment;
import de.mrapp.android.preference.activity.RestoreDefaultsListener;
import de.mrapp.android.preference.activity.example.R;

/**
 * A preference fragment, which allows to edit the settings concerning the behavior of the
 * activities which are used by the app to demonstrate the use of the class {@link
 * PreferenceActivity}.
 *
 * @author Michael Rapp
 */
public class BehaviorPreferenceFragment extends AbstractPreferenceFragment
        implements RestoreDefaultsListener {

    /**
     * Creates a listener, which allows to adapt the behavior of the {@link PreferenceActivity} when
     * the value, which determines, whether the action bar's back button should be overwritten, has
     * been changed.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceChangeListener}
     */
    private OnPreferenceChangeListener createOverrideBackButtonChangeListener() {
        return new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if (newValue != null) {
                    boolean overrideNavigationIcon = (boolean) newValue;
                    ((PreferenceActivity) getActivity())
                            .overrideNavigationIcon(overrideNavigationIcon);
                }

                return true;
            }

        };
    }

    /**
     * Creates a listener, which allows to adapt the behavior of the {@link PreferenceActivity} when
     * the value, which determines, whether the navigation should be hidden, has been changed.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceChangeListener}
     */
    private OnPreferenceChangeListener createHideNavigationChangeListener() {
        return new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if (newValue != null) {
                    boolean hideNavigation = (boolean) newValue;
                    ((PreferenceActivity) getActivity()).hideNavigation(hideNavigation);
                }

                return true;
            }

        };
    }

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.behavior_preferences);
        Preference overrideBackButtonPreference =
                findPreference(getString(R.string.override_navigation_icon_preference_key));
        overrideBackButtonPreference
                .setOnPreferenceChangeListener(createOverrideBackButtonChangeListener());
        Preference hideNavigationPreference =
                findPreference(getString(R.string.hide_navigation_preference_key));
        hideNavigationPreference
                .setOnPreferenceChangeListener(createHideNavigationChangeListener());
    }

    @Override
    public final void onRestoredDefaultValue(@NonNull final PreferenceFragment fragment,
                                             @NonNull final Preference preference,
                                             final Object oldValue, final Object newValue) {
        if (preference.getKey()
                .equals(getString(R.string.override_navigation_icon_preference_key))) {
            createOverrideBackButtonChangeListener().onPreferenceChange(preference, newValue);
        } else if (preference.getKey().equals(getString(R.string.hide_navigation_preference_key))) {
            createHideNavigationChangeListener().onPreferenceChange(preference, newValue);
        }
    }

}