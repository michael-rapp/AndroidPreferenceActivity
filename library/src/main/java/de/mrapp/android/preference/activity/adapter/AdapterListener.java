/*
 * Copyright 2014 - 2016 Michael Rapp
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
package de.mrapp.android.preference.activity.adapter;

import android.support.annotation.NonNull;

import de.mrapp.android.preference.activity.PreferenceHeader;

/**
 * Defines the interface, a class, which should be notified, when the underlying data of a {@link
 * PreferenceHeaderAdapter} has been changed, must implement.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public interface AdapterListener {

    /**
     * The method, which is invoked, when a preference header has been added to the adapter.
     *
     * @param adapter
     *         The obtained adapter as an instance of the class {@link PreferenceHeaderAdapter}
     * @param preferenceHeader
     *         The preference header, which has been added to the adapter, as an instance of the
     *         class {@link PreferenceHeader}
     * @param position
     *         The position of the preference header, which has been added, as an {@link Integer}
     *         value
     */
    void onPreferenceHeaderAdded(@NonNull PreferenceHeaderAdapter adapter,
                                 @NonNull PreferenceHeader preferenceHeader, int position);

    /**
     * The method, which is invoked, when a preference header has been removed from the adapter.
     *
     * @param adapter
     *         The obtained adapter as an instance of the class {@link PreferenceHeaderAdapter}
     * @param preferenceHeader
     *         The preference header, which has been removed from the adapter, as an instance of the
     *         class {@link PreferenceHeader}
     * @param position
     *         The position of the preference header, which has been removed, as an {@link Integer}
     *         value
     */
    void onPreferenceHeaderRemoved(@NonNull PreferenceHeaderAdapter adapter,
                                   @NonNull PreferenceHeader preferenceHeader, int position);

}