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
        implements NavigationPreference.Callback, PreferenceListView.AdapterFactory {

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

        /**
         * The method, which is invoked, when the adapter, which contains the navigation items, has
         * been created.
         */
        void onNavigationAdapterCreated();

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
     * The adapter, which contains the navigation items.
     */
    private NavigationPreferenceGroupAdapter adapter;

    /**
     * Notifies the callback, that the navigation fragment has been attached to its activity.
     */
    private void notifyOnNavigationCreated() {
        if (callback != null) {
            callback.onNavigationCreated(this);
        }
    }

    /**
     * Notifies the callback, that the adapter, which contains the navigation items, has been
     * created.
     */
    private void notifyOnNavigationAdapterCreated() {
        if (callback != null) {
            callback.onNavigationAdapterCreated();
        }
    }

    /**
     * Notifies the callback, that the fragment of a {@link NavigationPreference}, which is
     * contained by the fragment, should be shown.
     *
     * @param navigationPreference
     *         The navigation preference, whose fragment should be shown, as an instance of the
     *         class {@link NavigationPreference}. The navigation preference may not be null
     * @return True, if the fragment has been shown, false otherwise
     */
    private boolean notifyOnShowFragment(@NonNull final NavigationPreference navigationPreference) {
        return navigationPreferenceCallback != null &&
                navigationPreferenceCallback.onShowFragment(navigationPreference);
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

    /**
     * Returns the number of navigation preferences, which are contained by the navigation.
     *
     * @return The number of navigation preferences, which are contained by the navigation, as an
     * {@link Integer} value
     */
    public final int getNavigationPreferenceCount() {
        return adapter != null ? adapter.getNavigationPreferenceCount() : 0;
    }

    /**
     * Selects a specific navigation preference.
     *
     * @param index
     *         The index of the navigation preference, which should be selected, among all
     *         navigation preferences, as an {@link Integer} value or -1, if no navigation
     *         preference should be selected
     */
    public final void selectNavigationPreference(final int index) {
        if (adapter != null) {
            adapter.selectNavigationPreference(index);
        }
    }

    @Override
    public final boolean onShowFragment(@NonNull final NavigationPreference navigationPreference) {
        return notifyOnShowFragment(navigationPreference);
    }

    @NonNull
    @Override
    public final PreferenceGroupAdapter createAdapter(@NonNull final Context context,
                                                      @NonNull final ListAdapter encapsulatedAdapter) {
        if (adapter == null) {
            adapter = new NavigationPreferenceGroupAdapter(context, encapsulatedAdapter,
                    NavigationFragment.this);
            notifyOnNavigationAdapterCreated();
        }

        return adapter;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            notifyOnNavigationCreated();
        }
    }

    @NonNull
    @Override
    protected final View onInflateView(@NonNull final LayoutInflater inflater,
                                       @Nullable final ViewGroup parent,
                                       @Nullable final Bundle savedInstanceState) {
        PreferenceListView listView =
                (PreferenceListView) inflater.inflate(R.layout.navigation_fragment, parent, false);
        listView.setAdapterFactory(this);
        return listView;
    }

}