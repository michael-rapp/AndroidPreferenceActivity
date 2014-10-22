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

import java.util.LinkedHashSet;
import java.util.Set;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import static de.mrapp.android.preference.activity.util.Condition.ensureNotNull;

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
	 * The color of the separator, which is drawn between the preferences and
	 * the button, which allows to restore the preferences' default values.
	 */
	private int buttonBarSeparatorColor;

	/**
	 * A set, which contains the listeners, which should be notified, when the
	 * preferences' default values should be restored.
	 */
	private Set<DefaultValueListener> defaultValueListeners = new LinkedHashSet<DefaultValueListener>();

	/**
	 * Inflates the view group, which contains the button, which allows to
	 * restore the preferences' default values.
	 */
	private void inflateRestoreDefaultsButtonBar() {
		if (buttonBar == null) {
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
				if (notifyOnRestoreDefaultValues()) {
					restoreDefaults();
				}
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
	 * Restores the default preferences, which are contained by a specific
	 * preference group.
	 * 
	 * @param preferenceGroup
	 *            The preference group, whose preferences should be restored, as
	 *            an instance of the class {@link PreferenceGroup}
	 * @param sharedPreferences
	 *            The shared preferences, which should be used to restore the
	 *            preferences, as an instance of the type
	 *            {@link SharedPreferences}
	 */
	private void restoreDefaults(final PreferenceGroup preferenceGroup,
			final SharedPreferences sharedPreferences) {
		for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
			Preference preference = preferenceGroup.getPreference(i);

			if (preference instanceof PreferenceGroup) {
				restoreDefaults((PreferenceGroup) preference, sharedPreferences);
			} else if (preference.getKey() != null) {
				sharedPreferences.edit().remove(preference.getKey()).commit();
			}

			preferenceGroup.removePreference(preference);
			preferenceGroup.addPreference(preference);
		}
	}

	/**
	 * Notifies all registered listeners, that the preferences' default values
	 * should be restored.
	 * 
	 * @return True, if restoring the preferences' default values should be
	 *         proceeded, false otherwise
	 */
	private boolean notifyOnRestoreDefaultValues() {
		boolean result = true;

		for (DefaultValueListener listener : defaultValueListeners) {
			result &= listener.onRestoreDefaultValues(this);
		}

		return result;
	}

	/**
	 * Restores the default values of all preferences, which are contained by
	 * the fragment.
	 */
	public final void restoreDefaults() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		restoreDefaults(getPreferenceScreen(), sharedPreferences);
	}

	/**
	 * Adds a new listener, which should be notified, when the preferences'
	 * default values should be restored, to the fragment.
	 * 
	 * @param listener
	 *            The listener, which should be added as an instance of the type
	 *            {@link DefaultValueListener}. The listener may not be null
	 */
	public final void addDefaultValueListener(
			final DefaultValueListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		this.defaultValueListeners.add(listener);
	}

	/**
	 * Removes a specific listener, which should not be notified anymore, when
	 * the preferences' default values should be restored, from the fragment.
	 * 
	 * @param listener
	 *            The listener, which should be removed as an instance of the
	 *            type {@link DefaultValueListener}. The listener may not be
	 *            null
	 */
	public final void removeDefaultValueListener(
			final DefaultValueListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		this.defaultValueListeners.remove(listener);
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
	 * Returns the color of the separator, which is drawn between the
	 * preferences and the button, which allows to restore the default values.
	 * 
	 * @return The color of the separator as an {@link Integer} value or -1, if
	 *         the button is not shown
	 */
	public final int getButtonBarSeparatorColor() {
		if (getButtonBarSeparator() != null) {
			return buttonBarSeparatorColor;
		} else {
			return -1;
		}
	}

	/**
	 * Sets the color of the separator, which is drawn between the preferences
	 * and the button, which allows to restore the default values. The color is
	 * only set when the button is shown.
	 * 
	 * @param separatorColor
	 *            The color, which should be set as an {@link Integer} value
	 * @return True, if the color has been set, false otherwise
	 */
	public final boolean setButtonBarSeparatorColor(final int separatorColor) {
		if (getButtonBarSeparator() != null) {
			this.buttonBarSeparatorColor = separatorColor;
			getButtonBarSeparator().setBackgroundColor(separatorColor);
			return true;
		}

		return false;
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
		setButtonBarSeparatorColor(getResources().getColor(R.color.separator));
		return layout;
	}

}