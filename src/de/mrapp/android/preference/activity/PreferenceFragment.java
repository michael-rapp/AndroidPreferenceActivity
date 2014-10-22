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
import android.widget.RelativeLayout;

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
	 * The button, which allows to restore the preferences' default values.
	 */
	private Button restoreDefaultsButton;

	private void createRestoreDefaultsButton() {
		restoreDefaultsButton = new Button(getActivity());
		restoreDefaultsButton.setText(getResources().getString(
				R.string.restore_defaults_button_label));
		restoreDefaultsButton.setId(R.id.restore_defaults_button);
		restoreDefaultsButton
				.setOnClickListener(createRestoreDefaultsListener());
	}

	private OnClickListener createRestoreDefaultsListener() {
		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub

			}

		};
	}

	private void addRestoreDefaultsButton() {
		if (layout != null && restoreDefaultsButton != null) {
			RelativeLayout relativeLayout = new RelativeLayout(getActivity());
			RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			// if (Device.isTablet(getActivity())) {
			// restoreButtonLayoutParams
			// .addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			// } else {
			// restoreButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			// restoreButtonLayoutParams.setMargins(
			// RESTORE_BUTTON_MARGIN_SMARTPHONE,
			// RESTORE_BUTTON_MARGIN_SMARTPHONE,
			// RESTORE_BUTTON_MARGIN_SMARTPHONE,
			// RESTORE_BUTTON_MARGIN_SMARTPHONE);
			// }

			relativeLayout.addView(restoreDefaultsButton, buttonLayoutParams);
			layout.addView(relativeLayout, relativeLayoutParams);
		}
	}

	private void removeRestoreDefaultsButton() {
		if (layout != null && restoreDefaultsButton != null) {
			layout.removeView(restoreDefaultsButton);
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
			createRestoreDefaultsButton();
			addRestoreDefaultsButton();
		} else {
			removeRestoreDefaultsButton();
			restoreDefaultsButton = null;
		}
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
		addRestoreDefaultsButton();
		return layout;
	}

}