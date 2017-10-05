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
     * The activity's toolbar.
     */
    private Toolbar toolbar;

    /**
     * The view, which is used to display a large toolbar, when using the split screen layout.
     */
    private ToolbarLarge toolbarLarge;

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
    private boolean navigationHidden;

    /**
     * Obtains all relevant attributes from the activity's theme.
     */
    private void obtainStyledAttributes() {
        obtainUseSplitScreen();
        obtainNavigationWidth();
        obtainNavigationVisibility();
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
        boolean hideNavigation = ThemeUtil.getBoolean(this, R.attr.hideNavigation, true);
        hideNavigation(hideNavigation);
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
        setTitle(getTitle());
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
     */
    private void showPreferenceFragment(@NonNull final NavigationPreference navigationPreference) {
        String fragment = navigationPreference.getFragment();

        if (preferenceFragment == null ||
                !preferenceFragment.getClass().getName().equals(fragment)) {
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
        }
    }

    /**
     * Removes the currently shown preference fragment, if the split screen layout is not used.
     *
     * @return True, if a preference fragment has been removed, false otherwise
     */
    private boolean removePreferenceFragment() {
        if (!isSplitScreen() && preferenceFragment != null) {
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
        return navigationHidden;
    }

    /**
     * Hides or shows the navigation. When the activity is used as a wizard on devices with a small
     * screen, the navigation is always hidden.
     *
     * @param hideNavigation
     *         True, if the navigation should be hidden, false otherwise
     */
    public final void hideNavigation(final boolean hideNavigation) {
        this.navigationHidden = hideNavigation;
        adaptNavigationVisibility();
    }

    @Override
    public final void setTitle(@StringRes final int resourceId) {
        setTitle(getText(resourceId));
    }

    @Override
    public final void setTitle(@Nullable final CharSequence title) {
        super.setTitle(title);
        ActionBar actionBar = getSupportActionBar();

        if (isSplitScreen()) {
            toolbarLarge.setTitle(title);

            if (actionBar != null) {
                actionBar.setTitle(null);
            }
        } else {
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    @Override
    public void onNavigationCreated(@NonNull final android.preference.PreferenceFragment fragment) {

    }

    @Override
    public final void onShowFragment(@NonNull final NavigationPreference navigationPreference) {
        showPreferenceFragment(navigationPreference);
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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainStyledAttributes();
        inflateLayout();
        initializeToolbar();
        initializeFragments();
    }

}