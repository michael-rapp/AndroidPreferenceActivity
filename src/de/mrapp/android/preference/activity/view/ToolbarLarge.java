/*
 * AndroidPreferenceActivity Copyright 2014 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>. 
 */
package de.mrapp.android.preference.activity.view;

import static de.mrapp.android.preference.activity.util.Condition.ensureAtLeast;
import static de.mrapp.android.preference.activity.util.Condition.ensureGreaterThan;
import static de.mrapp.android.preference.activity.util.DisplayUtil.convertDpToPixels;
import static de.mrapp.android.preference.activity.util.DisplayUtil.convertPixelsToDp;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.mrapp.android.preference.activity.R;

/**
 * A custom view, which is used to visualize a large toolbar on devices with a
 * large screen.
 * 
 * @author Michael Rapp
 *
 * @since 2.0.0
 */
public class ToolbarLarge extends FrameLayout {

	private View backgroundView;

	private TextView titleTextView;

	private TextView breadCrumbTextView;

	private View shadowView;

	private int shadowColor;

	private View overlayView;

	private void inflate() {
		inflate(getContext(), R.layout.toolbar_large, this);
		this.backgroundView = findViewById(R.id.toolbar_background_view);
		this.titleTextView = (TextView) findViewById(android.R.id.title);
		this.breadCrumbTextView = (TextView) findViewById(R.id.toolbar_bread_crumb_view);
		this.shadowView = findViewById(R.id.toolbar_shadow_view);
		this.overlayView = findViewById(R.id.toolbar_overlay_view);
	}

	private void obtainStyledAttributes(final Context context,
			final AttributeSet attributeSet) {
		TypedArray typedArray = context.obtainStyledAttributes(attributeSet,
				R.styleable.Toolbar);
		int theme = obtainTheme(typedArray);
		typedArray.recycle();

		if (theme != -1) {
			obtainBackgroundColor(theme);
			obtainTitleColor(theme);
		}
	}

	private int obtainTheme(TypedArray typedArray) {
		return typedArray.getResourceId(R.styleable.Toolbar_theme, -1);
	}

	private void obtainBackgroundColor(final int theme) {
		TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
				theme, new int[] { R.attr.colorPrimary });
		int colorPrimary = typedArray.getColor(0, -1);
		typedArray.recycle();

		if (colorPrimary != -1) {
			backgroundView.setBackgroundColor(colorPrimary);
		}
	}

	private void obtainTitleColor(final int theme) {
		TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
				theme, new int[] { android.R.attr.textColorPrimary });
		int textColorPrimary = typedArray.getResourceId(0, -1);
		typedArray.recycle();

		if (textColorPrimary != -1) {
			int titleColor = getContext().getResources().getColor(
					textColorPrimary);
			titleTextView.setTextColor(titleColor);
			breadCrumbTextView.setTextColor(titleColor);
		}
	}

	public ToolbarLarge(final Context context) {
		super(context);
		inflate();
	}

	public ToolbarLarge(final Context context, final AttributeSet attributeSet) {
		super(context, attributeSet);
		inflate();
		obtainStyledAttributes(context, attributeSet);
	}

	public ToolbarLarge(final Context context, final AttributeSet attributeSet,
			final int defaultStyle) {
		super(context, attributeSet, defaultStyle);
		inflate();
		obtainStyledAttributes(context, attributeSet);
	}

	public final CharSequence getTitle() {
		return titleTextView.getText();
	}

	public final void setTitle(final CharSequence title) {
		titleTextView.setText(title);
	}

	public final void setTitle(final int resourceId) {
		titleTextView.setText(resourceId);
	}

	public final CharSequence getBreadCrumbTitle() {
		return breadCrumbTextView.getText();
	}

	public final void setBreadCrumbTitle(final CharSequence title) {
		breadCrumbTextView.setText(title);
	}

	public final void setBreadCrumbTitle(final int resourceId) {
		breadCrumbTextView.setText(resourceId);
	}

	public final int getNavigationWidth() {
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) overlayView
				.getLayoutParams();
		return convertPixelsToDp(getContext(), layoutParams.leftMargin);
	}

	public final void setNavigationWidth(final int width) {
		ensureGreaterThan(width, 0, "The width must be greater than 0");
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) overlayView
				.getLayoutParams();
		int widthInPixels = convertDpToPixels(getContext(), width);
		layoutParams.leftMargin = widthInPixels;
		overlayView.requestLayout();
		int titleMaxWidth = widthInPixels
				- getContext().getResources().getDimensionPixelSize(
						R.dimen.toolbar_title_margin_left)
				- getContext().getResources().getDimensionPixelSize(
						R.dimen.list_view_item_padding);
		titleTextView.setMaxWidth(titleMaxWidth);
	}

	public final int getShadowColor() {
		return shadowColor;
	}

	@SuppressWarnings("deprecation")
	public final void setShadowColor(final int shadowColor) {
		this.shadowColor = shadowColor;
		GradientDrawable gradient = new GradientDrawable(
				Orientation.LEFT_RIGHT, new int[] { shadowColor,
						Color.TRANSPARENT });
		shadowView.setBackgroundDrawable(gradient);
	}

	public final int getShadowWidth() {
		return convertPixelsToDp(getContext(),
				shadowView.getLayoutParams().width);
	}

	public final void setShadowWidth(final int width) {
		ensureAtLeast(width, 0, "The width must be at least 0");
		shadowView.getLayoutParams().width = convertDpToPixels(getContext(),
				width);
		shadowView.requestLayout();
	}

}