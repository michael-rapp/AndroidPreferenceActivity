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
package de.mrapp.android.preference.activity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListAdapter;

import de.mrapp.android.preference.activity.NavigationPreference;
import de.mrapp.android.preference.activity.PreferenceActivity;

/**
 * A {@link PreferenceGroupAdapter}, which is used to visualize the navigation preferences of a
 * {@link PreferenceActivity}. It allows to register a callback at the adapter's {@link
 * NavigationPreference}s
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
public class NavigationPreferenceGroupAdapter extends PreferenceGroupAdapter {

    /**
     * The callback, which is registered at the adapter's {@link NavigationPreference}s.
     */
    private final NavigationPreference.Callback callback;

    /**
     * Creates a new {@link PreferenceGroupAdapter}, which is used to visualize the navigation
     * preferences of a {@link PreferenceActivity}.
     *
     * @param context
     *         The context, which should be used by the adapter, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param encapsulatedAdapter
     *         The adapter, which should be encapsulated, as an instance of the type {@link
     *         ListAdapter}. The adapter may not be null
     * @param callback
     *         The callback, which should be registered at the adapter's {@link
     *         NavigationPreference}s, as an instance of the type {@link
     *         NavigationPreference.Callback} or null, if no callback should be registered
     */
    public NavigationPreferenceGroupAdapter(@NonNull final Context context,
                                            @NonNull final ListAdapter encapsulatedAdapter,
                                            @Nullable final NavigationPreference.Callback callback) {
        super(context, encapsulatedAdapter);
        this.callback = callback;
    }

    @Override
    protected final void onVisualizeItem(@NonNull final Object item) {
        super.onVisualizeItem(item);

        if (item instanceof NavigationPreference) {
            ((NavigationPreference) item).setCallback(callback);
        }
    }

}