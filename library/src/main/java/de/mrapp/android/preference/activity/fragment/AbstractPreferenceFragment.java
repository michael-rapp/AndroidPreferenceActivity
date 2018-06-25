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

import android.annotation.SuppressLint;
import android.content.res.Resources.NotFoundException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mrapp.android.preference.activity.R;
import de.mrapp.android.preference.activity.adapter.PreferenceAdapter;
import de.mrapp.android.util.DisplayUtil;
import de.mrapp.android.util.ThemeUtil;

/**
 * An abstract base class for all fragments, which show multiple preferences.
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
public abstract class AbstractPreferenceFragment extends PreferenceFragmentCompat {

    /**
     * An item decoration, which draws dividers above preference categories.
     */
    private class DividerDecoration extends RecyclerView.ItemDecoration {

        /**
         * The paint, which is used to draw dividers.
         */
        private final Paint paint = new Paint();

        /**
         * The height of a divider in pixels.
         */
        private int dividerHeight = 0;

        /**
         * The color of a divider.
         */
        private int dividerColor = Color.TRANSPARENT;

        /**
         * Invalidates all item decorations.
         */
        private void invalidateItemDecorations() {
            RecyclerView recyclerView = getListView();

            if (recyclerView != null) {
                recyclerView.invalidateItemDecorations();
            }
        }

        /**
         * Returns, whether a divider should be drawn above a view, or not.
         *
         * @param view
         *         The view, which should be checked, as an instance of the class {@link View}. The
         *         view may not be null
         * @param recyclerView
         *         The recycler view, the given view belongs to, as an instance of the class {@link
         *         RecyclerView}. The recycler view may not be null
         * @return True, if a divider should be drawn above the given view, false otherwise
         */
        private boolean shouldDrawDividerAbove(@NonNull final View view,
                                               @NonNull final RecyclerView recyclerView) {
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            int position = viewHolder.getAdapterPosition();

            if (position > 0) {
                @SuppressLint("RestrictedApi") Preference preference = adapter.getItem(position);
                return preference instanceof PreferenceGroup;
            }

            return false;
        }

        @Override
        public void onDrawOver(final Canvas canvas, final RecyclerView parent,
                               final RecyclerView.State state) {
            paint.setColor(dividerColor);
            int parentWidth = parent.getWidth();

            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);

                if (shouldDrawDividerAbove(view, parent)) {
                    float top = view.getY();
                    canvas.drawRect(0, top, parentWidth, top + dividerHeight, paint);
                }
            }
        }

        @Override
        public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
                                   final RecyclerView.State state) {
            if (shouldDrawDividerAbove(view, parent)) {
                outRect.bottom = dividerHeight;
            }
        }

        /**
         * Returns the divider color.
         *
         * @return The divider color as an {@link Integer} value
         */
        @ColorInt
        public int getDividerColor() {
            return dividerColor;
        }

        /**
         * Sets the divider color.
         *
         * @param dividerColor
         *         The color, which should be set, as an {@link Integer} value
         */
        public void setDividerColor(@ColorInt final int dividerColor) {
            this.dividerColor = dividerColor;
            invalidateItemDecorations();
        }

        /**
         * Returns the divider height.
         *
         * @return The divider height in pixels as an {@link Integer} value
         */
        @Px
        public int getDividerHeight() {
            return dividerHeight;
        }

        /**
         * Sets the divider height.
         *
         * @param dividerHeight
         *         The height, which should be set, as an {@link Integer} value
         */
        public void setDividerHeight(@Px final int dividerHeight) {
            this.dividerHeight = dividerHeight;
            invalidateItemDecorations();
        }

    }

    /**
     * The item decoration, which is used to draw dividers above preference categories.
     */
    private final DividerDecoration dividerDecoration = new DividerDecoration();

    /**
     * The adapter, which is used to manage the fragment's preferences.
     */
    private PreferenceAdapter adapter;

    /**
     * Obtains all relevant attributes from the activity's current theme.
     */
    private void obtainStyledAttributes() {
        obtainDividerDecoration();
    }

    /**
     * Obtains the appearance of the dividers, which are shown above preference categories, from the
     * activity's theme.
     */
    private void obtainDividerDecoration() {
        int dividerColor;

        try {
            dividerColor = ThemeUtil.getColor(getActivity(), R.attr.dividerColor);
        } catch (NotFoundException e) {
            dividerColor =
                    ContextCompat.getColor(getActivity(), R.color.preference_divider_color_light);
        }

        this.dividerDecoration.setDividerColor(dividerColor);
        this.dividerDecoration.setDividerHeight(DisplayUtil.dpToPixels(getActivity(), 1));
    }

    /**
     * Returns the color of the dividers, which are shown above preference categories.
     *
     * @return The color of the dividers, which are shown above preference categories, as an {@link
     * Integer} value
     */
    @ColorInt
    public final int getDividerColor() {
        return dividerDecoration.getDividerColor();
    }

    /**
     * Sets the color of the dividers, which are shown above preference categories.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value
     */
    public final void setDividerColor(@ColorInt final int color) {
        this.dividerDecoration.setDividerColor(color);
    }

    /**
     * The method, which is invoked in order to create the adapter, which is used to manage the
     * fragment's preferences. This method may be overridden by subclasses in order to use custom
     * adapters.
     *
     * @param preferenceScreen
     *         The preference screen, which contains the preferences, which should be managed by the
     *         adapter, as an instance of the class PreferenceScreen. The preference screen may not
     *         be null
     * @return The adapter, which has been created, as an instance of the class {@link
     * PreferenceAdapter}. The adapter may not be null
     */
    @NonNull
    protected PreferenceAdapter onCreatePreferenceAdapter(
            @NonNull final PreferenceScreen preferenceScreen) {
        return new PreferenceAdapter(preferenceScreen);
    }

    @CallSuper
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainStyledAttributes();
    }

    @CallSuper
    @NonNull
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent,
                             final Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);
        RecyclerView recyclerView = getListView();

        for (int i = recyclerView.getItemDecorationCount() - 1; i >= 0; i--) {
            recyclerView.removeItemDecorationAt(i);
        }

        recyclerView.addItemDecoration(dividerDecoration);
        return view;
    }

    @NonNull
    @Override
    protected final RecyclerView.Adapter<?> onCreateAdapter(
            final PreferenceScreen preferenceScreen) {
        if (adapter == null) {
            adapter = onCreatePreferenceAdapter(preferenceScreen);
        }

        return adapter;
    }

}