/*
 * Copyright 2014 - 2017 Michael Rapp
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

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.StringRes;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.LinkedHashSet;
import java.util.Set;

import de.mrapp.android.preference.activity.adapter.NavigationPreferenceGroupAdapter;
import de.mrapp.android.preference.activity.fragment.NavigationFragment;
import de.mrapp.android.preference.activity.view.ToolbarLarge;
import de.mrapp.android.util.Condition;
import de.mrapp.android.util.DisplayUtil.DeviceType;
import de.mrapp.android.util.ThemeUtil;
import de.mrapp.android.util.view.ElevationShadowView;

import static de.mrapp.android.util.Condition.ensureGreater;
import static de.mrapp.android.util.Condition.ensureNotEmpty;
import static de.mrapp.android.util.Condition.ensureNotNull;
import static de.mrapp.android.util.DisplayUtil.getDeviceType;
import static de.mrapp.android.util.DisplayUtil.getDisplayWidth;

/**
 * An activity, which provides a navigation for multiple groups of preferences, in which each group
 * is represented by an instance of the class {@link PreferenceHeader}. On devices with small
 * screens, e.g. on smartphones, the navigation is designed to use the whole available space and
 * selecting an item causes the corresponding preferences to be shown full screen as well. On
 * devices with large screens, e.g. on tablets, the navigation and the preferences of the currently
 * selected item are shown split screen.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public abstract class PreferenceActivity extends AppCompatActivity
        implements NavigationFragment.Callback, NavigationPreferenceGroupAdapter.Callback {

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
     * toolbar, which is used to show the title of the currently selected preference header, should
     * not be displayed.
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
     * The name of the extra, which is used to store whether the split screen layout is used, or
     * not, within a bundle.
     */
    private static final String USE_SPLIT_SCREEN_EXTRA =
            PreferenceActivity.class.getName() + "::UseSplitScreen";

    /**
     * The name of the extra, which is used to store the navigation width within a bundle.
     */
    private static final String NAVIGATION_WIDTH_EXTRA =
            PreferenceActivity.class.getName() + "::NavigationWidth";

    /**
     * The name of the extra, which is used to store whether the navigation is hidden, or not,
     * within a bundle.
     */
    private static final String HIDE_NAVIGATION_EXTRA =
            PreferenceActivity.class.getName() + "::HideNavigation";

    /**
     * The name of the extra, which is used to store, whether the button bar is shown, or not,
     * within a bundle.
     */
    private static final String SHOW_BUTTON_BAR_EXTRA =
            PreferenceActivity.class.getName() + "::ShowButtonBar";

    /**
     * The name of the extra, which is used to store the text of the next button within a bundle.
     */
    private static final String NEXT_BUTTON_TEXT_EXTRA =
            PreferenceActivity.class.getName() + "::NextButtonText";

    /**
     * The name of the extra, which is used to store the text of the back button within a bundle.
     */
    private static final String BACK_BUTTON_TEXT_EXTRA =
            PreferenceActivity.class.getName() + "::BackButtonText";

    /**
     * The name of the extra, which is used to store the text of the finish button within a bundle.
     */
    private static final String FINISH_BUTTON_TEXT_EXTRA =
            PreferenceActivity.class.getName() + "::FinishButtonText";

    /**
     * The name of the extra, which is used to store, whether the progress is shown, when the
     * activity is used as a wizard, or not, within a bundle.
     */
    private static final String SHOW_PROGRESS_EXTRA =
            PreferenceActivity.class.getName() + "::ShowProgress";

    /**
     * The name of the extra, which is used to store the format of the progress, which is shown,
     * when the activity is used as a wizard, within a bundle.
     */
    private static final String PROGRESS_FORMAT_EXTRA =
            PreferenceActivity.class.getName() + "::ProgressFormat";

    /**
     * The name of the extra, which is used to store, whether the toolbar, which is used to show the
     * breadcrumb of the currently selected navigation preference, is shown, or not, within a
     * bundle.
     */
    private static final String BREADCRUMB_VISIBILITY_EXTRA =
            PreferenceActivity.class.getName() + "::BreadcrumbVisibility";

    /**
     * The name of the extra, which is used to store, whether the behavior of the navigation icon
     * should be overridden, or not, within a bundle.
     */
    private static final String OVERRIDE_NAVIGATION_ICON_EXTRA =
            PreferenceActivity.class.getName() + "::OverrideNavigationIcon";

    /**
     * The name of the extra, which is used to store the arguments, which have been passed to the
     * currently shown preference fragment, within a bundle.
     */
    private static final String PREFERENCE_FRAGMENT_ARGUMENTS_EXTRA =
            PreferenceActivity.class.getName() + "::PreferenceFragmentArguments";

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
     * The container, the currently shown preference fragment is attached to.
     */
    private ViewGroup preferenceFragmentContainer;

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
     * The visibility of the toolbar, which is used to show the breadcrumb of the currently selected
     * preference header.
     */
    private int breadCrumbVisibility;

    /**
     * True, if the navigation icon of the activity's toolbar is shown by default, false otherwise.
     */
    private boolean displayHomeAsUp;

    /**
     * The arguments which have been passed to the currently shown preference fragment or null, if
     * no arguments have been passed to the fragment or no preference fragment is shown.
     */
    private Bundle preferenceFragmentArguments;

    /**
     * A set, which contains the listeners, which have been registered to be notified, when the user
     * navigates within the activity, when it used as a wizard.
     */
    private Set<WizardListener> wizardListeners = new LinkedHashSet<>();

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
        obtainBreadcrumbVisibility();
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
            navigationWidth =
                    getResources().getDimensionPixelSize(R.dimen.default_navigation_width);
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
            text = getText(R.string.next_button_label);
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
            text = getText(R.string.back_button_label);
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
            text = getText(R.string.finish_button_label);
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
    private void obtainBreadcrumbVisibility() {
        int visibility = ThemeUtil.getInt(this, R.attr.breadCrumbVisibility, 0);
        setBreadCrumbVisibility(
                visibility == 0 ? View.VISIBLE : (visibility == 1 ? View.INVISIBLE : View.GONE));
    }

    /**
     * Handles extras the intent, which has been used to start the activity.
     */
    private void handleIntent() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
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
     * Handles the extra, which specifies, whether the button bar should be shown, or not, of the
     * intent, which has been used to start the activity.
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
     * Handles the extra, which specifies the text of the next button, of the intent, which has been
     * used to start the activity.
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
     * Handles the extra, which specifies the text of the back button, of the intent, which has been
     * used to start the activity.
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
     * Handles the extra, which specifies the text of the finish button, of the intent, which has
     * been used to start the activity.
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
     * Handles the extra, which specifies, whether the progress should be shown, when the activity
     * is used as a wizard, or not, of the intent, which has been used to start the activity.
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
     * Handles the extra, which specifies the format of the progress, which is shown, when the
     * activity is used as a wizard, of the intent, which has been used to start the activity.
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
     * Handles the extra, which specifies that no bread crumbs should be shown.
     *
     * @param extras
     *         The extras of the intent, which has been used to start the activity, as an instance
     *         of the class {@link Bundle}. The bundle may not be null
     */
    private void handleNoBreadcrumbsIntent(@NonNull final Bundle extras) {
        if (extras.containsKey(EXTRA_NO_BREAD_CRUMBS)) {
            setBreadCrumbVisibility(
                    extras.getBoolean(EXTRA_NO_BREAD_CRUMBS) ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Inflates the activity's layout, depending on whether the split screen layout is used, or not.
     */
    private void inflateLayout() {
        setContentView(isSplitScreen() ? R.layout.preference_activity_tablet :
                R.layout.preference_activity_phone);
        navigationFragmentContainer = findViewById(R.id.navigation_fragment_container);
        preferenceFragmentContainer = findViewById(R.id.preference_fragment_container);
        cardView = findViewById(R.id.card_view);
        toolbar = findViewById(R.id.toolbar);
        toolbarLarge = findViewById(R.id.toolbar_large);
        breadCrumbToolbar = findViewById(R.id.bread_crumb_toolbar);
        buttonBar = findViewById(R.id.wizard_button_bar);
        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(createNextButtonListener());
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(createBackButtonListener());
        finishButton = findViewById(R.id.finish_button);
        finishButton.setOnClickListener(createFinishButtonListener());
        buttonBarShadowView = findViewById(R.id.wizard_button_bar_shadow_view);
        adaptNavigationWidth();
        adaptNavigationVisibility();
        adaptButtonBarVisibility();
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
        navigationFragment = (NavigationFragment) getFragmentManager()
                .findFragmentByTag(NAVIGATION_FRAGMENT_TAG);

        if (navigationFragment == null) {
            navigationFragment = (NavigationFragment) Fragment
                    .instantiate(this, NavigationFragment.class.getName());
            navigationFragment.setRetainInstance(true);
            navigationFragment.setCallback(this);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.navigation_fragment_container, navigationFragment,
                    NAVIGATION_FRAGMENT_TAG);
            transaction.commit();
        }

        navigationFragment.setAdapterCallback(this);
        preferenceFragment = getFragmentManager().findFragmentByTag(PREFERENCE_FRAGMENT_TAG);
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

        preferenceFragmentArguments =
                arguments != null ? arguments : navigationPreference.getExtras();

        if (!TextUtils.isEmpty(navigationPreference.getFragment())) {
            Fragment fragment = Fragment.instantiate(this, navigationPreference.getFragment(),
                    preferenceFragmentArguments);
            showPreferenceFragment(fragment);
            showBreadCrumb(navigationPreference);
        } else {
            removePreferenceFragmentUnconditionally();

            if (isSplitScreen()) {
                showBreadCrumb(navigationPreference);
            }
        }

        adaptWizardButtonVisibilities();
    }

    /**
     * Shows a specific preference fragment.
     *
     * @param fragment
     *         The fragment, which should be shown, as an instance of the class {@link Fragment}.
     *         The fragment may not be null
     */
    private void showPreferenceFragment(@NonNull final Fragment fragment) {
        fragment.setRetainInstance(true);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (!isSplitScreen()) {
            transaction.hide(navigationFragment);

            if (preferenceFragment != null) {
                transaction.remove(preferenceFragment);
            }

            transaction.add(R.id.navigation_fragment_container, fragment, PREFERENCE_FRAGMENT_TAG);
        } else {
            transaction
                    .replace(R.id.preference_fragment_container, fragment, PREFERENCE_FRAGMENT_TAG);
        }

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        showToolbarNavigationIcon();
        adaptBreadCrumbVisibility(preferenceFragmentArguments);
        this.preferenceFragment = fragment;
    }

    /**
     * Removes the currently shown preference fragment, if the split screen layout is not used.
     *
     * @return True, if a preference fragment has been removed, false otherwise
     */
    private boolean removePreferenceFragment() {
        if (!isSplitScreen() && isPreferenceFragmentShown() && !isNavigationHidden() &&
                !isButtonBarShown()) {
            navigationFragment.selectNavigationPreference(-1, null);
            removePreferenceFragmentUnconditionally();
            preferenceFragmentArguments = null;
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
            adaptBreadCrumbVisibility(breadCrumbVisibility);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(preferenceFragment);

            if (!isSplitScreen()) {
                transaction.show(navigationFragment);
            }

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();
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
     */
    private void showBreadCrumb(@NonNull final NavigationPreference navigationPreference) {
        CharSequence breadCrumbTitle = navigationPreference.getBreadCrumbTitle();

        if (TextUtils.isEmpty(breadCrumbTitle)) {
            breadCrumbTitle = navigationPreference.getTitle();

            if (TextUtils.isEmpty(breadCrumbTitle)) {
                breadCrumbTitle = getTitle();
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

        if (!TextUtils.isEmpty(formattedBreadCrumbTitle)) {
            if (isSplitScreen()) {
                breadCrumbToolbar.setTitle(formattedBreadCrumbTitle);
            } else {
                showTitle(formattedBreadCrumbTitle);
            }
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
        if (navigationFragmentContainer != null && cardView != null && toolbarLarge != null) {
            ViewCompat.setPaddingRelative(navigationFragmentContainer, 0, 0,
                    getDisplayWidth(this) - navigationWidth, 0);

            if (!isNavigationHidden()) {
                FrameLayout.LayoutParams preferenceScreenLayoutParams =
                        (FrameLayout.LayoutParams) cardView.getLayoutParams();
                MarginLayoutParamsCompat.setMarginStart(preferenceScreenLayoutParams,
                        navigationWidth - getResources()
                                .getDimensionPixelSize(R.dimen.card_view_intrinsic_margin));
                cardView.requestLayout();
                toolbarLarge.setNavigationWidth(navigationWidth);
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
                int preferenceScreenHorizontalMargin = getResources()
                        .getDimensionPixelSize(R.dimen.preference_screen_horizontal_margin);
                int preferenceScreenMarginRight = getResources()
                        .getDimensionPixelSize(R.dimen.preference_screen_margin_right);
                int cardViewIntrinsicMargin =
                        getResources().getDimensionPixelSize(R.dimen.card_view_intrinsic_margin);
                FrameLayout.LayoutParams cardViewLayoutParams =
                        (FrameLayout.LayoutParams) cardView.getLayoutParams();
                MarginLayoutParamsCompat.setMarginStart(cardViewLayoutParams,
                        (isNavigationHidden() ? preferenceScreenHorizontalMargin :
                                navigationWidth) - cardViewIntrinsicMargin);
                MarginLayoutParamsCompat.setMarginEnd(cardViewLayoutParams,
                        (isNavigationHidden() ? preferenceScreenHorizontalMargin :
                                preferenceScreenMarginRight) - cardViewIntrinsicMargin);
                cardViewLayoutParams.gravity =
                        isNavigationHidden() ? Gravity.CENTER_HORIZONTAL : Gravity.NO_GRAVITY;
                cardView.requestLayout();
            }
        } else {
            if (getSelectedNavigationPreference() != null) {
                if (isNavigationHidden()) {
                    hideToolbarNavigationIcon();
                } else {
                    showToolbarNavigationIcon();
                }
            } else if (isNavigationHidden()) {
                if (navigationFragment != null &&
                        navigationFragment.getNavigationPreferenceCount() > 0) {
                    navigationFragment.selectNavigationPreference(0, null);
                } else if (navigationFragment != null) {
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
            showBreadCrumb(selectedNavigationPreference);
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
            adaptBreadCrumbVisibility(hideBreadCrumb ? View.GONE : View.VISIBLE);
        } else {
            adaptBreadCrumbVisibility(breadCrumbVisibility);
        }
    }

    /**
     * Adapts the visibility of the toolbar, which is used to show the breadcrumb of the currently
     * selected navigation preference.
     *
     * @param visibility
     *         The visibility, which should be set, as an {@link Integer} value. The visibility may
     *         either be <code>View.VISIBLE</code>, <code>View.INVISIBLE</code> or
     *         <code>View.GONE</code>
     */
    private void adaptBreadCrumbVisibility(final int visibility) {
        if (isSplitScreen()) {
            if (breadCrumbToolbar != null) {
                breadCrumbToolbar.setVisibility(visibility);
                // TODO breadCrumbShadowView.setVisibility(visibility);
            }
        } else {
            if (toolbar != null) {
                toolbar.setVisibility(visibility);
                // TODO toolbarShadowView.setVisibility(visibility);
            }
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
                                preferenceFragmentArguments);

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
                                preferenceFragmentArguments);

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
                        preferenceFragmentArguments);
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
                        preferenceFragmentArguments);
            }
        }

        return result;
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
     * Returns, whether the split screen layout is used, or not.
     *
     * @return True, if the split screen layout is used, false otherwise
     */
    public final boolean isSplitScreen() {
        return useSplitScreen && getDeviceType(this) == DeviceType.TABLET;
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
     * overridden in order to return to the navigation when a preference screen is currently
     * shown and the split screen layout is used, or not.
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
     * and the last preference header is currently selected.
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
     * the last preference header is currently selected.
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
     * the last preference header is currently selected.
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
     * Returns the visibility of the toolbar, which is used to show the breadcrumb of the currently
     * selected navigation preference.
     *
     * @return The visibility of the toolbar, which is used to show the breadcrumb of the currently
     * selected navigation preference, as an {@link Integer} value. The visibility must either be
     * <code>View.VISIBLE</code>, <code>View.INVISIBLE</code> or <code>View.GONE</code>
     */
    public final int getBreadCrumbVisibility() {
        return breadCrumbVisibility;
    }

    /**
     * Sets the visibility of the toolbar, which is used to show the breadcrumb of the currently
     * selected navigation preference. This takes effect regardless of whether the split screen
     * layout is used, or not.
     *
     * @param visibility
     *         The visibility, which should be set, as an {@link Integer} value. The visibility must
     *         either be <code>View.VISIBLE</code>, <code>View.INVISIBLE</code> or
     *         <code>View.GONE</code>
     */
    public final void setBreadCrumbVisibility(final int visibility) {
        this.breadCrumbVisibility = visibility;
        adaptBreadCrumbVisibility(visibility);
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
    public void onNavigationCreated(@NonNull final android.preference.PreferenceFragment fragment) {

    }

    @Override
    public final void onNavigationAdapterCreated() {
        if (navigationFragment.getNavigationPreferenceCount() > 0 &&
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

        if (savedInstanceState == null) {
            obtainStyledAttributes();
            handleIntent();
        } else {
            useSplitScreen(savedInstanceState.getBoolean(USE_SPLIT_SCREEN_EXTRA));
            setNavigationWidth(savedInstanceState.getInt(NAVIGATION_WIDTH_EXTRA));
            hideNavigation(savedInstanceState.getBoolean(HIDE_NAVIGATION_EXTRA));
            overrideNavigationIcon(savedInstanceState.getBoolean(OVERRIDE_NAVIGATION_ICON_EXTRA));
            showButtonBar(savedInstanceState.getBoolean(SHOW_BUTTON_BAR_EXTRA));
            setNextButtonText(savedInstanceState
                    .getCharSequence(NEXT_BUTTON_TEXT_EXTRA, getText(R.string.next_button_label)));
            setBackButtonText(savedInstanceState
                    .getCharSequence(BACK_BUTTON_TEXT_EXTRA, getText(R.string.back_button_label)));
            setFinishButtonText(savedInstanceState.getCharSequence(FINISH_BUTTON_TEXT_EXTRA,
                    getText(R.string.finish_button_label)));
            showProgress(savedInstanceState.getBoolean(SHOW_PROGRESS_EXTRA));
            setProgressFormat(savedInstanceState
                    .getString(PROGRESS_FORMAT_EXTRA, getString(R.string.progress_format)));
            setBreadCrumbVisibility(savedInstanceState.getInt(PROGRESS_FORMAT_EXTRA, 0));
            preferenceFragmentArguments =
                    savedInstanceState.getBundle(PREFERENCE_FRAGMENT_ARGUMENTS_EXTRA);
        }

        inflateLayout();
        initializeToolbar();
        initializeFragments();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(USE_SPLIT_SCREEN_EXTRA, useSplitScreen);
        outState.putInt(NAVIGATION_WIDTH_EXTRA, navigationWidth);
        outState.putBoolean(HIDE_NAVIGATION_EXTRA, hideNavigation);
        outState.putBoolean(OVERRIDE_NAVIGATION_ICON_EXTRA, overrideNavigationIcon);
        outState.putBoolean(SHOW_BUTTON_BAR_EXTRA, showButtonBar);
        outState.putCharSequence(NEXT_BUTTON_TEXT_EXTRA, nextButtonText);
        outState.putCharSequence(BACK_BUTTON_TEXT_EXTRA, backButtonText);
        outState.putCharSequence(FINISH_BUTTON_TEXT_EXTRA, finishButtonText);
        outState.putBoolean(SHOW_PROGRESS_EXTRA, showProgress);
        outState.putInt(BREADCRUMB_VISIBILITY_EXTRA, breadCrumbVisibility);
        outState.putString(PROGRESS_FORMAT_EXTRA, progressFormat);
        outState.putBundle(PREFERENCE_FRAGMENT_ARGUMENTS_EXTRA, preferenceFragmentArguments);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        NavigationPreference selectedNavigationPreference = getSelectedNavigationPreference();

        if (selectedNavigationPreference != null) {
            showBreadCrumb(selectedNavigationPreference);
            showToolbarNavigationIcon();
        }
    }

}