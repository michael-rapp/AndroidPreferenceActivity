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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import de.mrapp.android.preference.activity.PreferenceFragment;
import de.mrapp.android.preference.activity.RestoreDefaultsListener;
import de.mrapp.android.preference.activity.example.R;

/**
 * An abstract base class for all preference fragments.
 *
 * @author Michael Rapp
 */
public abstract class AbstractPreferenceFragment extends PreferenceFragment
        implements RestoreDefaultsListener {

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

    /**
     * Creates and returns a listener, which allows to restore the default values of the fragment's
     * preferences.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * DialogInterface.OnClickListener}
     */
    private DialogInterface.OnClickListener createRestoreDefaultsListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                restoreDefaults();
            }

        };
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        initializeButtonBarElevation(sharedPreferences);
        addRestoreDefaultsListener(this);
    }

    @Override
    public final boolean onRestoreDefaultValuesRequested(
            @NonNull final PreferenceFragment fragment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.restore_defaults_dialog_title);
        dialogBuilder.setMessage(R.string.restore_defaults_dialog_message);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(android.R.string.ok, createRestoreDefaultsListener());
        dialogBuilder.setNegativeButton(android.R.string.cancel, null);
        dialogBuilder.show();
        return false;
    }

    @Override
    public final boolean onRestoreDefaultValueRequested(@NonNull final PreferenceFragment fragment,
                                                        @NonNull final Preference preference,
                                                        final Object currentValue) {
        return true;
    }

    @Override
    public void onRestoredDefaultValue(@NonNull final PreferenceFragment fragment,
                                       @NonNull final Preference preference, final Object oldValue,
                                       final Object newValue) {

    }

}