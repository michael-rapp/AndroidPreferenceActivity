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

import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import de.mrapp.android.preference.activity.R;
import de.mrapp.android.preference.activity.view.PreferenceListView;
import de.mrapp.android.util.ThemeUtil;

/**
 * An abstract base class for all fragments, which show multiple preferences.
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
public abstract class AbstractPreferenceFragment extends android.preference.PreferenceFragment {

    /**
     * The list view, which is used to show the fragment's preferences.
     */
    private PreferenceListView listView;

    /**
     * The color of the dividers which are shown above preference categories.
     */
    private int dividerColor = -1;

    /**
     * Obtains all relevant attributes from the activity's current theme.
     */
    private void obtainStyledAttributes() {
        obtainDividerColor();
    }

    /**
     * Obtains the color of the dividers, which are shown above preference categories, from the
     * activity's theme.
     */
    private void obtainDividerColor() {
        int color;

        try {
            color = ThemeUtil.getColor(getActivity(), R.attr.dividerColor);
        } catch (NotFoundException e) {
            color = ContextCompat.getColor(getActivity(), R.color.preference_divider_color_light);
        }

        setDividerColor(color);
    }

    /**
     * Adapts the divider color of the list view, which is used to show the fragment's preferences.
     */
    private void adaptDividerColor() {
        if (listView != null) {
            listView.setDividerColor(dividerColor);
        }
    }

    /**
     * The method, which is invoked on implementing subclasses in order to inflate the fragment's
     * layout.
     *
     * @param inflater
     *         The layout inflater, which should be used, as an instance of the class {@link
     *         LayoutInflater}. The layout inflater may not be null
     * @param parent
     *         The parent of the view, which should be inflated, as an instance of the class {@link
     *         ViewGroup} or null, if no parent is available
     * @param savedInstanceState
     *         The saved instance state of the fragment as a {@link Bundle} or null, if no saved
     *         instance state is available
     * @return The view, which has been inflated, as an instance of the class {@link View}. The view
     * may not be null
     */
    @NonNull
    protected abstract View onInflateView(@NonNull final LayoutInflater inflater,
                                          @Nullable final ViewGroup parent,
                                          @Nullable final Bundle savedInstanceState);

    /**
     * Returns the list view, which is used to show the fragment's preferences.
     *
     * @return The list view, which is used to show the fragment's preferences, as an instance of
     * the class {@link ListView} or null, if the fragment has not been created yet
     */
    public final ListView getListView() {
        return listView;
    }

    /**
     * Returns the color of the dividers, which are shown above preference categories.
     *
     * @return The color of the dividers, which are shown above preference categories, as an {@link
     * Integer} value or -1, if the default color is used
     */
    @ColorInt
    public final int getDividerColor() {
        return dividerColor;
    }

    /**
     * Sets the color of the dividers, which are shown above preference categories.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value or -1, if the default
     *         color should be used
     */
    public final void setDividerColor(@ColorInt final int color) {
        this.dividerColor = color;
        adaptDividerColor();
    }

    @CallSuper
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainStyledAttributes();
    }

    @CallSuper
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent,
                             final Bundle savedInstanceState) {
        View view = onInflateView(inflater, parent, savedInstanceState);

        if (view instanceof ListView && view.getId() == android.R.id.list) {
            listView = (PreferenceListView) view;
        } else {
            listView = view.findViewById(android.R.id.list);
        }

        adaptDividerColor();
        return view;
    }

}