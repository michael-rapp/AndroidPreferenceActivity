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
package de.mrapp.android.preference.activity.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import de.mrapp.android.preference.activity.NavigationPreference;
import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.R;
import de.mrapp.android.preference.activity.adapter.NavigationPreferenceAdapter;
import de.mrapp.android.preference.activity.adapter.PreferenceAdapter;

/**
 * A fragment, which contains the navigation of a {@link PreferenceActivity}.
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
public class NavigationFragment extends AbstractPreferenceFragment
        implements NavigationPreferenceAdapter.Callback {

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
         *         The navigation fragment as an instance of the class PreferenceFragmentCompat. The
         *         fragment may not be null
         */
        void onNavigationFragmentCreated(@NonNull PreferenceFragmentCompat fragment);

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
     * The callback, which is notified, about the events of the adapter, which contains the
     * navigation items.
     */
    private NavigationPreferenceAdapter.Callback adapterCallback;

    /**
     * The adapter, which contains the navigation preferences.
     */
    private NavigationPreferenceAdapter adapter;

    /**
     * The background color of the currently selected navigation preference.
     */
    private int selectionColor = Color.TRANSPARENT;

    /**
     * True, if the navigation is enabled, false otherwise.
     */
    private boolean enabled = true;

    /**
     * Notifies the callback, that the navigation fragment has been attached to its activity.
     */
    private void notifyOnNavigationFragmentCreated() {
        if (callback != null) {
            callback.onNavigationFragmentCreated(this);
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
     * Notifies the callback, that a {@link NavigationPreference}, which is contained by the
     * fragment, is about to be selected.
     *
     * @param navigationPreference
     *         The navigation preference, which is about to be selected, as an instance of the class
     *         {@link NavigationPreference}. The navigation preference may not be null
     * @return True, if the navigation preference should be selected, false otherwise
     */
    private boolean notifyOnSelectNavigationPreference(
            @NonNull final NavigationPreference navigationPreference) {
        return adapterCallback == null ||
                adapterCallback.onSelectNavigationPreference(navigationPreference);
    }

    /**
     * Notifies the callback, that a {@link NavigationPreference}, which is contained by the
     * fragment, has been selected.
     *
     * @param navigationPreference
     *         The navigation preference, which has been selected, as an instance of the class
     *         {@link NavigationPreference}. The navigation preference may not be null
     * @param arguments
     *         The arguments, which should be passed to the fragment, which is associated with the
     *         navigation preference, as an instance of the class {@link Bundle} or null, if no
     *         arguments should be passed to the fragment
     */
    private void notifyOnNavigationPreferenceSelected(
            @NonNull final NavigationPreference navigationPreference,
            @Nullable final Bundle arguments) {
        if (adapterCallback != null) {
            adapterCallback.onNavigationPreferenceSelected(navigationPreference, arguments);
        }
    }

    /**
     * Notifies the callback, that a navigation preference has been unselected.
     */
    private void notifyOnNavigationPreferenceUnselected() {
        if (adapterCallback != null) {
            adapterCallback.onNavigationPreferenceUnselected();
        }
    }

    /**
     * Notifies the callback, that a navigation preference has been added.
     *
     * @param navigationPreference
     *         The navigation preference, which has been added, as an instance of the class {@link
     *         NavigationPreference}. The navigation preference may not be null
     */
    private void notifyOnNavigationPreferenceAdded(
            @NonNull final NavigationPreference navigationPreference) {
        if (adapterCallback != null) {
            adapterCallback.onNavigationPreferenceAdded(navigationPreference);
        }
    }

    /**
     * Notifies the callback, that a navigation preference has been removed.
     *
     * @param navigationPreference
     *         The navigation preference, which has been removed, as an instance of the class {@link
     *         NavigationPreference}. The navigation preference may not be null
     */
    private void notifyOnNavigationPreferenceRemoved(
            @NonNull final NavigationPreference navigationPreference) {
        if (adapterCallback != null) {
            adapterCallback.onNavigationPreferenceRemoved(navigationPreference);
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
     * Returns the callback, which is notified about the fragment's events.
     *
     * @return The callback, which is notified about the fragment's events, as an instance of the
     * type {@link Callback} or null, if no callback is notified
     */
    @Nullable
    public final Callback getCallback() {
        return callback;
    }

    /**
     * Sets the callback, which should be notified about the events of the adapter, which contains
     * the navigation preferences.
     *
     * @param callback
     *         The callback, which should be set, as an instance of the type {@link
     *         NavigationPreferenceAdapter.Callback} or null, if no callback should be notified
     */
    public final void setAdapterCallback(
            @Nullable final NavigationPreferenceAdapter.Callback callback) {
        this.adapterCallback = callback;
    }

    /**
     * Returns the number of navigation preferences, which are contained by the navigation.
     *
     * @return The number of navigation preferences, which are contained by the navigation, as an
     * {@link Integer} value
     */
    public final int getNavigationPreferenceCount() {
        return isAdapterCreated() ? adapter.getNavigationPreferenceCount() : 0;
    }

    /**
     * Returns a list, which contains all navigation preferences, which are contained by the
     * navigation.
     *
     * @return A list, which contains all navigation preferences, which are contained by the
     * navigation, as an instance of the type {@link List} or an empty collection, if no navigation
     * preferences are contained by the navigation
     */
    public final List<NavigationPreference> getAllNavigationPreferences() {
        return isAdapterCreated() ? adapter.getAllNavigationPreferences() :
                Collections.<NavigationPreference>emptyList();
    }

    /**
     * Returns the navigation preference, which corresponds to a specific index.
     *
     * @param index
     *         The index of the navigation preference, which should be returned, among all
     *         navigation preferences as an {@link Integer} value
     * @return The navigation preference, which corresponds to the given index, as an instance of
     * the class {@link NavigationPreference} or null, if the adapter, which contains the navigation
     * preferences, has not been initialized yet
     */
    public final NavigationPreference getNavigationPreference(final int index) {
        return isAdapterCreated() ? adapter.getNavigationPreference(index) : null;
    }

    /**
     * Returns the currently selected navigation preference.
     *
     * @return The currently selected navigation preference as an instance of the class {@link
     * NavigationPreference} or null, if no navigation preference is currently selected
     */
    @Nullable
    public final NavigationPreference getSelectedNavigationPreference() {
        return isAdapterCreated() ? adapter.getSelectedNavigationPreference() : null;
    }

    /**
     * Returns the index of the navigation preference, which is currently selected, among all
     * navigation preferences.
     *
     * @return The index of the navigation preference, which is currently selected, as an {@link
     * Integer} value or -1, if no navigation preference is selected
     */
    public final int getSelectedNavigationPreferenceIndex() {
        return isAdapterCreated() ? adapter.getSelectedNavigationPreferenceIndex() : -1;
    }

    /**
     * Selects a specific navigation preference.
     *
     * @param index
     *         The index of the navigation preference, which should be selected, among all
     *         navigation preferences, as an {@link Integer} value or -1, if no navigation
     *         preference should be selected
     * @param arguments
     *         The arguments, which should be passed to the fragment, which is associated with the
     *         navigation preference, as an instance of the class {@link Bundle} or null, if no
     *         arguments should be passed to the fragment
     */
    public final void selectNavigationPreference(final int index,
                                                 @Nullable final Bundle arguments) {
        if (isAdapterCreated()) {
            adapter.selectNavigationPreference(index, arguments);
        }
    }

    /**
     * Sets the background color of the currently selected navigation preference.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value
     */
    public final void setSelectionColor(@ColorInt final int color) {
        this.selectionColor = color;

        if (isAdapterCreated()) {
            adapter.setSelectionColor(color);
        }
    }

    /**
     * Sets, whether the navigation should be enabled, i.e. whether the navigation preferences
     * should be clickable, or not.
     *
     * @param enabled
     *         True, if the navigation should be enabled, false otherwise
     */
    public final void setEnabled(final boolean enabled) {
        this.enabled = enabled;

        if (isAdapterCreated()) {
            adapter.setEnabled(enabled);
        }
    }

    /**
     * Returns, whether the adapter, which contains the navigation preferences, has been created
     * yet, or not.
     *
     * @return True, if the adapter has been created yet, false otherwise
     */
    public final boolean isAdapterCreated() {
        return adapter != null;
    }

    @Override
    public final boolean onSelectNavigationPreference(
            @NonNull final NavigationPreference navigationPreference) {
        return notifyOnSelectNavigationPreference(navigationPreference);
    }

    @Override
    public final void onNavigationPreferenceSelected(
            @NonNull final NavigationPreference navigationPreference,
            @Nullable final Bundle arguments) {
        notifyOnNavigationPreferenceSelected(navigationPreference, arguments);
    }

    @Override
    public final void onNavigationPreferenceUnselected() {
        notifyOnNavigationPreferenceUnselected();
    }

    @Override
    public final void onNavigationPreferenceAdded(
            @NonNull final NavigationPreference navigationPreference) {
        notifyOnNavigationPreferenceAdded(navigationPreference);
    }

    @Override
    public final void onNavigationPreferenceRemoved(
            @NonNull final NavigationPreference navigationPreference) {
        notifyOnNavigationPreferenceRemoved(navigationPreference);
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!isAdapterCreated()) {
            notifyOnNavigationFragmentCreated();
        }
    }

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {

    }

    @NonNull
    @Override
    public final RecyclerView onCreateRecyclerView(final LayoutInflater inflater,
                                                   final ViewGroup parent,
                                                   final Bundle savedInstanceState) {
        RecyclerView recyclerView =
                super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        recyclerView.setClipToPadding(false);
        int paddingTop =
                getActivity().getResources().getDimensionPixelSize(R.dimen.list_view_padding_top);
        recyclerView.setPadding(recyclerView.getPaddingLeft(), paddingTop,
                recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        return recyclerView;
    }

    @NonNull
    protected final PreferenceAdapter onCreatePreferenceAdapter(
            @NonNull final PreferenceScreen preferenceScreen) {
        this.adapter = new NavigationPreferenceAdapter(preferenceScreen, this);
        this.adapter.setSelectionColor(selectionColor);
        this.adapter.setEnabled(enabled);
        notifyOnNavigationAdapterCreated();
        return adapter;
    }

}