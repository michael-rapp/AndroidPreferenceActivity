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
package de.mrapp.android.preference.activity.example;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;

import de.mrapp.android.preference.activity.PreferenceActivity;

/**
 * An abstract base class for all activities, which are used by the app to demonstrate the use of
 * the class {@link PreferenceActivity}. This base class implements adapting of the settings, which
 * are defined by the app's shared preferences.
 *
 * @author Michael Rapp
 */
public abstract class AbstractPreferenceActivity extends PreferenceActivity {

    /**
     * Initializes the width of the navigation.
     *
     * @param sharedPreferences
     *         The shared preferences, which should be used, as an instance of the type {@link
     *         SharedPreferences}
     */
    private void initializeNavigationWidth(final SharedPreferences sharedPreferences) {
        String key = getString(R.string.navigation_width_preference_key);
        String defaultValue = getString(R.string.navigation_width_preference_default_value);
        int width = Integer.valueOf((sharedPreferences.getString(key, defaultValue)));
        setNavigationWidth(width);
    }

    /**
     * Initializes the elevation of the preference screen.
     *
     * @param sharedPreferences
     *         The shared preferences, which should be used, as an instance of the type {@link
     *         SharedPreferences}
     */
    private void initializePreferenceScreenElevation(final SharedPreferences sharedPreferences) {
        String key = getString(R.string.preference_screen_elevation_preference_key);
        String defaultValue =
                getString(R.string.preference_screen_elevation_preference_default_value);
        int elevation = Integer.valueOf(sharedPreferences.getString(key, defaultValue));
        setPreferenceScreenElevation(elevation);
    }

    /**
     * Initializes the elevation of the bread crumbs.
     *
     * @param sharedPreferences
     *         The shared preferences, which should be used, as an instance of the type {@link
     *         SharedPreferences}
     */
    private void initializeBreadCrumbElevation(final SharedPreferences sharedPreferences) {
        String key = getString(R.string.bread_crumb_elevation_preference_key);
        String defaultValue = getString(R.string.bread_crumb_elevation_preference_default_value);
        int elevation = Integer.valueOf(sharedPreferences.getString(key, defaultValue));
        setBreadCrumbElevation(elevation);
    }

    /**
     * Initializes the elevation of a wizard's button bar.
     *
     * @param sharedPreferences
     *         The shared preferences, which should be used, as an instance of the type {@link
     *         SharedPreferences}
     */
    private void initializeWizardButtonBarElevation(final SharedPreferences sharedPreferences) {
        String key = getString(R.string.wizard_button_bar_elevation_preference_key);
        String defaultValue =
                getString(R.string.wizard_button_bar_elevation_preference_default_value);
        int elevation = Integer.valueOf(sharedPreferences.getString(key, defaultValue));
        setButtonBarElevation(elevation);
    }

    /**
     * Initializes, whether the action bar's back button should be overridden, or not.
     *
     * @param sharedPreferences
     *         The shared preferences, which should be used, as an instance of the type {@link
     *         SharedPreferences}
     */
    private void initializeOverrideBackButton(final SharedPreferences sharedPreferences) {
        String key = getString(R.string.override_navigation_icon_preference_key);
        boolean defaultValue = Boolean.valueOf(
                getString(R.string.override_navigation_icon_preference_default_value));
        boolean overrideNavigationIcon = sharedPreferences.getBoolean(key, defaultValue);
        overrideNavigationIcon(overrideNavigationIcon);
    }

    /**
     * Initializes, whether the navigation should be hidden, or not.
     *
     * @param sharedPreferences
     *         The shared preferences, which should be used, as an instance of the type {@link
     *         SharedPreferences}
     */
    private void initializeHideNavigation(final SharedPreferences sharedPreferences) {
        String key = getString(R.string.hide_navigation_preference_key);
        boolean defaultValue =
                Boolean.valueOf(getString(R.string.hide_navigation_preference_default_value));
        boolean hideNavigation = sharedPreferences.getBoolean(key, defaultValue);
        hideNavigation(hideNavigation);
    }

    @Override
    public final void setTheme(final int resid) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(R.string.theme_preference_key);
        String defaultValue = getString(R.string.theme_preference_default_value);
        int theme = Integer.valueOf(sharedPreferences.getString(key, defaultValue));

        if (theme != 0) {
            super.setTheme(R.style.DarkTheme);
        } else {
            super.setTheme(R.style.LightTheme);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initializeNavigationWidth(sharedPreferences);
        initializePreferenceScreenElevation(sharedPreferences);
        initializeBreadCrumbElevation(sharedPreferences);
        initializeOverrideBackButton(sharedPreferences);
        initializeHideNavigation(sharedPreferences);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public final void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initializeWizardButtonBarElevation(sharedPreferences);
    }

}