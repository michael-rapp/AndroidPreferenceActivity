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
 * A preference fragment, which allows to edit the settings concerning the appearance of the
 * activities which are used by the app to demonstrate the use of the class {@link
 * PreferenceActivity}.
 *
 * @author Michael Rapp
 */
public class AppearancePreferenceFragment extends AbstractPreferenceFragment
        implements RestoreDefaultsListener {

    /**
     * Creates and returns a listener, which allows to adapt the app's theme, when the value of the
     * corresponding preference has been changed.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceChangeListener}
     */
    private OnPreferenceChangeListener createThemeChangeListener() {
        return new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                getActivity().recreate();
                return true;
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to adapt the width of the navigation, when the
     * value of the corresponding preference has been changed.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceChangeListener}
     */
    private OnPreferenceChangeListener createNavigationWidthChangeListener() {
        return new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                int width = Integer.valueOf((String) newValue);
                ((PreferenceActivity) getActivity()).setNavigationWidth(width);
                return true;
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to adapt the elevation of the preference screen,
     * when the value of the corresponding preference has been changed.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceChangeListener}
     */
    private OnPreferenceChangeListener createPreferenceScreenElevationChangeListener() {
        return new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                int elevation = Integer.valueOf((String) newValue);
                ((PreferenceActivity) getActivity()).setPreferenceScreenElevation(elevation);
                return true;
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to adapt the elevation of the bread crumbs, when
     * the value of the corresponding preference has been changed.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceChangeListener}
     */
    private OnPreferenceChangeListener createBreadCrumbElevationChangeListener() {
        return new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int elevation = Integer.valueOf((String) newValue);
                ((PreferenceActivity) getActivity()).setBreadCrumbElevation(elevation);
                return true;
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to adapt the elevation of a wizard's button bar,
     * when the value of the corresponding preference has been changed.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceChangeListener}
     */
    private OnPreferenceChangeListener createWizardButtonBarElevationChangeListener() {
        return new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                int elevation = Integer.valueOf((String) newValue);
                ((PreferenceActivity) getActivity()).setButtonBarElevation(elevation);
                return true;
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to adapt the elevation of a preference
     * fragment's button bar, when the value of the corresponding preference has been changed.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceChangeListener}
     */
    private OnPreferenceChangeListener createPreferenceFragmentButtonBarElevationChangeListener() {
        return new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                int elevation = Integer.valueOf((String) newValue);
                setButtonBarElevation(elevation);
                return true;
            }

        };
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.appearance_preferences);

        Preference themePreference = findPreference(getString(R.string.theme_preference_key));
        themePreference.setOnPreferenceChangeListener(createThemeChangeListener());
        Preference navigationWidthPreference =
                findPreference(getString(R.string.navigation_width_preference_key));
        navigationWidthPreference
                .setOnPreferenceChangeListener(createNavigationWidthChangeListener());
        Preference preferenceScreenElevationPreference =
                findPreference(getString(R.string.preference_screen_elevation_preference_key));
        preferenceScreenElevationPreference
                .setOnPreferenceChangeListener(createPreferenceScreenElevationChangeListener());
        Preference breadCrumbElevationPreference =
                findPreference(getString(R.string.bread_crumb_elevation_preference_key));
        breadCrumbElevationPreference
                .setOnPreferenceChangeListener(createBreadCrumbElevationChangeListener());
        Preference wizardButtonBarElevationPreference =
                findPreference(getString(R.string.wizard_button_bar_elevation_preference_key));
        wizardButtonBarElevationPreference
                .setOnPreferenceChangeListener(createWizardButtonBarElevationChangeListener());
        Preference preferenceFragmentButtonBarElevationPreference = findPreference(
                getString(R.string.preference_fragment_button_bar_elevation_preference_key));
        preferenceFragmentButtonBarElevationPreference.setOnPreferenceChangeListener(
                createPreferenceFragmentButtonBarElevationChangeListener());

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
        if (preference.getKey().equals(getString(R.string.theme_preference_key))) {
            createThemeChangeListener().onPreferenceChange(preference, newValue);
        } else if (preference.getKey()
                .equals(getString(R.string.navigation_width_preference_key))) {
            createNavigationWidthChangeListener().onPreferenceChange(preference, newValue);
        } else if (preference.getKey()
                .equals(getString(R.string.preference_screen_elevation_preference_key))) {
            createPreferenceScreenElevationChangeListener()
                    .onPreferenceChange(preference, newValue);
        } else if (preference.getKey()
                .equals(getString(R.string.bread_crumb_elevation_preference_key))) {
            createBreadCrumbElevationChangeListener().onPreferenceChange(preference, newValue);
        } else if (preference.getKey()
                .equals(getString(R.string.wizard_button_bar_elevation_preference_key))) {
            createWizardButtonBarElevationChangeListener().onPreferenceChange(preference, newValue);
        } else if (preference.getKey().equals(getString(
                R.string.preference_fragment_button_bar_elevation_preference_key))) {
            createPreferenceFragmentButtonBarElevationChangeListener()
                    .onPreferenceChange(preference, newValue);
        }
    }

}