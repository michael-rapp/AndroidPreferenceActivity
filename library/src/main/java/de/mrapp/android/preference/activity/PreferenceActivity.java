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
     * The name of the extra, which is used to store whether the split screen layout should be used,
     * or not, within a bundle.
     */
    private static final String USE_SPLIT_SCREEN_EXTRA =
            PreferenceActivity.class.getName() + "::UseSplitScreen";

    /**
     * The name of the extra, which is used to store the navigation width within a bundle.
     */
    private static final String NAVIGATION_WIDTH_EXTRA =
            PreferenceActivity.class.getName() + "::NavigationWidth";

    /**
     * The name of the extra, which is used to store whether the navigation should be hidden, or
     * not, within a bundle.
     */
    private static final String HIDE_NAVIGATION_EXTRA =
            PreferenceActivity.class.getName() + "::HideNavigation";

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
     * @return True, if the fragment has been shown, false otherwise
     */
    private boolean showPreferenceFragment(@NonNull final NavigationPreference navigationPreference,
                                           @Nullable final Bundle arguments) {
        if (arguments != null && navigationPreference.getExtras() != null) {
            arguments.putAll(navigationPreference.getExtras());
        }

        String fragment = navigationPreference.getFragment();

        if (!TextUtils.isEmpty(fragment)) {
            preferenceFragmentArguments =
                    arguments != null ? arguments : navigationPreference.getExtras();
            preferenceFragment = Fragment.instantiate(this, fragment, preferenceFragmentArguments);
            preferenceFragment.setRetainInstance(true);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            if (!isSplitScreen()) {
                transaction.hide(navigationFragment);
                transaction.add(R.id.navigation_fragment_container, preferenceFragment,
                        PREFERENCE_FRAGMENT_TAG);
            } else {
                transaction.replace(R.id.preference_fragment_container, preferenceFragment,
                        PREFERENCE_FRAGMENT_TAG);
            }

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.commit();
            showBreadCrumb(navigationPreference);
            showToolbarNavigationIcon();
            return true;
        }

        return false;
    }

    /**
     * Removes the currently shown preference fragment, if the split screen layout is not used.
     *
     * @return True, if a preference fragment has been removed, false otherwise
     */
    private boolean removePreferenceFragment() {
        if (!isSplitScreen() && isPreferenceFragmentShown()) {
            navigationFragment.selectNavigationPreference(-1, null);
            resetTitle();
            hideToolbarNavigationIcon();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(preferenceFragment);
            transaction.show(navigationFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();
            preferenceFragment = null;
            preferenceFragmentArguments = null;
            return true;
        }

        return false;
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
        // TODO: Format bread crumb title
        if (!TextUtils.isEmpty(breadCrumbTitle)) {
            if (isSplitScreen()) {
                breadCrumbToolbar.setTitle(breadCrumbTitle);
            } else {
                showTitle(breadCrumbTitle);
            }
        }
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
     * Adapts the width of the navigation.
     */
    private void adaptNavigationWidth() {
        if (navigationFragmentContainer != null && cardView != null && toolbarLarge != null) {
            ViewCompat.setPaddingRelative(navigationFragmentContainer, 0, 0,
                    getDisplayWidth(this) - navigationWidth, 0);
            FrameLayout.LayoutParams preferenceScreenLayoutParams =
                    (FrameLayout.LayoutParams) cardView.getLayoutParams();
            MarginLayoutParamsCompat.setMarginStart(preferenceScreenLayoutParams, navigationWidth -
                    getResources().getDimensionPixelSize(R.dimen.card_view_intrinsic_margin));
            cardView.requestLayout();
            toolbarLarge.setNavigationWidth(navigationWidth);
        }
    }

    /**
     * Adapts the visibility of the navigation.
     */
    private void adaptNavigationVisibility() {
        // TODO
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
            adaptWizardButtons();
        }
    }

    /**
     * Adapts the buttons of the button bar which is shown, when the activity is used as a wizard,
     * depending on the currently selected navigation preference.
     */
    private void adaptWizardButtons() {
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
        // TODO Recreate activity if necessary
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
        if (isSplitScreen() && navigationFragment.getNavigationPreferenceCount() > 0) {
            navigationFragment.selectNavigationPreference(0, null);
        }
    }

    @Override
    public final boolean onNavigationPreferenceSelected(
            @NonNull final NavigationPreference navigationPreference,
            @Nullable final Bundle arguments) {
        return showPreferenceFragment(navigationPreference, arguments);
    }

    @CallSuper
    @Override
    public void onBackPressed() {
        if (!removePreferenceFragment()) {
            super.onBackPressed();
        }
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isNavigationIconOverridden() && !isNavigationHidden() && !isButtonBarShown() &&
                    removePreferenceFragment()) {
                return true;
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
        } else {
            useSplitScreen(savedInstanceState.getBoolean(USE_SPLIT_SCREEN_EXTRA));
            setNavigationWidth(savedInstanceState.getInt(NAVIGATION_WIDTH_EXTRA));
            hideNavigation(savedInstanceState.getBoolean(HIDE_NAVIGATION_EXTRA));
            overrideNavigationIcon(savedInstanceState.getBoolean(OVERRIDE_NAVIGATION_ICON_EXTRA));
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