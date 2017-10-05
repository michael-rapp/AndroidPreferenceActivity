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
package de.mrapp.android.preference.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import de.mrapp.android.preference.activity.NavigationPreference;
import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.R;
import de.mrapp.android.preference.activity.adapter.NavigationPreferenceGroupAdapter;
import de.mrapp.android.preference.activity.adapter.PreferenceGroupAdapter;
import de.mrapp.android.preference.activity.view.PreferenceListView;

/**
 * A fragment, which contains the navigation of a {@link PreferenceActivity}.
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
public class NavigationFragment extends AbstractPreferenceFragment
        implements NavigationPreference.Callback {

    /**
     * Defines the interface, a class, which should be notified about the fragment's events, must
     * implement.
     */
    public interface Callback {

        /**
         * The method, which is invoked, when the navigation fragment has been attached to its
         * activity.
         *
         * @param fragment
         *         The navigation fragment as an instance of the class {@link PreferenceFragment}.
         *         The fragment may not be null
         */
        void onNavigationCreated(@NonNull PreferenceFragment fragment);

    }

    /**
     * The callback, which is notified about the fragment's events.
     */
    private Callback callback;

    /**
     * The callback, which is notified, when the fragment of a {@link NavigationPreference}, which
     * is contained by the fragment, should be shown.
     */
    private NavigationPreference.Callback navigationPreferenceCallback;

    /**
     * Creates and returns a factory, which allows to create the adapter, which should be used by
     * the list view, which shows the fragment's preferences.
     *
     * @return The factory, which has been created, as an instance of the type {@link
     * PreferenceListView.AdapterFactory}. The factory may not be null
     */
    @NonNull
    private PreferenceListView.AdapterFactory createAdapterFactory() {
        return new PreferenceListView.AdapterFactory() {

            @NonNull
            @Override
            public PreferenceGroupAdapter createAdapter(@NonNull final Context context,
                                                        @NonNull final ListAdapter adapter) {
                return new NavigationPreferenceGroupAdapter(context, adapter,
                        NavigationFragment.this);
            }

        };
    }

    /**
     * Notifies the callback, that the navigation fragment has been attached to its activity.
     */
    private void notifyOnNavigationCreated() {
        if (callback != null) {
            callback.onNavigationCreated(this);
        }
    }

    /**
     * Notifies the callback, that the fragment of a {@link NavigationPreference}, which is
     * contained by the fragment, should be shown.
     *
     * @param navigationPreference
     *         The navigation preference, whose fragment should be shown, as an instance of the
     *         class {@link NavigationPreference}. The navigation preference may not be null
     */
    private void notifyOnShowFragment(@NonNull final NavigationPreference navigationPreference) {
        if (navigationPreferenceCallback != null) {
            navigationPreferenceCallback.onShowFragment(navigationPreference);
        }
    }

    /**
     * Sets the callback, which should be notified about the fragment's events.
     *
     * @param callback
     *         The callback, which should be set, as an instance of the type {@link Callback} or
     *         null, if no callback should be notified
     */
    public final void setCallback(@Nullable final Callback callback) {
        this.callback = callback;
    }

    /**
     * Sets the callback, which should be notified, when the fragment of a {@link
     * NavigationPreference}, which is contained by the fragment, should be shown.
     *
     * @param callback
     *         The callback, which should be set, as an instance of the type {@link
     *         NavigationPreference.Callback} or null, if no callback should be notified
     */
    public final void setNavigationPreferenceCallback(
            @Nullable final NavigationPreference.Callback callback) {
        this.navigationPreferenceCallback = callback;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notifyOnNavigationCreated();
        setCallback(null);
    }

    @Override
    public final void onShowFragment(@NonNull final NavigationPreference navigationPreference) {
        notifyOnShowFragment(navigationPreference);
    }

    @NonNull
    @Override
    protected final View onInflateView(@NonNull final LayoutInflater inflater,
                                       @Nullable final ViewGroup parent,
                                       @Nullable final Bundle savedInstanceState) {
        PreferenceListView listView =
                (PreferenceListView) inflater.inflate(R.layout.navigation_fragment, parent, false);
        listView.setAdapterFactory(createAdapterFactory());
        return listView;
    }

}