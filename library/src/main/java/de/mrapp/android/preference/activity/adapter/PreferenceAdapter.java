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
package de.mrapp.android.preference.activity.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

/**
 * A recycler view adapter, which extends the class PreferenceGroupAdapter in order to modify the
 * appearance of preferences.
 *
 * @author Michael Rapp
 * @since 6.0.0
 */
@SuppressLint("RestrictedApi")
public class PreferenceAdapter extends PreferenceGroupAdapter {

    /**
     * The method, which is invoked, when a specific preference is visualized. This method may be
     * overridden by subclasses in order to modify the appearance of the preference.
     *
     * @param preference
     *         The preference, which is visualized, as an instance of the class Preference. The
     *         preference may not be null
     * @param viewHolder
     *         The view holder, which corresponds to the preference, as an instance of the class
     *         PreferenceViewHolder. The view holder may not be null
     */
    @CallSuper
    protected void onVisualizePreference(@NonNull final Preference preference,
                                         @NonNull final PreferenceViewHolder viewHolder) {
        if (preference instanceof PreferenceCategory) {
            RecyclerView.LayoutParams layoutParams =
                    (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            layoutParams.height = TextUtils.isEmpty(preference.getTitle()) ? 0 :
                    RecyclerView.LayoutParams.WRAP_CONTENT;
        }
    }

    /**
     * Creates a new recycler view adapter, which extends the class PreferenceGroupAdapter in order
     * to modify the appearance of preferences.
     *
     * @param preferenceScreen
     *         The preference screen, which contains the preferences, which should be managed by the
     *         adapter, as an instance of the class PreferenceScreen. The preference screen may not
     *         be null
     */
    public PreferenceAdapter(@NonNull final PreferenceScreen preferenceScreen) {
        super(preferenceScreen);
    }

    @Override
    public final void onBindViewHolder(@NonNull final PreferenceViewHolder viewHolder,
                                       final int position) {
        super.onBindViewHolder(viewHolder, position);
        Preference preference = getItem(position);
        onVisualizePreference(preference, viewHolder);
    }

}