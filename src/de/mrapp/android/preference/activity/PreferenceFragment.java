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
package de.mrapp.android.preference.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * A fragment, which allows to show multiple preferences. Additionally, a
 * button, which allows to restore the preferences' default values, can be
 * shown.
 * 
 * @author Michael Rapp
 * 
 * @since 1.1.0
 */
public class PreferenceFragment extends android.preference.PreferenceFragment {

	/**
	 * The layout, which contains the fragment's preferences as well as the
	 * button, which allows to restore the default values.
	 */
	private LinearLayout layout;

	/**
	 * The view group, which contains the button, which allows to restore the
	 * preferences' default values.
	 */
	private ViewGroup buttonBar;

	/**
	 * The view, which is used to draw a separator between the preferences and
	 * the button, which allows to restore the preferences' default values.
	 */
	private View buttonBarSeparator;

	/**
	 * The button, which allows to restore the preferences' default values.
	 */
	private Button restoreDefaultsButton;

	/**
	 * Inflates the view group, which contains the button, which allows to
	 * restore the preferences' default values.
	 */
	private void inflateRestoreDefaultsButtonBar() {
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();
		buttonBar = (ViewGroup) layoutInflater.inflate(
				R.layout.restore_defaults_button_bar, layout, false);
		buttonBarSeparator = buttonBar
				.findViewById(R.id.restore_defaults_button_bar_separator);
		restoreDefaultsButton = (Button) buttonBar
				.findViewById(R.id.restore_defaults_button);
		restoreDefaultsButton
				.setOnClickListener(createRestoreDefaultsListener());
	}

	/**
	 * Creates and returns a listener, which allows to restore the preferences'
	 * default values.
	 * 
	 * @return The listener, which has been created, as an instance of the type
	 *         {@link OnClickListener}
	 */
	private OnClickListener createRestoreDefaultsListener() {
		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub

			}

		};
	}

	/**
	 * Adds the view group, which contains the button, which allows to restore
	 * the preferences' default values, to the fragment.
	 */
	private void addRestoreDefaultsButtonBar() {
		if (layout != null && buttonBar != null) {
			layout.addView(buttonBar);
		}
	}

	/**
	 * Removes the view group, which contains the button, which allows to
	 * restore the preferences' default values, from the fragment.
	 */
	private void removeRestoreDefaultsButtonBar() {
		if (layout != null && buttonBar != null) {
			layout.removeView(buttonBar);
		}
	}

	/**
	 * Returns, whether the button, which allows to restore the preferences'
	 * default values, is currently shown, or not.
	 * 
	 * @return True, if the button, which allows to restore the preferences'
	 *         default values, is currently shown, false otherwise
	 */
	public final boolean isRestoreDefaultsButtonShown() {
		return restoreDefaultsButton != null;
	}

	/**
	 * Shows or hides the button, which allows to restore the preferences'
	 * default values.
	 * 
	 * @param show
	 *            True, if the button, which allows to restore the preferences'
	 *            default values, should be shown, false otherwise
	 */
	public final void showRestoreDefaultsButton(final boolean show) {
		if (show) {
			inflateRestoreDefaultsButtonBar();
			addRestoreDefaultsButtonBar();
		} else {
			removeRestoreDefaultsButtonBar();
			buttonBar = null;
			buttonBarSeparator = null;
			restoreDefaultsButton = null;
		}
	}

	/**
	 * Returns the view group, which contains the button, which allows to
	 * restore the preferences' default values.
	 * 
	 * @return The view group, which contains the button, which allows to
	 *         restore the preferences' default values, as an instance of the
	 *         class {@link ViewGroup} or null, if the button is not shown
	 */
	public final ViewGroup getButtonBar() {
		return buttonBar;
	}

	/**
	 * Returns the view, which is used to draw a separator between the
	 * preferences and the button, which allows to restore the preferences'
	 * default values.
	 * 
	 * @return The view, which is used to draw a separator between the
	 *         preferences and the button, which allows to restore the
	 *         preferences' default values, as an instance of the class
	 *         {@link View} or null, if the button is not shown
	 */
	public final View getButtonBarSeparator() {
		return buttonBarSeparator;
	}

	/**
	 * Returns the button, which allows to restore the preferences' default
	 * values.
	 * 
	 * @return The button, which allows to restore the preferences' default
	 *         values, as an instance of the class {@link Button} or null, if
	 *         the button is not shown
	 */
	public final Button getRestoreDefaultsButton() {
		return restoreDefaultsButton;
	}

	@Override
	public final View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		layout = (LinearLayout) super.onCreateView(inflater, container,
				savedInstanceState);
		addRestoreDefaultsButtonBar();
		return layout;
	}

}