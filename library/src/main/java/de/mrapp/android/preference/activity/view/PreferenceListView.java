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
import android.database.DataSetObserver;
import android.os.Build;
import android.preference.PreferenceCategory;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import de.mrapp.android.preference.activity.R;

import static de.mrapp.android.util.Condition.ensureAtLeast;
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
     * A list adapter, which encapsulates another adapter in order to add items, which are
     * visualized as dividers, above preference categories.
     */
    private static class PreferenceGroupAdapterWrapper extends BaseAdapter {

        /**
         * The item, which represents a divider.
         */
        private static final Object DIVIDER = new Object();

        /**
         * The context, which is used by the adapter.
         */
        private final Context context;

        /**
         * The adapter, which is encapsulated by the adapter.
         */
        private final ListAdapter encapsulatedAdapter;

        /**
         * The color of the divider's which are shown above preference categories.
         */
        private int dividerColor;

        /**
         * Creates and returns a data set observer, which notifies the adapter, when the
         * encapsulated adapter's data set has been changed or invalidated.
         *
         * @return The data set observer, which has been created, as an instance of the class {@link
         * DataSetObserver}. The data set observer may not be null
         */
        @NonNull
        private DataSetObserver createDataSetObserver() {
            return new DataSetObserver() {

                @Override
                public void onChanged() {
                    super.onChanged();
                    PreferenceGroupAdapterWrapper.this.notifyDataSetChanged();
                }

                @Override
                public void onInvalidated() {
                    super.onInvalidated();
                    PreferenceGroupAdapterWrapper.this.notifyDataSetInvalidated();
                }

            };
        }

        /**
         * Returns a pair, which contains the item, which corresponds to the given position, as well
         * as the item's position in the encapsulated adapter, if the item is not a divider.
         *
         * @param position
         *         The position of the item, which should be returned, as an {@link Integer} value
         * @return A pair, which contains the item, which corresponds to the given position, as well
         * as the item's position in the encapsulated adapter, if the item is not a divider, as an
         * instance of the class {@link Pair}. The pair may not be null
         */
        @NonNull
        private Pair<Object, Integer> getItemInternal(final int position) {
            if (position == 0) {
                System.out.println("gotcha");
            }
            ensureAtLeast(position, 0, null, IndexOutOfBoundsException.class);
            Object item = null;
            int offset = 0;
            int i = 0;

            while (i <= position) {
                item = encapsulatedAdapter.getItem(i - offset);

                if (i > 0 && item instanceof PreferenceCategory) {
                    if (i == position) {
                        return Pair.create(DIVIDER, -1);
                    } else {
                        offset++;
                        i++;
                    }
                }

                i++;
            }

            ensureNotNull(item, null, IndexOutOfBoundsException.class);
            return Pair.create(item, i - 1 - offset);
        }

        /**
         * Adapts the background color of a divider.
         *
         * @param divider
         *         The divider, whose background color should be adapted, as an instance of the
         *         class {@link View}. The view may not be null
         */
        private void adaptDividerColor(@NonNull final View divider) {
            int color = dividerColor;

            if (color == -1) {
                color = ContextCompat.getColor(context, R.color.preference_divider_color_light);
            }

            divider.setBackgroundColor(color);
        }

        /**
         * Creates a new list adapter, which encapsulates another adapter in order to add items,
         * which are visualized as dividers, above preference categories.
         *
         * @param context
         *         The context, which should be used by the adapter, as an instance of the class
         *         {@link Context}. The context may not be null
         * @param encapsulatedAdapter
         *         The adapter, which should be encapsulated, as an instance of the type {@link
         *         ListAdapter}. The adapter may not be null
         */
        PreferenceGroupAdapterWrapper(@NonNull final Context context,
                                      @NonNull final ListAdapter encapsulatedAdapter) {
            ensureNotNull(context, "The context may not be null");
            ensureNotNull(encapsulatedAdapter, "The encapsulated adapter may not be null");
            this.context = context;
            this.encapsulatedAdapter = encapsulatedAdapter;
            encapsulatedAdapter.registerDataSetObserver(createDataSetObserver());
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
            notifyDataSetChanged();
        }

        @Override
        public final int getCount() {
            int encapsulatedAdapterCount = encapsulatedAdapter.getCount();
            int count = encapsulatedAdapterCount;

            for (int i = 0; i < encapsulatedAdapterCount; i++) {
                Object item = encapsulatedAdapter.getItem(i);

                if (i > 0 && item instanceof PreferenceCategory) {
                    count++;
                }
            }

            return count;
        }

        @Override
        public final Object getItem(final int position) {
            return getItemInternal(position).first;
        }

        @Override
        public final long getItemId(final int position) {
            return position;
        }

        @Override
        public final boolean hasStableIds() {
            return false;
        }

        @Override
        public final View getView(final int position, final View convertView,
                                  final ViewGroup parent) {
            Pair<Object, Integer> pair = getItemInternal(position);

            if (pair.first == DIVIDER) {
                View view = convertView;

                if (view == null) {
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    view = layoutInflater.inflate(R.layout.preference_divider, parent, false);
                }

                adaptDividerColor(view);
                return view;
            } else {
                return encapsulatedAdapter.getView(pair.second, convertView, parent);
            }
        }

        @Override
        public final boolean isEnabled(final int position) {
            Pair<Object, Integer> pair = getItemInternal(position);
            return pair.first != DIVIDER && encapsulatedAdapter.isEnabled(pair.second);
        }

        @Override
        public final int getItemViewType(final int position) {
            Pair<Object, Integer> pair = getItemInternal(position);
            return pair.first == DIVIDER ? IGNORE_ITEM_VIEW_TYPE :
                    encapsulatedAdapter.getItemViewType(pair.second);
        }

        @Override
        public final int getViewTypeCount() {
            return encapsulatedAdapter.getViewTypeCount();
        }

    }

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

        if (adapter instanceof PreferenceGroupAdapterWrapper) {
            ((PreferenceGroupAdapterWrapper) adapter).setDividerColor(color);
        }
    }

    @Override
    public final void setAdapter(@Nullable final ListAdapter adapter) {
        if (adapter != null) {
            PreferenceGroupAdapterWrapper adapterWrapper =
                    new PreferenceGroupAdapterWrapper(getContext(), adapter);
            adapterWrapper.setDividerColor(dividerColor);
            super.setAdapter(adapterWrapper);
        } else {
            super.setAdapter(null);
        }
    }

}