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
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.mrapp.android.preference.activity.NavigationPreference;
import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.R;
import de.mrapp.android.util.ThemeUtil;
import de.mrapp.android.util.ViewUtil;

import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * A {@link PreferenceAdapter}, which is used to visualize the navigation preferences of a {@link
 * PreferenceActivity}. It allows to register a callback at the adapter's {@link
 * NavigationPreference}s
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
@SuppressLint("RestrictedApi")
public class NavigationPreferenceAdapter extends PreferenceAdapter
        implements NavigationPreference.Callback {

    /**
     * Defines the callback, a class, which should be notified about the adapter's events, must
     * implement.
     */
    public interface Callback {

        /**
         * The method, which is invoked, when a navigation preference is about to be selected.
         *
         * @param navigationPreference
         *         The navigation preference, which is about to be selected, as an instance of the
         *         class {@link NavigationPreference}. The navigation preference may not be null
         * @return True, if the navigation preference should be selected, false otherwise
         */
        boolean onSelectNavigationPreference(
                @NonNull final NavigationPreference navigationPreference);

        /**
         * The method, which is invoked, when a navigation preference has been selected.
         *
         * @param navigationPreference
         *         The navigation preference, which has been selected, as an instance of the class
         *         {@link NavigationPreference}. The navigation preference may not be null
         * @param arguments
         *         The arguments, which should be passed to the fragment, which is associated with
         *         the navigation preference, as an instance of the class {@link Bundle} or null, if
         *         no arguments should be passed to the fragment
         */
        void onNavigationPreferenceSelected(
                @NonNull final NavigationPreference navigationPreference,
                @Nullable final Bundle arguments);

        /**
         * The method, which is invoked, when a navigation preference has been unselected.
         */
        void onNavigationPreferenceUnselected();

        /**
         * The method, which is invoked, when a navigation preference has been added.
         *
         * @param navigationPreference
         *         The navigation preference, which has been added, as an instance of the class
         *         {@link NavigationPreference}. The navigation preference may not be null
         */
        void onNavigationPreferenceAdded(@NonNull final NavigationPreference navigationPreference);

        /**
         * The method, which is invoked, when a navigation preference has been removed.
         *
         * @param navigationPreference
         *         The navigation preference, which has been removed, as an instance of the class
         *         {@link NavigationPreference}. The navigation preference may not be null
         */
        void onNavigationPreferenceRemoved(
                @NonNull final NavigationPreference navigationPreference);

    }

    /**
     * The callback, which is notified about the adapter's events.
     */
    private final Callback callback;

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
     * The background color of the currently selected navigation preference.
     */
    private int selectionColor;

    /**
     * True, if the items of the adapter are enabled, false otherwise.
     */
    private boolean enabled;

    /**
     * Updates the navigation preferences, which are contained by the adapter.
     */
    private void updateNavigationPreferences() {
        List<NavigationPreference> oldNavigationPreferences =
                new ArrayList<>(navigationPreferences);
        navigationPreferences.clear();
        boolean selectedNavigationPreferenceFound = selectedNavigationPreference == null;

        for (int i = 0; i < getItemCount(); i++) {
            Preference item = getItem(i);

            if (item instanceof NavigationPreference) {
                NavigationPreference navigationPreference = (NavigationPreference) item;
                navigationPreferences.add(navigationPreference);

                if (selectedNavigationPreference == navigationPreference) {
                    selectedNavigationPreferenceFound = true;
                }

                if (!oldNavigationPreferences.contains(navigationPreference)) {
                    notifyOnNavigationPreferenceAdded(navigationPreference);
                    oldNavigationPreferences.remove(navigationPreference);
                }
            }
        }

        for (NavigationPreference removedNavigationPreference : oldNavigationPreferences) {
            notifyOnNavigationPreferenceRemoved(removedNavigationPreference);
        }

        if (!selectedNavigationPreferenceFound) {
            if (getNavigationPreferenceCount() > 0) {
                selectNavigationPreference(Math.min(selectedNavigationPreferenceIndex,
                        getNavigationPreferenceCount() - 1), null);
            } else {
                selectNavigationPreference(null, null);
            }
        }
    }

    /**
     * Notifies the callback, that a navigation preference is about to be selected.
     *
     * @param navigationPreference
     *         The navigation preference, which is about to be selected, as an instance of the class
     *         {@link NavigationPreference}. The navigation preference may not be null
     * @return True, if the navigation preference should be selected, false otherise
     */
    private boolean notifyOnSelectNavigationPreference(
            @NonNull final NavigationPreference navigationPreference) {
        return callback == null || callback.onSelectNavigationPreference(navigationPreference);
    }

    /**
     * Notifies te callback, that a navigation preference has been selected.
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
        if (callback != null) {
            callback.onNavigationPreferenceSelected(navigationPreference, arguments);
        }
    }

    /**
     * Notifies the callback, that a navigation preference has been unselected.
     */
    private void notifyOnNavigationPreferenceUnselected() {
        if (callback != null) {
            callback.onNavigationPreferenceUnselected();
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
        if (callback != null) {
            callback.onNavigationPreferenceAdded(navigationPreference);
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
        if (callback != null) {
            callback.onNavigationPreferenceRemoved(navigationPreference);
        }
    }

    /**
     * Creates and returns a data observer, which allows to adapt the navigation preferences, when
     * the adapter's underlying data has been changed.
     *
     * @return The data observer, which has been created, as an instance of the class {@link
     * RecyclerView.AdapterDataObserver}. The data observer may not be null
     */
    @NonNull
    private RecyclerView.AdapterDataObserver createAdapterDataObserver() {
        return new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                updateNavigationPreferences();
            }

            @Override
            public void onItemRangeChanged(final int positionStart, final int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                updateNavigationPreferences();
            }

            @Override
            public void onItemRangeChanged(final int positionStart, final int itemCount,
                                           @Nullable final Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                updateNavigationPreferences();
            }

            @Override
            public void onItemRangeInserted(final int positionStart, final int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateNavigationPreferences();
            }

            @Override
            public void onItemRangeRemoved(final int positionStart, final int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                updateNavigationPreferences();
            }

            @Override
            public void onItemRangeMoved(final int fromPosition, final int toPosition,
                                         final int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                updateNavigationPreferences();
            }

        };
    }

    /**
     * Creates a new {@link PreferenceAdapter}, which is used to visualize the navigation
     * preferences of a {@link PreferenceActivity}.
     *
     * @param preferenceScreen
     *         The preference screen, which contains the preferences, which should be managed by the
     *         adapter, as an instance of the class PreferenceScreen. The preference screen may not
     *         be null
     * @param callback
     *         The callback, which should be notified about the adapter's events, as an instance of
     *         the type {@link Callback} or null, if no callback should be notified
     */
    public NavigationPreferenceAdapter(@NonNull final PreferenceScreen preferenceScreen,
                                       @Nullable final Callback callback) {
        super(preferenceScreen);
        this.callback = callback;
        this.navigationPreferences = new ArrayList<>();
        this.selectedNavigationPreference = null;
        this.selectedNavigationPreferenceIndex = -1;
        this.enabled = true;
        registerAdapterDataObserver(createAdapterDataObserver());
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
     * Returns a list, which contains all navigation preferences, which are contained by the
     * adapter.
     *
     * @return A list, which contains all navigation preferences, which are contained by the
     * adapter, as an instance of the type {@link List} or an empty collection, if no navigation
     * preferences are contained by the adapter
     */
    @NonNull
    public final List<NavigationPreference> getAllNavigationPreferences() {
        return navigationPreferences;
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
     * Returns the navigation preference, which corresponds to a specific index.
     *
     * @param index
     *         The index of the navigation preference, which should be returned, among all
     *         navigation preferences as an {@link Integer} value
     * @return The navigation preference, which corresponds to the given index, as an instance of
     * the class {@link NavigationPreference}. The navigation preference may not be null
     */
    @NonNull
    public final NavigationPreference getNavigationPreference(final int index) {
        return navigationPreferences.get(index);
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
     * @param arguments
     *         The arguments, which should be passed to the fragment, which is associated with the
     *         navigation preference, as an instance of the class {@link Bundle} or null, if no
     *         arguments should be passed to the fragment
     */
    public final void selectNavigationPreference(
            @Nullable final NavigationPreference navigationPreference,
            @Nullable final Bundle arguments) {
        selectNavigationPreference(navigationPreference == null ? -1 :
                indexOfNavigationPreference(navigationPreference), arguments);
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
        NavigationPreference navigationPreference =
                index == -1 ? null : navigationPreferences.get(index);

        if (selectedNavigationPreference != navigationPreference) {
            if (navigationPreference != null &&
                    notifyOnSelectNavigationPreference(navigationPreference)) {
                selectedNavigationPreference = navigationPreference;
                selectedNavigationPreferenceIndex = index;
            } else {
                selectedNavigationPreference = null;
                selectedNavigationPreferenceIndex = -1;
            }

            if (navigationPreference != null) {
                notifyOnNavigationPreferenceSelected(navigationPreference, arguments);
            } else {
                notifyOnNavigationPreferenceUnselected();
            }

            super.notifyDataSetChanged();
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
        super.notifyDataSetChanged();
    }

    /**
     * Sets, whether the items of the adapter are enabled, i.e. clickable, or not.
     *
     * @param enabled
     *         True, if the items of the adapter should be enabled, false otherwise
     */
    public final void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public final void onShowFragment(@NonNull final NavigationPreference navigationPreference) {
        if (enabled) {
            selectNavigationPreference(navigationPreference, null);
        }
    }

    @Override
    protected final void onVisualizePreference(@NonNull final Preference preference,
                                               @NonNull final PreferenceViewHolder viewHolder) {
        super.onVisualizePreference(preference, viewHolder);

        if (preference instanceof NavigationPreference) {
            NavigationPreference navigationPreference = (NavigationPreference) preference;
            navigationPreference.setCallback(this);
            boolean selected = selectedNavigationPreference == navigationPreference;
            ViewUtil.setBackground(viewHolder.itemView,
                    selected ? new ColorDrawable(selectionColor) : ThemeUtil
                            .getDrawable(preference.getContext(), R.attr.selectableItemBackground));
        }
    }

}