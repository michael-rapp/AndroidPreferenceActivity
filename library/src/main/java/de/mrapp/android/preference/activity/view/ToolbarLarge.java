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
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.mrapp.android.preference.activity.R;

import static de.mrapp.android.preference.activity.util.Condition.ensureAtLeast;
import static de.mrapp.android.preference.activity.util.Condition.ensureAtMaximum;
import static de.mrapp.android.preference.activity.util.Condition.ensureGreaterThan;
import static de.mrapp.android.preference.activity.util.DisplayUtil.convertDpToPixels;
import static de.mrapp.android.preference.activity.util.DisplayUtil.convertPixelsToDp;

/**
 * A custom view, which may be used to visualize a large toolbar on devices with a large screen.
 *
 * @author Michael Rapp
 * @since 2.0.0
 */
public class ToolbarLarge extends FrameLayout {

    /**
     * The default elevation of the navigation in dp.
     */
    private static final int DEFAULT_NAVIGATION_ELEVATION = 3;

    /**
     * The view, which is used to visualize the toolbar's background.
     */
    private View backgroundView;

    /**
     * The text view, which is used to show the toolbar's title.
     */
    private TextView titleTextView;

    /**
     * The text view, which is used to show the bread crumb title of the currently selected
     * preference header.
     */
    private TextView breadCrumbTextView;

    /**
     * The view, which is used to visualize the shadow of the navigation.
     */
    private View shadowView;

    /**
     * The view, which is used to overlay the toolbar's background.
     */
    private View overlayView;

    /**
     * The width of the navigation in dp.
     */
    private int navigationWidth;

    /**
     * The elevation of the navigation in dp.
     */
    private int navigationElevation;

    /**
     * Inflates the view's layout.
     */
    private void inflate() {
        inflate(getContext(), R.layout.toolbar_large, this);
        this.backgroundView = findViewById(R.id.toolbar_background_view);
        this.titleTextView = (TextView) findViewById(android.R.id.title);
        this.breadCrumbTextView = (TextView) findViewById(R.id.toolbar_bread_crumb_view);
        this.shadowView = findViewById(R.id.toolbar_shadow_view);
        this.overlayView = findViewById(R.id.toolbar_overlay_view);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) overlayView.getLayoutParams();
        this.navigationWidth = convertPixelsToDp(getContext(), layoutParams.leftMargin);
        setNavigationElevation(DEFAULT_NAVIGATION_ELEVATION);
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
            titleTextView.setTextColor(titleColor);
            breadCrumbTextView.setTextColor(titleColor);
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
        return titleTextView.getText();
    }

    /**
     * Sets the toolbar's title.
     *
     * @param title
     *         The title, which should be set, as an instance of the class {@link CharSequence} or
     *         null, if no title should be set
     */
    public final void setTitle(@Nullable final CharSequence title) {
        titleTextView.setText(title);
    }

    /**
     * Sets the toolbar's title.
     *
     * @param resourceId
     *         The resource id of the title, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setTitle(@StringRes final int resourceId) {
        titleTextView.setText(resourceId);
    }

    /**
     * Returns the bread crumb title of the currently selected preference header.
     *
     * @return The bread crumb title of the currently selected preference header as an instance of
     * the class {@link CharSequence} or null, if no bread crumb title is set
     */
    public final CharSequence getBreadCrumbTitle() {
        return breadCrumbTextView.getText();
    }

    /**
     * Sets the bread crumb title of the currently selected preference header.
     *
     * @param title
     *         The bread crumb title, which should be set, as an instance of the class {@link
     *         CharSequence} or null, of no bread crumb title should be set
     */
    public final void setBreadCrumbTitle(@Nullable final CharSequence title) {
        breadCrumbTextView.setText(title);
    }

    /**
     * Sets the bread crumb title of the currently selected preference header.
     *
     * @param resourceId
     *         The resource id of the bread crumb title, which should be set, as an {@link Integer}
     *         value. The resource id must correspond to a valid string resource
     */
    public final void setBreadCrumbTitle(@StringRes final int resourceId) {
        breadCrumbTextView.setText(resourceId);
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
        ensureGreaterThan(width, 0, "The width must be greater than 0");
        this.navigationWidth = width;
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) overlayView.getLayoutParams();
        int widthInPixels = convertDpToPixels(getContext(), width);
        layoutParams.leftMargin = widthInPixels;
        overlayView.requestLayout();
        int titleMaxWidth = widthInPixels - getContext().getResources()
                .getDimensionPixelSize(R.dimen.toolbar_title_margin_left) -
                getContext().getResources()
                        .getDimensionPixelSize(R.dimen.list_view_item_horizontal_padding);
        titleTextView.setMaxWidth(titleMaxWidth);
    }

    /**
     * Returns, whether the navigation is hidden, or not.
     *
     * @return True, if the navigation is hidden, false otherwise
     */
    public final boolean isNavigationHidden() {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) overlayView.getLayoutParams();
        return layoutParams.leftMargin == 0;
    }

    /**
     * Hides or shows the navigation.
     *
     * @param navigationHidden
     *         True, if the navigation should be hidden, false otherwise
     */
    @SuppressWarnings("deprecation")
    public final void hideNavigation(final boolean navigationHidden) {
        shadowView.setVisibility(navigationHidden ? View.GONE : View.VISIBLE);
        titleTextView.setVisibility(navigationHidden ? View.INVISIBLE : View.VISIBLE);
        float breadCrumbTextSize = getResources().getDimension(
                navigationHidden ? R.dimen.toolbar_title_text_size : R.dimen.bread_crumb_text_size);
        breadCrumbTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, breadCrumbTextSize);
        overlayView.setBackgroundColor(
                navigationHidden ? Color.TRANSPARENT : getResources().getColor(R.color.overlay));

        if (navigationHidden) {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) overlayView.getLayoutParams();
            layoutParams.leftMargin = 0;
            overlayView.requestLayout();
        } else {
            setNavigationWidth(navigationWidth);
        }
    }

    /**
     * Returns the elevation of the navigation.
     *
     * @return The elevation of the navigation in dp as an {@link Integer} value
     */
    public final int getNavigationElevation() {
        return navigationElevation;
    }

    /**
     * Sets the elevation of the navigation.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 1 and at maximum 5
     */
    @SuppressWarnings("deprecation")
    public final void setNavigationElevation(final int elevation) {
        String[] shadowColors =
                getResources().getStringArray(R.array.navigation_elevation_shadow_colors);
        String[] shadowWidths =
                getResources().getStringArray(R.array.navigation_elevation_shadow_widths);
        ensureAtLeast(elevation, 1, "The elevation must be at least 1");
        ensureAtMaximum(elevation, shadowWidths.length,
                "The elevation must be at maximum " + shadowWidths.length);

        this.navigationElevation = elevation;
        int shadowColor = Color.parseColor(shadowColors[elevation - 1]);
        int shadowWidth =
                convertDpToPixels(getContext(), Integer.valueOf(shadowWidths[elevation - 1]));

        GradientDrawable gradient = new GradientDrawable(Orientation.LEFT_RIGHT,
                new int[]{shadowColor, Color.TRANSPARENT});
        shadowView.setBackgroundDrawable(gradient);
        shadowView.getLayoutParams().width = shadowWidth;
        shadowView.requestLayout();
    }

}