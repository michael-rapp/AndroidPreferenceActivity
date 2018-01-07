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

/**
 * Defines the interface, a class, which should be notified when {@link NavigationPreference}s are
 * added or removed to/from a {@link PreferenceActivity}, must implement.
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
public interface NavigationListener {

    /**
     * The method, which is invoked, when a navigation preference has been added.
     *
     * @param navigationPreference
     *         The navigation preference, which has been added, as an instance of the class {@link
     *         NavigationPreference}. The navigation preference may not be null
     */
    void onNavigationPreferenceAdded(@NonNull NavigationPreference navigationPreference);

    /**
     * The method, which is invoked, when a navigation preference has been removed.
     *
     * @param navigationPreference
     *         The navigation preference, which has been removed, as an instance of the class {@link
     *         NavigationPreference}. The navigation preference may not be null
     */
    void onNavigationPreferenceRemoved(@NonNull NavigationPreference navigationPreference);

}