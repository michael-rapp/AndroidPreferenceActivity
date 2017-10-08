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
import android.widget.FrameLayout;

import de.mrapp.android.preference.activity.fragment.NavigationFragment;
import de.mrapp.android.preference.activity.view.ToolbarLarge;
import de.mrapp.android.util.Condition;
import de.mrapp.android.util.DisplayUtil.DeviceType;
import de.mrapp.android.util.ThemeUtil;

import static de.mrapp.android.util.Condition.ensureGreater;
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
        implements NavigationFragment.Callback, NavigationPreference.Callback {

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
     * The fragment, which contains the activity's navigation.
     */
    private NavigationFragment navigationFragment;

    /**
     * The card view, which contains the currently shown preference fragment, as well as its
     * breadcrumb, when using the split screen layout.
     */
    private CardView cardView;

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
     * True, if the navigation icon of the activity's toolbar is shown by default, false otherwise.
     */
    private boolean displayHomeAsUp;

    /**
     * Obtains all relevant attributes from the activity's theme.
     */
    private void obtainStyledAttributes() {
        obtainUseSplitScreen();
        obtainNavigationWidth();
        obtainNavigationVisibility();
        obtainOverrideNavigationIcon();
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
     * Obtains, whether the behavior of the navigation icon should be overridden, or not.
     */
    private void obtainOverrideNavigationIcon() {
        boolean overrideNavigationIcon =
                ThemeUtil.getBoolean(this, R.attr.overrideNavigationIcon, true);
        overrideNavigationIcon(overrideNavigationIcon);
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
        adaptNavigationWidth();
        adaptNavigationVisibility();
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

        navigationFragment.setNavigationPreferenceCallback(this);
        preferenceFragment = getFragmentManager().findFragmentByTag(PREFERENCE_FRAGMENT_TAG);
    }

    /**
     * Shows the fragment, which is associated with a specific navigation preference.
     *
     * @param navigationPreference
     *         The navigation preference, whose fragment should be shown, as an instance of the
     *         class {@link NavigationPreference}. The navigation preference may not be null
     * @return True, if the fragment has been shown, false otherwise
     */
    private boolean showPreferenceFragment(
            @NonNull final NavigationPreference navigationPreference) {
        String fragment = navigationPreference.getFragment();

        if (!TextUtils.isEmpty(fragment)) {
            preferenceFragment = Fragment.instantiate(this, fragment);
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
            navigationFragment.selectNavigationPreference(-1);
            resetTitle();
            hideToolbarNavigationIcon();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(preferenceFragment);
            transaction.show(navigationFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();
            preferenceFragment = null;
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
        return false; // TODO
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
            navigationFragment.selectNavigationPreference(0);
        }
    }

    @Override
    public final boolean onShowFragment(@NonNull final NavigationPreference navigationPreference) {
        return showPreferenceFragment(navigationPreference);
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