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
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import de.mrapp.android.preference.activity.NavigationPreference;
import de.mrapp.android.preference.activity.PreferenceActivity;

import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * A {@link PreferenceGroupAdapter}, which is used to visualize the navigation preferences of a
 * {@link PreferenceActivity}. It allows to register a callback at the adapter's {@link
 * NavigationPreference}s
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
public class NavigationPreferenceGroupAdapter extends PreferenceGroupAdapter
        implements NavigationPreference.Callback {

    /**
     * The callback, which is registered at the adapter's {@link NavigationPreference}s.
     */
    private final NavigationPreference.Callback callback;

    /**
     * A list, which contains all navigation preferences, which are contained by the adapter.
     */
    private final List<NavigationPreference> navigationPreferences;

    /**
     * The currently selected navigation preference.
     */
    private NavigationPreference selectedNavigationPreference;

    /**
     * The index of the currently selected navigation preference.
     */
    private int selectedNavigationPreferenceIndex;

    /**
     * Updates the navigation preferences, which are contained by the adapter.
     */
    private void updateNavigationPreferences() {
        navigationPreferences.clear();

        // TODO: Check if selected navigation preference is still present, if not select a new one
        for (int i = 0; i < getEncapsulatedAdapter().getCount(); i++) {
            Object item = getEncapsulatedAdapter().getItem(i);

            if (item instanceof NavigationPreference) {
                navigationPreferences.add((NavigationPreference) item);
            }
        }
    }

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
        this.navigationPreferences = new ArrayList<>();
        this.selectedNavigationPreference = null;
        this.selectedNavigationPreferenceIndex = -1;
        updateNavigationPreferences();
    }

    /**
     * Returns the number of navigation preferences, which are contained by the adapter.
     *
     * @return The number of navigation preferences, which are contained by the adapter, as an
     * {@link Integer} value
     */
    public final int getNavigationPreferenceCount() {
        return navigationPreferences.size();
    }

    /**
     * Returns the index of a specific navigation preference among all navigation preferences, which
     * are contained by the adapter.
     *
     * @param navigationPreference
     *         The navigation preference, whose index should be returned, as an instance of the
     *         class {@link NavigationPreference}. The navigation preference may not be null
     * @return The index of the given navigation preference as an {@link Integer} value or -1, if
     * the navigation preference is not contained by the adapter
     */
    public final int indexOfNavigationPreference(
            @NonNull final NavigationPreference navigationPreference) {
        ensureNotNull(navigationPreference, "The navigation preference may not be null");
        return navigationPreferences.indexOf(navigationPreference);
    }

    /**
     * Returns the currently selected navigation preference.
     *
     * @return The currently selected navigation preference as an instance of the class {@link
     * NavigationPreference} or null, if no navigation preference is selected
     */
    @Nullable
    public final NavigationPreference getSelectedNavigationPreference() {
        return selectedNavigationPreference;
    }

    /**
     * Returns the index of the navigation preference, which is currently selected, among all
     * navigation preferences.
     *
     * @return The index of the navigation preference, which is currently selected, as an {@link
     * Integer} value or -1, if no navigation preference is selected
     */
    public final int getSelectedNavigationPreferenceIndex() {
        return selectedNavigationPreferenceIndex;
    }

    /**
     * Selects a specific navigation preference.
     *
     * @param navigationPreference
     *         The navigation preference, which should be selected, as an instance of the class
     *         {@link NavigationPreference} or null, if no navigation preference should be selected
     * @return True, if the selection has been changed, false otherwise
     */
    public final boolean selectNavigationPreference(
            @Nullable final NavigationPreference navigationPreference) {
        if (selectedNavigationPreference != navigationPreference &&
                (callback == null || navigationPreference == null ||
                        callback.onShowFragment(navigationPreference))) {
            int index = navigationPreference == null ? -1 :
                    indexOfNavigationPreference(navigationPreference);
            selectedNavigationPreference = navigationPreference;
            selectedNavigationPreferenceIndex = index;
            super.notifyDataSetInvalidated();
            return true;
        }

        return false;
    }

    /**
     * Selects a specific navigation preference.
     *
     * @param index
     *         The index of the navigation preference, which should be selected, among all
     *         navigation preferences, as an {@link Integer} value or -1, if no navigation
     *         preference should be selected
     * @return True, if the selection has been changed, false otherwise
     */
    public final boolean selectNavigationPreference(final int index) {
        if (index == -1) {
            return selectNavigationPreference(null);
        } else {
            NavigationPreference navigationPreference = navigationPreferences.get(index);

            if (selectedNavigationPreference != navigationPreference &&
                    (callback == null || callback.onShowFragment(navigationPreference))) {
                selectedNavigationPreference = navigationPreference;
                selectedNavigationPreferenceIndex = index;
                super.notifyDataSetInvalidated();
                return true;
            }

            return false;
        }
    }

    @Override
    public final void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        updateNavigationPreferences();
    }

    @Override
    public final boolean onShowFragment(@NonNull final NavigationPreference navigationPreference) {
        return selectNavigationPreference(navigationPreference);
    }

    @Override
    protected final void onVisualizeItem(@NonNull final Object item) {
        super.onVisualizeItem(item);

        if (item instanceof NavigationPreference) {
            ((NavigationPreference) item).setCallback(this);
        }
    }

    @Override
    protected final void onVisualizedItem(@NonNull final Object item, @NonNull final View view) {
        if (item instanceof NavigationPreference) {
            // TODO Make color customizable
            view.setBackgroundColor(selectedNavigationPreference == item ? Color.argb(32, 0, 0, 0) :
                    Color.TRANSPARENT);
        }
    }

}