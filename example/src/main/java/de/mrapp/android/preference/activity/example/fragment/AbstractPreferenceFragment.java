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