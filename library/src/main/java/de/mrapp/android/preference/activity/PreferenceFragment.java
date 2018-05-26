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
package de.mrapp.android.preference.activity;

import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.AndroidResources;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.LinkedHashSet;
import java.util.Set;

import de.mrapp.android.preference.activity.animation.HideViewOnScrollAnimation;
import de.mrapp.android.preference.activity.fragment.AbstractPreferenceFragment;
import de.mrapp.android.util.ThemeUtil;
import de.mrapp.android.util.ViewUtil;
import de.mrapp.android.util.view.ElevationShadowView;

import static de.mrapp.android.util.Condition.ensureAtLeast;
import static de.mrapp.android.util.Condition.ensureAtMaximum;
import static de.mrapp.android.util.Condition.ensureNotEmpty;
import static de.mrapp.android.util.Condition.ensureNotNull;
import static de.mrapp.android.util.DisplayUtil.pixelsToDp;

/**
 * A fragment, which allows to show multiple preferences. Additionally, a button, which allows to
 * restore the preferences' default values, can be shown.
 *
 * @author Michael Rapp
 * @since 1.1.0
 */
public abstract class PreferenceFragment extends AbstractPreferenceFragment {

    /**
     * When attaching this fragment to an activity, the passed bundle can contain this extra boolean
     * to display the button, which allows to restore the preferences' default values.
     */
    public static final String EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON =
            "extra_prefs_show_restore_defaults_button";

    /**
     * When attaching this fragment to an activity and using <code>EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON</code>,
     * this extra can also be specified to supply a custom text for the button, which allows to
     * restore the preferences' default values.
     */
    public static final String EXTRA_RESTORE_DEFAULTS_BUTTON_TEXT =
            "extra_prefs_restore_defaults_button_text";

    /**
     * A set, which contains the listeners, which should be notified, when the preferences' default
     * values should be restored.
     */
    private final Set<RestoreDefaultsListener> restoreDefaultsListeners = new LinkedHashSet<>();

    /**
     * The frame layout, which contains the fragment's views. It is the root view of the fragment.
     */
    private FrameLayout frameLayout;

    /**
     * The parent view of the button bar.
     */
    private ViewGroup buttonBarParent;

    /**
     * The button bar.
     */
    private ViewGroup buttonBar;

    /**
     * The view, which is used to draw a shadow above the button bar.
     */
    private ElevationShadowView shadowView;

    /**
     * The button, which allows to restore the preferences' default values.
     */
    private Button restoreDefaultsButton;

    /**
     * True, if the button, which allows to restore the preferences' default values, is shown, false
     * otherwise.
     */
    private boolean showRestoreDefaultsButton;

    /**
     * The text of the button, which allows to restore the preferences' default values.
     */
    private CharSequence restoreDefaultsButtonText;

    /**
     * The background of the button bar.
     */
    private Drawable buttonBarBackground;

    /**
     * The elevation of the button bar in dp.
     */
    private int buttonBarElevation;

    /**
     * Obtains all relevant attributes from the activity's current theme.
     */
    private void obtainStyledAttributes() {
        obtainShowRestoreDefaultsButton();
        obtainRestoreDefaultsButtonText();
        obtainButtonBarBackground();
        obtainButtonBarElevation();
    }

    /**
     * Obtains, whether the button, which allows to restore the preferences' default values, should
     * be shown, or not, from the activity's current theme.
     */
    private void obtainShowRestoreDefaultsButton() {
        boolean show = ThemeUtil.getBoolean(getActivity(), R.attr.showRestoreDefaultsButton, false);
        showRestoreDefaultsButton(show);
    }

    /**
     * Obtains the text of the button, which allows to restore the preferences' default values, from
     * the activity's current theme.
     */
    private void obtainRestoreDefaultsButtonText() {
        CharSequence text;

        try {
            text = ThemeUtil.getText(getActivity(), R.attr.restoreDefaultsButtonText);
        } catch (NotFoundException e) {
            text = getText(R.string.restore_defaults_button_text);
        }

        setRestoreDefaultsButtonText(text);
    }

    /**
     * Obtains the background of the button bar from the activity's current theme.
     */
    private void obtainButtonBarBackground() {
        try {
            int color =
                    ThemeUtil.getColor(getActivity(), R.attr.restoreDefaultsButtonBarBackground);
            setButtonBarBackgroundColor(color);
        } catch (NotFoundException e) {
            int resourceId = ThemeUtil
                    .getResId(getActivity(), R.attr.restoreDefaultsButtonBarBackground, -1);

            if (resourceId != -1) {
                setButtonBarBackground(resourceId);
            } else {
                setButtonBarBackgroundColor(
                        ContextCompat.getColor(getActivity(), R.color.button_bar_background_light));
            }
        }
    }

    /**
     * Obtains the elevation of the button bar from the activity's current theme.
     */
    private void obtainButtonBarElevation() {
        int elevation;

        try {
            elevation = ThemeUtil
                    .getDimensionPixelSize(getActivity(), R.attr.restoreDefaultsButtonBarElevation);
        } catch (NotFoundException e) {
            elevation = getResources().getDimensionPixelSize(R.dimen.button_bar_elevation);
        }

        setButtonBarElevation(pixelsToDp(getActivity(), elevation));
    }

    /**
     * Handles the arguments, which have been passed to the fragment.
     */
    private void handleArguments() {
        Bundle arguments = getArguments();

        if (arguments != null) {
            handleShowRestoreDefaultsButtonArgument(arguments);
            handleRestoreDefaultsButtonTextArgument(arguments);
        }
    }

    /**
     * Handles the extra of the arguments, which have been passed to the fragment, that allows to
     * show the button, which allows to restore the preferences' default values.
     *
     * @param arguments
     *         The arguments, which have been passed to the fragment, as an instance of the class
     *         {@link Bundle}. The arguments may not be null
     */
    private void handleShowRestoreDefaultsButtonArgument(@NonNull final Bundle arguments) {
        boolean showButton = arguments.getBoolean(EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON, false);
        showRestoreDefaultsButton(showButton);
    }

    /**
     * Handles the extra of the arguments, which have been passed to the fragment, that allows to
     * specify a custom text for the button, which allows to restore the preferences' default
     * values.
     *
     * @param arguments
     *         The arguments, which have been passed to the fragment, as an instance of the class
     *         {@link Bundle}. The arguments may not be null
     */
    private void handleRestoreDefaultsButtonTextArgument(@NonNull final Bundle arguments) {
        CharSequence buttonText =
                getCharSequenceFromArguments(arguments, EXTRA_RESTORE_DEFAULTS_BUTTON_TEXT);

        if (!TextUtils.isEmpty(buttonText)) {
            setRestoreDefaultsButtonText(buttonText);
        }
    }

    /**
     * Returns the char sequence, which is specified by a specific extra of the arguments, which
     * have been passed to the fragment. The char sequence can either be specified as a string or as
     * a resource id.
     *
     * @param arguments
     *         The arguments, which have been passed to the fragment, as an instance of the class
     *         {@link Bundle}. The arguments may not be null
     * @param name
     *         The name of the extra, which specifies the char sequence, as a {@link String}. The
     *         name may not be null
     * @return The char sequence, which is specified by the arguments, as an instance of the class
     * {@link CharSequence} or null, if the arguments do not specify a char sequence with the given
     * name
     */
    private CharSequence getCharSequenceFromArguments(@NonNull final Bundle arguments,
                                                      @NonNull final String name) {
        CharSequence charSequence = arguments.getCharSequence(name);

        if (charSequence == null) {
            int resourceId = arguments.getInt(name, -1);

            if (resourceId != -1) {
                charSequence = getText(resourceId);
            }
        }

        return charSequence;
    }

    /**
     * Creates and returns a listener, which allows to restore the preferences' default values.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}
     */
    private OnClickListener createRestoreDefaultsListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (notifyOnRestoreDefaultValuesRequested()) {
                    restoreDefaults();
                }
            }

        };
    }

    /**
     * Restores the default preferences, which are contained by a specific preference group.
     *
     * @param preferenceGroup
     *         The preference group, whose preferences should be restored, as an instance of the
     *         class {@link PreferenceGroup}. The preference group may not be null
     * @param sharedPreferences
     *         The shared preferences, which should be used to restore the preferences, as an
     *         instance of the type {@link SharedPreferences}. The shared preferences may not be
     *         null
     */
    private void restoreDefaults(@NonNull final PreferenceGroup preferenceGroup,
                                 @NonNull final SharedPreferences sharedPreferences) {
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);

            if (preference instanceof PreferenceGroup) {
                restoreDefaults((PreferenceGroup) preference, sharedPreferences);
            } else if (preference.getKey() != null && !preference.getKey().isEmpty()) {
                Object oldValue = sharedPreferences.getAll().get(preference.getKey());

                if (notifyOnRestoreDefaultValueRequested(preference, oldValue)) {
                    sharedPreferences.edit().remove(preference.getKey()).apply();
                    preferenceGroup.removePreference(preference);
                    preferenceGroup.addPreference(preference);
                    Object newValue = sharedPreferences.getAll().get(preference.getKey());
                    notifyOnRestoredDefaultValue(preference, oldValue, newValue);
                } else {
                    preferenceGroup.removePreference(preference);
                    preferenceGroup.addPreference(preference);
                }

            }
        }
    }

    /**
     * Notifies all registered listeners, that the preferences' default values should be restored.
     *
     * @return True, if restoring the preferences' default values should be proceeded, false
     * otherwise
     */
    private boolean notifyOnRestoreDefaultValuesRequested() {
        boolean result = true;

        for (RestoreDefaultsListener listener : restoreDefaultsListeners) {
            result &= listener.onRestoreDefaultValuesRequested(this);
        }

        return result;
    }

    /**
     * Notifies all registered listeners, that the default value of a specific preference should be
     * restored.
     *
     * @param preference
     *         The preference, whose default value should be restored, as an instance of the class
     *         {@link Preference}. The preference may not be null
     * @param currentValue
     *         The current value of the preference, whose default value should be restored, as an
     *         instance of the class {@link Object}
     * @return True, if restoring the preference's default value should be proceeded, false
     * otherwise
     */
    private boolean notifyOnRestoreDefaultValueRequested(@NonNull final Preference preference,
                                                         final Object currentValue) {
        boolean result = true;

        for (RestoreDefaultsListener listener : restoreDefaultsListeners) {
            result &= listener.onRestoreDefaultValueRequested(this, preference, currentValue);
        }

        return result;
    }

    /**
     * Notifies all registered listeners, that the default value of a specific preference has been
     * be restored.
     *
     * @param preference
     *         The preference, whose default value has been restored, as an instance of the class
     *         {@link Preference}. The preference may not be null
     * @param oldValue
     *         The old value of the preference, whose default value has been restored, as an
     *         instance of the class {@link Object}
     * @param newValue
     *         The new value of the preference, whose default value has been restored, as an
     *         instance of the class {@link Object}
     */
    private void notifyOnRestoredDefaultValue(@NonNull final Preference preference,
                                              final Object oldValue, final Object newValue) {
        for (RestoreDefaultsListener listener : restoreDefaultsListeners) {
            listener.onRestoredDefaultValue(this, preference, oldValue,
                    newValue != null ? newValue : oldValue);
        }
    }

    /**
     * Adapts the visibility of the button bar.
     */
    private void adaptButtonBarVisibility() {
        if (buttonBarParent != null) {
            buttonBarParent.setVisibility(showRestoreDefaultsButton ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Adapts the text of the button, which allows to restore the preferences' default values.
     */
    private void adaptRestoreDefaultsButtonText() {
        if (restoreDefaultsButton != null) {
            restoreDefaultsButton.setText(restoreDefaultsButtonText);
        }
    }

    /**
     * Adapts the background of the button bar.
     */
    private void adaptButtonBarBackground() {
        if (buttonBar != null) {
            ViewUtil.setBackground(buttonBar, buttonBarBackground);
        }
    }

    /**
     * Adapts the elevation of the button bar.
     */
    private void adaptButtonBarElevation() {
        if (shadowView != null) {
            shadowView.setShadowElevation(buttonBarElevation);
        }
    }

    /**
     * Returns the frame layout, which contains the fragment's views. It is the root view of the
     * fragment.
     *
     * @return The frame layout, which contains the fragment's views, as an instance of the class
     * {@link FrameLayout} or null, if the fragment has not been created yet
     */
    public final FrameLayout getFrameLayout() {
        return frameLayout;
    }

    /**
     * Returns the view group, which contains the button, which allows to restore the preferences'
     * default values.
     *
     * @return The view group, which contains the button, which allows to restore the preferences'
     * default values, as an instance of the class {@link ViewGroup} or null, if the button is not
     * shown or if the fragment has not been created yet
     */
    public final ViewGroup getButtonBar() {
        return buttonBar;
    }

    /**
     * Returns the button, which allows to restore the preferences' default values.
     *
     * @return The button, which allows to restore the preferences' default values, as an instance
     * of the class {@link Button} or null, if the button is not shown
     */
    public final Button getRestoreDefaultsButton() {
        return restoreDefaultsButton;
    }

    /**
     * Adds a new listener, which should be notified, when the preferences' default values should be
     * restored, to the fragment.
     *
     * @param listener
     *         The listener, which should be added as an instance of the type {@link
     *         RestoreDefaultsListener}. The listener may not be null
     */
    public final void addRestoreDefaultsListener(@NonNull final RestoreDefaultsListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        this.restoreDefaultsListeners.add(listener);
    }

    /**
     * Removes a specific listener, which should not be notified anymore, when the preferences'
     * default values should be restored, from the fragment.
     *
     * @param listener
     *         The listener, which should be removed as an instance of the type {@link
     *         RestoreDefaultsListener}. The listener may not be null
     */
    public final void removeRestoreDefaultsListener(
            @NonNull final RestoreDefaultsListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        this.restoreDefaultsListeners.remove(listener);
    }

    /**
     * Restores the default values of all preferences, which are contained by the fragment.
     */
    public final void restoreDefaults() {
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

        if (getPreferenceScreen() != null) {
            restoreDefaults(getPreferenceScreen(), sharedPreferences);
        }
    }

    /**
     * Returns, whether the button, which allows to restore the preferences' default values, is
     * currently shown, or not.
     *
     * @return True, if the button, which allows to restore the preferences' default values, is
     * currently shown, false otherwise
     */
    public final boolean isRestoreDefaultsButtonShown() {
        return restoreDefaultsButton != null;
    }

    /**
     * Shows or hides the button, which allows to restore the preferences' default values.
     *
     * @param show
     *         True, if the button, which allows to restore the preferences' default values, should
     *         be shown, false otherwise
     */
    public final void showRestoreDefaultsButton(final boolean show) {
        this.showRestoreDefaultsButton = show;
        adaptButtonBarVisibility();
    }

    /**
     * Returns the text of the button, which allows to restore the preferences' default values.
     *
     * @return The text of the button, which allows to restore the preferences' default values, as
     * an instance of the class {@link CharSequence}
     */
    @NonNull
    public final CharSequence getRestoreDefaultsButtonText() {
        return restoreDefaultsButtonText;
    }

    /**
     * Sets the text of the button, which allows to restore the preferences' default values. The
     * text is only set, if the button is shown.
     *
     * @param resourceId
     *         The resource id of the text, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setRestoreDefaultsButtonText(@StringRes final int resourceId) {
        setRestoreDefaultsButtonText(getText(resourceId));
    }

    /**
     * Sets the text of the button, which allows to restore the preferences' default values. The
     * text is only set, if the button is shown.
     *
     * @param text
     *         The text, which should be set, as an instance of the class {@link CharSequence}. The
     *         text may neither be null, nor empty
     */
    public final void setRestoreDefaultsButtonText(@NonNull final CharSequence text) {
        ensureNotNull(text, "The text may not be null");
        ensureNotEmpty(text, "The text may not be empty");
        this.restoreDefaultsButtonText = text;
        adaptRestoreDefaultsButtonText();
    }

    /**
     * Returns the background of the view group, which contains the button, which allows to restore
     * the preferences' default values.
     *
     * @return The background of the view group, which contains the button, which allows to restore
     * the preferences' default values, as an instance of the class {@link Drawable}
     */
    public final Drawable getButtonBarBackground() {
        return buttonBarBackground;
    }

    /**
     * Sets the background of the view group, which contains the button, which allows to restore the
     * preferences' default values.
     *
     * @param resourceId
     *         The resource id of the background, which should be set, as an {@link Integer} value.
     *         The resource id must correspond to a valid drawable resource
     */
    public final void setButtonBarBackground(@DrawableRes final int resourceId) {
        setButtonBarBackground(ContextCompat.getDrawable(getActivity(), resourceId));
    }

    /**
     * Sets the background color of the view group, which contains the button, which allows to
     * restore the preferences' default values. shown.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value
     */
    public final void setButtonBarBackgroundColor(@ColorInt final int color) {
        setButtonBarBackground(new ColorDrawable(color));
    }

    /**
     * Sets the background of the view group, which contains the button, which allows to restore the
     * preferences' default values.
     *
     * @param background
     *         The background, which should be set, as an instance of the class {@link Drawable} or
     *         null, if no background should be set
     */
    public final void setButtonBarBackground(@Nullable final Drawable background) {
        this.buttonBarBackground = background;
        adaptButtonBarBackground();
    }

    /**
     * Returns the elevation of the view group, which contains the button, which allows to restore
     * the preferences' default values.
     *
     * @return The elevation in dp as an {@link Integer} value
     */
    public final int getButtonBarElevation() {
        return buttonBarElevation;
    }

    /**
     * Sets the elevation of the view group, which contains the button, which allows to restore the
     * preferences' default values.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 1 and at maximum 16
     */
    public final void setButtonBarElevation(final int elevation) {
        ensureAtLeast(elevation, 0, "The elevation must be at least 0");
        ensureAtMaximum(elevation, 16, "The elevation must be at maximum 16");
        this.buttonBarElevation = elevation;
        adaptButtonBarElevation();
    }

    @CallSuper
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainStyledAttributes();
        handleArguments();
    }

    @CallSuper
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().addOnScrollListener(new HideViewOnScrollAnimation(buttonBarParent,
                HideViewOnScrollAnimation.Direction.DOWN));
    }

    @NonNull
    @CallSuper
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent,
                             final Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);
        View listContainer = view.findViewById(AndroidResources.ANDROID_R_LIST_CONTAINER);

        if (!(listContainer instanceof FrameLayout)) {
            throw new RuntimeException(
                    "Fragment contains a view with id 'android.R.id.list_container' that is not a FrameLayout");
        }

        frameLayout = (FrameLayout) listContainer;
        buttonBarParent = (ViewGroup) inflater
                .inflate(R.layout.restore_defaults_button_bar, frameLayout, false);
        frameLayout.addView(buttonBarParent);
        buttonBarParent.setVisibility(showRestoreDefaultsButton ? View.VISIBLE : View.GONE);
        buttonBar = buttonBarParent.findViewById(R.id.restore_defaults_button_bar);
        ViewUtil.setBackground(buttonBar, buttonBarBackground);
        restoreDefaultsButton = buttonBarParent.findViewById(R.id.restore_defaults_button);
        restoreDefaultsButton.setOnClickListener(createRestoreDefaultsListener());
        restoreDefaultsButton.setText(restoreDefaultsButtonText);
        shadowView = buttonBarParent.findViewById(R.id.restore_defaults_button_bar_shadow_view);
        shadowView.setShadowElevation(buttonBarElevation);
        return view;
    }

}