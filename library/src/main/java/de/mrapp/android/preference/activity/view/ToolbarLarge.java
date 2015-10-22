/*
 * AndroidPreferenceActivity Copyright 2014 - 2015 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package de.mrapp.android.preference.activity.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import de.mrapp.android.preference.activity.R;

import static de.mrapp.android.util.Condition.ensureGreater;
import static de.mrapp.android.util.DisplayUtil.dpToPixels;
import static de.mrapp.android.util.DisplayUtil.pixelsToDp;

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
     * The text view, which is used to show the toolbar's title.
     */
    private Toolbar preferenceHeaderToolbar;

    /**
     * The width of the navigation in dp.
     */
    private int navigationWidth;

    /**
     * Inflates the view's layout.
     */
    private void inflate() {
        inflate(getContext(), R.layout.toolbar_large, this);
        this.backgroundView = findViewById(R.id.toolbar_background_view);
        this.preferenceHeaderToolbar = (Toolbar) findViewById(R.id.preference_header_toolbar);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) preferenceHeaderToolbar.getLayoutParams();
        this.navigationWidth = pixelsToDp(getContext(), layoutParams.width);
    }

    /**
     * Obtains all attributes from a specific attribute set.
     *
     * @param attributeSet
     *         The attribute set, the attributes should be obtained from, as an instance of the type
     *         {@link AttributeSet} or null, if no attributes should be obtained
     */
    private void obtainStyledAttributes(@Nullable final AttributeSet attributeSet) {
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
    @SuppressWarnings("deprecation")
    private void obtainTitleColor(final int theme) {
        TypedArray typedArray = getContext().getTheme()
                .obtainStyledAttributes(theme, new int[]{android.R.attr.textColorPrimary});
        int textColorPrimary = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        if (textColorPrimary != 0) {
            int titleColor = getContext().getResources().getColor(textColorPrimary);
            preferenceHeaderToolbar.setTitleTextColor(titleColor);
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
        obtainStyledAttributes(attributeSet);
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
        obtainStyledAttributes(attributeSet);
    }

    /**
     * Returns the toolbar's title.
     *
     * @return The toolbar's title as an instance of the class {@link CharSequence} or null, if no
     * title is set
     */
    public final CharSequence getTitle() {
        return preferenceHeaderToolbar.getTitle();
    }

    /**
     * Sets the toolbar's title.
     *
     * @param title
     *         The title, which should be set, as an instance of the class {@link CharSequence} or
     *         null, if no title should be set
     */
    public final void setTitle(@Nullable final CharSequence title) {
        preferenceHeaderToolbar.setTitle(title);
    }

    /**
     * Sets the toolbar's title.
     *
     * @param resourceId
     *         The resource id of the title, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setTitle(@StringRes final int resourceId) {
        preferenceHeaderToolbar.setTitle(resourceId);
    }

    /**
     * Returns the width of the navigation.
     *
     * @return The width of the navigation in dp as an {@link Integer} value
     */
    public final int getNavigationWidth() {
        return navigationWidth;
    }

    /**
     * Sets the width of the navigation.
     *
     * @param width
     *         The width, which should be set, in dp as an {@link Integer} value. The width must be
     *         greater than 0
     */
    public final void setNavigationWidth(final int width) {
        ensureGreater(width, 0, "The width must be greater than 0");
        this.navigationWidth = width;
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) preferenceHeaderToolbar.getLayoutParams();
        layoutParams.width = dpToPixels(getContext(), width);
        preferenceHeaderToolbar.requestLayout();
    }

    /**
     * Returns, whether the navigation is hidden, or not.
     *
     * @return True, if the navigation is hidden, false otherwise
     */
    public final boolean isNavigationHidden() {
        return preferenceHeaderToolbar.getVisibility() != View.VISIBLE;
    }

    /**
     * Hides or shows the navigation.
     *
     * @param navigationHidden
     *         True, if the navigation should be hidden, false otherwise
     */
    @SuppressWarnings("deprecation")
    public final void hideNavigation(final boolean navigationHidden) {
        preferenceHeaderToolbar.setVisibility(navigationHidden ? View.INVISIBLE : View.VISIBLE);

        if (!navigationHidden) {
            setNavigationWidth(navigationWidth);
        }
    }

}