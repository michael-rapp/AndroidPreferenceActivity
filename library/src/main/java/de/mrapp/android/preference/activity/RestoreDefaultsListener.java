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
package de.mrapp.android.preference.activity;

import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;

/**
 * Defines the interface, a class, which should be notified, when the default values of the
 * preferences, which belong to a {@link PreferenceFragment}, should be restored.
 *
 * @author Michael Rapp
 * @since 1.1.0
 */
public interface RestoreDefaultsListener {

    /**
     * The method, which is invoked, when the default values of the preferences, which belong to a
     * specific preference fragment, should be restored.
     *
     * @param fragment
     *         The fragment, whose preferences' default values should be restored, as an instance of
     *         the class {@link PreferenceFragment}
     * @return True, if restoring the preferences' default values should be proceeded, false
     * otherwise
     */
    boolean onRestoreDefaultValuesRequested(@NonNull PreferenceFragment fragment);

    /**
     * The method, which is invoked, when the default value of a specific preference, should be
     * restored.
     *
     * @param fragment
     *         The fragment, the preference, whose default value should be restored, belongs to, as
     *         an instance of the class {@link PreferenceFragment}
     * @param preference
     *         The preference, whose default value should be restored, as an instance of the class
     *         Preference
     * @param currentValue
     *         The current value of the preference, whose default value should be restored, as an
     *         instance of the class {@link Object}
     * @return True, if restoring the preference's default value should be proceeded, false
     * otherwise
     */
    boolean onRestoreDefaultValueRequested(@NonNull PreferenceFragment fragment,
                                           @NonNull Preference preference, Object currentValue);

    /**
     * The method, which is invoked, when the default value of a specific preference has been
     * restored.
     *
     * @param fragment
     *         The fragment, the preference, whose default value has been restored, belongs to, as
     *         an instance of the class {@link PreferenceFragment}
     * @param preference
     *         The preference, whose default value has been restored, as an instance of the class
     *         Preference
     * @param oldValue
     *         The old value of the preference, whose default value has been restored, as an
     *         instance of the class {@link Object}
     * @param newValue
     *         The new value of the preference, whose default value has been restored, as an
     *         instance of the class {@link Object}
     */
    void onRestoredDefaultValue(@NonNull PreferenceFragment fragment,
                                @NonNull Preference preference, Object oldValue, Object newValue);

}