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
package de.mrapp.android.preference.activity;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.LinkedHashSet;
import java.util.Set;

import de.mrapp.android.preference.activity.animation.HideViewOnScrollAnimation;
import de.mrapp.android.preference.activity.animation.HideViewOnScrollAnimation.Direction;
import de.mrapp.android.preference.activity.decorator.PreferenceDecorator;

import static de.mrapp.android.preference.activity.util.Condition.ensureAtLeast;
import static de.mrapp.android.preference.activity.util.Condition.ensureAtMaximum;
import static de.mrapp.android.preference.activity.util.Condition.ensureNotNull;
import static de.mrapp.android.preference.activity.util.DisplayUtil.convertDpToPixels;
import static de.mrapp.android.preference.activity.util.DisplayUtil.convertPixelsToDp;

/**
 * A fragment, which allows to show multiple preferences. Additionally, a button, which allows to
 * restore the preferences' default values, can be shown.
 *
 * @author Michael Rapp
 * @since 1.1.0
 */
public abstract class PreferenceFragment extends android.preference.PreferenceFragment {

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
     * The default elevation of the button bar in dp.
     */
    private static final int DEFAULT_BUTTON_BAR_ELEVATION = 2;

    /**
     * The fragment's parent parentView.
     */
    private LinearLayout parentView;

    /**
     * The list view, which contains the fragment's preferences.
     */
    private ListView listView;

    /**
     * The frame layout, which contains the fragment's views. It is the root view of the fragment.
     */
    private FrameLayout frameLayout;

    /**
     * The parent view of the view group, which contains the button, which allows to restore the
     * preferences' default values.
     */
    private ViewGroup buttonBarParent;

    /**
     * The view group, which contains the button, which allows to restore the preferences' default
     * values.
     */
    private ViewGroup buttonBar;

    /**
     * The view, which is used to draw a shadow above the button bar.
     */
    private View shadowView;

    /**
     * The button, which allows to restore the preferences' default values.
     */
    private Button restoreDefaultsButton;

    /**
     * The elevation of the button bar in dp.
     */
    private int buttonBarElevation;

    /**
     * A set, which contains the listeners, which should be notified, when the preferences' default
     * values should be restored.
     */
    private Set<RestoreDefaultsListener> restoreDefaultsListeners = new LinkedHashSet<>();

    /**
     * Initializes the list view, which is used to show the fragment's preferences.
     */
    private void initializeListView() {
        listView = (ListView) parentView.findViewById(android.R.id.list);
        parentView.removeView(listView);
        frameLayout = new FrameLayout(getActivity());
        frameLayout.setId(R.id.preference_fragment_frame_layout);
        parentView.addView(frameLayout, listView.getLayoutParams());
        frameLayout.addView(listView, FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.list_view_padding_top);
        listView.setPadding(0, paddingTop, 0, 0);
    }

    /**
     * Inflates the view group, which contains the button, which allows to restore the preferences'
     * default values.
     */
    private void inflateRestoreDefaultsButtonBar() {
        if (buttonBarParent == null) {
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            buttonBarParent = (ViewGroup) layoutInflater
                    .inflate(R.layout.restore_defaults_button_bar, frameLayout, false);
            buttonBar = (ViewGroup) buttonBarParent.findViewById(R.id.restore_defaults_button_bar);
            restoreDefaultsButton =
                    (Button) buttonBarParent.findViewById(R.id.restore_defaults_button);
            restoreDefaultsButton.setOnClickListener(createRestoreDefaultsListener());
            shadowView = buttonBarParent.findViewById(R.id.restore_defaults_button_bar_shadow_view);
            obtainStyledAttributes();
        }
    }

    /**
     * Obtains all relevant attributes from the activity's current theme.
     */
    private void obtainStyledAttributes() {
        int theme = obtainTheme();

        if (theme != 0) {
            obtainButtonBarBackground(theme);
            obtainButtonBarElevation(theme);
        }
    }

    /**
     * Obtains the resource id of the activity's current theme.
     *
     * @return The resource id of the acitivty's current theme as an {@link Integer} value or 0, if
     * an error occurred while obtaining the theme
     */
    private int obtainTheme() {
        try {
            String packageName = getActivity().getClass().getPackage().getName();
            PackageInfo packageInfo = getActivity().getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return packageInfo.applicationInfo.theme;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * Obtains the background of the button bar from a specific theme.
     *
     * @param theme
     *         The resource id of the theme, the background should be obtained from, as an {@link
     *         Integer} value
     */
    private void obtainButtonBarBackground(final int theme) {
        TypedArray typedArray = getActivity().getTheme().obtainStyledAttributes(theme,
                new int[]{R.attr.restoreDefaultsButtonBarBackground});
        int color = typedArray.getColor(0, 0);

        if (color != 0) {
            setButtonBarBackgroundColor(color);
        } else {
            int resourceId = typedArray.getResourceId(0, 0);

            if (resourceId != 0) {
                setButtonBarBackground(resourceId);
            }
        }
    }

    /**
     * Obtains the elevation of the button bar from a specific theme.
     *
     * @param theme
     *         The resource id of the theme, the navigation width should be obtained from, as an
     *         {@link Integer} value
     */
    private void obtainButtonBarElevation(final int theme) {
        TypedArray typedArray = getActivity().getTheme()
                .obtainStyledAttributes(theme, new int[]{R.attr.restoreDefaultsButtonBarElevation});
        int elevation = convertPixelsToDp(getActivity(), typedArray.getDimensionPixelSize(0, 0));

        if (elevation != 0) {
            this.buttonBarElevation = elevation;
        }
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
     * Adds the view group, which contains the button, which allows to restore the preferences'
     * default values, to the fragment.
     */
    private void addRestoreDefaultsButtonBar() {
        if (frameLayout != null && buttonBarParent != null) {
            FrameLayout.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
            frameLayout.addView(buttonBarParent, layoutParams);
            listView.setOnScrollListener(
                    new HideViewOnScrollAnimation(buttonBarParent, Direction.DOWN));
            setButtonBarElevation(buttonBarElevation);
        }
    }

    /**
     * Removes the view group, which contains the button, which allows to restore the preferences'
     * default values, from the fragment.
     */
    private void removeRestoreDefaultsButtonBar() {
        if (frameLayout != null && buttonBarParent != null) {
            frameLayout.removeView(buttonBarParent);
        }
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
     * Applies Material style on all preferences, which are contained by a specific preference
     * group, and on the group itself.
     *
     * @param preferenceGroup
     *         The preference group, at whose preferences the Material style should be applied on,
     *         as an instance of the class {@link PreferenceGroup}. The preference group may not be
     *         null
     * @param decorator
     *         The decorator, which should be used to apply the Material style, as an instance of
     *         the class {@link PreferenceDecorator}. The decorator may not be null
     */
    private void applyMaterialStyle(@NonNull final PreferenceGroup preferenceGroup,
                                    @NonNull final PreferenceDecorator decorator) {
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);

            if (preference instanceof PreferenceGroup) {
                decorator.applyDecorator(preference);
                applyMaterialStyle((PreferenceGroup) preference, decorator);
            } else {
                decorator.applyDecorator(preference);
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
     * Handles the extra of the arguments, which have been passed to the fragment, that allows to
     * show the button, which allows to restore the preferences' default values.
     */
    private void handleShowRestoreDefaultsButtonArgument() {
        boolean showButton = getArguments().getBoolean(EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON, false);

        if (showButton) {
            showRestoreDefaultsButton(true);
        }
    }

    /**
     * Handles the extra of the arguments, which have been passed to the fragment, that allows to
     * specify a custom text for the button, which allows to restore the preferences' default
     * values.
     */
    private void handleRestoreDefaultsButtonTextArgument() {
        CharSequence buttonText = getCharSequenceFromArguments(EXTRA_RESTORE_DEFAULTS_BUTTON_TEXT);

        if (!TextUtils.isEmpty(buttonText)) {
            setRestoreDefaultsButtonText(buttonText);
        }
    }

    /**
     * Returns the char sequence, which is specified by a specific extra of the arguments, which
     * have been passed to the fragment. The char sequence can either be specified as a string or as
     * a resource id.
     *
     * @param name
     *         The name of the extra, which specifies the char sequence, as a {@link String}. The
     *         name may not be null
     * @return The char sequence, which is specified by the arguments, as an instance of the class
     * {@link CharSequence} or null, if the arguments do not specify a char sequence with the given
     * name
     */
    private CharSequence getCharSequenceFromArguments(@NonNull final String name) {
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
     * Restores the default values of all preferences, which are contained by the fragment.
     */
    public final void restoreDefaults() {
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

        if (getPreferenceScreen() != null) {
            restoreDefaults(getPreferenceScreen(), sharedPreferences);
        }
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
        if (show) {
            inflateRestoreDefaultsButtonBar();
            addRestoreDefaultsButtonBar();
        } else {
            removeRestoreDefaultsButtonBar();
            listView.setOnScrollListener(null);
            buttonBarParent = null;
            buttonBar = null;
            restoreDefaultsButton = null;
        }
    }

    /**
     * Returns the frame layout, which contains the fragment's views. It is the root view of the
     * fragment.
     *
     * @return The frame layout, which contains the fragment's views
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
     * shown
     */
    public final ViewGroup getButtonBar() {
        return buttonBar;
    }

    /**
     * Returns the background of the view group, which contains the button, which allows to restore
     * the preferences' default values.
     *
     * @return The background of the view group, which contains the button, which allows to restore
     * the preferences' default values, as an instance of the class {@link Drawable} or null, if the
     * button is not shown or no background is set
     */
    public final Drawable getButtonBarBackground() {
        if (getButtonBar() != null) {
            return buttonBar.getBackground();
        }

        return null;
    }

    /**
     * Sets the background of the view group, which contains the button, which allows to restore the
     * preferences' default values. The background is only set, if the button is shown.
     *
     * @param resourceId
     *         The resource id of the background, which should be set, as an {@link Integer} value.
     *         The resource id must correspond to a valid drawable resource
     * @return True, if the background has been set, false otherwise
     */
    @SuppressWarnings("deprecation")
    public final boolean setButtonBarBackground(@DrawableRes final int resourceId) {
        return setButtonBarBackground(getResources().getDrawable(resourceId));
    }

    /**
     * Sets the background color of the view group, which contains the button, which allows to
     * restore the preferences' default values. The background color is only set, if the button is
     * shown.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value
     * @return True, if the background color has been set, false otherwise
     */
    public final boolean setButtonBarBackgroundColor(@ColorInt final int color) {
        return setButtonBarBackground(new ColorDrawable(color));
    }

    /**
     * Sets the background of the view group, which contains the button, which allows to restore the
     * preferences' default values. The background is only set, if the button is shown.
     *
     * @param drawable
     *         The background, which should be set, as an instance of the class {@link Drawable} or
     *         null, if no background should be set
     * @return True, if the background has been set, false otherwise
     */
    @SuppressWarnings("deprecation")
    public final boolean setButtonBarBackground(@Nullable final Drawable drawable) {
        if (getButtonBar() != null) {
            getButtonBar().setBackgroundDrawable(drawable);
            return true;
        }

        return false;
    }

    /**
     * Returns the elevation of the view group, which contains the button, which allows to restore
     * the preferences' default values.
     *
     * @return The elevation in dp as an {@link Integer} value or -1, if the button is not shown
     */
    public final int getButtonBarElevation() {
        if (isRestoreDefaultsButtonShown()) {
            return buttonBarElevation;
        } else {
            return -1;
        }
    }

    /**
     * Sets the elevation of the view group, which contains the button, which allows to restore the
     * preferences' default values. The elevation is only set when the button is shown.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 1 and at maximum 5
     * @return True, if the elevation has been set, false otherwise
     */
    @SuppressWarnings("deprecation")
    public final boolean setButtonBarElevation(final int elevation) {
        String[] shadowColors =
                getResources().getStringArray(R.array.button_bar_elevation_shadow_colors);
        String[] shadowWidths =
                getResources().getStringArray(R.array.button_bar_elevation_shadow_widths);
        ensureAtLeast(elevation, 1, "The elevation must be at least 1");
        ensureAtMaximum(elevation, shadowWidths.length,
                "The elevation must be at maximum " + shadowWidths.length);

        if (isRestoreDefaultsButtonShown()) {
            this.buttonBarElevation = elevation;
            int shadowColor = Color.parseColor(shadowColors[elevation - 1]);
            int shadowWidth =
                    convertDpToPixels(getActivity(), Integer.valueOf(shadowWidths[elevation - 1]));

            GradientDrawable gradient = new GradientDrawable(Orientation.BOTTOM_TOP,
                    new int[]{shadowColor, Color.TRANSPARENT});
            shadowView.setBackgroundDrawable(gradient);
            shadowView.getLayoutParams().height = shadowWidth;
            shadowView.requestLayout();
            return true;
        }

        return false;
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
     * Returns the text of the button, which allows to restore the preferences' default values.
     *
     * @return The text of the button, which allows to restore the preferences' default values, as
     * an instance of the class {@link CharSequence} or null, if the button is not shown
     */
    public final CharSequence getRestoreDefaultsButtonText() {
        if (restoreDefaultsButton != null) {
            return restoreDefaultsButton.getText();
        }

        return null;
    }

    /**
     * Sets the text of the button, which allows to restore the preferences' default values. The
     * text is only set, if the button is shown.
     *
     * @param text
     *         The text, which should be set, as an instance of the class {@link CharSequence}. The
     *         text may not be null
     * @return True, if the text has been set, false otherwise
     */
    public final boolean setRestoreDefaultsButtonText(@NonNull final CharSequence text) {
        ensureNotNull(text, "The text may not be null");

        if (restoreDefaultsButton != null) {
            restoreDefaultsButton.setText(text);
            return true;
        }

        return false;
    }

    /**
     * Sets the text of the button, which allows to restore the preferences' default values. The
     * text is only set, if the button is shown.
     *
     * @param resourceId
     *         The resource id of the text, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     * @return True, if the text has been set, false otherwise
     */
    public final boolean setRestoreDefaultsButtonText(@StringRes final int resourceId) {
        return setRestoreDefaultsButtonText(getText(resourceId));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.buttonBarElevation = DEFAULT_BUTTON_BAR_ELEVATION;

        if (getArguments() != null) {
            handleShowRestoreDefaultsButtonArgument();
            handleRestoreDefaultsButtonTextArgument();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        parentView = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);

        if (savedInstanceState == null) {
            initializeListView();
            addRestoreDefaultsButtonBar();
        }

        return parentView;
    }

    @Override
    public final void addPreferencesFromResource(@XmlRes final int resourceId) {
        super.addPreferencesFromResource(resourceId);
        PreferenceDecorator decorator = new PreferenceDecorator(getActivity());

        if (getPreferenceScreen() != null) {
            applyMaterialStyle(getPreferenceScreen(), decorator);
        }
    }

}