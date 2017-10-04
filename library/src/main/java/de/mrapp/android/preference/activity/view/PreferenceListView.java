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
package de.mrapp.android.preference.activity.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

import de.mrapp.android.preference.activity.adapter.PreferenceGroupAdapter;

/**
 * A custom {@link ListView}, whose adapter is wrapped in order to display dividers above preference
 * categories.
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
public class PreferenceListView extends ListView {

    /**
     * The color of the divider's which are shown above preference categories.
     */
    private int dividerColor;

    /**
     * Creates a custom {@link ListView}, whose adapter is wrapped in order to display dividers
     * above preference categories.
     *
     * @param context
     *         The context, the toolbar should belong to, as an instance of the class {@link
     *         Context}. The context may not be null
     */
    public PreferenceListView(@NonNull final Context context) {
        super(context);
    }

    /**
     * Creates a custom {@link ListView}, whose adapter is wrapped in order to display dividers
     * above preference categories.
     *
     * @param context
     *         The context, the toolbar should belong to, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param attributeSet
     *         The attributes of the XML tag that is inflating the preference, as an instance of the
     *         type {@link AttributeSet} or null, if no attributes are available
     */
    public PreferenceListView(@NonNull final Context context,
                              @Nullable final AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * Creates a custom {@link ListView}, whose adapter is wrapped in order to display dividers
     * above preference categories.
     *
     * @param context
     *         The context, the toolbar should belong to, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param attributeSet
     *         The attributes of the XML tag that is inflating the preference, as an instance of the
     *         type {@link AttributeSet} or null, if no attributes are available
     * @param defaultStyle
     *         The default style to apply to this preference. If 0, no style will be applied (beyond
     *         what is included in the theme). This may either be an attribute resource, whose value
     *         will be retrieved from the current theme, or an explicit style resource
     */
    public PreferenceListView(@NonNull final Context context,
                              @Nullable final AttributeSet attributeSet,
                              @StyleRes final int defaultStyle) {
        super(context, attributeSet, defaultStyle);
    }

    /**
     * Creates a custom {@link ListView}, whose adapter is wrapped in order to display dividers
     * above preference categories.
     *
     * @param context
     *         The context, the toolbar should belong to, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param attributeSet
     *         The attributes of the XML tag that is inflating the preference, as an instance of the
     *         type {@link AttributeSet} or null, if no attributes are available
     * @param defaultStyle
     *         The default style to apply to this preference. If 0, no style will be applied (beyond
     *         what is included in the theme). This may either be an attribute resource, whose value
     *         will be retrieved from the current theme, or an explicit style resource
     * @param defaultStyleResource
     *         A resource identifier of a style resource that supplies default values for the view,
     *         used only if the default style is 0 or can not be found in the theme. Can be 0 to not
     *         look for defaults
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PreferenceListView(@NonNull final Context context,
                              @NonNull final AttributeSet attributeSet,
                              @StyleRes final int defaultStyle,
                              @AttrRes final int defaultStyleResource) {
        super(context, attributeSet, defaultStyle, defaultStyleResource);
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
        ListAdapter adapter = getAdapter();

        if (adapter instanceof PreferenceGroupAdapter) {
            ((PreferenceGroupAdapter) adapter).setDividerColor(color);
        }
    }

    @Override
    public final void setAdapter(@Nullable final ListAdapter adapter) {
        if (adapter != null) {
            PreferenceGroupAdapter preferenceGroupAdapter =
                    new PreferenceGroupAdapter(getContext(), adapter);
            preferenceGroupAdapter.setDividerColor(dividerColor);
            super.setAdapter(preferenceGroupAdapter);
        } else {
            super.setAdapter(null);
        }
    }

}