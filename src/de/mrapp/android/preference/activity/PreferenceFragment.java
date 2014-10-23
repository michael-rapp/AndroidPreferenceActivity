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

import static de.mrapp.android.preference.activity.util.Condition.ensureNotNull;

import java.util.LinkedHashSet;
import java.util.Set;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.text.TextUtils;
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
	 * When attaching this fragment to an activity, the passed bundle can
	 * contain this extra boolean to display the button, which allows to restore
	 * the preferences' default values.
	 */
	public static final String EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON = "extra_prefs_show_restore_defaults_button";

	/**
	 * When attaching this fragment to an activity and using
	 * <code>EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON</code>, this extra can also be
	 * specified to define, whether the default buttons of disabled preferences
	 * should also be restored, or not.
	 */
	public static final String EXTRA_RESTORE_DISABLED_PREFERENCES = "extra_prefs_restore_disabled_preferences";

	/**
	 * When attaching this fragment to an activity and using
	 * <code>EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON</code>, this extra can also be
	 * specified to supply a custom text for the button, which allows to restore
	 * the preferences' default values.
	 */
	public static final String EXTRA_RESTORE_DEFAULTS_BUTTON_TEXT = "extra_prefs_restore_defaults_button_text";

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
	 * True, if the default values of preferences, which are currently disabled,
	 * should also be restored when clicking the appropriate button.
	 */
	private boolean restoreDisabledPreferences;

	/**
	 * A set, which contains the listeners, which should be notified, when the
	 * preferences' default values should be restored.
	 */
	private Set<RestoreDefaultsListener> restoreDefaultsListeners = new LinkedHashSet<RestoreDefaultsListener>();

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
			} else if (preference.getKey() != null
					&& (!areDisabledPreferencesRestored() || preference
							.isEnabled())) {
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

		for (RestoreDefaultsListener listener : restoreDefaultsListeners) {
			result &= listener.onRestoreDefaultValues(this);
		}

		return result;
	}

	/**
	 * Handles the extra of the arguments, which have been passed to the
	 * fragment, that allows to show the button, which allows to restore the
	 * preferences' default values.
	 */
	private void handleShowRestoreDefaultsButtonArgument() {
		boolean showButton = getArguments().getBoolean(
				EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON, false);

		if (showButton) {
			showRestoreDefaultsButton(true);
		}
	}

	/**
	 * Handles the extra of the arguments, which have been passed to the
	 * fragment, that allows to specify, whether the default values of disabled
	 * preference should also be restored, or not.
	 */
	private void handleRestoreDisabledPreferencesArgument() {
		boolean restoreDisabled = getArguments().getBoolean(
				EXTRA_RESTORE_DISABLED_PREFERENCES, true);

		if (!restoreDisabled) {
			setRestoreDisabledPreferences(false);
		}
	}

	/**
	 * Handles the extra of the arguments, which have been passed to the
	 * fragment, that allows to specify a custom text for the button, which
	 * allows to restore the preferences' default values.
	 */
	private void handleRestoreDefaultsButtonTextArgument() {
		CharSequence buttonText = getCharSequenceFromArguments(EXTRA_RESTORE_DEFAULTS_BUTTON_TEXT);

		if (!TextUtils.isEmpty(buttonText)) {
			setRestoreDefaultsButtonText(buttonText);
		}
	}

	/**
	 * Returns the char sequence, which is specified by a specific extra of the
	 * arguments, which have been passed to the fragment. The char sequence can
	 * either be specified as a string or as a resource id.
	 * 
	 * @param name
	 *            The name of the extra, which specifies the char sequence, as a
	 *            {@link String}
	 * @return The char sequence, which is specified by the arguments, as an
	 *         instance of the class {@link CharSequence} or null, if the
	 *         arguments do not specify a char sequence with the given name
	 */
	private CharSequence getCharSequenceFromArguments(final String name) {
		CharSequence charSequence = getArguments().getCharSequence(name);

		if (charSequence == null) {
			int resourceId = getArguments().getInt(name, 0);

			if (resourceId != 0) {
				charSequence = getText(resourceId);
			}
		}

		return charSequence;
	}

	/**
	 * Restores the default values of all preferences, which are contained by
	 * the fragment.
	 */
	public final void restoreDefaults() {
		SharedPreferences sharedPreferences = getPreferenceManager()
				.getSharedPreferences();

		if (getPreferenceScreen() != null) {
			restoreDefaults(getPreferenceScreen(), sharedPreferences);
		}
	}

	/**
	 * Adds a new listener, which should be notified, when the preferences'
	 * default values should be restored, to the fragment.
	 * 
	 * @param listener
	 *            The listener, which should be added as an instance of the type
	 *            {@link RestoreDefaultsListener}. The listener may not be null
	 */
	public final void addRestoreDefaultsListener(
			final RestoreDefaultsListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		this.restoreDefaultsListeners.add(listener);
	}

	/**
	 * Removes a specific listener, which should not be notified anymore, when
	 * the preferences' default values should be restored, from the fragment.
	 * 
	 * @param listener
	 *            The listener, which should be removed as an instance of the
	 *            type {@link RestoreDefaultsListener}. The listener may not be
	 *            null
	 */
	public final void removeRestoreDefaultsListener(
			final RestoreDefaultsListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		this.restoreDefaultsListeners.remove(listener);
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
	 * Returns, whether the default values of preferences, which are currently
	 * disabled, are also restored when clicking the appropriate button.
	 * 
	 * @return True, if the default values of preferences, which are currently
	 *         disabled, are also restored when clicking the appropriate button,
	 *         false otherwise
	 */
	public final boolean areDisabledPreferencesRestored() {
		return restoreDisabledPreferences;
	}

	/**
	 * Sets, whether the default values of preferences, which are currently
	 * disabled, should also be restored when clicking the appropriate button.
	 * 
	 * @param restoreDisabledPreferences
	 *            True, if the default values of preferences, which are
	 *            currently disabled, should also be restored when clicking the
	 *            appropriate button, false otherwise
	 */
	public final void setRestoreDisabledPreferences(
			final boolean restoreDisabledPreferences) {
		this.restoreDisabledPreferences = restoreDisabledPreferences;
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

	/**
	 * Returns the text of the button, which allows to restore the preferences'
	 * default values.
	 * 
	 * @return The text of the button, which allows to restore the preferences'
	 *         default values, as an instance of the class {@link CharSequence}
	 *         or null, if the button is not shown
	 */
	public final CharSequence getRestoreDefaultsButtonText() {
		if (restoreDefaultsButton != null) {
			return restoreDefaultsButton.getText();
		}

		return null;
	}

	/**
	 * Sets the text of the button, which allows to restore the preferences'
	 * default values. The text is only set, if the button is shown.
	 * 
	 * @param text
	 *            The text, which should be set, as an instance of the class
	 *            {@link CharSequence}. The text may not be null
	 * @return True, if the text has been set, false otherwise
	 */
	public final boolean setRestoreDefaultsButtonText(final CharSequence text) {
		ensureNotNull(text, "The text may not be null");

		if (restoreDefaultsButton != null) {
			restoreDefaultsButton.setText(text);
			return true;
		}

		return false;
	}

	/**
	 * Sets the text of the button, which allows to restore the preferences'
	 * default values. The text is only set, if the button is shown.
	 * 
	 * @param resourceId
	 *            The resource id of the text, which should be set, as an
	 *            {@link Integer} value. The resource id must correspond to a
	 *            valid string resource
	 * @return True, if the text has been set, false otherwise
	 */
	public final boolean setRestoreDefaultsButtonText(final int resourceId) {
		return setRestoreDefaultsButtonText(getText(resourceId));
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.buttonBarSeparatorColor = getResources().getColor(
				R.color.separator);
		setRestoreDisabledPreferences(true);

		if (getArguments() != null) {
			handleShowRestoreDefaultsButtonArgument();
			handleRestoreDisabledPreferencesArgument();
			handleRestoreDefaultsButtonTextArgument();
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		layout = (LinearLayout) super.onCreateView(inflater, container,
				savedInstanceState);
		addRestoreDefaultsButtonBar();
		return layout;
	}

}