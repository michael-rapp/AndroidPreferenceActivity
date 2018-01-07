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
package de.mrapp.android.preference.activity.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import de.mrapp.android.preference.activity.R;

import static de.mrapp.android.util.Condition.ensureGreater;

/**
 * A custom view, which may be used to visualize a large toolbar on devices with a large screen.
 *
 * @author Michael Rapp
 * @since 2.0.0
 */
public class ToolbarLarge extends FrameLayout {

    /**
     * The view, which is used to visualize the toolbar's background.
     */
    private View backgroundView;

    /**
     * The toolbar, which is used to show the title.
     */
    private Toolbar toolbar;

    /**
     * The width of the navigation in pixels.
     */
    private int navigationWidth;

    /**
     * Inflates the view's layout.
     */
    private void inflate() {
        inflate(getContext(), R.layout.toolbar_large, this);
        this.backgroundView = findViewById(R.id.toolbar_background_view);
        this.toolbar = findViewById(R.id.navigation_toolbar);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        this.navigationWidth = layoutParams.width;
    }

    /**
     * Obtains all attributes from a specific attribute set.
     */
    private void obtainStyledAttributes() {
        int theme = obtainTheme();

        if (theme != 0) {
            obtainBackgroundColor(theme);
            obtainTitleColor(theme);
        }
    }

    /**
     * Obtains the resource id of the theme, which should be applied on the toolbar.
     *
     * @return The resource id of the theme as an {@link Integer} value
     */
    private int obtainTheme() {
        TypedArray typedArray =
                getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.toolbarTheme});
        return typedArray.getResourceId(0, 0);
    }

    /**
     * Obtains the color of the toolbar's background from a specific typed array.
     *
     * @param theme
     *         The resource id of the theme, which should be applied on the toolbar, as an {@link
     *         Integer} value
     */
    private void obtainBackgroundColor(final int theme) {
        TypedArray typedArray = getContext().getTheme()
                .obtainStyledAttributes(theme, new int[]{R.attr.colorPrimary});
        int colorPrimary = typedArray.getColor(0, 0);
        typedArray.recycle();

        if (colorPrimary != 0) {
            backgroundView.setBackgroundColor(colorPrimary);
        }
    }

    /**
     * Obtains the color of the toolbar's title from a specific typed array.
     *
     * @param theme
     *         The resource id of the theme, which should be applied on the toolbar, as an {@link
     *         Integer} value
     */
    private void obtainTitleColor(final int theme) {
        TypedArray typedArray = getContext().getTheme()
                .obtainStyledAttributes(theme, new int[]{android.R.attr.textColorPrimary});
        int textColorPrimary = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        if (textColorPrimary != 0) {
            int titleColor = ContextCompat.getColor(getContext(), textColorPrimary);
            toolbar.setTitleTextColor(titleColor);
        }
    }

    /**
     * Creates a custom view, which may be used to visualize a large toolbar on devices with a large
     * screen.
     *
     * @param context
     *         The context, the toolbar should belong to, as an instance of the class {@link
     *         Context}. The context may not be null
     */
    public ToolbarLarge(@NonNull final Context context) {
        super(context);
        inflate();
    }

    /**
     * Creates a custom view, which may be used to visualize a large toolbar on devices with a large
     * screen.
     *
     * @param context
     *         The context, the toolbar should belong to, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param attributeSet
     *         The attributes of the XML tag that is inflating the preference, as an instance of the
     *         type {@link AttributeSet} or ull, if no attributes are available
     */
    public ToolbarLarge(@NonNull final Context context, @Nullable final AttributeSet attributeSet) {
        super(context, attributeSet);
        inflate();
        obtainStyledAttributes();
    }

    /**
     * Creates a custom view, which may be used to visualize a large toolbar on devices with a large
     * screen.
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
    public ToolbarLarge(@NonNull final Context context, @Nullable final AttributeSet attributeSet,
                        final int defaultStyle) {
        super(context, attributeSet, defaultStyle);
        inflate();
        obtainStyledAttributes();
    }

    /**
     * Returns the toolbar's title.
     *
     * @return The toolbar's title as an instance of the class {@link CharSequence} or null, if no
     * title is set
     */
    public final CharSequence getTitle() {
        return toolbar.getTitle();
    }

    /**
     * Sets the toolbar's title.
     *
     * @param title
     *         The title, which should be set, as an instance of the class {@link CharSequence} or
     *         null, if no title should be set
     */
    public final void setTitle(@Nullable final CharSequence title) {
        toolbar.setTitle(title);
    }

    /**
     * Sets the toolbar's title.
     *
     * @param resourceId
     *         The resource id of the title, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setTitle(@StringRes final int resourceId) {
        toolbar.setTitle(resourceId);
    }

    /**
     * Returns the width of the navigation.
     *
     * @return The width of the navigation in pixels as an {@link Integer} value
     */
    @Px
    public final int getNavigationWidth() {
        return navigationWidth;
    }

    /**
     * Sets the width of the navigation.
     *
     * @param width
     *         The width, which should be set, in pixels as an {@link Integer} value. The width must
     *         be greater than 0
     */
    public final void setNavigationWidth(@Px final int width) {
        ensureGreater(width, 0, "The width must be greater than 0");
        this.navigationWidth = width;

        if (!isNavigationHidden()) {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
            layoutParams.width = width;
            toolbar.requestLayout();
        }
    }

    /**
     * Returns, whether the navigation is hidden, or not.
     *
     * @return True, if the navigation is hidden, false otherwise
     */
    public final boolean isNavigationHidden() {
        return toolbar.getVisibility() != View.VISIBLE;
    }

    /**
     * Hides or shows the navigation.
     *
     * @param navigationHidden
     *         True, if the navigation should be hidden, false otherwise
     */
    public final void hideNavigation(final boolean navigationHidden) {
        toolbar.setVisibility(navigationHidden ? View.INVISIBLE : View.VISIBLE);

        if (!navigationHidden) {
            setNavigationWidth(navigationWidth);
        }
    }

    /**
     * Returns the toolbar, which is used to show the title.
     *
     * @return The toolbar, which is used to show the title, as an instance of the class Toolbar
     */
    public final Toolbar getToolbar() {
        return toolbar;
    }

}