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

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Collections;
import java.util.List;

import de.mrapp.android.preference.activity.adapter.NavigationPreferenceAdapter;
import de.mrapp.android.preference.activity.fragment.NavigationFragment;
import de.mrapp.android.preference.activity.view.ToolbarLarge;
import de.mrapp.android.util.Condition;
import de.mrapp.android.util.DisplayUtil.DeviceType;
import de.mrapp.android.util.ElevationUtil;
import de.mrapp.android.util.ThemeUtil;
import de.mrapp.android.util.ViewUtil;
import de.mrapp.android.util.datastructure.ListenerList;
import de.mrapp.android.util.view.ElevationShadowView;

import static de.mrapp.android.util.Condition.ensureAtLeast;
import static de.mrapp.android.util.Condition.ensureAtMaximum;
import static de.mrapp.android.util.Condition.ensureGreater;
import static de.mrapp.android.util.Condition.ensureNotEmpty;
import static de.mrapp.android.util.Condition.ensureNotNull;
import static de.mrapp.android.util.DisplayUtil.dpToPixels;
import static de.mrapp.android.util.DisplayUtil.getDeviceType;
import static de.mrapp.android.util.DisplayUtil.getDisplayWidth;
import static de.mrapp.android.util.DisplayUtil.pixelsToDp;

/**
 * An activity, which provides a navigation for accessing preferences, which are grouped as
 * different sections. Each group of preferences is accessible by clicking a {@link
 * NavigationPreference}. On devices with small screens, e.g. on smartphones, the navigation is
 * designed to use the whole available space and selecting an item causes the corresponding
 * preferences to be shown full screen as well. On devices with large screens, e.g. on tablets, the
 * navigation and the preferences of the currently selected item are shown using a split screen
 * layout. However, the full screen layout can optionally be used on tablets as well.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public abstract class PreferenceActivity extends AppCompatActivity
        implements NavigationFragment.Callback, NavigationPreferenceAdapter.Callback {

    /**
     * When starting this activity, the invoking intent can contain this extra string to specify
     * which fragment should be initially displayed.
     */
    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";

    /**
     * When starting this activity and using <code>EXTRA_SHOW_FRAGMENT</code>, this extra can also
     * be specified to supply a bundle of arguments to pass to that fragment when it is instantiated
     * during the initial creation of the activity.
     */
    public static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":android:show_fragment_args";

    /**
     * When starting this activity and using <code>EXTRA_SHOW_FRAGMENT</code>, this extra can also
     * be specified to supply the title to be shown for that fragment.
     */
    public static final String EXTRA_SHOW_FRAGMENT_TITLE = ":android:show_fragment_title";

    /**
     * When starting this activity, the invoking intent can contain this extra boolean to specify
     * that the navigation should not be displayed. This is most often used in conjunction with
     * <code>EXTRA_SHOW_FRAGMENT</code> to launch the activity to display a specific fragment that
     * the user has navigated to.
     */
    public static final String EXTRA_HIDE_NAVIGATION = ":android:no_headers";

    /**
     * When starting this activity, the invoking intent can contain this extra boolean to display
     * back and next buttons in order to use the activity as a wizard.
     */
    public static final String EXTRA_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";

    /**
     * When starting this activity and using <code>EXTRA_SHOW_BUTTON_BAR</code>, this extra can also
     * be specified to supply a custom text for the next button.
     */
    public static final String EXTRA_NEXT_BUTTON_TEXT = "extra_prefs_set_next_text";

    /**
     * When starting this activity and using <code>EXTRA_SHOW_BUTTON_BAR</code>, this extra can also
     * be specified to supply a custom text for the back button.
     */
    public static final String EXTRA_BACK_BUTTON_TEXT = "extra_prefs_set_back_text";

    /**
     * When starting this activity and using <code>EXTRA_SHOW_BUTTON_BAR</code>, this extra can also
     * be specified to supply a custom text for the back button when the last navigation preference
     * is selected.
     */
    public static final String EXTRA_FINISH_BUTTON_TEXT = "extra_prefs_set_finish_text";

    /**
     * When starting this activity using <code>EXTRA_SHOW_BUTTON_BAR</code>, this boolean extra can
     * also used to specify, whether the number of the currently shown wizard step and the number of
     * total steps should be shown as the bread crumb title.
     */
    public static final String EXTRA_SHOW_PROGRESS = "extra_prefs_show_progress";

    /**
     * When starting this activity, the invoking intent can contain this extra boolean that the
     * toolbar, which is used to show the title of the currently selected navigation preference,
     * should not be displayed.
     */
    public static final String EXTRA_NO_BREAD_CRUMBS = ":extra_prefs_no_bread_crumbs";

    /**
     * When starting this activity using <code>EXTRA_SHOW_BUTTON_BAR</code> and
     * <code>EXTRA_SHOW_PROGRESS</code>, this string extra can also be specified to supply a custom
     * format for showing the progress. The string must be formatted according to the following
     * syntax: "*%d*%d*%s*"
     */
    public static final String EXTRA_PROGRESS_FORMAT = "extra_prefs_progress_format";

    /**
     * The tag of the fragment, which contains the activity's navigation.
     */
    private static final String NAVIGATION_FRAGMENT_TAG =
            PreferenceActivity.class.getName() + "::NavigationFragment";

    /**
     * The tag of the currently shown preference fragment.
     */
    private static final String PREFERENCE_FRAGMENT_TAG =
            PreferenceActivity.class.getName() + "::PreferenceFragment";

    /**
     * The name of the extra, which is used to store the arguments, which have been passed to the
     * currently shown preference fragment, within a bundle.
     */
    private static final String SELECTED_PREFERENCE_FRAGMENT_ARGUMENTS_EXTRA =
            PreferenceActivity.class.getName() + "::SelectedPreferenceFragmentArguments";

    /**
     * The name of the extra, which is used to store the fully classified class name of the
     * currently shown preference fragment within a bundle.
     */
    private static final String SELECTED_PREFERENCE_FRAGMENT_EXTRA =
            PreferenceActivity.class.getName() + "::SelectedPreferenceFragment";

    /**
     * The activity's toolbar.
     */
    private Toolbar toolbar;

    /**
     * The view, which is used to display a large toolbar, when using the split screen layout.
     */
    private ToolbarLarge toolbarLarge;

    /**
     * The toolbar, which is used to show the bread crumb of the currently selected navigation
     * preference, when using the split screen layout.
     */
    private Toolbar breadCrumbToolbar;

    /**
     * The container, the navigation fragment is attached to.
     */
    private ViewGroup navigationFragmentContainer;

    /**
     * The activity's root view.
     */
    private FrameLayout frameLayout;

    /**
     * The card view, which contains the currently shown preference fragment, as well as its
     * breadcrumb, when using the split screen layout.
     */
    private CardView cardView;

    /**
     * The view group, which contains the buttons, which are shown when the activity is used as a
     * wizard.
     */
    private ViewGroup buttonBar;

    /**
     * The back button, which is shown, when the activity is used as a wizard and the first
     * navigation preference is currently not selected.
     */
    private Button backButton;

    /**
     * The next button, which is shown, when the activity is used as a wizard and the last
     * navigation preference is currently not selected.
     */
    private Button nextButton;

    /**
     * The finish button, which is shown, when the activity is used as a wizard and the last
     * navigation preference is currently selected.
     */
    private Button finishButton;

    /**
     * The view, which is used to display a shadow above the button bar, which is shown when the
     * activity is used as a wizard.
     */
    private ElevationShadowView buttonBarShadowView;

    /**
     * The view, which is used to draw a shadow below the activity's toolbar.
     */
    private ElevationShadowView toolbarShadowView;

    /**
     * The view, which is used to draw a shadow below the toolbar, which is used to show the bread
     * crumb of the currently selected navigation preference when using the split screen layout.
     */
    private ElevationShadowView breadCrumbShadowView;

    /**
     * The fragment, which contains the activity's navigation.
     */
    private NavigationFragment navigationFragment;

    /**
     * The currently shown preference fragment.
     */
    private Fragment preferenceFragment;

    /**
     * True, if the split screen layout is used on tablets, false otherwise.
     */
    private boolean useSplitScreen;

    /**
     * The width of the navigation, when using the split screen layout, in pixels.
     */
    private int navigationWidth;

    /**
     * True, if the navigation is currently hidden, false otherwise.
     */
    private boolean hideNavigation;

    /**
     * True, if the behavior of the navigation icon of the activity's toolbar is overridden in order
     * to return to the navigation when a preference fragment is currently shown and the split
     * screen layout is used.
     */
    private boolean overrideNavigationIcon;

    /**
     * True, if the activity is used as a wizard, false otherwise.
     */
    private boolean showButtonBar;

    /**
     * The text of the next button.
     */
    private CharSequence nextButtonText;

    /**
     * The text of the back button.
     */
    private CharSequence backButtonText;

    /**
     * The text of the finish button.
     */
    private CharSequence finishButtonText;

    /**
     * True, if the progress is shown, when the activity is used as a wizard, false otherwise.
     */
    private boolean showProgress;

    /**
     * The string, which is used to format the progress, which is shown when the activity is used as
     * a wizard.
     */
    private String progressFormat;

    /**
     * The elevation of the activity's toolbar in dp.
     */
    private int toolbarElevation;

    /**
     * The elevation of the toolbar, which is used to show the bread crumb of the currently selected
     * preference fragment, when using the split screen layout, in dp.
     */
    private int breadCrumbElevation;

    /**
     * The elevation of the card view, which contains the currently shown preference fragment, when
     * using the split screen layout, in dp.
     */
    private int cardViewElevation;

    /**
     * Sets the elevation of the button bar, which is shown when using the activity as a wizard.
     */
    private int buttonBarElevation;

    /**
     * True, if the toolbar, which is used to show the bread crumb of the currently selected
     * navigation preference, is hidden, false otherwise.
     */
    private boolean hideBreadCrumb;

    /**
     * The background color fo the card view, which contains the currently shown preference
     * fragment, when using the split screen layout.
     */
    private int cardViewBackgroundColor;

    /**
     * The background color of the toolbar, which is used to show the bread crumb of the currently
     * selected navigation preference.
     */
    private int breadCrumbBackgroundColor;

    /**
     * The background of the button bar, which is shown, when the activity is used as a wizard.
     */
    private Drawable buttonBarBackground;

    /**
     * The background of the navigation.
     */
    private Drawable navigationBackground;

    /**
     * The background color of the currently selected navigation preference.
     */
    private int navigationSelectionColor;

    /**
     * The color of dividers, which are contained by the navigation.
     */
    private int navigationDividerColor;

    /**
     * True, if the navigation icon of the activity's toolbar is shown by default, false otherwise.
     */
    private boolean displayHomeAsUp;

    /**
     * The fully classified class name of the currently shown preference fragment.
     */
    private String selectedPreferenceFragment;

    /**
     * The arguments which have been passed to the currently shown preference fragment or null, if
     * no arguments have been passed to the fragment or no preference fragment is shown.
     */
    private Bundle selectedPreferenceFragmentArguments;

    /**
     * A set, which contains the listeners, which have been registered to be notified, when the
     * currently shown preference fragment has changed.
     */
    private ListenerList<PreferenceFragmentListener> preferenceFragmentListeners =
            new ListenerList<>();

    /**
     * A set, which contains the listeners, which have been registered to be notified, when the user
     * navigates within the activity, when it used as a wizard.
     */
    private ListenerList<WizardListener> wizardListeners = new ListenerList<>();

    /**
     * A set, which contains the listeners, which have been registered to be notified, when
     * navigation preferences have been added or removed to/from the activity.
     */
    private ListenerList<NavigationListener> navigationListeners = new ListenerList<>();

    /**
     * Obtains all relevant attributes from the activity's theme.
     */
    private void obtainStyledAttributes() {
        obtainUseSplitScreen();
        obtainNavigationWidth();
        obtainNavigationVisibility();
        obtainOverrideNavigationIcon();
        obtainShowButtonBar();
        obtainNextButtonText();
        obtainBackButtonText();
        obtainFinishButtonText();
        obtainShowProgress();
        obtainProgressFormat();
        obtainBreadCrumbVisibility();
        obtainToolbarElevation();
        obtainBreadcrumbElevation();
        obtainCardViewElevation();
        obtainButtonBarElevation();
        obtainCardViewBackgroundColor();
        obtainBreadCrumbBackgroundColor();
        obtainButtonBarBackground();
        obtainNavigationBackground();
        obtainNavigationSelectionColor();
        obtainNavigationDividerColor();
    }

    /**
     * Obtains, whether the split screen layout should be used on tablets, from the activities
     * theme.
     */
    private void obtainUseSplitScreen() {
        boolean useSplitScreen = ThemeUtil.getBoolean(this, R.attr.useSplitScreen, true);
        useSplitScreen(useSplitScreen);
    }

    /**
     * Obtains the width of the navigation from the activity's theme.
     */
    private void obtainNavigationWidth() {
        int navigationWidth;

        try {
            navigationWidth = ThemeUtil.getDimensionPixelSize(this, R.attr.navigationWidth);
        } catch (NotFoundException e) {
            navigationWidth = getResources().getDimensionPixelSize(R.dimen.navigation_width);
        }

        setNavigationWidth(navigationWidth);
    }

    /**
     * Obtains, whether the navigation should be shown, from the activity's theme.
     */
    private void obtainNavigationVisibility() {
        boolean hideNavigation = ThemeUtil.getBoolean(this, R.attr.hideNavigation, false);
        hideNavigation(hideNavigation);
    }

    /**
     * Obtains, whether the behavior of the navigation icon should be overridden, or not, from the
     * activity's theme.
     */
    private void obtainOverrideNavigationIcon() {
        boolean overrideNavigationIcon =
                ThemeUtil.getBoolean(this, R.attr.overrideNavigationIcon, true);
        overrideNavigationIcon(overrideNavigationIcon);
    }

    /**
     * Obtains, whether the activity should be used as a wizard, or not, from the activity's theme.
     */
    private void obtainShowButtonBar() {
        boolean showButtonBar = ThemeUtil.getBoolean(this, R.attr.showButtonBar, false);
        showButtonBar(showButtonBar);
    }

    /**
     * Obtains the text of the next button from the activity's theme.
     */
    private void obtainNextButtonText() {
        CharSequence text;

        try {
            text = ThemeUtil.getText(this, R.attr.nextButtonText);
        } catch (NotFoundException e) {
            text = getText(R.string.next_button_text);
        }

        setNextButtonText(text);
    }

    /**
     * Obtains the text of the back button from the activity's theme.
     */
    private void obtainBackButtonText() {
        CharSequence text;

        try {
            text = ThemeUtil.getText(this, R.attr.backButtonText);
        } catch (NotFoundException e) {
            text = getText(R.string.back_button_text);
        }

        setBackButtonText(text);
    }

    /**
     * Obtains the text of the finish button from the activity's theme.
     */
    private void obtainFinishButtonText() {
        CharSequence text;

        try {
            text = ThemeUtil.getText(this, R.attr.finishButtonText);
        } catch (NotFoundException e) {
            text = getText(R.string.finish_button_text);
        }

        setFinishButtonText(text);
    }

    /**
     * Obtains, whether the progress should be shown, when the activity is used as a wizard, or not,
     * from the activity's theme.
     */
    private void obtainShowProgress() {
        boolean showProgress = ThemeUtil.getBoolean(this, R.attr.showProgress, true);
        showProgress(showProgress);
    }

    /**
     * Obtains the string, which is used to format the progress, which is shown, when the activity
     * is used as a wizard, from the activity's theme.
     */
    private void obtainProgressFormat() {
        String progressFormat;

        try {
            progressFormat = ThemeUtil.getString(this, R.attr.progressFormat);
        } catch (NotFoundException e) {
            progressFormat = getString(R.string.progress_format);
        }

        setProgressFormat(progressFormat);
    }

    /**
     * Obtains the visibility of the toolbar, which is used to show the breadcrumb of the currently
     * selected navigation preference, from the activity's theme.
     */
    private void obtainBreadCrumbVisibility() {
        boolean hide = ThemeUtil.getBoolean(this, R.attr.hideBreadCrumb, false);
        hideBreadCrumb(hide);
    }

    /**
     * Obtains the elevation of the activity's toolbar from the activity's theme.
     */
    private void obtainToolbarElevation() {
        int elevation;

        try {
            elevation = ThemeUtil.getDimensionPixelSize(this, R.attr.toolbarElevation);
        } catch (NotFoundException e) {
            elevation = getResources().getDimensionPixelSize(R.dimen.toolbar_elevation);
        }

        setToolbarElevation(pixelsToDp(this, elevation));
    }

    /**
     * Obtains the elevation of the toolbar, which is used to show the bread crumb of the currently
     * selected preference fragment, when using the split screen layout.
     */
    private void obtainBreadcrumbElevation() {
        int elevation;

        try {
            elevation = ThemeUtil.getDimensionPixelSize(this, R.attr.breadCrumbElevation);
        } catch (NotFoundException e) {
            elevation = getResources().getDimensionPixelSize(R.dimen.bread_crumb_toolbar_elevation);
        }

        setBreadCrumbElevation(pixelsToDp(this, elevation));
    }

    /**
     * Obtains the elevation of the card view, which contains the currently shown preference
     * fragment, when using the split screen layout, from the activity's theme.
     */
    private void obtainCardViewElevation() {
        int elevation;

        try {
            elevation = ThemeUtil.getDimensionPixelSize(this, R.attr.cardViewElevation);
        } catch (NotFoundException e) {
            elevation = getResources().getDimensionPixelSize(R.dimen.card_view_elevation);
        }

        setCardViewElevation(pixelsToDp(this, elevation));
    }

    /**
     * Obtains the elevation of the button bar, which is shown when using the activity as a wizard,
     * from the activity's theme.
     */
    private void obtainButtonBarElevation() {
        int elevation;

        try {
            elevation = ThemeUtil.getDimensionPixelSize(this, R.attr.buttonBarElevation);
        } catch (NotFoundException e) {
            elevation = getResources().getDimensionPixelSize(R.dimen.button_bar_elevation);
        }

        setButtonBarElevation(pixelsToDp(this, elevation));
    }

    /**
     * Obtains the background color of the card view, which contains the currently shown preference
     * fragment, when using the split screen layout, from the activity's theme.
     */
    private void obtainCardViewBackgroundColor() {
        int color;

        try {
            color = ThemeUtil.getColor(this, R.attr.cardViewBackgroundColor);
        } catch (NotFoundException e) {
            color = ContextCompat.getColor(this, R.color.card_view_background_light);
        }

        setCardViewBackgroundColor(color);
    }

    /**
     * Obtains the background color of the toolbar, which is used to show the bread crumb of the
     * currently selected navigation preference, when using the split screen layout, from the
     * activity's theme.
     */
    private void obtainBreadCrumbBackgroundColor() {
        int color;

        try {
            color = ThemeUtil.getColor(this, R.attr.breadCrumbBackgroundColor);
        } catch (NotFoundException e) {
            color = ContextCompat.getColor(this, R.color.bread_crumb_background_light);
        }

        setBreadCrumbBackgroundColor(color);
    }

    /**
     * Obtains the background of the button bar from the activity's theme.
     */
    private void obtainButtonBarBackground() {
        try {
            setButtonBarBackgroundColor(ThemeUtil.getColor(this, R.attr.buttonBarBackground));
        } catch (NotFoundException e) {
            int resourceId = ThemeUtil.getResId(this, R.attr.buttonBarBackground, -1);

            if (resourceId != -1) {
                setButtonBarBackground(resourceId);
            } else {
                setButtonBarBackground(null);
            }
        }
    }

    /**
     * Obtains the background of the navigation from the activity's theme.
     */
    private void obtainNavigationBackground() {
        try {
            setNavigationBackgroundColor(ThemeUtil.getColor(this, R.attr.navigationBackground));
        } catch (NotFoundException e) {
            int resourceId = ThemeUtil.getResId(this, R.attr.navigationBackground, -1);

            if (resourceId != -1) {
                setNavigationBackground(resourceId);
            } else {
                setNavigationBackground(null);
            }
        }
    }

    /**
     * Obtains the background color of the currently selected navigation preference from the
     * activity's theme.
     */
    private void obtainNavigationSelectionColor() {
        int color;

        try {
            color = ThemeUtil.getColor(this, R.attr.navigationSelectionColor);
        } catch (NotFoundException e) {
            color = ContextCompat.getColor(this, R.color.preference_selection_color_light);
        }

        setNavigationSelectionColor(color);
    }

    /**
     * Obtains the color of the dividers, which are contained by the navigation.
     */
    private void obtainNavigationDividerColor() {
        int color;

        try {
            color = ThemeUtil.getColor(this, R.attr.navigationDividerColor);
        } catch (NotFoundException e) {
            color = ContextCompat.getColor(this, R.color.preference_divider_color_light);
        }

        setNavigationDividerColor(color);
    }

    /**
     * Handles intent extras, that allow to initially display a specific fragment.
     *
     * @return True, if a fragment is initially shown, false otherwise
     */
    private boolean handleShowFragmentIntent() {
        String initialFragment = getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT);
        return showInitialFragment(initialFragment);
    }

    /**
     * Initially displays a specific fragment.
     *
     * @param initialFragment
     *         The fully classified class name of the preference fragment, which should be shown, as
     *         a {@link String} or null, if no fragment should be initially shown
     * @return True, if a fragment has been shown, false otherwise
     */
    private boolean showInitialFragment(@Nullable final String initialFragment) {
        if (!TextUtils.isEmpty(initialFragment)) {
            for (int i = 0; i < navigationFragment.getNavigationPreferenceCount(); i++) {
                NavigationPreference navigationPreference =
                        navigationFragment.getNavigationPreference(i);

                if (navigationPreference != null && navigationPreference.getFragment() != null &&
                        navigationPreference.getFragment().equals(initialFragment)) {
                    Bundle arguments = getIntent().getBundleExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS);
                    CharSequence title =
                            getCharSequenceFromIntent(getIntent(), EXTRA_SHOW_FRAGMENT_TITLE);

                    if (title != null) {
                        if (arguments == null) {
                            arguments = new Bundle();
                        }

                        arguments.putCharSequence(EXTRA_SHOW_FRAGMENT_TITLE, title);
                    }

                    navigationFragment.selectNavigationPreference(i, arguments);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the char sequence, which is specified by a specific intent extra. The char sequence
     * can either be specified as a string or as a resource id.
     *
     * @param intent
     *         The intent, which specifies the char sequence, as an instance of the class {@link
     *         Intent}. The intent may not be null
     * @param name
     *         The name of the intent extra, which specifies the char sequence, as a {@link String}.
     *         The name may not be null
     * @return The char sequence, which is specified by the given intent, as an instance of the
     * class {@link CharSequence} or null, if the intent does not specify a char sequence with the
     * given name
     */
    private CharSequence getCharSequenceFromIntent(@NonNull final Intent intent,
                                                   @NonNull final String name) {
        CharSequence charSequence = intent.getCharSequenceExtra(name);

        if (charSequence == null) {
            int resourceId = intent.getIntExtra(name, 0);

            if (resourceId != 0) {
                charSequence = getText(resourceId);
            }
        }

        return charSequence;
    }

    /**
     * Handles extras the intent, which has been used to start the activity.
     */
    private void handleIntent() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            handleHideNavigationIntent(extras);
            handleShowButtonBarIntent(extras);
            handleNextButtonTextIntent(extras);
            handleBackButtonTextIntent(extras);
            handleFinishButtonTextIntent(extras);
            handleShowProgressIntent(extras);
            handleProgressFormatIntent(extras);
            handleNoBreadcrumbsIntent(extras);
        }
    }

    /**
     * Handles the intent extra, which specifies, whether the navigation should be shown, or not.
     *
     * @param extras
     *         The extras of the intent, which has been used to start the activity, as an instance
     *         of the class {@link Bundle}. The bundle may not be null
     */
    private void handleHideNavigationIntent(@NonNull final Bundle extras) {
        if (extras.containsKey(EXTRA_HIDE_NAVIGATION)) {
            hideNavigation(extras.getBoolean(EXTRA_HIDE_NAVIGATION));
        }
    }

    /**
     * Handles the intent extra, which specifies, whether the button bar should be shown, or not.
     *
     * @param extras
     *         The extras of the intent, which has been used to start the activity, as an instance
     *         of the class {@link Bundle}. The bundle may not be null
     */
    private void handleShowButtonBarIntent(@NonNull final Bundle extras) {
        if (extras.containsKey(EXTRA_SHOW_BUTTON_BAR)) {
            showButtonBar(extras.getBoolean(EXTRA_SHOW_BUTTON_BAR));
        }
    }

    /**
     * Handles the intent extra, which specifies the text of the next button.
     *
     * @param extras
     *         The extras of the intent, which has been used to start the activity, as an instance
     *         of the class {@link Bundle}. The bundle may not be null
     */
    private void handleNextButtonTextIntent(@NonNull final Bundle extras) {
        CharSequence text = extras.getString(EXTRA_NEXT_BUTTON_TEXT);

        if (!TextUtils.isEmpty(text)) {
            setNextButtonText(text);
        }
    }

    /**
     * Handles the intent extra, which specifies the text of the back button.
     *
     * @param extras
     *         The extras of the intent, which has been used to start the activity, as an instance
     *         of the class {@link Bundle}. The bundle may not be null
     */
    private void handleBackButtonTextIntent(@NonNull final Bundle extras) {
        CharSequence text = extras.getString(EXTRA_BACK_BUTTON_TEXT);

        if (!TextUtils.isEmpty(text)) {
            setBackButtonText(text);
        }
    }

    /**
     * Handles the intent extra, which specifies the text of the finish button.
     *
     * @param extras
     *         The extras of the intent, which has been used to start the activity, as an instance
     *         of the class {@link Bundle}. The bundle may not be null
     */
    private void handleFinishButtonTextIntent(@NonNull final Bundle extras) {
        CharSequence text = extras.getString(EXTRA_FINISH_BUTTON_TEXT);

        if (!TextUtils.isEmpty(text)) {
            setFinishButtonText(text);
        }
    }

    /**
     * Handles the intent extra, which specifies, whether the progress should be shown, when the
     * activity is used as a wizard, or not.
     *
     * @param extras
     *         The extras of the intent, which has been used to start the activity, as an instance
     *         of the class {@link Bundle}. The bundle may not be null
     */
    private void handleShowProgressIntent(@NonNull final Bundle extras) {
        if (extras.containsKey(EXTRA_SHOW_PROGRESS)) {
            showProgress(extras.getBoolean(EXTRA_SHOW_PROGRESS));
        }
    }

    /**
     * Handles the intent extra, which specifies the format of the progress, which is shown, when
     * the activity is used as a wizard.
     *
     * @param extras
     *         The extras of the intent, which has been used to start the activity, as an instance
     *         of the class {@link Bundle}. The bundle may not be null
     */
    private void handleProgressFormatIntent(@NonNull final Bundle extras) {
        String progressFormat = extras.getString(EXTRA_PROGRESS_FORMAT);

        if (!TextUtils.isEmpty(progressFormat)) {
            setProgressFormat(progressFormat);
        }
    }

    /**
     * Handles the intent extra, which specifies whether bread crumbs should be shown, not.
     *
     * @param extras
     *         The extras of the intent, which has been used to start the activity, as an instance
     *         of the class {@link Bundle}. The bundle may not be null
     */
    private void handleNoBreadcrumbsIntent(@NonNull final Bundle extras) {
        if (extras.containsKey(EXTRA_NO_BREAD_CRUMBS)) {
            hideBreadCrumb(extras.getBoolean(EXTRA_NO_BREAD_CRUMBS));
        }
    }

    /**
     * Inflates the activity's layout, depending on whether the split screen layout is used, or
     * not.
     */
    private void inflateLayout() {
        setContentView(isSplitScreen() ? R.layout.preference_activity_tablet :
                R.layout.preference_activity_phone);
        frameLayout = findViewById(R.id.frame_layout);
        navigationFragmentContainer = findViewById(R.id.navigation_fragment_container);
        cardView = findViewById(R.id.card_view);
        toolbar = findViewById(R.id.toolbar);
        toolbarLarge = findViewById(R.id.large_toolbar);
        breadCrumbToolbar = findViewById(R.id.bread_crumb_toolbar);
        buttonBar = findViewById(R.id.wizard_button_bar);
        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(createNextButtonListener());
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(createBackButtonListener());
        finishButton = findViewById(R.id.finish_button);
        finishButton.setOnClickListener(createFinishButtonListener());
        buttonBarShadowView = findViewById(R.id.wizard_button_bar_shadow_view);
        toolbarShadowView = findViewById(R.id.toolbar_shadow_view);
        breadCrumbShadowView = findViewById(R.id.bread_crumb_shadow_view);
    }

    /**
     * Initializes the activity's toolbar.
     */
    private void initializeToolbar() {
        Condition.ensureTrue(getSupportActionBar() == null,
                "An action bar is already attached to the activity. Use the theme " +
                        "\"@style/Theme.AppCompat.NoActionBar\" or " +
                        "\"@style/Theme.AppCompat.Light.NoActionBar\" as the activity's theme",
                IllegalStateException.class);
        if (isSplitScreen()) {
            toolbarLarge.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(toolbar);
        resetTitle();
    }

    /**
     * Initializes the activity's fragments.
     */
    private void initializeFragments() {
        navigationFragment = (NavigationFragment) getSupportFragmentManager()
                .findFragmentByTag(NAVIGATION_FRAGMENT_TAG);

        if (navigationFragment == null) {
            navigationFragment = (NavigationFragment) Fragment
                    .instantiate(this, NavigationFragment.class.getName());
            navigationFragment.setRetainInstance(true);
            navigationFragment.setCallback(this);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.navigation_fragment_container, navigationFragment,
                    NAVIGATION_FRAGMENT_TAG);
            transaction.commit();
        } else if (!navigationFragment.isAdapterCreated()) {
            navigationFragment.setCallback(this);
        }

        navigationFragment.setAdapterCallback(this);
        preferenceFragment = getSupportFragmentManager().findFragmentByTag(PREFERENCE_FRAGMENT_TAG);
        adaptNavigationSelectionColor();
        adaptNavigationDividerColor();
        adaptNavigationEnabledState();
    }

    /**
     * Shows the fragment, which is associated with a specific navigation preference.
     *
     * @param navigationPreference
     *         The navigation preference, whose fragment should be shown, as an instance of the
     *         class {@link NavigationPreference}. The navigation preference may not be null
     * @param arguments
     *         The arguments, which should be passed to the fragment, as an instance of the class
     *         {@link Bundle} or null, if the navigation preferences's extras should be used
     *         instead
     */
    private void showPreferenceFragment(@NonNull final NavigationPreference navigationPreference,
                                        @Nullable final Bundle arguments) {
        if (arguments != null && navigationPreference.getExtras() != null) {
            arguments.putAll(navigationPreference.getExtras());
        }

        selectedPreferenceFragment = navigationPreference.getFragment();
        selectedPreferenceFragmentArguments =
                arguments != null ? arguments : navigationPreference.getExtras();

        if (!TextUtils.isEmpty(selectedPreferenceFragment)) {
            Fragment fragment = Fragment.instantiate(this, navigationPreference.getFragment(),
                    selectedPreferenceFragmentArguments);
            showPreferenceFragment(navigationPreference, fragment);
            showBreadCrumb(navigationPreference, selectedPreferenceFragmentArguments);
        } else {
            removePreferenceFragmentUnconditionally();

            if (isSplitScreen()) {
                showBreadCrumb(navigationPreference, selectedPreferenceFragmentArguments);
            }
        }

        adaptWizardButtonVisibilities();
    }

    /**
     * Shows a specific preference fragment.
     *
     * @param navigationPreference
     *         The navigation preference, the fragment, which should be shown, is associated with,
     *         as an instance of the class {@link NavigationPreference}. The navigation preference
     *         may not be null
     * @param fragment
     *         The fragment, which should be shown, as an instance of the class Fragment. The
     *         fragment may not be null
     */
    private void showPreferenceFragment(@NonNull final NavigationPreference navigationPreference,
                                        @NonNull final Fragment fragment) {
        fragment.setRetainInstance(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (!isSplitScreen()) {
            transaction.hide(navigationFragment);

            if (preferenceFragment != null) {
                transaction.remove(preferenceFragment);
                notifyOnPreferenceFragmentHidden(preferenceFragment);
            }

            transaction.add(R.id.navigation_fragment_container, fragment, PREFERENCE_FRAGMENT_TAG);
        } else {
            if (preferenceFragment != null) {
                notifyOnPreferenceFragmentHidden(preferenceFragment);
            }

            transaction
                    .replace(R.id.preference_fragment_container, fragment, PREFERENCE_FRAGMENT_TAG);
        }

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        this.preferenceFragment = fragment;
        showToolbarNavigationIcon();
        adaptBreadCrumbVisibility(selectedPreferenceFragmentArguments);
        notifyOnPreferenceFragmentShown(navigationPreference, fragment);
    }

    /**
     * Removes the currently shown preference fragment, if the split screen layout is not used and
     * the navigation is not hidden.
     *
     * @return True, if a preference fragment has been removed, false otherwise
     */
    private boolean removePreferenceFragment() {
        if (!isSplitScreen() && isPreferenceFragmentShown() && !isNavigationHidden() &&
                !isButtonBarShown()) {
            navigationFragment.selectNavigationPreference(-1, null);
            removePreferenceFragmentUnconditionally();
            selectedPreferenceFragment = null;
            selectedPreferenceFragmentArguments = null;
            return true;
        }

        return false;
    }

    /**
     * Removes the currently preference fragment, regardless of whether the split screen layout is
     * used, or not.
     */
    private void removePreferenceFragmentUnconditionally() {
        if (isPreferenceFragmentShown()) {
            resetTitle();
            hideToolbarNavigationIcon();
            adaptBreadCrumbVisibility();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(preferenceFragment);

            if (!isSplitScreen()) {
                transaction.show(navigationFragment);
            }

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();
            notifyOnPreferenceFragmentHidden(preferenceFragment);
            preferenceFragment = null;
        }
    }

    /**
     * Shows the bread crumb of a specific navigation preference. When using the split screen
     * layout, the bread crumb is shown above the currently shown preference fragment, otherwise the
     * bread crumb is shown as the toolbar title.
     *
     * @param navigationPreference
     *         The navigation preference, whose bread crumb should be shown, as an instance of the
     *         class {@link NavigationPreference}. The navigation preference may not be null
     * @param arguments
     *         The arguments, which are passed to the fragment, which is associated with the
     *         navigation preference, as an instance of the class {@link Bundle} or null, if no
     *         arguments are passed to the fragment
     */
    private void showBreadCrumb(@NonNull final NavigationPreference navigationPreference,
                                @Nullable final Bundle arguments) {
        CharSequence breadCrumbTitle = null;

        if (arguments != null && arguments.containsKey(EXTRA_SHOW_FRAGMENT_TITLE)) {
            breadCrumbTitle = arguments.getCharSequence(EXTRA_SHOW_FRAGMENT_TITLE);
        }

        if (TextUtils.isEmpty(breadCrumbTitle)) {
            breadCrumbTitle = navigationPreference.getBreadCrumbTitle();

            if (TextUtils.isEmpty(breadCrumbTitle)) {
                breadCrumbTitle = navigationPreference.getTitle();

                if (TextUtils.isEmpty(breadCrumbTitle)) {
                    breadCrumbTitle = getTitle();
                }
            }
        }

        showBreadCrumb(breadCrumbTitle);
    }

    /**
     * Shows a specific bread crumb. When using the split screen layout, the bread crumb is shown
     * above the currently shown preference fragment, otherwise the bread crumb is shown as the
     * toolbar title.
     *
     * @param breadCrumbTitle
     *         The bread crumb title, which should be shown, as an instance of the type {@link
     *         CharSequence} or null, if no bread crumb should be shown
     */
    private void showBreadCrumb(@Nullable final CharSequence breadCrumbTitle) {
        CharSequence formattedBreadCrumbTitle = formatBreadCrumbTitle(breadCrumbTitle);

        if (isSplitScreen()) {
            breadCrumbToolbar.setTitle(formattedBreadCrumbTitle);
        } else if (!TextUtils.isEmpty(formattedBreadCrumbTitle)) {
            showTitle(formattedBreadCrumbTitle);
        }
    }

    /**
     * Formats a specific bread crumb title, depending on whether the activity is used as a wizard
     * and whether the progress should be shown, or not.
     *
     * @param breadCrumbTitle
     *         The bread crumb title, which should be formatted, as an instance of the class {@link
     *         CharSequence} or null, if no bread crumb title should be shown
     * @return The formatted bread crumb title as an instance of the class {@link CharSequence} or
     * null, if no bread crumb title should be shown
     */
    @Nullable
    private CharSequence formatBreadCrumbTitle(@Nullable final CharSequence breadCrumbTitle) {
        if (!TextUtils.isEmpty(breadCrumbTitle) && isButtonBarShown() &&
                navigationFragment != null) {
            String format = getProgressFormat();
            int selectedNavigationPreferenceIndex =
                    navigationFragment.getSelectedNavigationPreferenceIndex();

            if (!TextUtils.isEmpty(format) && selectedNavigationPreferenceIndex != -1) {
                int currentStep = selectedNavigationPreferenceIndex + 1;
                int totalSteps = navigationFragment.getNavigationPreferenceCount();
                return String.format(format, currentStep, totalSteps, breadCrumbTitle);
            }
        }

        return breadCrumbTitle;
    }

    /**
     * Shows a specific title.
     *
     * @param title
     *         The title, which should be shown, as an instance of the type {@link CharSequence} or
     *         null, if no title should be shown
     */
    private void showTitle(@Nullable final CharSequence title) {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            if (isSplitScreen()) {
                if (toolbarLarge != null) {
                    toolbarLarge.setTitle(title);
                }

                actionBar.setTitle(null);
            } else {
                actionBar.setTitle(title);
            }
        }
    }

    /**
     * Resets the title of the activity.
     */
    private void resetTitle() {
        setTitle(getTitle());
    }

    /**
     * Returns, whether the navigation icon of the activity's toolbar is currently shown, or not.
     *
     * @return True, if the navigation icon of the activity's toolbar is currently shown, false
     * otherwise
     */
    private boolean isDisplayHomeAsUpEnabled() {
        return getSupportActionBar() != null &&
                (getSupportActionBar().getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) ==
                        ActionBar.DISPLAY_HOME_AS_UP;
    }

    /**
     * Shows the navigation icon of the activity's toolbar.
     */
    private void showToolbarNavigationIcon() {
        if (isPreferenceFragmentShown() && isNavigationIconOverridden() && !isNavigationHidden() &&
                !(!isSplitScreen() && isButtonBarShown())) {
            displayHomeAsUp = isDisplayHomeAsUpEnabled();
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    /**
     * Hides the navigation icon of the activity's toolbar, respectively sets it to the previous
     * icon.
     */
    private void hideToolbarNavigationIcon() {
        if (!displayHomeAsUp) {
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setHomeButtonEnabled(false);
            }
        }
    }

    /**
     * Sets the start margin of specific layout params.
     *
     * @param layoutParams
     *         The layout params whose margin should be set, as an instance of the class {@link
     *         FrameLayout.LayoutParams}. The layout params may not be null
     * @param margin
     *         The start margin, which should be set, in pixels as an {@link Integer} valueF
     */
    private void setMarginStart(@NonNull final FrameLayout.LayoutParams layoutParams,
                                final int margin) {
        if (isRtlLayoutUsed()) {
            layoutParams.rightMargin = margin;
        } else {
            layoutParams.leftMargin = margin;
        }
    }

    /**
     * Sets the end margin of specific layout params.
     *
     * @param layoutParams
     *         The layout params whose margin should be set, as an instance of the class {@link
     *         FrameLayout.LayoutParams}. The layout params may not be null
     * @param margin
     *         The end margin, which should be set, in pixels as an {@link Integer} valueF
     */
    private void setMarginEnd(@NonNull final FrameLayout.LayoutParams layoutParams,
                              final int margin) {
        if (isRtlLayoutUsed()) {
            layoutParams.leftMargin = margin;
        } else {
            layoutParams.rightMargin = margin;
        }
    }

    /**
     * Returns, whether the activity uses a right-to-left (RTL) layout, or not.
     *
     * @return True, if the activity uses a RTL layout, false otherwise
     */
    private boolean isRtlLayoutUsed() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    /**
     * Adapts, whether the split screen layout is used, or not.
     */
    private void adaptSplitScreen() {
        if (navigationFragmentContainer != null) {
            recreate();
        }
    }

    /**
     * Adapts the width of the navigation.
     */
    private void adaptNavigationWidth() {
        if (frameLayout != null && navigationFragmentContainer != null && cardView != null &&
                toolbarLarge != null) {
            ViewCompat.setPaddingRelative(navigationFragmentContainer, 0, 0,
                    getDisplayWidth(this) - navigationWidth, 0);

            if (!isNavigationHidden()) {
                toolbarLarge.setNavigationWidth(navigationWidth);
                FrameLayout.LayoutParams cardViewLayoutParams =
                        (FrameLayout.LayoutParams) cardView.getLayoutParams();
                int margin = navigationWidth -
                        getResources().getDimensionPixelSize(R.dimen.card_view_intrinsic_margin);
                setMarginStart(cardViewLayoutParams, margin);
            }
        }
    }

    /**
     * Adapts the visibility of the navigation.
     */
    private void adaptNavigationVisibility() {
        if (isSplitScreen()) {
            if (navigationFragmentContainer != null && cardView != null && toolbarLarge != null) {
                navigationFragmentContainer
                        .setVisibility(isNavigationHidden() ? View.GONE : View.VISIBLE);
                toolbarLarge.hideNavigation(isNavigationHidden());
                int preferenceScreenHorizontalMargin =
                        getResources().getDimensionPixelSize(R.dimen.card_view_horizontal_margin);
                int preferenceScreenMarginRight =
                        getResources().getDimensionPixelSize(R.dimen.card_view_margin_right);
                int cardViewIntrinsicMargin =
                        getResources().getDimensionPixelSize(R.dimen.card_view_intrinsic_margin);
                FrameLayout.LayoutParams cardViewLayoutParams =
                        (FrameLayout.LayoutParams) cardView.getLayoutParams();
                cardViewLayoutParams.gravity =
                        isNavigationHidden() ? Gravity.CENTER_HORIZONTAL : Gravity.NO_GRAVITY;
                int marginStart = (isNavigationHidden() ? preferenceScreenHorizontalMargin :
                        navigationWidth) - cardViewIntrinsicMargin;
                int marginEnd = (isNavigationHidden() ? preferenceScreenHorizontalMargin :
                        preferenceScreenMarginRight) - cardViewIntrinsicMargin;
                setMarginStart(cardViewLayoutParams, marginStart);
                setMarginEnd(cardViewLayoutParams, marginEnd);
            }
        } else {
            if (getSelectedNavigationPreference() != null) {
                if (isNavigationHidden()) {
                    hideToolbarNavigationIcon();
                } else {
                    showToolbarNavigationIcon();
                }
            } else if (isNavigationHidden() && navigationFragment != null &&
                    navigationFragment.getCallback() == null) {
                if (navigationFragment.getNavigationPreferenceCount() > 0) {
                    navigationFragment.selectNavigationPreference(0, null);
                } else {
                    finish();
                }
            }
        }
    }

    /**
     * Adapts the visibility of the button bar, which is shown, when the activity is used as a
     * wizard.
     */
    private void adaptButtonBarVisibility() {
        if (buttonBar != null && buttonBarShadowView != null) {
            buttonBar.setVisibility(isButtonBarShown() ? View.VISIBLE : View.GONE);
            buttonBarShadowView.setVisibility(isButtonBarShown() ? View.VISIBLE : View.GONE);

            if (isButtonBarShown() && !isSplitScreen()) {
                adaptNavigationVisibility();
            }

            adaptNavigationEnabledState();
            adaptWizardButtonVisibilities();
        }
    }

    /**
     * Adapts the visibilities of the buttons of the button bar which is shown, when the activity is
     * used as a wizard, depending on the currently selected navigation preference.
     */
    private void adaptWizardButtonVisibilities() {
        if (buttonBar != null && backButton != null && nextButton != null && finishButton != null &&
                navigationFragment != null) {
            int selectedNavigationPreferenceIndex =
                    navigationFragment.getSelectedNavigationPreferenceIndex();

            if (selectedNavigationPreferenceIndex != -1 && isButtonBarShown()) {
                int navigationPreferenceCount = navigationFragment.getNavigationPreferenceCount();
                backButton.setVisibility(
                        (selectedNavigationPreferenceIndex != 0) ? View.VISIBLE : View.GONE);
                nextButton.setVisibility(
                        (selectedNavigationPreferenceIndex != navigationPreferenceCount - 1) ?
                                View.VISIBLE : View.GONE);
                finishButton.setVisibility(
                        (selectedNavigationPreferenceIndex == navigationPreferenceCount - 1) ?
                                View.VISIBLE : View.GONE);
            } else if (isButtonBarShown()) {
                backButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                finishButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Adapts the text of the next button.
     */
    private void adaptNextButtonText() {
        if (nextButton != null) {
            nextButton.setText(nextButtonText);
        }
    }

    /**
     * Adapts the text of the back button.
     */
    private void adaptBackButtonText() {
        if (backButton != null) {
            backButton.setText(backButtonText);
        }
    }

    /**
     * Adapts the text of the finish button.
     */
    private void adaptFinishButtonText() {
        if (finishButton != null) {
            finishButton.setText(finishButtonText);
        }
    }

    /**
     * Adapts the progress which is shown, when the activity is used as a wizard.
     */
    private void adaptProgress() {
        NavigationPreference selectedNavigationPreference = getSelectedNavigationPreference();

        if (selectedNavigationPreference != null) {
            showBreadCrumb(selectedNavigationPreference, null);
        }
    }

    /**
     * Adapts the visibility of the toolbar, which is used to show the breadcrumb of the currently
     * selected navigation preference, depending on the arguments of the currently selected
     * navigation preference.
     *
     * @param arguments
     *         The arguments of the currently selected navigation preference as an instance of the
     *         class {@link Bundle} or null, if the navigation preference has no arguments
     */
    private void adaptBreadCrumbVisibility(@Nullable final Bundle arguments) {
        if (arguments != null && arguments.containsKey(EXTRA_NO_BREAD_CRUMBS)) {
            boolean hideBreadCrumb = arguments.getBoolean(EXTRA_NO_BREAD_CRUMBS, false);
            adaptBreadCrumbVisibility(hideBreadCrumb);
        } else {
            adaptBreadCrumbVisibility();
        }
    }

    /**
     * Adapts the visibility of the toolbar, which is used to show the breadcrumb of the currently
     * selected navigation preference.
     */
    private void adaptBreadCrumbVisibility() {
        adaptBreadCrumbVisibility(hideBreadCrumb);
    }

    /**
     * Adapts the visibility of the toolbar, which is used to show the breadcrumb of the currently
     * selected navigation preference.
     *
     * @param hideBreadCrumb
     *         True, if the toolbar, which is used to show the bread crumb of the currently selected
     *         navigation preference, should be hidden, false otherwise
     */
    private void adaptBreadCrumbVisibility(final boolean hideBreadCrumb) {
        if (isSplitScreen()) {
            if (breadCrumbToolbar != null && breadCrumbShadowView != null) {
                breadCrumbToolbar.setVisibility(hideBreadCrumb ? View.GONE : View.VISIBLE);
                breadCrumbShadowView.setVisibility(hideBreadCrumb ? View.GONE : View.VISIBLE);
            }
        } else {
            if (toolbar != null && toolbarShadowView != null) {
                toolbar.setVisibility(hideBreadCrumb ? View.GONE : View.VISIBLE);
                toolbarShadowView.setVisibility(hideBreadCrumb ? View.GONE : View.VISIBLE);
            }
        }
    }

    /**
     * Adapts the elevation of the activity's toolbar.
     */
    private void adaptToolbarElevation() {
        if (toolbarShadowView != null) {
            toolbarShadowView.setShadowElevation(toolbarElevation);
        }
    }

    /**
     * Adapts the elevation of the toolbar, which is used to show the bread crumb of the currently
     * selected preference fragment, when using the split screen layout.
     */
    private void adaptBreadCrumbElevation() {
        if (breadCrumbShadowView != null) {
            breadCrumbShadowView.setShadowElevation(breadCrumbElevation);
        }
    }

    /**
     * Adapts the elevation of the card view, which contains the currently shown preference
     * fragment, when using the split screen layout.
     */
    private void adaptCardViewElevation() {
        if (cardView != null) {
            cardView.setCardElevation(dpToPixels(this, cardViewElevation));
        }
    }

    /**
     * Adapts the elevation of the button bar, which is shown when using the activity as a wizard.
     */
    private void adaptButtonBarElevation() {
        if (buttonBarShadowView != null) {
            buttonBarShadowView.setShadowElevation(buttonBarElevation);
        }
    }

    /**
     * Adapts the background color of the card view, which contains the currently shown preference
     * fragment, when using the split screen layout.
     */
    private void adaptCardViewBackgroundColor() {
        if (cardView != null) {
            cardView.setCardBackgroundColor(cardViewBackgroundColor);
        }
    }

    /**
     * Adapts the background color of the toolbar, which is used to show the bread crumb of the
     * currently selected navigation preferences, when using the split screen layout.
     */
    private void adaptBreadCrumbBackgroundColor() {
        if (breadCrumbToolbar != null) {
            GradientDrawable background = (GradientDrawable) ContextCompat
                    .getDrawable(this, R.drawable.breadcrumb_background);
            background.setColor(breadCrumbBackgroundColor);
            ViewUtil.setBackground(getBreadCrumbToolbar(), background);
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
     * Adapts the background of the navigation.
     */
    private void adaptNavigationBackground() {
        if (navigationFragmentContainer != null) {
            ViewUtil.setBackground(navigationFragmentContainer, navigationBackground);
        }
    }

    /**
     * Adapts the background color of the currently selected navigation preference.
     */
    private void adaptNavigationSelectionColor() {
        if (navigationFragment != null) {
            navigationFragment.setSelectionColor(navigationSelectionColor);
        }
    }

    /**
     * Adapts the color of the divider's, which are contained by the navigation.
     */
    private void adaptNavigationDividerColor() {
        if (navigationFragment != null) {
            navigationFragment.setDividerColor(navigationDividerColor);
        }
    }

    /**
     * Adapts, whether the navigation is enabled, i.e. clickable, or not.
     */
    private void adaptNavigationEnabledState() {
        if (navigationFragment != null) {
            navigationFragment.setEnabled(!isButtonBarShown());
        }
    }

    /**
     * Returns a listener, which allows to proceed to the next step, when the activity is used as a
     * wizard.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * View.OnClickListener}. The listener may not be null
     */
    @NonNull
    private View.OnClickListener createNextButtonListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (navigationFragment != null) {
                    int currentIndex = navigationFragment.getSelectedNavigationPreferenceIndex();

                    if (currentIndex < navigationFragment.getNavigationPreferenceCount() - 1) {
                        Bundle params = notifyOnNextStep();

                        if (params != null) {
                            navigationFragment.selectNavigationPreference(currentIndex + 1, params);
                        }
                    }
                }
            }

        };
    }

    /**
     * Returns a listener, which allows to resume to the previous step, when the activity is used as
     * a wizard.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * View.OnClickListener}. The listener may not be null
     */
    @NonNull
    private View.OnClickListener createBackButtonListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (navigationFragment != null) {
                    int currentIndex = navigationFragment.getSelectedNavigationPreferenceIndex();

                    if (currentIndex > 0) {
                        Bundle params = notifyOnPreviousStep();

                        if (params != null) {
                            navigationFragment.selectNavigationPreference(currentIndex - 1, params);
                        }
                    }
                }
            }

        };
    }

    /**
     * Returns a listener, which allows to finish the last step, when the activity is used as a
     * wizard.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * View.OnClickListener}. The listener may not be null
     */
    @NonNull
    private View.OnClickListener createFinishButtonListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                notifyOnFinish();
            }

        };
    }

    /**
     * Notifies all registered listeners that a preference fragment has been shown.
     *
     * @param navigationPreference
     *         The navigation preference, the fragment, which has been shown, is associated with, as
     *         an instance of the class {@link NavigationPreference}. The navigation preference may
     *         not be null
     * @param fragment
     *         The fragment, which has been shown, as an instance of the class Fragment. The
     *         fragment may not be null
     */
    private void notifyOnPreferenceFragmentShown(
            @NonNull final NavigationPreference navigationPreference,
            @NonNull final Fragment fragment) {
        for (PreferenceFragmentListener listener : preferenceFragmentListeners) {
            listener.onPreferenceFragmentShown(navigationPreference, fragment);
        }
    }

    /**
     * Notifies all registered listeners that a preference fragment has been hidden.
     *
     * @param fragment
     *         The fragment, which has been hidden, as an instance of the class Fragment. The
     *         fragment may not be null
     */
    private void notifyOnPreferenceFragmentHidden(@NonNull final Fragment fragment) {
        for (PreferenceFragmentListener listener : preferenceFragmentListeners) {
            listener.onPreferenceFragmentHidden(fragment);
        }
    }

    /**
     * Notifies all registered listeners, that the user wants to navigate to the next step of the
     * wizard.
     *
     * @return A bundle, which may contain key-value pairs, which have been acquired in the wizard,
     * if navigating to the next step of the wizard should be allowed, as an instance of the class
     * {@link Bundle}, null otherwise
     */
    private Bundle notifyOnNextStep() {
        Bundle result = null;
        NavigationPreference selectedNavigationPreference =
                navigationFragment.getSelectedNavigationPreference();

        if (selectedNavigationPreference != null && preferenceFragment != null) {
            for (WizardListener listener : wizardListeners) {
                Bundle bundle =
                        listener.onNextStep(selectedNavigationPreference, preferenceFragment,
                                selectedPreferenceFragmentArguments);

                if (bundle != null) {
                    if (result == null) {
                        result = new Bundle();
                    }

                    result.putAll(bundle);
                }
            }
        }

        return result;
    }

    /**
     * Notifies all registered listeners that the user wants to navigate to the previous step of the
     * wizard.
     *
     * @return A bundle, which may contain key-value pairs, which have been acquired in the wizard,
     * if navigating to the previous step of the wizard should be allowed, as an instance of the
     * class {@link Bundle}, null otherwise
     */
    private Bundle notifyOnPreviousStep() {
        Bundle result = null;
        NavigationPreference selectedNavigationPreference =
                navigationFragment.getSelectedNavigationPreference();

        if (selectedNavigationPreference != null && preferenceFragment != null) {
            for (WizardListener listener : wizardListeners) {
                Bundle bundle =
                        listener.onPreviousStep(selectedNavigationPreference, preferenceFragment,
                                selectedPreferenceFragmentArguments);

                if (bundle != null) {
                    if (result == null) {
                        result = new Bundle();
                    }

                    result.putAll(bundle);
                }
            }
        }

        return result;
    }

    /**
     * Notifies all registered listeners that the user wants to finish the last step of the wizard.
     *
     * @return True, if finishing the wizard should be allowed, false otherwise
     */
    private boolean notifyOnFinish() {
        boolean result = true;
        NavigationPreference selectedNavigationPreference =
                navigationFragment.getSelectedNavigationPreference();

        if (selectedNavigationPreference != null && preferenceFragment != null) {
            for (WizardListener listener : wizardListeners) {
                result &= listener.onFinish(selectedNavigationPreference, preferenceFragment,
                        selectedPreferenceFragmentArguments);
            }
        }

        return result;
    }

    /**
     * Notifies all registered listeners that the user wants to skip the wizard.
     *
     * @return True, if skipping the wizard should be allowed, false otherwise
     */
    private boolean notifyOnSkip() {
        boolean result = true;
        NavigationPreference selectedNavigationPreference =
                navigationFragment.getSelectedNavigationPreference();

        if (selectedNavigationPreference != null && preferenceFragment != null) {
            for (WizardListener listener : wizardListeners) {
                result &= listener.onSkip(selectedNavigationPreference, preferenceFragment,
                        selectedPreferenceFragmentArguments);
            }
        }

        return result;
    }

    /**
     * Notifies all registered listeners, that a navigation preference has been added to the
     * activity.
     *
     * @param navigationPreference
     *         The navigation preference, which has been added, as an instance of the class {@link
     *         NavigationPreference}. The navigation preference may not be null
     */
    private void notifyOnNavigationPreferenceAdded(
            @NonNull final NavigationPreference navigationPreference) {
        for (NavigationListener listener : navigationListeners) {
            listener.onNavigationPreferenceAdded(navigationPreference);
        }
    }

    /**
     * Notifies all registered listeners, that a navigation preference has been removed from the
     * activity.
     *
     * @param navigationPreference
     *         The navigation preference, which has been removed, as an instance of the class {@link
     *         NavigationPreference}. The navigation preference may not be null
     */
    private void notifyOnNavigationPreferenceRemoved(
            @NonNull final NavigationPreference navigationPreference) {
        for (NavigationListener listener : navigationListeners) {
            listener.onNavigationPreferenceRemoved(navigationPreference);
        }
    }

    /**
     * Adds a new listener, which should be notified, when the currently shown preference fragment
     * has been changed, to the activity.
     *
     * @param listener
     *         The listener, which should be added, as an instance of the type {@link
     *         PreferenceFragmentListener}. The listener may not be null
     */
    public final void addPreferenceFragmentListener(
            @NonNull final PreferenceFragmentListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        preferenceFragmentListeners.add(listener);
    }

    /**
     * Removes a specific listener, which should not be notified, when the currently shown
     * preference fragment has been changed, anymore.
     *
     * @param listener
     *         The listener, which should be removed, as an instance of the type {@link
     *         PreferenceFragmentListener}. The listener may not be null
     */
    public final void removePreferenceFragmentListener(
            @NonNull final PreferenceFragmentListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        preferenceFragmentListeners.remove(listener);
    }

    /**
     * Adds a new listener, which should be notified, when the user navigates within the activity,
     * if it is used as a wizard.
     *
     * @param listener
     *         The listener, which should be added, as an instance of the type {@link
     *         WizardListener}. The listener may not be null
     */
    public final void addWizardListener(@NonNull final WizardListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        wizardListeners.add(listener);
    }

    /**
     * Removes a specific listener, which should not be notified, when the user navigates within the
     * activity, if it is used as a wizard.
     *
     * @param listener
     *         The listener, which should be removed, as an instance of the type {@link
     *         WizardListener}. The listener may not be null
     */
    public final void removeWizardListener(@NonNull final WizardListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        wizardListeners.remove(listener);
    }

    /**
     * Adds a new listener, which should be notified, when a navigation preference has been added or
     * removed to/from the activity.
     *
     * @param listener
     *         The listener, which should be added, as an instance of the type {@link
     *         NavigationListener}. The listener may not be null
     */
    public final void addNavigationListener(@NonNull final NavigationListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        navigationListeners.add(listener);
    }

    /**
     * Removes a specific listener, which should not be notified, when a navigation preference has
     * been added or removed to/from the activity, anymore.
     *
     * @param listener
     *         The listener, which should be removed, as an instance of the type {@link
     *         NavigationListener}. The listener may not be null
     */
    public final void removeNavigationListener(@NonNull final NavigationListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        navigationListeners.remove(listener);
    }

    /**
     * Returns, whether the split screen layout is used, or not.
     *
     * @return True, if the split screen layout is used, false otherwise
     */
    public final boolean isSplitScreen() {
        return useSplitScreen && getDeviceType(this) == DeviceType.TABLET;
    }

    /**
     * Returns the preference fragment, which contains the activity's navigation.
     *
     * @return The preference fragment, which contains the activity's navigation, as an instance of
     * the class PreferenceFragmentCompat or null, if the navigation has not been created yet
     */
    public final PreferenceFragmentCompat getNavigationFragment() {
        return navigationFragment;
    }

    /**
     * Returns the currently shown preference fragment.
     *
     * @return The currently shown preference fragment as an instance of the class Fragment or null,
     * if no preference fragment is currently shown
     */
    public final Fragment getPreferenceFragment() {
        return preferenceFragment;
    }

    /**
     * Returns the frame layout, which contains the activity's views. It is the activity's root
     * view.
     *
     * @return The frame layout, which contains the activity's views, as an instance of the class
     * {@link FrameLayout} or null, if the activity has not been created yet
     */
    public final FrameLayout getFrameLayout() {
        return frameLayout;
    }

    /**
     * Returns the card view, which contains the currently shown preference fragment, when using the
     * split screen layout.
     *
     * @return The card view, which contains the currently shown preference fragment, when using the
     * split screen layout, as an instance of the class CardView or null, if the activity has not
     * been created yet or if the split screen layout is not used
     */
    public final CardView getCardView() {
        return cardView;
    }

    /**
     * Returns the toolbar, which is used to show the activity's title, when using the split screen
     * layout.
     *
     * @return The toolbar, which is used to show the activity's title, when using the split screen
     * layout, as an instance of the class Toolbar or null, if the activity has not been created yet
     * or if the split screen layout is not used
     */
    public final Toolbar getNavigationToolbar() {
        return toolbarLarge != null ? toolbarLarge.getToolbar() : null;
    }

    /**
     * Returns the toolbar, which is used to show the bread crumb of the currently selected
     * navigation preference, when using the split screen layout.
     *
     * @return The toolbar, which is used to show the bread crumb of the currently selected
     * navigation preference, when using the split screen layout, as an instance of the class
     * Toolbar or null, if the activity has not been created yet or if the split screen layout is
     * not used
     */
    public final Toolbar getBreadCrumbToolbar() {
        return breadCrumbToolbar;
    }

    /**
     * Returns the button bar, which is shown when the activity is used as a wizard.
     *
     * @return The button bar as an instance of the class {@link ViewGroup} or null, if the activity
     * has not been created yet
     */
    public final ViewGroup getButtonBar() {
        return buttonBar;
    }

    /**
     * Returns the next button, which is shown when the activity is used as a wizard and the last
     * navigation preference is not selected.
     *
     * @return The next button as an instance of the class {@link Button} or null, if the activity
     * has not been created yet
     */
    public final Button getNextButton() {
        return nextButton;
    }

    /**
     * Returns the back button, which is shown when the activity is used as a wizard and the first
     * navigation preference is not selected.
     *
     * @return The back button as an instance of the class {@link Button} or null, if the activity
     * has not been created yet
     */
    public final Button getBackButton() {
        return backButton;
    }

    /**
     * Returns the finish button, which is shown when the activity is used as a wizard and the last
     * navigation preference is selected.
     *
     * @return The finish button as an instance of the class {@link Button} or null, if the activity
     * has not been created yet
     */
    public final Button getFinishButton() {
        return finishButton;
    }

    /**
     * Sets, whether the split screen layout should be used on tablets, or not.
     *
     * @param useSplitScreen
     *         True, if the split screen layout should be used on tablets, false otherwise
     */
    public final void useSplitScreen(final boolean useSplitScreen) {
        this.useSplitScreen = useSplitScreen;
        adaptSplitScreen();
    }

    /**
     * Returns the width of the navigation, when using the split screen layout.
     *
     * @return The width of the navigation, when using the split screen layout, in pixels as an
     * {@link Integer} value
     */
    @Px
    public final int getNavigationWidth() {
        return navigationWidth;
    }

    /**
     * Sets the width of the navigation, when using the split screen layout.
     *
     * @param width
     *         The width, which should be set, in pixels as an {@link Integer} value. The width must
     *         be greater than 0
     */
    public final void setNavigationWidth(@Px final int width) {
        ensureGreater(width, 0, "The width must be greater than 0");
        this.navigationWidth = width;
        adaptNavigationWidth();
    }

    /**
     * Returns, whether the navigation is currently hidden, or not.
     *
     * @return True, if the navigation is currently hidden, false otherwise
     */
    public final boolean isNavigationHidden() {
        return hideNavigation;
    }

    /**
     * Hides or shows the navigation. When the activity is used as a wizard on devices with a small
     * screen, the navigation is always hidden.
     *
     * @param hideNavigation
     *         True, if the navigation should be hidden, false otherwise
     */
    public final void hideNavigation(final boolean hideNavigation) {
        this.hideNavigation = hideNavigation;
        adaptNavigationVisibility();
    }

    /**
     * Returns, whether the behavior of the navigation icon of the activity's toolbar is overridden
     * in order to return to the navigation when a preference fragment is currently shown and the
     * split screen layout is used.
     *
     * @return True, if the behavior of the navigation icon is overridden, false otherwise
     */
    public final boolean isNavigationIconOverridden() {
        return overrideNavigationIcon;
    }

    /**
     * Sets, whether the behavior of the navigation icon of the activity's toolbar should be
     * overridden in order to return to the navigation when a preference screen is currently shown
     * and the split screen layout is used, or not.
     *
     * @param overrideNavigationIcon
     *         True, if the behavior of the navigation icon should be overridden, false otherwise
     */
    public final void overrideNavigationIcon(final boolean overrideNavigationIcon) {
        this.overrideNavigationIcon = overrideNavigationIcon;

        if (isPreferenceFragmentShown()) {
            if (overrideNavigationIcon) {
                showToolbarNavigationIcon();
            } else {
                hideToolbarNavigationIcon();
            }
        }
    }

    /**
     * Returns, whether the activity is used as a wizard, or not.
     *
     * @return True, if the activity is used as a wizard, false otherwise
     */
    public final boolean isButtonBarShown() {
        return showButtonBar;
    }

    /**
     * Shows or hides the view group, which contains the buttons, which are shown when the activity
     * is used as a wizard.
     *
     * @param show
     *         True, if the button bar should be shown, false otherwise
     */
    public final void showButtonBar(final boolean show) {
        this.showButtonBar = show;
        adaptButtonBarVisibility();
    }

    /**
     * Returns the text of the next button, which is shown, when the activity is used as a wizard.
     *
     * @return The text of the next button as an instance of the class {@link CharSequence}. The
     * text may neither be null, nor empty
     */
    @NonNull
    public final CharSequence getNextButtonText() {
        return nextButtonText;
    }

    /**
     * Sets the text of the next button, which is shown, when the activity is used as a wizard.
     *
     * @param resourceId
     *         The resource id of the text, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setNextButtonText(@StringRes final int resourceId) {
        setNextButtonText(getText(resourceId));
    }

    /**
     * Sets the text of the next button, which is shown, when the activity is used as a wizard.
     *
     * @param text
     *         The text, which should be set, as an instance of the class {@link CharSequence}. The
     *         text may neither be null, nor empty
     */
    public final void setNextButtonText(@NonNull final CharSequence text) {
        ensureNotNull(text, "The text may not be null");
        ensureNotEmpty(text, "The text may not be empty");
        this.nextButtonText = text;
        adaptNextButtonText();
    }

    /**
     * Returns the text of the back button, which is shown, when the activity is used as a wizard.
     *
     * @return The text of the back button as an instance of the class {@link CharSequence}. The
     * text may neither null, nor empty
     */
    @NonNull
    public final CharSequence getBackButtonText() {
        return backButtonText;
    }

    /**
     * Sets the text of the back button, which is shown, when the activity is used as a wizard.
     *
     * @param resourceId
     *         The resource id of the text, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setBackButtonText(@StringRes final int resourceId) {
        setBackButtonText(getText(resourceId));
    }

    /**
     * Sets the text of the back button, which is shown, when the activity is used as a wizard.
     *
     * @param text
     *         The text, which should be set, as an instance of the class {@link CharSequence}. The
     *         text may neither null, nor empty
     */
    public final void setBackButtonText(@NonNull final CharSequence text) {
        ensureNotNull(text, "The text may not be null");
        ensureNotEmpty(text, "The text may not be empty");
        this.backButtonText = text;
        adaptBackButtonText();
    }

    /**
     * Returns the text of the finish button, which is shown, when the activity is used as a wizard
     * and the last navigation preference is currently selected.
     *
     * @return The text of the finish button as an instance of the class {@link CharSequence}. The
     * text may neither be null, nor empty
     */
    @NonNull
    public final CharSequence getFinishButtonText() {
        return finishButtonText;
    }

    /**
     * Sets the text of the next button, which is shown, when the activity is used as a wizard and
     * the last navigation preference is currently selected.
     *
     * @param resourceId
     *         The resource id of the text, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setFinishButtonText(@StringRes final int resourceId) {
        setFinishButtonText(getText(resourceId));
    }

    /**
     * Sets the text of the next button, which is shown, when the activity is used as a wizard and
     * the last navigation preference is currently selected.
     *
     * @param text
     *         The text, which should be set, as an instance of the class {@link CharSequence}. The
     *         text may neither be null, nor empty
     */
    public final void setFinishButtonText(@NonNull final CharSequence text) {
        ensureNotNull(text, "The text may not be null");
        ensureNotEmpty(text, "The text may not be empty");
        this.finishButtonText = text;
        adaptFinishButtonText();
    }

    /**
     * Returns, whether the progress is shown, if the activity is used as a wizard.
     *
     * @return True, if the progress is shown, false otherwise or if the activity is not used as a
     * wizard
     */
    public final boolean isProgressShown() {
        return showProgress;
    }

    /**
     * Sets, whether the progress should be shown, when the activity is used as a wizard.
     *
     * @param showProgress
     *         True, if the progress should be shown, when the activity is used as a wizard, false
     *         otherwise
     */
    public final void showProgress(final boolean showProgress) {
        this.showProgress = showProgress;
        adaptProgress();
    }

    /**
     * Returns the string, which is used to format the progress, which is shown, when the activity
     * is used as a wizard.
     *
     * @return The string, which is used to format the progress, as a {@link String}. The string may
     * neither be null, nor empty
     */
    @NonNull
    public final String getProgressFormat() {
        return progressFormat;
    }

    /**
     * Sets the string, which should be used to format the progress, which is shown, when the
     * activity is used as a wizard.
     *
     * @param resourceId
     *         The resource id of the string, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource. It must be formatted
     *         according to the following syntax: "*%d*%d*%s*"
     */
    public final void setProgressFormat(@StringRes final int resourceId) {
        setProgressFormat(getString(resourceId));
    }

    /**
     * Sets the string, which should be used to format the progress, which is shown, when the
     * activity is used as a wizard.
     *
     * @param progressFormat
     *         The string, which should be set, as a {@link String}. The string may neither be null,
     *         nor empty. It must be formatted according to the following syntax: "*%d*%d*%s*"
     */
    public final void setProgressFormat(@NonNull final String progressFormat) {
        ensureNotNull(progressFormat, "The progress format may not be null");
        ensureNotEmpty(progressFormat, "The progress format may not be empty");
        this.progressFormat = progressFormat;
        adaptProgress();
    }

    /**
     * Returns, whether the toolbar, which is used to show the bread crumb of the currently selected
     * navigation preference, is hidden, or not.
     *
     * @return True, if the toolbar, which is used to show the bread crumb of the currently selected
     * navigation preference, is hidden, false otherwise
     */
    public final boolean isBreadCrumbHidden() {
        return hideBreadCrumb;
    }

    /**
     * Sets, whether the toolbar, which is used to show the bread crumb of the currently selected
     * navigation preference, should be hidden, or not.
     *
     * @param hide
     *         True, if the toolbar, which is used to show the bread crumb of the currently selected
     *         navigation preference, should be hidden, false otherwise
     */
    public final void hideBreadCrumb(final boolean hide) {
        this.hideBreadCrumb = hide;
        adaptBreadCrumbVisibility();
    }

    /**
     * Returns the elevation of the activity's toolbar.
     *
     * @return The elevation of the activity's toolbar in dp as an {@link Integer} value
     */
    public final int getToolbarElevation() {
        return toolbarElevation;
    }

    /**
     * Sets the elevation of the activity's toolbar.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 0 and at maximum 16
     */
    public final void setToolbarElevation(final int elevation) {
        ensureAtLeast(elevation, 0, "The elevation must be at least 0");
        ensureAtMaximum(elevation, ElevationUtil.MAX_ELEVATION,
                "The elevation must at maximum " + ElevationUtil.MAX_ELEVATION);
        this.toolbarElevation = elevation;
        adaptToolbarElevation();
    }

    /**
     * Returns the elevation of the toolbar, which is used to show the bread crumb of the currently
     * selected navigation preference, when using the split screen layout.
     *
     * @return The elevation of the toolbar, which is used to show the bread crumb of the currently
     * selected navigation preference, when using the split screen layout, in dp as an {@link
     * Integer} value
     */
    public final int getBreadCrumbElevation() {
        return breadCrumbElevation;
    }

    /**
     * Sets the elevation of the toolbar, which is used to show the bread crumb of the currently
     * selected navigation preference, when using the split screen layout.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 0 and at maximum 16
     */
    public final void setBreadCrumbElevation(final int elevation) {
        ensureAtLeast(elevation, 0, "The elevation must be at least 0");
        ensureAtMaximum(elevation, ElevationUtil.MAX_ELEVATION,
                "The elevation must at maximum " + ElevationUtil.MAX_ELEVATION);
        this.breadCrumbElevation = elevation;
        adaptBreadCrumbElevation();
    }

    /**
     * Returns the elevation of the card view, which contains the currently shown preference
     * fragment, when using the split screen layout.
     *
     * @return The elevation of the card view, which contains the currently shown preference
     * fragment, when using the split screen layout, in dp as an {@link Integer} value
     */
    public final int getCardViewElevation() {
        return cardViewElevation;
    }

    /**
     * Sets the elevation of the card view, which contains the currently shown prefernce fragment,
     * when using the split screen layout.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 0 and at maximum 16
     */
    public final void setCardViewElevation(final int elevation) {
        ensureAtLeast(elevation, 0, "The elevation must be at least 0");
        ensureAtMaximum(elevation, ElevationUtil.MAX_ELEVATION,
                "The elevation must be at maximum " + ElevationUtil.MAX_ELEVATION);
        this.cardViewElevation = elevation;
        adaptCardViewElevation();
    }

    /**
     * Returns the elevation of the button bar, which is shown when using the activity as a wizard.
     *
     * @return The elevation of the button bar, which is shown when using the activity as a wizard,
     * in dp as an {@link Integer} value
     */
    public final int getButtonBarElevation() {
        return buttonBarElevation;
    }

    /**
     * Sets the elevation of the button bar, which is shown when using the activity as a wizard.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 0 and at maximum 16
     */
    public final void setButtonBarElevation(final int elevation) {
        ensureAtLeast(elevation, 0, "The elevation must be at least 0");
        ensureAtMaximum(elevation, ElevationUtil.MAX_ELEVATION,
                "The elevation must be at maximum " + ElevationUtil.MAX_ELEVATION);
        this.buttonBarElevation = elevation;
        adaptButtonBarElevation();
    }

    /**
     * Returns the background color of the card view, which contains the currently shown preference
     * fragment, when using the split screen layout.
     *
     * @return The background color of the card view, which contains the currently shown preference
     * fragment, when using the split screen layout, as an {@link Integer} value
     */
    @ColorInt
    public final int getCardViewBackgroundColor() {
        return cardViewBackgroundColor;
    }

    /**
     * Sets the background color of the card view, which contains the currently shown preference
     * fragment, when using the split screen layout.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value
     */
    public final void setCardViewBackgroundColor(@ColorInt final int color) {
        this.cardViewBackgroundColor = color;
        adaptCardViewBackgroundColor();
    }

    /**
     * Returns the background color of the toolbar, which is used to show the bread crumb of the
     * currently selected navigation preference, when using the split screen layout.
     *
     * @return The background color of the toolbar, which is used to show the bread crumb of the
     * currently selected navigation preference, when using the split screen klayout, as an {@link
     * Integer} value
     */
    @ColorInt
    public final int getBreadCrumbBackgroundColor() {
        return breadCrumbBackgroundColor;
    }

    /**
     * Sets the background color of the toolbar, which is used to show the bread crumb of the
     * currently selected navigation preference, when using the split screen layout.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value
     */
    public final void setBreadCrumbBackgroundColor(@ColorInt final int color) {
        this.breadCrumbBackgroundColor = color;
        adaptBreadCrumbBackgroundColor();
    }

    /**
     * Returns the background of the button bar, which is shown, when the activity is used as a
     * wizard.
     *
     * @return The background of the button bar, which is shown, when the activity is used as a
     * wizard, as an instance of the class {@link Drawable} or null, if no background is set
     */
    @Nullable
    public final Drawable getButtonBarBackground() {
        return buttonBarBackground;
    }

    /**
     * Sets the background of the button bar, which is shown, when the activity is used as a
     * wizard.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value
     */
    public final void setButtonBarBackgroundColor(@ColorInt final int color) {
        setButtonBarBackground(new ColorDrawable(color));
    }

    /**
     * Sets the background of the button bar, which is shown, when the activity is used as a
     * wizard.
     *
     * @param resourceId
     *         The resource id of the background, which should be set, as an {@link Integer} value.
     *         The resource id must correspond to a valid drawable resource
     */
    public final void setButtonBarBackground(@DrawableRes final int resourceId) {
        setButtonBarBackground(ContextCompat.getDrawable(this, resourceId));
    }

    /**
     * Sets the background of the button bar, which is shown, when the activity is used as a
     * wizard.
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
     * Returns the background of the navigation.
     *
     * @return The background of the navigation as an instance of the class {@link Drawable} or
     * null, if no background is set
     */
    @Nullable
    public final Drawable getNavigationBackground() {
        return navigationBackground;
    }

    /**
     * Sets the background color of the navigation.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value
     */
    public final void setNavigationBackgroundColor(@ColorInt final int color) {
        setNavigationBackground(new ColorDrawable(color));
    }

    /**
     * Sets the background of the navigation.
     *
     * @param resourceId
     *         The resource id of the background, which should be set, as an {@link Integer} value.
     *         The resource id must correspond to a valid drawable resource
     */
    public final void setNavigationBackground(@DrawableRes final int resourceId) {
        setNavigationBackground(ContextCompat.getDrawable(this, resourceId));
    }

    /**
     * Sets the background of the navigation.
     *
     * @param background
     *         The background, which should be set, as an instance of the class {@link Drawable} or
     *         null, if no background should be set
     */
    public final void setNavigationBackground(@Nullable final Drawable background) {
        this.navigationBackground = background;
        adaptNavigationBackground();
    }

    /**
     * Returns the background color of the currently selected navigation preference.
     *
     * @return The background color of the currently selected navigation preference as an {@link
     * Integer}
     */
    @ColorInt
    public final int getNavigationSelectionColor() {
        return navigationSelectionColor;
    }

    /**
     * Sets the background color of the currently selected navigation preference.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value
     */
    public final void setNavigationSelectionColor(@ColorInt final int color) {
        this.navigationSelectionColor = color;
        adaptNavigationSelectionColor();
    }

    /**
     * Returns the color of the dividers, which are contained by the navigation.
     *
     * @return The color of the dividers, which are contained by the navigation, as an {@link
     * Integer} value
     */
    @ColorInt
    public final int getNavigationDividerColor() {
        return navigationDividerColor;
    }

    /**
     * Sets the color of the dividers, which are contained by the navigation.
     *
     * @param color
     *         The color, which should be set, as an {@link Integer} value
     */
    public final void setNavigationDividerColor(@ColorInt final int color) {
        this.navigationDividerColor = color;
        adaptNavigationDividerColor();
    }

    /**
     * Returns, whether a preference fragment is currently shown, or not.
     *
     * @return True, if a preference fragment is currently shown, false otherwise
     */
    public final boolean isPreferenceFragmentShown() {
        return preferenceFragment != null;
    }

    /**
     * Returns the number of navigation preferences, which are contained by the activity.
     *
     * @return The number of navigation preferences, which are contained by the activity, as an
     * {@link Integer} value
     */
    public final int getNavigationPreferenceCount() {
        return navigationFragment != null ? navigationFragment.getNavigationPreferenceCount() : 0;
    }

    /**
     * Returns a list, which contains all navigation preferences, which are contained by the
     * activity.
     *
     * @return A list, which contains all navigation preferences, which are contained by the
     * activity, as an instance of the type {@link List} or an empty collection, if no navigation
     * preferences are contained by the activity
     */
    @NonNull
    public final List<NavigationPreference> getAllNavigationPreferences() {
        return navigationFragment != null ? navigationFragment.getAllNavigationPreferences() :
                Collections.<NavigationPreference>emptyList();
    }

    /**
     * Returns, whether a navigation preference is currently selected, or not.
     *
     * @return True, if a navigation preference is currently selected, false otherwise
     */
    public final boolean isNavigationPreferenceSelected() {
        return getSelectedNavigationPreference() != null;
    }

    /**
     * Returns the currently selected navigation preference.
     *
     * @return The currently selected navigation preference as an instance of the class {@link
     * NavigationPreference} or null, if no navigation preference is currently selected
     */
    @Nullable
    public final NavigationPreference getSelectedNavigationPreference() {
        return navigationFragment != null ? navigationFragment.getSelectedNavigationPreference() :
                null;
    }

    /**
     * Selects a specific navigation preference.
     *
     * @param navigationPreference
     *         The navigation preference, which should be selected, as an instance of the class
     *         {@link NavigationPreference}. The navigation preference may not be null
     */
    public final void selectNavigationPreference(
            @NonNull final NavigationPreference navigationPreference) {
        selectNavigationPreference(navigationPreference, null);
    }

    /**
     * Selects a specific navigation preference.
     *
     * @param navigationPreference
     *         The navigation preference, which should be selected, as an instance of the class
     *         {@link NavigationPreference}. The navigation preference may not be null
     * @param arguments
     *         The arguments, which should be passed to the fragment, which is associated with the
     *         navigation preference, as an instance of the class {@link Bundle} or null, if no
     *         arguments should be passed to the fragment
     */
    public final void selectNavigationPreference(
            @NonNull final NavigationPreference navigationPreference,
            @Nullable final Bundle arguments) {
        ensureNotNull(navigationPreference, "The navigation preference may not be null");

        if (navigationFragment != null) {
            int index = getAllNavigationPreferences().indexOf(navigationPreference);

            if (index != -1) {
                navigationFragment.selectNavigationPreference(index, arguments);
            }
        }
    }

    /**
     * Unselects the currently selected navigation preference and hides the associated fragment, if
     * the split screen layout is not used and the navigation is not hidden.
     */
    public final void unselectNavigationPreference() {
        removePreferenceFragment();
    }

    /**
     * The method, which is invoked on implementing subclasses, when the navigation is created. It
     * may be overridden in order to add preferences to the preference fragment, which contains the
     * navigation's preferences.
     *
     * @param fragment
     *         The preference fragment, which contains the navigation's preferences, as an instance
     *         of the class PreferenceFragmentCompat. The preference fragment may not be null
     */
    protected void onCreateNavigation(@NonNull final PreferenceFragmentCompat fragment) {

    }

    @Override
    public final void setTitle(@StringRes final int resourceId) {
        setTitle(getText(resourceId));
    }

    @Override
    public final void setTitle(@Nullable final CharSequence title) {
        super.setTitle(title);
        showTitle(title);
    }

    @Override
    public final void onNavigationFragmentCreated(
            @NonNull final PreferenceFragmentCompat fragment) {
        onCreateNavigation(fragment);
    }

    @Override
    public final void onNavigationAdapterCreated() {
        navigationFragment.setCallback(null);
        adaptNavigationWidth();
        adaptNavigationVisibility();
        adaptButtonBarVisibility();
        adaptWizardButtonVisibilities();
        adaptNextButtonText();
        adaptBackButtonText();
        adaptFinishButtonText();
        adaptProgress();
        adaptBreadCrumbVisibility();
        adaptBreadCrumbVisibility();
        adaptBreadCrumbVisibility();
        adaptToolbarElevation();
        adaptBreadCrumbElevation();
        adaptCardViewElevation();
        adaptButtonBarElevation();
        adaptCardViewBackgroundColor();
        adaptBreadCrumbBackgroundColor();
        adaptButtonBarBackground();
        adaptNavigationBackground();
        adaptNavigationSelectionColor();
        adaptNavigationDividerColor();

        if (!handleShowFragmentIntent() && !showInitialFragment(selectedPreferenceFragment) &&
                navigationFragment.getNavigationPreferenceCount() > 0 &&
                (isSplitScreen() || isButtonBarShown())) {
            navigationFragment.selectNavigationPreference(0, null);
        }
    }

    @Override
    public final boolean onSelectNavigationPreference(
            @NonNull final NavigationPreference navigationPreference) {
        return !TextUtils.isEmpty(navigationPreference.getFragment()) || isSplitScreen();
    }

    @Override
    public final void onNavigationPreferenceSelected(
            @NonNull final NavigationPreference navigationPreference,
            @Nullable final Bundle arguments) {
        showPreferenceFragment(navigationPreference, arguments);
    }

    @Override
    public final void onNavigationPreferenceUnselected() {
        removePreferenceFragmentUnconditionally();
        showBreadCrumb(null);
    }

    @Override
    public final void onNavigationPreferenceAdded(
            @NonNull final NavigationPreference navigationPreference) {
        if (isSplitScreen() && navigationFragment.getNavigationPreferenceCount() == 1) {
            navigationFragment.selectNavigationPreference(0, null);
        }

        notifyOnNavigationPreferenceAdded(navigationPreference);
    }

    @Override
    public final void onNavigationPreferenceRemoved(
            @NonNull final NavigationPreference navigationPreference) {
        if (isSplitScreen() && isNavigationHidden() &&
                navigationFragment.getNavigationPreferenceCount() == 0) {
            finish();
        }

        notifyOnNavigationPreferenceRemoved(navigationPreference);
    }

    @CallSuper
    @Override
    public void onBackPressed() {
        boolean handled = removePreferenceFragment();

        if (!handled) {
            handled = isButtonBarShown() && !notifyOnSkip();
        }

        if (!handled) {
            super.onBackPressed();
        }
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isNavigationIconOverridden() && removePreferenceFragment()) {
                return true;
            } else if (isButtonBarShown()) {
                return !notifyOnSkip() || super.onOptionsItemSelected(item);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @CallSuper
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainStyledAttributes();

        if (savedInstanceState == null) {
            handleIntent();
        } else {
            selectedPreferenceFragment =
                    savedInstanceState.getString(SELECTED_PREFERENCE_FRAGMENT_EXTRA);
            selectedPreferenceFragmentArguments =
                    savedInstanceState.getBundle(SELECTED_PREFERENCE_FRAGMENT_ARGUMENTS_EXTRA);
        }

        inflateLayout();
        initializeToolbar();
        initializeFragments();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_PREFERENCE_FRAGMENT_EXTRA, selectedPreferenceFragment);
        outState.putBundle(SELECTED_PREFERENCE_FRAGMENT_ARGUMENTS_EXTRA,
                selectedPreferenceFragmentArguments);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        NavigationPreference selectedNavigationPreference = getSelectedNavigationPreference();

        if (selectedNavigationPreference != null) {
            showBreadCrumb(selectedNavigationPreference, selectedPreferenceFragmentArguments);
            showToolbarNavigationIcon();
        }
    }

}