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
import android.preference.Preference;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import de.mrapp.android.preference.activity.adapter.PreferenceGroupAdapter;

import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * A custom {@link ListView}, whose adapter is wrapped in order to display dividers above preference
 * categories.
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
public class PreferenceListView extends ListView {

    /**
     * Defines the interface, a factory, which allows to create instances of the class {@link
     * PreferenceGroupAdapter}, must implement.
     */
    public interface AdapterFactory {

        /**
         * Creates and returns a {@link PreferenceGroupAdapter}.
         *
         * @param context
         *         The context, which should be used by the adapter, as an instance of the class
         *         {@link Context}. The context may not be null
         * @param encapsulatedAdapter
         *         The adapter, which should be encapsulated, as an instance of the type {@link
         *         ListAdapter}. The adapter may not be null
         * @return The adapter, which has been created, as an instance of the class {@link
         * PreferenceGroupAdapter}. The adapter may not be null
         */
        @NonNull
        PreferenceGroupAdapter createAdapter(@NonNull Context context,
                                             @NonNull ListAdapter encapsulatedAdapter);

    }

    /**
     * The color of the divider's which are shown above preference categories.
     */
    private int dividerColor;

    /**
     * The adapter factory, which is used to create the list view's adapter.
     */
    private AdapterFactory adapterFactory;

    /**
     * Initializes the view.
     */
    private void initialize() {
        setDividerColor(-1);
        setAdapterFactory(createDefaultAdapterFactory());
    }

    /**
     * Creates and returns the factory, which is used to create the list view's adapter by default.
     *
     * @return The factory, which has been created, as an instance of the type {@link
     * AdapterFactory}. The adapter factory may not be null
     */
    @NonNull
    private AdapterFactory createDefaultAdapterFactory() {
        return new AdapterFactory() {

            @NonNull
            @Override
            public PreferenceGroupAdapter createAdapter(@NonNull final Context context,
                                                        @NonNull final ListAdapter encapsulatedAdapter) {
                return new PreferenceGroupAdapter(context, encapsulatedAdapter);
            }

        };
    }

    /**
     * Creates and returns a listener, which forwards an item click to an encapsulated adapter using
     * the original item position.
     *
     * @param listener
     *         The listener, which should be encapsulated, as an instance of the type {@link
     *         OnItemClickListener}. The listener may not be null
     * @return The listener, which has been created, as an instance of the type {@link
     * OnItemClickListener}. The listener may not be null
     */
    @NonNull
    private OnItemClickListener createOnItemClickListener(
            @NonNull final OnItemClickListener listener) {
        return new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view,
                                    final int position, final long id) {
                PreferenceGroupAdapter adapter = (PreferenceGroupAdapter) getAdapter();
                Pair<Object, Integer> pair = adapter.getItemInternal(position);

                if (pair.first instanceof Preference) {
                    listener.onItemClick(adapterView, view, pair.second, id);
                }
            }

        };
    }

    /**
     * Creates a custom {@link ListView}, whose adapter is wrapped in order to display dividers
     * above preference categories.
     *
     * @param context
     *         The context, the toolbar should belong to, as an instance of the class {@link
     *         Context}. The context may not be null
     */
    public PreferenceListView(@NonNull final Context context) {
        this(context, null);
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
        initialize();
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
        initialize();
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
        initialize();
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

    /**
     * Sets the adapter factory, which should be used to create the list view's adapter.
     *
     * @param adapterFactory
     *         The adapter factory, which should be set, as an instance of the type {@link
     *         AdapterFactory}. The adapter factory may not be null
     */
    public final void setAdapterFactory(@NonNull final AdapterFactory adapterFactory) {
        ensureNotNull(adapterFactory, "The adapter factory may not be null");
        this.adapterFactory = adapterFactory;
    }

    @Override
    public final void setAdapter(@Nullable final ListAdapter adapter) {
        if (adapter != null) {
            PreferenceGroupAdapter preferenceGroupAdapter =
                    adapterFactory.createAdapter(getContext(), adapter);
            preferenceGroupAdapter.setDividerColor(dividerColor);
            super.setAdapter(preferenceGroupAdapter);
        } else {
            super.setAdapter(null);
        }
    }

    @Override
    public final void setOnItemClickListener(@Nullable final OnItemClickListener listener) {
        super.setOnItemClickListener(listener != null ? createOnItemClickListener(listener) : null);
    }

}