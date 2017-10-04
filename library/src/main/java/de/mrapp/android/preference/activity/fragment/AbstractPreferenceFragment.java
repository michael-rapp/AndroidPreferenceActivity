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

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.annotation.XmlRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import de.mrapp.android.preference.activity.R;
import de.mrapp.android.preference.activity.decorator.PreferenceDecorator;
import de.mrapp.android.preference.activity.view.PreferenceListView;

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
    private int dividerColor;

    /**
     * Obtains all relevant attributes from the activity's current theme.
     */
    private void obtainStyledAttributes() {
        int themeResourceId = obtainThemeResourceId();

        if (themeResourceId != -1) {
            onObtainStyledAttributes(themeResourceId);
        }
    }

    /**
     * Obtains the resource id of the activity's current theme.
     *
     * @return The resource id of the activity's current theme as an {@link Integer} value or 0, if
     * an error occurred while obtaining the theme
     */
    private int obtainThemeResourceId() {
        try {
            String packageName = getActivity().getClass().getPackage().getName();
            PackageInfo packageInfo = getActivity().getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return packageInfo.applicationInfo.theme;
        } catch (NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * Obtains the color of the dividers, which are shown above preference categories, from a
     * specific theme.
     *
     * @param themeResourceId
     *         The resource id of the theme, the color should be obtained from, as an {@link
     *         Integer} value
     */
    private void obtainDividerColor(@StyleRes final int themeResourceId) {
        TypedArray typedArray = getActivity().getTheme()
                .obtainStyledAttributes(themeResourceId, new int[]{R.attr.dividerColor});
        setDividerColor(typedArray.getColor(0, -1));
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
     * Applies Material style to all preferences, which are contained by the fragment.
     */
    private void applyMaterialStyle() {
        PreferenceDecorator decorator = new PreferenceDecorator(getActivity());

        if (getPreferenceScreen() != null) {
            applyMaterialStyle(getPreferenceScreen(), decorator);
        }
    }

    /**
     * Applies Material style to all preferences, which are contained by a specific preference
     * group, and on the group itself.
     *
     * @param preferenceGroup
     *         The preference group, at whose preferences the Material style should be applied to,
     *         as an instance of the class {@link PreferenceGroup}. The preference group may not be
     *         null
     * @param decorator
     *         The decorator, which should be used to apply the Material style, as an instance of
     *         the class {@link PreferenceDecorator}. The decorator may not be null
     */
    private void applyMaterialStyle(@NonNull final PreferenceGroup preferenceGroup,
                                    @NonNull final PreferenceDecorator decorator) {
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);
            decorator.applyDecorator(preference);

            if (preference instanceof PreferenceGroup) {
                PreferenceGroup group = (PreferenceGroup) preference;
                applyMaterialStyle(group, decorator);
            }
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
     * The method, which is invoked in order to obtain relevant attributes from the activity's
     * current theme. This method may be overriden by subclasses in order to obtain additional
     * attributes.
     *
     * @param themeResourceId
     *         The resource id of the theme, the attributes should be obtained from, as an {@link
     *         Integer} value
     */
    @CallSuper
    protected void onObtainStyledAttributes(@StyleRes final int themeResourceId) {
        obtainDividerColor(themeResourceId);
    }

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

    @CallSuper
    @Override
    public void addPreferencesFromResource(@XmlRes final int resourceId) {
        super.addPreferencesFromResource(resourceId);
        applyMaterialStyle();
    }

    @CallSuper
    @Override
    public void addPreferencesFromIntent(final Intent intent) {
        super.addPreferencesFromIntent(intent);
        applyMaterialStyle();
    }

}