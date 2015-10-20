/*
 * AndroidPreferenceActivity Copyright 2014 - 2015 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package de.mrapp.android.preference.activity.example.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.support.annotation.NonNull;

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
                boolean overrideNavigationIcon = (boolean) newValue;
                ((PreferenceActivity) getActivity()).overrideNavigationIcon(overrideNavigationIcon);
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
                boolean hideNavigation = (boolean) newValue;
                ((PreferenceActivity) getActivity()).hideNavigation(hideNavigation);
                return true;
            }

        };
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.behavior_preferences);
        Preference overrideBackButtonPreference =
                findPreference(getString(R.string.override_navigation_icon_preference_key));
        overrideBackButtonPreference
                .setOnPreferenceChangeListener(createOverrideBackButtonChangeListener());
        Preference hideNavigationPreference =
                findPreference(getString(R.string.hide_navigation_preference_key));
        hideNavigationPreference
                .setOnPreferenceChangeListener(createHideNavigationChangeListener());
        addRestoreDefaultsListener(new RestoreDefaultsDialogListener(getActivity()));
        addRestoreDefaultsListener(this);
    }

    @Override
    public final boolean onRestoreDefaultValuesRequested(
            @NonNull final PreferenceFragment fragment) {
        return true;
    }

    @Override
    public final boolean onRestoreDefaultValueRequested(@NonNull final PreferenceFragment fragment,
                                                        @NonNull final Preference preference,
                                                        final Object currentValue) {
        return true;
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