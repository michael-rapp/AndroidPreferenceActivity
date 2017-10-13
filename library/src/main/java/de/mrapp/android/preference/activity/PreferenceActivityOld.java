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
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.XmlRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import de.mrapp.android.preference.activity.adapter.AdapterListener;
import de.mrapp.android.preference.activity.adapter.PreferenceHeaderAdapter;
import de.mrapp.android.preference.activity.fragment.FragmentListener;
import de.mrapp.android.preference.activity.fragment.PreferenceHeaderFragment;
import de.mrapp.android.preference.activity.parser.PreferenceHeaderParser;
import de.mrapp.android.preference.activity.view.ToolbarLarge;
import de.mrapp.android.util.ElevationUtil;
import de.mrapp.android.util.ViewUtil;
import de.mrapp.android.util.view.ElevationShadowView;

import static de.mrapp.android.util.Condition.ensureAtLeast;
import static de.mrapp.android.util.Condition.ensureAtMaximum;
import static de.mrapp.android.util.Condition.ensureGreater;
import static de.mrapp.android.util.Condition.ensureNotNull;
import static de.mrapp.android.util.DisplayUtil.dpToPixels;
import static de.mrapp.android.util.DisplayUtil.pixelsToDp;

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
public abstract class PreferenceActivityOld extends AppCompatActivity
        implements FragmentListener, OnItemClickListener, AdapterListener {

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
     * When starting this activity and using <code>EXTRA_SHOW_FRAGMENT</code>, this extra can also
     * be specified to supply the short title to be shown for that fragment.
     */
    public static final String EXTRA_SHOW_FRAGMENT_SHORT_TITLE =
            ":android:show_fragment_short_title";

    /**
     * When starting this activity, the invoking intent can contain this extra boolean that the
     * header list should not be displayed. This is most often used in conjunction with
     * <code>EXTRA_SHOW_FRAGMENT</code> to launch the activity to display a specific fragment that
     * the user has navigated to.
     */
    public static final String EXTRA_NO_HEADERS = ":android:no_headers";

    /**
     * When starting this activity, the invoking intent can contain this extra boolean that the
     * toolbar, which is used to show the title of the currently selected preference header, should
     * not be displayed.
     */
    public static final String EXTRA_NO_BREAD_CRUMBS = ":extra_prefs_no_bread_crumbs";

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
     * be specified to supply a custom text for the back button when the last preference header is
     * shown.
     */
    public static final String EXTRA_FINISH_BUTTON_TEXT = "extra_prefs_set_finish_text";

    /**
     * When starting this activity using <code>EXTRA_SHOW_BUTTON_BAR</code>, this boolean extra can
     * also used to specify, whether the number of the currently shown wizard step and the number of
     * total steps should be shown as the bread crumb title.
     */
    public static final String EXTRA_SHOW_PROGRESS = "extra_prefs_show_progress";

    /**
     * When starting this activity using <code>EXTRA_SHOW_BUTTON_BAR</code> and
     * <code>EXTRA_SHOW_PROGRESS</code>, this string extra can also be specified to supply a custom
     * format for showing the progress. The string must be formatted according to the following
     * syntax: "*%d*%d*%s*"
     */
    public static final String EXTRA_PROGRESS_FORMAT = "extra_prefs_progress_format";

    /**
     * The name of the extra, which is used to save the parameters, which have been passed when the
     * currently shown fragment has been created, within a bundle.
     */
    @VisibleForTesting
    protected static final String CURRENT_BUNDLE_EXTRA =
            PreferenceActivityOld.class.getSimpleName() + "::CurrentBundle";

    /**
     * The name of the extra, which is used to save the title, which is currently used by the bread
     * crumb, within a bundle.
     */
    @VisibleForTesting
    protected static final String CURRENT_TITLE_EXTRA =
            PreferenceActivityOld.class.getSimpleName() + "::CurrentTitle";

    /**
     * The name of the extra, which is used to save the short title, which is currently used by the
     * bread crumb, within a bundle.
     */
    @VisibleForTesting
    protected static final String CURRENT_SHORT_TITLE_EXTRA =
            PreferenceActivityOld.class.getSimpleName() + "::CurrentShortTitle";

    /**
     * The name of the extra, which is used to save the currently selected preference header, within
     * a bundle.
     */
    @VisibleForTesting
    protected static final String CURRENT_PREFERENCE_HEADER_EXTRA =
            PreferenceActivityOld.class.getSimpleName() + "::CurrentPreferenceHeader";

    /**
     * The name of the extra, which is used to saved the preference headers within a bundle.
     */
    @VisibleForTesting
    protected static final String PREFERENCE_HEADERS_EXTRA =
            PreferenceActivityOld.class.getSimpleName() + "::PreferenceHeaders";

    /**
     * The name of the extra, which is used to save the fragment, which is currently shown as the
     * preference screen, within a bundle.
     */
    private static final String PREFERENCE_SCREEN_FRAGMENT_EXTRA =
            PreferenceActivityOld.class.getSimpleName() + "::PreferenceScreenFragment";

    /**
     * The saved instance state, which has been passed to the activity, when it has been created.
     */
    private Bundle savedInstanceState;

    /**
     * The fragment, which has been shown as the preference screen before the activity has been
     * recreated or null, if no preference header has been previously selected.
     */
    private Fragment restoredPreferenceScreenFragment;

    /**
     * The activity's main toolbar.
     */
    private Toolbar toolbar;

    /**
     * The view, which is used to visualize a large toolbar on devices with a large screen.
     */
    private ToolbarLarge toolbarLarge;

    /**
     * The fragment, which contains the preference headers and provides the navigation to each
     * header's fragment.
     */
    private PreferenceHeaderFragment preferenceHeaderFragment;

    /**
     * The fragment, which is currently shown as the preference screen or null, if no preference
     * header is currently selected.
     */
    private Fragment preferenceScreenFragment;

    /**
     * The frame layout, which contains the activity's views. It is the activity's root view.
     */
    private FrameLayout frameLayout;

    /**
     * The parent view of the fragment, which provides the navigation to each preference header's
     * fragment.
     */
    private ViewGroup preferenceHeaderParentView;

    /**
     * The parent view of the fragment, which is used to show the preferences of the currently
     * selected preference header on devices with a large screen.
     */
    private ViewGroup preferenceScreenParentView;

    /**
     * The card view, which contains all views, e.g. the preferences themselves and the bread crumb,
     * which are shown when a preference header is selected on devices with a large screen.
     */
    private CardView preferenceScreenContainer;

    /**
     * The toolbar, which is used to show the bread crumb of the currently selected preference
     * header on devices with a large screen.
     */
    private Toolbar breadCrumbToolbar;

    /**
     * The view, which is used to draw a shadow below the toolbar, which is used to show the bread
     * crumb of the currently selected preference header on devices with a large screen.
     */
    private ElevationShadowView breadCrumbShadowView;

    /**
     * The view group, which contains the buttons, which are shown when the activity is used as a
     * wizard.
     */
    private ViewGroup buttonBar;

    /**
     * The back button, which is shown, if the activity is used as a wizard.
     */
    private Button backButton;

    /**
     * The next button, which is shown, if the activity is used as a wizard and the last preference
     * header is currently not selected.
     */
    private Button nextButton;

    /**
     * The finish button, which is shown, if the activity is used as a wizard and the last
     * preference header is currently selected.
     */
    private Button finishButton;

    /**
     * The view, which is used to draw a shadow below the activity's toolbar.
     */
    private ElevationShadowView toolbarShadowView;

    /**
     * The view, which is used to draw a shadow above the button bar when the activity is used as a
     * wizard.
     */
    private ElevationShadowView buttonBarShadowView;

    /**
     * The preference header, which is currently selected or null, if no preference header is
     * currently selected.
     */
    private PreferenceHeader currentHeader;

    /**
     * The parameters which have been passed to the currently shown fragment or null, if no
     * parameters have been passed.
     */
    private Bundle currentBundle;

    /**
     * The title, which is currently used by the bread crumb or null, if no bread crumb is currently
     * shown.
     */
    private CharSequence currentTitle;

    /**
     * The short title, which is currently used by the bread crumb or null, if no bread crumb is
     * currently shown.
     */
    private CharSequence currentShortTitle;

    /**
     * True, if the navigation icon of the activity's toolbar should be shown by default, false
     * otherwise.
     */
    private Boolean displayHomeAsUp;

    /**
     * The default title of the activity.
     */
    private CharSequence defaultTitle;

    /**
     * True, if the behavior of the navigation icon of the activity's toolbar is overridden in order
     * to return to the navigation when a preference header is currently selected on devices with a
     * small screen.
     */
    private boolean overrideNavigationIcon;

    /**
     * The width of the parent view of the fragment, which provides navigation to each preference
     * header's fragment on devices with a large screen, in dp.
     */
    private int navigationWidth;

    /**
     * True, if the fragment, which provides navigation to each preference header's fragment on
     * devices with a large screen, is currently hidden or not.
     */
    private boolean navigationHidden;

    /**
     * The background color of the view group, which contains the views, which are shown when a
     * preference screen is selected on a device with a large screen.
     */
    private int preferenceScreenBackgroundColor;

    /**
     * The background color of the toolbar, which is used to show the title of the currently
     * selected preference header on devices with a large screen.
     */
    private int breadCrumbBackgroundColor;

    /**
     * The elevation of the view group, which contains the views, which are shown when a preference
     * screen is selected on a device with a large screen, in dp.
     */
    private int preferenceScreenElevation;

    /**
     * True, if the progress should be shown as the bread crumb title, when the activity is used as
     * a wizard, false otherwise.
     */
    private boolean showProgress;

    /**
     * The text, which is used to format the progress, which is shown as the bread crumb title.
     */
    private String progressFormat;

    /**
     * The visibility of the toolbar, which is used to show the title of the currently selected
     * preference header.
     */
    private int breadCrumbVisibility = View.VISIBLE;

    /**
     * A set, which contains the listeners, which have been registered to be notified when the
     * currently shown preference fragment has been changed.
     */
    private Set<PreferenceFragmentListener> preferenceFragmentListeners = new LinkedHashSet<>();

    /**
     * A set, which contains the listeners, which have registered to be notified when the user
     * navigates within the activity, if it used as a wizard.
     */
    private Set<WizardListenerOld> wizardListeners = new LinkedHashSet<>();

    /**
     * Initializes the action bar's toolbar.
     */
    private void initializeToolbar() {
        if (getSupportActionBar() != null) {
            throw new IllegalStateException("An action bar is already attached to the activity. " +
                    "Use the theme \"@style/Theme.AppCompat.NoActionBar\" or " +
                    "\"@style/Theme.AppCompat.Light.NoActionBar\" as the activity's theme");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (isSplitScreen()) {
            toolbarLarge = (ToolbarLarge) findViewById(R.id.toolbar_large);
            toolbarLarge.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(toolbar);
        setTitle(getTitle());
    }

    /**
     * Initializes the preference header, which is selected by default on devices with a large
     * screen.
     */
    private void initializeSelectedPreferenceHeader() {
        if (isSplitScreen()) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            if (!getListAdapter().isEmpty()) {
                selectPreferenceHeader(0);
            }
        }
    }

    /**
     * Handles extras of the intent, which has been used to start the activity, that allow to
     * initially display a specific fragment.
     */
    private void handleInitialFragmentIntent() {
        String initialFragment = getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT);
        Bundle initialArguments = getIntent().getBundleExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS);
        CharSequence initialTitle =
                getCharSequenceFromIntent(getIntent(), EXTRA_SHOW_FRAGMENT_TITLE);
        CharSequence initialShortTitle =
                getCharSequenceFromIntent(getIntent(), EXTRA_SHOW_FRAGMENT_SHORT_TITLE);

        if (initialFragment != null) {
            for (PreferenceHeader preferenceHeader : getListAdapter().getAllItems()) {
                if (preferenceHeader.getFragment() != null &&
                        preferenceHeader.getFragment().equals(initialFragment)) {
                    selectPreferenceHeader(preferenceHeader, initialArguments);

                    if (initialTitle != null) {
                        showBreadCrumb(initialTitle, initialShortTitle);
                    }
                }
            }
        }
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
     * Returns a listener, which allows to proceed to the next step, when the activity is used as a
     * wizard.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}
     */
    private OnClickListener createNextButtonListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final View v) {
                int currentIndex = getListAdapter().indexOf(currentHeader);

                if (currentIndex < getNumberOfPreferenceHeaders() - 1) {
                    Bundle params = notifyOnNextStep();

                    if (params != null) {
                        selectPreferenceHeader(currentIndex + 1, params);
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
     * OnClickListener}
     */
    private OnClickListener createBackButtonListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final View v) {
                int currentIndex = getListAdapter().indexOf(currentHeader);

                if (currentIndex > 0) {
                    Bundle params = notifyOnPreviousStep();

                    if (params != null) {
                        selectPreferenceHeader(currentIndex - 1, params);
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
     * OnClickListener}
     */
    private OnClickListener createFinishButtonListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final View v) {
                notifyOnFinish();
            }

        };
    }

    /**
     * Notifies all registered listeners that the user wants to navigate to the next step of the
     * wizard.
     *
     * @return A bundle, which may contain key-value pairs, which have been acquired in the wizard,
     * if navigating to the next step of the wizard should be allowed, as an instance of the class
     * {@link Bundle}, null otherwise
     */
    private Bundle notifyOnNextStep() {
        Bundle result = null;

        for (WizardListenerOld listener : wizardListeners) {
            Bundle bundle =
                    listener.onNextStep(getListAdapter().indexOf(currentHeader), currentHeader,
                            preferenceScreenFragment, currentBundle);

            if (bundle != null) {
                if (result == null) {
                    result = new Bundle();
                }

                result.putAll(bundle);
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

        for (WizardListenerOld listener : wizardListeners) {
            Bundle bundle =
                    listener.onPreviousStep(getListAdapter().indexOf(currentHeader), currentHeader,
                            preferenceScreenFragment, currentBundle);

            if (bundle != null) {
                if (result == null) {
                    result = new Bundle();
                }

                result.putAll(bundle);
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

        for (WizardListenerOld listener : wizardListeners) {
            result &= listener.onFinish(getListAdapter().indexOf(currentHeader), currentHeader,
                    preferenceScreenFragment, currentBundle);
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

        for (WizardListenerOld listener : wizardListeners) {
            result &= listener.onSkip(getListAdapter().indexOf(currentHeader), currentHeader,
                    preferenceScreenFragment, currentBundle);
        }

        return result;
    }

    /**
     * Notifies all registered listeners that a preference fragment has been shown.
     */
    private void notifyOnPreferenceFragmentShown() {
        for (PreferenceFragmentListener listener : preferenceFragmentListeners) {
            listener.onPreferenceFragmentShown(getListAdapter().indexOf(currentHeader),
                    currentHeader, preferenceScreenFragment);
        }
    }

    /**
     * Nptifies all registered listeners that a preference fragment has been hidden.
     */
    private void notifyOnPreferenceFragmentHidden() {
        for (PreferenceFragmentListener listener : preferenceFragmentListeners) {
            listener.onPreferenceFragmentHidden();
        }
    }

    /**
     * Shows the fragment, which corresponds to a specific preference header.
     *
     * @param preferenceHeader
     *         The preference header, the fragment, which should be shown, corresponds to, as an
     *         instance of the class {@link PreferenceHeader}. The preference header may not be
     *         null
     * @param parameters
     *         The parameters, which should be passed to the fragment, as an instance of the class
     *         {@link Bundle} or null, if the preference header's extras should be used instead
     * @param forceTransition
     *         True, if instantiating a new fragment should be enforced, even if a fragment instance
     *         of the same class is already shown, false otherwise. Must be true for transitions,
     *         which have been initiated manually by the user in order to support using the same
     *         fragment class for multiple preference headers
     */
    private void showPreferenceScreen(@NonNull final PreferenceHeader preferenceHeader,
                                      @Nullable final Bundle parameters,
                                      final boolean forceTransition) {
        if (parameters != null && preferenceHeader.getExtras() != null) {
            parameters.putAll(preferenceHeader.getExtras());
        }

        showPreferenceScreen(preferenceHeader, parameters, true, forceTransition);
    }

    /**
     * Shows the fragment, which corresponds to a specific preference header.
     *
     * @param preferenceHeader
     *         The preference header, the fragment, which should be shown, corresponds to, as an
     *         instance of the class {@link PreferenceHeader}. The preference header may not be
     *         null
     * @param parameters
     *         The parameters, which should be passed to the fragment, as an instance of the class
     *         {@link Bundle} or null, if the preference header's extras should be used instead
     * @param launchIntent
     *         True, if a preference header's intent should be launched, false otherwise
     * @param forceTransition
     *         True, if instantiating a new fragment should be enforced, even if a fragment instance
     *         of the same class is already shown, false otherwise. Must be true for transitions,
     *         which have been initiated manually by the user in order to support using the same
     *         fragment class for multiple preference headers
     */
    private void showPreferenceScreen(@NonNull final PreferenceHeader preferenceHeader,
                                      @Nullable final Bundle parameters, final boolean launchIntent,
                                      final boolean forceTransition) {
        if (currentHeader == null || !currentHeader.equals(preferenceHeader)) {
            currentHeader = preferenceHeader;
            adaptWizardButtons();
            adaptBreadCrumbVisibility(
                    parameters != null ? parameters : preferenceHeader.getExtras());

            if (preferenceHeader.getFragment() != null) {
                showBreadCrumb(preferenceHeader);
                currentBundle = (parameters != null) ? parameters : preferenceHeader.getExtras();
                showPreferenceScreenFragment(preferenceHeader, currentBundle, forceTransition);
            } else if (preferenceScreenFragment != null) {
                showBreadCrumb(preferenceHeader);
                removeFragment(preferenceScreenFragment);
                preferenceScreenFragment = null;
            }
        }

        if (launchIntent && preferenceHeader.getIntent() != null) {
            startActivity(preferenceHeader.getIntent());
        }
    }

    /**
     * Shows the fragment, which corresponds to a specific class name.
     *
     * @param preferenceHeader
     *         The preference header, the fragment, which should be shown, corresponds to, as an
     *         instance of the class {@link PreferenceHeader}. The preference header may not be
     *         null
     * @param params
     *         The parameters, which should be passed to the fragment, as an instance of the class
     *         {@link Bundle} or null, if the preference header's extras should be used instead
     * @param forceTransition
     *         True, if instantiating a new fragment should be enforced, even if a fragment instance
     *         of the same class is already shown, false otherwise. Must be true for transitions,
     *         which have been initiated manually by the user in order to support using the same
     *         fragment class for multiple preference headers
     */
    private void showPreferenceScreenFragment(@NonNull final PreferenceHeader preferenceHeader,
                                              @Nullable final Bundle params,
                                              final boolean forceTransition) {
        String fragmentName = preferenceHeader.getFragment();

        if (forceTransition || preferenceScreenFragment == null ||
                !preferenceScreenFragment.getClass().getName().equals(fragmentName)) {
            preferenceScreenFragment = Fragment.instantiate(this, fragmentName, params);
        }

        if (isSplitScreen()) {
            replaceFragment(preferenceScreenFragment, R.id.preference_screen_parent, 0);
        } else {
            updateSavedInstanceState();
            replaceFragment(preferenceScreenFragment, R.id.preference_header_parent,
                    FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            // TODO showToolbarNavigationIcon();
        }

        notifyOnPreferenceFragmentShown();
    }

    /**
     * Adapts the buttons which are shown, when the activity is used as a wizard, depending on the
     * currently selected preference header.
     */
    private void adaptWizardButtons() {
        if (currentHeader != null && isButtonBarShown()) {
            int index = getListAdapter().indexOf(currentHeader);
            getBackButton().setVisibility((index != 0) ? View.VISIBLE : View.GONE);
            getNextButton().setVisibility(
                    (index != getListAdapter().getCount() - 1) ? View.VISIBLE : View.GONE);
            getFinishButton().setVisibility(
                    (index == getListAdapter().getCount() - 1) ? View.VISIBLE : View.GONE);
        } else if (isButtonBarShown()) {
            getBackButton().setVisibility(View.GONE);
            getNextButton().setVisibility(View.GONE);
            getFinishButton().setVisibility(View.VISIBLE);
        }
    }

    /**
     * Adapts the visibility of the toolbar, which is used to show the title of the currently
     * selected preference header, depending on the currently selected preference header.
     *
     * @param parameters
     *         The parameters of the currently selected preference header as an instance of the
     *         class {@link Bundle} or null, if the preference header contains no parameters
     */
    private void adaptBreadCrumbVisibility(@Nullable final Bundle parameters) {
        if (parameters != null && parameters.containsKey(EXTRA_NO_BREAD_CRUMBS)) {
            boolean hideBreadCrumb = parameters.getBoolean(EXTRA_NO_BREAD_CRUMBS, false);
            adaptBreadCrumbVisibility(hideBreadCrumb ? View.GONE : View.VISIBLE);
        } else {
            adaptBreadCrumbVisibility(breadCrumbVisibility);
        }
    }

    /**
     * Adapts the visibility of the toolbar, which is used to show the title of the currently
     * selected preference header.
     *
     * @param visibility
     *         The visibility, which should be set, as an {@link Integer} value. The visibility may
     *         either be <code>View.VISIBLE</code>, <code>View.INVISIBLE</code> or
     *         <code>View.GONE</code>
     */
    private void adaptBreadCrumbVisibility(final int visibility) {
        if (isSplitScreen()) {
            breadCrumbToolbar.setVisibility(visibility);
            breadCrumbShadowView.setVisibility(visibility);
        } else {
            toolbar.setVisibility(visibility);
            toolbarShadowView.setVisibility(visibility);
        }
    }

    /**
     * Adapts the GUI, depending on whether the navigation is currently hidden or not.
     *
     * @param navigationHidden
     *         True, if the navigation is currently hidden, false otherwise
     */
    private void adaptNavigation(final boolean navigationHidden) {
        if (isSplitScreen()) {
            getPreferenceHeaderParentView()
                    .setVisibility(navigationHidden ? View.GONE : View.VISIBLE);
            FrameLayout.LayoutParams preferenceScreenLayoutParams =
                    (FrameLayout.LayoutParams) getPreferenceScreenContainer().getLayoutParams();
            MarginLayoutParamsCompat.setMarginStart(preferenceScreenLayoutParams,
                    (navigationHidden ? getResources()
                            .getDimensionPixelSize(R.dimen.preference_screen_horizontal_margin) :
                            dpToPixels(this, navigationWidth)) - getResources()
                            .getDimensionPixelSize(R.dimen.card_view_intrinsic_margin));
            MarginLayoutParamsCompat.setMarginEnd(preferenceScreenLayoutParams, getResources()
                    .getDimensionPixelSize(
                            navigationHidden ? R.dimen.preference_screen_horizontal_margin :
                                    R.dimen.preference_screen_margin_right) -
                    getResources().getDimensionPixelSize(R.dimen.card_view_intrinsic_margin));
            preferenceScreenLayoutParams.gravity =
                    navigationHidden ? Gravity.CENTER_HORIZONTAL : Gravity.NO_GRAVITY;
            getPreferenceScreenContainer().requestLayout();
            toolbarLarge.hideNavigation(navigationHidden);
        } else {
            if (isPreferenceHeaderSelected()) {
                if (navigationHidden) {
                    // TODO hideToolbarNavigationIcon();
                } else {
                    // TODO showToolbarNavigationIcon();
                }
            } else if (navigationHidden) {
                if (getListAdapter() != null && !getListAdapter().isEmpty()) {
                    showPreferenceScreen(getListAdapter().getItem(0), null, false);
                } else if (getListAdapter() != null) {
                    finish();
                }
            }
        }
    }

    /**
     * Shows the fragment, which provides the navigation to each preference header's fragment.
     */
    private void showPreferenceHeaders() {
        notifyOnPreferenceFragmentHidden();
        int transition = 0;

        if (isPreferenceHeaderSelected()) {
            transition = FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;
            currentHeader = null;
            preferenceScreenFragment = null;
        }

        adaptBreadCrumbVisibility(breadCrumbVisibility);
        replaceFragment(preferenceHeaderFragment, R.id.preference_header_parent, transition);
    }

    /**
     * Replaces the fragment, which is currently contained by a specific parent view, by an other
     * fragment.
     *
     * @param fragment
     *         The fragment, which should replace the current fragment, as an instance of the class
     *         {@link Fragment}. The fragment may not be null
     * @param parentViewId
     *         The id of the parent view, which contains the fragment, that should be replaced, as
     *         an {@link Integer} value
     * @param transition
     *         The transition, which should be shown when replacing the fragment, as an {@link
     *         Integer} value or 0, if no transition should be shown
     */
    private void replaceFragment(@NonNull final Fragment fragment, final int parentViewId,
                                 final int transition) {
        if (fragment.isAdded()) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
            getFragmentManager().executePendingTransactions();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(transition);
        transaction.replace(parentViewId, fragment);
        transaction.commit();
    }

    /**
     * Removes a specific fragment from its parent view.
     *
     * @param fragment
     *         The fragment, which should be removed, as an instance of the class {@link Fragment}.
     *         The fragment may not be null
     */

    private void removeFragment(@NonNull final Fragment fragment) {
        notifyOnPreferenceFragmentHidden();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    /**
     * Shows the bread crumb for a specific preference header, depending on whether the device has a
     * large screen or not. On devices with a large screen the bread crumb will be shown above the
     * currently shown fragment, on devices with a small screen the bread crumb will be shown as the
     * action bar's title instead.
     *
     * @param preferenceHeader
     *         The preference header, the bread crumb should be shown for, as an instance of the
     *         class {@link PreferenceHeader}. The preference header may not be null
     */
    private void showBreadCrumb(@NonNull final PreferenceHeader preferenceHeader) {
        CharSequence title = preferenceHeader.getBreadCrumbTitle();

        if (title == null) {
            title = preferenceHeader.getTitle();
        }

        if (title == null) {
            title = getTitle();
        }

        showBreadCrumb(title, preferenceHeader.getBreadCrumbShortTitle());
    }

    /**
     * Shows the bread crumb using a specific title and short title, depending on whether the device
     * has a large screen or not. On devices with a large screen the bread crumb will be shown above
     * the currently shown fragment, on devices with a small screen the bread crumb will be shown as
     * the action bar's title instead.
     *
     * @param title
     *         The title, which should be used by the bread crumb, as an instance of the class
     *         {@link CharSequence} or null, if no title should be used
     * @param shortTitle
     *         The short title, which should be used by the bread crumb, as an instance of the class
     *         {@link CharSequence} or null, if no short title should be used
     */
    private void showBreadCrumb(@Nullable final CharSequence title,
                                @Nullable final CharSequence shortTitle) {
        this.currentTitle = title;
        this.currentShortTitle = shortTitle;
        CharSequence breadCrumbTitle = createBreadCrumbTitle(title);

        if (isSplitScreen()) {
            breadCrumbToolbar.setTitle(breadCrumbTitle);
        } else if (breadCrumbTitle != null) {
            if (defaultTitle == null) {
                defaultTitle = getTitle();
            }

            setTitle(breadCrumbTitle);
        }
    }

    /**
     * Creates and returns the title of the bread crumb, depending on whether the activity is used
     * as a wizard and whether the progress should be shown, or not.
     *
     * @param title
     *         The title, which should be used by the bread crumb, as an instance of the class
     *         {@link CharSequence} or null, if no title should be used
     * @return The title, which has been created, as an instance of the class {@link CharSequence}
     * or null, if no title should be used
     */
    private CharSequence createBreadCrumbTitle(@Nullable final CharSequence title) {
        if (title != null) {
            String format = getProgressFormat();

            if (format != null) {
                int currentStep = getListAdapter().indexOf(currentHeader) + 1;
                int totalSteps = getListAdapter().getCount();
                return String.format(format, currentStep, totalSteps, title);
            }
        }

        return title;
    }

    /**
     * Resets the title of the activity to the default title, if it has been previously changed.
     */
    private void resetTitle() {
        if (defaultTitle != null) {
            setTitle(defaultTitle);
            defaultTitle = null;
            currentTitle = null;
            currentShortTitle = null;
        }
    }

    /**
     * Adds the preference headers, which are currently added to the activity, to the bundle, which
     * has been passed to the activity, when it has been created. If no bundle has been passed to
     * the activity, a new bundle will be created.
     */
    private void updateSavedInstanceState() {
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }

        savedInstanceState
                .putParcelableArrayList(PREFERENCE_HEADERS_EXTRA, getListAdapter().getAllItems());
    }

    /**
     * Obtains all relevant attributes from the activity's current theme.
     */
    private void obtainStyledAttributes() {
        obtainNavigationBackground();
        obtainPreferenceScreenBackgroundColor();
        obtainBreadCrumbBackgroundColor();
        obtainWizardButtonBarBackground();
        obtainToolbarElevation();
        obtainBreadCrumbElevation();
        obtainWizardButtonBarElevation();
    }

    /**
     * Obtains the background of the navigation from a specific theme.
     */
    private void obtainNavigationBackground() {
        TypedArray typedArray =
                getTheme().obtainStyledAttributes(new int[]{R.attr.navigationBackground});
        int color = typedArray.getColor(0, 0);

        if (color != 0) {
            setNavigationBackgroundColor(color);
        } else {
            int resourceId = typedArray.getResourceId(0, 0);

            if (resourceId != 0) {
                setNavigationBackground(resourceId);
            }
        }
    }

    /**
     * Obtains the background color of the preference screen from a specific theme.
     */
    private void obtainPreferenceScreenBackgroundColor() {
        TypedArray typedArray =
                getTheme().obtainStyledAttributes(new int[]{R.attr.preferenceScreenBackground});
        int color = typedArray.getColor(0, 0);

        if (color != 0) {
            setPreferenceScreenBackgroundColor(color);
        }
    }

    /**
     * Obtains the background color of the bread crumb from a specific theme.
     */
    private void obtainBreadCrumbBackgroundColor() {
        TypedArray typedArray =
                getTheme().obtainStyledAttributes(new int[]{R.attr.breadCrumbBackground});
        int color = typedArray.getColor(0, 0);

        if (color != 0) {
            setBreadCrumbBackgroundColor(color);
        }
    }

    /**
     * Obtains the background of the wizard button bar from a specific theme.
     */
    private void obtainWizardButtonBarBackground() {
        View wizardButtonBar = findViewById(R.id.wizard_button_bar);
        TypedArray typedArray =
                getTheme().obtainStyledAttributes(new int[]{R.attr.wizardButtonBarBackground});
        int color = typedArray.getColor(0, 0);

        if (color != 0) {
            wizardButtonBar.setBackgroundColor(color);
        } else {
            int resourceId = typedArray.getResourceId(0, 0);

            if (resourceId != 0) {
                wizardButtonBar.setBackgroundResource(resourceId);
            }
        }
    }

    /**
     * Obtains the elevation of the activity's toolbar from a specific theme.
     */
    private void obtainToolbarElevation() {
        TypedArray typedArray =
                getTheme().obtainStyledAttributes(new int[]{R.attr.toolbarElevation});
        int defaultElevation =
                getResources().getDimensionPixelSize(R.dimen.default_toolbar_elevation);
        int elevation = pixelsToDp(this, typedArray.getDimensionPixelSize(0, defaultElevation));
        setToolbarElevation(elevation);
    }

    /**
     * Obtains the elevation of the toolbar, which is used to show the bread crumb of the currently
     * selected preference header on devices with a large screen, from a specific theme..
     */
    private void obtainBreadCrumbElevation() {
        TypedArray typedArray =
                getTheme().obtainStyledAttributes(new int[]{R.attr.breadCrumbElevation});
        int defaultElevation =
                getResources().getDimensionPixelSize(R.dimen.default_bread_crumb_elevation);
        int elevation = pixelsToDp(this, typedArray.getDimensionPixelSize(0, defaultElevation));
        setBreadCrumbElevation(elevation);
    }

    /**
     * Obtains the elevation, of the view group, which contains all views, which are shown when a
     * preference header is selected on devices with a large screen, from a specific theme.
     */
    private void obtainPreferenceScreenElevation() {
        TypedArray typedArray =
                getTheme().obtainStyledAttributes(new int[]{R.attr.preferenceScreenElevation});
        int defaultElevation =
                getResources().getDimensionPixelSize(R.dimen.default_preference_screen_elevation);
        int elevation = pixelsToDp(this, typedArray.getDimensionPixelSize(0, defaultElevation));
        setPreferenceScreenElevation(elevation);
    }

    /**
     * Obtains the elevation of the button bar from a specific theme.
     */
    private void obtainWizardButtonBarElevation() {
        TypedArray typedArray =
                getTheme().obtainStyledAttributes(new int[]{R.attr.wizardButtonBarElevation});
        int defaultElevation =
                getResources().getDimensionPixelSize(R.dimen.default_button_bar_elevation);
        int elevation = pixelsToDp(this, typedArray.getDimensionPixelSize(0, defaultElevation));
        setButtonBarElevation(elevation);
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
     * if it is used as a wizard, to the activity.
     *
     * @param listener
     *         The listener, which should be added, as an instance of the type {@link
     *         WizardListenerOld}. The listener may not be null
     */
    public final void addWizardListener(@NonNull final WizardListenerOld listener) {
        ensureNotNull(listener, "The listener may not be null");
        wizardListeners.add(listener);
    }

    /**
     * Removes a specific listener, which should not be notified, when the user navigates within the
     * activity, if it is used as a wizard, from the activity.
     *
     * @param listener
     *         The listener, which should be removed, as an instance of the type {@link
     *         WizardListenerOld}. The listener may not be null
     */
    public final void removeWizardListener(@NonNull final WizardListenerOld listener) {
        ensureNotNull(listener, "The listener may not be null");
        wizardListeners.remove(listener);
    }

    /**
     * Returns the parent view of the fragment, which provides the navigation to each preference
     * header's fragment. On devices with a small screen this parent view is also used to show a
     * preference header's fragment, when a header is currently selected.
     *
     * @return The parent view of the fragment, which provides the navigation to each preference
     * header's fragment, as an instance of the class {@link ViewGroup}. The parent view may not be
     * null
     */
    public final ViewGroup getPreferenceHeaderParentView() {
        return preferenceHeaderParentView;
    }

    /**
     * Returns the parent view of the fragment, which is used to show the preferences of the
     * currently selected preference header on devices with a large screen.
     *
     * @return The parent view of the fragment, which is used to show the preferences of the
     * currently selected preference header, as an instance of the class {@link ViewGroup} or null,
     * if the device has a small screen
     */
    public final ViewGroup getPreferenceScreenParentView() {
        return preferenceScreenParentView;
    }

    /**
     * Returns the view group, which contains all views, e.g. the preferences themselves and the
     * bread crumb, which are shown when a preference header is selected on devices with a large
     * screen.
     *
     * @return The view group, which contains all views, which are shown when a preference header is
     * selected, as an instance of the class CardView or null, if the device has a small screen
     */
    public final CardView getPreferenceScreenContainer() {
        return preferenceScreenContainer;
    }

    /**
     * Returns the toolbar, which is used to show the activity's title on devices with a large
     * screen.
     *
     * @return The toolbar, which is used to show the activity's title on devices with a large
     * screen, as an instance of the class Toolbar or null, if the device has a small screen
     */
    public final Toolbar getNavigationToolbar() {
        if (isSplitScreen()) {
            return toolbarLarge.getToolbar();
        }

        return null;
    }

    /**
     * Returns the toolbar, which is used to show the title of the currently selected preference
     * header on devices with a large screen.
     *
     * @return The toolbar, which is used to show the title of the currently selected preference
     * header on devices with a large screen, as an instance of the class Toolbar or null, if the
     * device has a small screen
     */
    public final Toolbar getBreadCrumbToolbar() {
        if (isSplitScreen()) {
            return breadCrumbToolbar;
        }

        return null;
    }

    /**
     * Returns the view group, which contains the buttons, which are shown when the activity is used
     * as a wizard.
     *
     * @return The view group, which contains the buttons, which are shown when the activity is used
     * as a wizard, as an instance of the class {@link ViewGroup} or null, if the wizard is not used
     * as a wizard
     */
    public final ViewGroup getButtonBar() {
        return buttonBar;
    }

    /**
     * Returns the next button, which is shown, when the activity is used as a wizard and the last
     * preference header is currently not selected.
     *
     * @return The next button as an instance of the class {@link Button} or null, if the activity
     * is not used as a wizard
     */
    public final Button getNextButton() {
        return nextButton;
    }

    /**
     * Returns the text of the next button, which is shown, when the activity is used as a wizard.
     *
     * @return The text of the next button as an instance of the class {@link CharSequence} or null,
     * if the activity is not used as a wizard
     */
    public final CharSequence getNextButtonText() {
        if (nextButton != null) {
            return nextButton.getText();
        }

        return null;
    }

    /**
     * Sets the text of the next button, which is shown, when the activity is used as a wizard. The
     * text is only set, if the activity is used as a wizard.
     *
     * @param text
     *         The text, which should be set, as an instance of the class {@link CharSequence}. The
     *         text may not be null
     * @return True, if the text has been set, false otherwise
     */
    public final boolean setNextButtonText(@NonNull final CharSequence text) {
        ensureNotNull(text, "The text may not be null");

        if (nextButton != null) {
            nextButton.setText(text);
            return true;
        }

        return false;
    }

    /**
     * Sets the text of the next button, which is shown, when the activity is used as a wizard. The
     * text is only set, if the activity is used as a wizard.
     *
     * @param resourceId
     *         The resource id of the text, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     * @return True, if the text has been set, false otherwise
     */
    public final boolean setNextButtonText(@StringRes final int resourceId) {
        return setNextButtonText(getText(resourceId));
    }

    /**
     * Returns the finish button, which is shown, when the activity is used as a wizard and the last
     * preference header is currently selected.
     *
     * @return The finish button as an instance of the class {@link Button} or null, if the activity
     * is not used as a wizard
     */
    public final Button getFinishButton() {
        return finishButton;
    }

    /**
     * Sets the text of the next button, which is shown, when the activity is used as a wizard and
     * the last preference header is currently selected. The text is only set, if the activity is
     * used as a wizard.
     *
     * @param text
     *         The text, which should be set, as an instance of the class {@link CharSequence}. The
     *         text may not be null
     * @return True, if the text has been set, false otherwise
     */
    public final boolean setFinishButtonText(@NonNull final CharSequence text) {
        ensureNotNull(text, "The text may not be null");

        if (finishButton != null) {
            finishButton.setText(text);
            return true;
        }

        return false;
    }

    /**
     * Sets the text of the next button, which is shown, when the activity is used as a wizard and
     * the last preference header is currently selected. The text is only set, if the activity is
     * used as a wizard.
     *
     * @param resourceId
     *         The resource id of the text, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     * @return True, if the text has been set, false otherwise
     */
    public final boolean setFinishButtonText(@StringRes final int resourceId) {
        return setFinishButtonText(getText(resourceId));
    }

    /**
     * Returns the text of the finish button, which is shown, when the activity is used as a wizard
     * and the last preference header is currently selected.
     *
     * @return The text of the finish button as an instance of the class {@link CharSequence} or
     * null, if the activity is not used as a wizard
     */
    public final CharSequence getFinishButtonText() {
        if (finishButton != null) {
            return finishButton.getText();
        }

        return null;
    }

    /**
     * Returns the back button, which is shown, when the activity is used as a wizard.
     *
     * @return The back button as an instance of the class {@link Button} or null, if the activity
     * is not used as a wizard
     */
    public final Button getBackButton() {
        return backButton;
    }

    /**
     * Returns the text of the back button, which is shown, when the activity is used as a wizard.
     *
     * @return The text of the back button as an instance of the class {@link CharSequence} or null,
     * if the activity is not used as a wizard
     */
    public final CharSequence getBackButtonText() {
        if (backButton != null) {
            return backButton.getText();
        }

        return null;
    }

    /**
     * Sets the text of the back button, which is shown, when the activity is used as a wizard. The
     * text is only set, if the activity is used as a wizard.
     *
     * @param text
     *         The text, which should be set, as an instance of the class {@link CharSequence}. The
     *         text may not be null
     * @return True, if the text has been set, false otherwise
     */
    public final boolean setBackButtonText(@NonNull final CharSequence text) {
        ensureNotNull(text, "The text may not be null");

        if (backButton != null) {
            backButton.setText(text);
            return true;
        }

        return false;
    }

    /**
     * Sets the text of the back button, which is shown, when the activity is used as a wizard. The
     * text is only set, if the activity is used as a wizard.
     *
     * @param resourceId
     *         The resource id of the text, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     * @return True, if the text has been set, false otherwise
     */
    public final boolean setBackButtonText(@StringRes final int resourceId) {
        return setBackButtonText(getText(resourceId));
    }

    /**
     * Returns, whether the progress is shown, if the activity is used as a wizard.
     *
     * @return True, if the progress is shown, false otherwise or if the activity is not used as a
     * wizard
     */
    public final boolean isProgressShown() {
        return isButtonBarShown() && showProgress;
    }

    /**
     * Shows or hides the progress, if the activity is used as a wizard.
     *
     * @param showProgress
     *         True, if the progress should be shown, false otherwise
     * @return True, if the progress has been shown or hidden, false otherwise
     */
    public final boolean showProgress(final boolean showProgress) {
        if (isButtonBarShown()) {
            this.showProgress = showProgress;

            if (currentHeader != null) {
                showBreadCrumb(currentHeader);
            }

            return true;
        }

        return false;
    }

    /**
     * Returns the string, which is used to format the progress, which may be shown, if the activity
     * is used as a wizard.
     *
     * @return The string, which is used to format the progress, as a {@link String} or null, if the
     * activity is not used as a wizard or if no progress is shown
     */
    public final String getProgressFormat() {
        if (isProgressShown()) {
            return progressFormat != null ? progressFormat : getString(R.string.progress_format);
        }

        return null;
    }

    /**
     * Sets the string, which should be used to format the progress, if the activity is used as a
     * wizard and the progress is shown.
     *
     * @param progressFormat
     *         The string, which should be set, as a {@link String}. The string may not be null. It
     *         must be formatted according to the following syntax: "*%d*%d*%s*"
     * @return True, if the string has been set, false otherwise
     */
    public final boolean setProgressFormat(@NonNull final String progressFormat) {
        ensureNotNull(progressFormat, "The progress format may not be null");

        if (isProgressShown()) {
            this.progressFormat = progressFormat;

            if (currentHeader != null) {
                showBreadCrumb(currentHeader);
            }

            return true;
        }

        return false;
    }

    /**
     * Sets the string, which should be used to format the progress, if the activity is used as a
     * wizard and the progress is shown.
     *
     * @param resourceId
     *         The resource id of the string, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource. It must be formatted
     *         according to the following syntax: "*%d*%d*%s*"
     * @return True, if the string has been set, false otherwise
     */
    public final boolean setProgressFormat(@StringRes final int resourceId) {
        return setProgressFormat(getString(resourceId));
    }

    /**
     * Returns the list view, which is used to show the preference headers.
     *
     * @return The list view, which is used to show the preference header, as an instance of the
     * class {@link ListView}. The list view may not be null
     */
    public final ListView getListView() {
        return preferenceHeaderFragment.getListView();
    }

    /**
     * Returns the adapter, which provides the preference headers for visualization using the list
     * view.
     *
     * @return The adapter, which provides the preference headers for visualization using the list
     * view, as an instance of the class {@link PreferenceHeaderAdapter}. The adapter may not be
     * null
     */
    public final PreferenceHeaderAdapter getListAdapter() {
        return preferenceHeaderFragment.getListAdapter();
    }

    /**
     * Adds all preference headers, which are specified by a specific XML resource, to the
     * activity.
     *
     * @param resourceId
     *         The resource id of the XML file, which specifies the preference headers, as an {@link
     *         Integer} value. The resource id must correspond to a valid XML resource
     */
    public final void addPreferenceHeadersFromResource(@XmlRes final int resourceId) {
        getListAdapter().addAllItems(PreferenceHeaderParser.fromResource(this, resourceId));
    }

    /**
     * Adds a new preference header to the activity.
     *
     * @param preferenceHeader
     *         The preference header, which should be added, as an instance of the class {@link
     *         PreferenceHeader}. The preference header may not be null
     */
    public final void addPreferenceHeader(@NonNull final PreferenceHeader preferenceHeader) {
        getListAdapter().addItem(preferenceHeader);
    }

    /**
     * Adds all preference headers, which are contained by a specific collection, to the activity.
     *
     * @param preferenceHeaders
     *         The collection, which contains the preference headers, which should be added, as an
     *         instance of the type {@link Collection} or an empty collection, if no preference
     *         headers should be added
     */
    public final void addAllPreferenceHeaders(
            @NonNull final Collection<PreferenceHeader> preferenceHeaders) {
        getListAdapter().addAllItems(preferenceHeaders);
    }

    /**
     * Removes a specific preference header from the activity.
     *
     * @param preferenceHeader
     *         The preference header, which should be removed, as an instance of the class {@link
     *         PreferenceHeader}. The preference header may not be null
     * @return True, if the preference header has been removed, false otherwise
     */
    public final boolean removePreferenceHeader(@NonNull final PreferenceHeader preferenceHeader) {
        return getListAdapter().removeItem(preferenceHeader);
    }

    /**
     * Returns a collection, which contains all preference headers, which are currently added to the
     * activity.
     *
     * @return A collection, which contains all preference headers, as an instance of the type
     * {@link Collection} or an empty collection, if the activity does not contain any preference
     * headers
     */
    public final Collection<PreferenceHeader> getAllPreferenceHeaders() {
        return getListAdapter().getAllItems();
    }

    /**
     * Returns the preference header, which belongs to a specific position.
     *
     * @param position
     *         The position of the preference header, which should be returned, as an {@link
     *         Integer} value
     * @return The preference header, which belongs to the given position, as an instance of the
     * class {@link PreferenceHeader}. The preference header may not be null
     */
    public final PreferenceHeader getPreferenceHeader(final int position) {
        return getListAdapter().getItem(position);
    }

    /**
     * Returns the number of preference headers, which are currently added to the activity.
     *
     * @return The number of preference header, which are currently added to the activity, as an
     * {@link Integer} value
     */
    public final int getNumberOfPreferenceHeaders() {
        return getListAdapter().getCount();
    }

    /**
     * Removes all preference headers, which are currently added to the activity.
     */
    public final void clearPreferenceHeaders() {
        getListAdapter().clear();
    }

    /**
     * Unselects the currently selected preference header and shows the navigation on devices with a
     * small screen, if the navigation is not hidden.
     *
     * @return True, if a preference header has been unselected, false otherwise
     */
    public final boolean unselectPreferenceHeader() {
        if (!isSplitScreen() && isPreferenceHeaderSelected() && !navigationHidden &&
                !isButtonBarShown()) {
            showPreferenceHeaders();
            // TODO hideToolbarNavigationIcon();
            resetTitle();
            return true;
        }

        return false;
    }

    /**
     * Selects a specific preference header.
     *
     * @param preferenceHeader
     *         The preference header, which should be selected, as an instance of the class {@link
     *         PreferenceHeader}. The preference header may not be null. If the preference header
     *         does not belong to the activity, a {@link NoSuchElementException} will be thrown
     */
    public final void selectPreferenceHeader(@NonNull final PreferenceHeader preferenceHeader) {
        selectPreferenceHeader(preferenceHeader, null);
    }

    /**
     * Selects a specific preference header.
     *
     * @param preferenceHeader
     *         The preference header, which should be selected, as an instance of the class {@link
     *         PreferenceHeader}. The preference header may not be null. If the preference header
     *         does not belong to the activity, a {@link NoSuchElementException} will be thrown
     * @param parameters
     *         The parameters, which should be passed to the preference header's fragment, as an
     *         instance of the class {@link Bundle} or null, if the preference header's extras
     *         should be used instead
     */
    public final void selectPreferenceHeader(@NonNull final PreferenceHeader preferenceHeader,
                                             @Nullable final Bundle parameters) {
        ensureNotNull(preferenceHeader, "The preference header may not be null");
        int position = getListAdapter().indexOf(preferenceHeader);

        if (position == -1) {
            throw new NoSuchElementException();
        }

        selectPreferenceHeader(position, parameters);
    }

    /**
     * Selects the preference header, which belongs to a specific position.
     *
     * @param position
     *         The position of the preference header, which should be selected, as an {@link
     *         Integer} value. If the position is invalid, an {@link IndexOutOfBoundsException} will
     *         be thrown
     */
    public final void selectPreferenceHeader(final int position) {
        selectPreferenceHeader(position, null);
    }

    /**
     * Selects the preference header, which belongs to a specific position.
     *
     * @param position
     *         The position of the preference header, which should be selected, as an {@link
     *         Integer} value. If the position is invalid, an {@link IndexOutOfBoundsException} will
     *         be thrown
     * @param parameters
     *         The parameters, which should be passed to the preference header's fragment, as an
     *         instance of the class {@link Bundle} or null, if the preference header's extras
     *         should be used instead
     */
    public final void selectPreferenceHeader(final int position,
                                             @Nullable final Bundle parameters) {
        getListView().setItemChecked(position, true);
        getListView().smoothScrollToPosition(position);
        showPreferenceScreen(getListAdapter().getItem(position), parameters, true);
    }

    /**
     * Returns, whether the preference headers and the corresponding fragments are shown split
     * screen, or not.
     *
     * @return True, if the preference headers and the corresponding fragments are shown split
     * screen, false otherwise
     */
    public final boolean isSplitScreen() {
        return getPreferenceScreenParentView() != null;
    }

    /**
     * Returns, whether the activity is used as a wizard, or not.
     *
     * @return True, if the activity is used as a wizard, false otherwise
     */
    public final boolean isButtonBarShown() {
        return buttonBar != null;
    }

    /**
     * Shows or hides the view group, which contains the buttons, which are shown when the activity
     * is used as a wizard.
     *
     * @param showButtonBar
     *         True, if the button bar should be shown, false otherwise
     */
    public final void showButtonBar(final boolean showButtonBar) {
        if (showButtonBar) {
            buttonBar = findViewById(R.id.wizard_button_bar);
            buttonBar.setVisibility(View.VISIBLE);
            buttonBarShadowView = findViewById(R.id.wizard_button_bar_shadow_view);
            buttonBarShadowView.setVisibility(View.VISIBLE);
            nextButton = findViewById(R.id.next_button);
            nextButton.setOnClickListener(createNextButtonListener());
            finishButton = findViewById(R.id.finish_button);
            finishButton.setOnClickListener(createFinishButtonListener());
            backButton = findViewById(R.id.back_button);
            backButton.setOnClickListener(createBackButtonListener());

            if (!isSplitScreen()) {
                adaptNavigation(true);
            }
        } else if (buttonBar != null) {
            buttonBar.setVisibility(View.GONE);
            buttonBarShadowView.setVisibility(View.GONE);
            buttonBar = null;
            buttonBarShadowView = null;
            finishButton = null;
            nextButton = null;
            backButton = null;
        }

        getListAdapter().setEnabled(!showButtonBar);
        adaptWizardButtons();
    }

    /**
     * Returns, whether a preference header is currently selected, or not.
     *
     * @return True, if a preference header is currently selected, false otherwise
     */
    public final boolean isPreferenceHeaderSelected() {
        return currentHeader != null && currentHeader.getFragment() != null;
    }

    /**
     * Returns the preference header, which is currently selected.
     *
     * @return The preference header, which is currently selected or null, if no preference header
     * is currently selected
     */
    public final PreferenceHeader getSelectedPreferenceHeader() {
        return currentHeader;
    }

    /**
     * Returns the position of the preference header, which is currently selected.
     *
     * @return The position of the preference header, which is currently selected or -1, if no
     * preference header is currently selected
     */
    public final int getSelectedPreferenceHeaderPosition() {
        return currentHeader != null ? getListAdapter().indexOf(currentHeader) : -1;
    }

    /**
     * Returns the elevation of the activity's toolbar.
     *
     * @return The elevation of the activity's toolbar in dp as an {@link Integer} value
     */
    public final int getToolbarElevation() {
        return toolbarShadowView.getShadowElevation();
    }

    /**
     * Sets the elevation of the activity's toolbar.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 0 and at maximum 16
     */
    public final void setToolbarElevation(final int elevation) {
        toolbarShadowView.setShadowElevation(elevation);
    }

    /**
     * Returns the elevation of the toolbar, which is used to show the title of the currently
     * selected preference header on devices with a large screen.
     *
     * @return The elevation of the toolbar, which is used to show the title of the currently
     * selected preference header on devices with a large screen, in dp as an {@link Integer} value
     * or -1, if the device has a small screen
     */
    public final int getBreadCrumbElevation() {
        if (isSplitScreen()) {
            return breadCrumbShadowView.getShadowElevation();
        }

        return -1;
    }

    /**
     * Sets the elevation of the toolbar, which is used to show the title of the currently selected
     * preference header on devices with a large screen. The elevation is only set on devices with a
     * large screen.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 0 and at maximum 16
     * @return True, if the elevation has been set, false otherwise
     */
    public final boolean setBreadCrumbElevation(final int elevation) {
        if (breadCrumbShadowView != null) {
            breadCrumbShadowView.setShadowElevation(elevation);
            return true;
        }

        return false;
    }

    /**
     * Returns the elevation of the view group, which contains all views, e.g. the preferences
     * themselves and the bread crumb, which are shown when a preference header is selected on
     * devices with a large screen.
     *
     * @return The elevation of the view group, which contains all views, which are shown when a
     * preference header is selected on devices with a large screen, in dp as an {@link Integer}
     * value or -1, if the device has a small screen
     */
    public final int getPreferenceScreenElevation() {
        if (isSplitScreen()) {
            return preferenceScreenElevation;
        }

        return -1;
    }

    /**
     * Sets the elevation of the view group, which contains all views, e.g. the preferences
     * themselves and the bread crumb, which are shown when a preference header is selected on
     * devices with a large screen. The elevation is only set on devices with a large screen.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 0 and at maximum 16
     * @return True, if the elevation has been set, false otherwise
     */
    public final boolean setPreferenceScreenElevation(final int elevation) {
        ensureAtLeast(elevation, 0, "The elevation must be at least 0");
        ensureAtMaximum(elevation, ElevationUtil.MAX_ELEVATION,
                "The elevation must be at least " + ElevationUtil.MAX_ELEVATION);

        if (preferenceScreenContainer != null) {
            preferenceScreenElevation = elevation;
            preferenceScreenContainer.setCardElevation(elevation);
            return true;
        }

        return false;
    }

    /**
     * Returns the elevation of the button bar, which contains the buttons, which are shown when the
     * activity is used as a wizard.
     *
     * @return The elevation of the button bar in dp as an {@link Integer} value or -1, if the
     * activity is not used as a wizard
     */
    public final int getButtonBarElevation() {
        if (isButtonBarShown()) {
            return buttonBarShadowView.getShadowElevation();
        }

        return -1;
    }

    /**
     * Sets the elevation of the button bar, which contains the buttons, which are shown when the
     * activity is used as a wizard. The elevation is only set when the activity is used as a
     * wizard.
     *
     * @param elevation
     *         The elevation, which should be set, in dp as an {@link Integer} value. The elevation
     *         must be at least 0 and at maximum 16
     * @return True, if the elevation has been set, false otherwise
     */
    public final boolean setButtonBarElevation(final int elevation) {
        if (buttonBarShadowView != null) {
            buttonBarShadowView.setShadowElevation(elevation);
            return true;
        }

        return false;
    }

    /**
     * Returns the background color of the view group, which contains all views, which are shown
     * when a preference header is selected on devices with a large screen.
     *
     * @return The background color of the view group, which contains all views, which are shown
     * when a preference header is selected, as an {@link Integer} value, or -1, if no background
     * color has been set or if the device has a small screen
     */
    public final int getPreferenceScreenBackgroundColor() {
        if (getPreferenceScreenContainer() != null) {
            return preferenceScreenBackgroundColor;
        } else {
            return -1;
        }
    }

    /**
     * Sets the background color of the view group, which contains all views, which are shown when a
     * preference header is selected. The background is only set on devices with a large screen.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value
     * @return True, if the background has been set, false otherwise
     */
    public final boolean setPreferenceScreenBackgroundColor(@ColorInt final int color) {
        if (getPreferenceScreenContainer() != null) {
            preferenceScreenBackgroundColor = color;
            getPreferenceScreenContainer().setCardBackgroundColor(color);
            return true;
        }

        return false;
    }

    /**
     * Returns the background color of the toolbar, which is used to show the title of the currently
     * selected preference header on devices with a large screen.
     *
     * @return The background color of the toolbar, which is used to show the title of the currently
     * selected preference header on devices with a large screen, as an {@link Integer} value, or
     * -1, if no background color has been set or if the device has a small screen
     */
    public final int getBreadCrumbBackgroundColor() {
        if (getBreadCrumbToolbar() != null) {
            return breadCrumbBackgroundColor;
        } else {
            return -1;
        }
    }

    /**
     * Sets the background color of the toolbar, which is used to show the title of the currently
     * selected preference header on devices with a large screen.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value
     * @return True, if the background has been set, false otherwise
     */
    public final boolean setBreadCrumbBackgroundColor(@ColorInt final int color) {
        if (getBreadCrumbToolbar() != null) {
            breadCrumbBackgroundColor = color;
            GradientDrawable background = (GradientDrawable) ContextCompat
                    .getDrawable(this, R.drawable.breadcrumb_background);
            background.setColor(color);
            ViewUtil.setBackground(getBreadCrumbToolbar(), background);
            return true;
        }

        return false;
    }

    /**
     * Returns the background of the parent view of the fragment, which provides navigation to each
     * preference header's fragment.
     *
     * @return The background of the parent view of the fragment, which provides navigation to each
     * preference header's fragment, as an instance of the class {@link Drawable} or null, if no
     * background has been set
     */
    public final Drawable getNavigationBackground() {
        return getPreferenceHeaderParentView().getBackground();
    }

    /**
     * Sets the background of the parent view of the fragment, which provides navigation to each
     * preference header's fragment.
     *
     * @param resourceId
     *         The resource id of the background, which should be set, as an {@link Integer} value.
     *         The resource id must correspond to a valid drawable resource
     */
    public final void setNavigationBackground(@DrawableRes final int resourceId) {
        setNavigationBackground(ContextCompat.getDrawable(this, resourceId));
    }

    /**
     * Sets the background color of the parent view of the fragment, which provides navigation to
     * each preference header's fragment.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value
     */
    public final void setNavigationBackgroundColor(@ColorInt final int color) {
        setNavigationBackground(new ColorDrawable(color));
    }

    /**
     * Sets the background of the parent view of the fragment, which provides navigation to each
     * preference header's fragment.
     *
     * @param drawable
     *         The background, which should be set, as an instance of the class {@link Drawable} or
     *         null, if no background should be set
     */
    public final void setNavigationBackground(@Nullable final Drawable drawable) {
        ViewUtil.setBackground(getPreferenceHeaderParentView(), drawable);
    }

    /**
     * Returns the background of the button bar, which contains the buttons, which are shown when
     * the activity is used as a wizard.
     *
     * @return The background of the button bar, which contains the buttons, which are shown when
     * the activity is used as a wizard, as an instance of the class {@link Drawable} or null, if no
     * background has been set or the activity is not used as a wizard
     */
    public final Drawable getButtonBarBackground() {
        if (getButtonBar() != null) {
            return getButtonBar().getBackground();
        } else {
            return null;
        }
    }

    /**
     * Sets the background of the button bar, which contains the buttons, which are shown when the
     * activity is used as a wizard. The background is only set when the activity is used as a
     * wizard.
     *
     * @param resourceId
     *         The resource id of the background, which should be set, as an {@link Integer} value.
     *         The resource id must correspond to a valid drawable resource
     * @return True, if the background has been set, false otherwise
     */
    public final boolean setButtonBarBackground(@DrawableRes final int resourceId) {
        return setButtonBarBackground(ContextCompat.getDrawable(this, resourceId));
    }

    /**
     * Sets the background color of the button bar, which contains the buttons, which are shown when
     * the activity is used as a wizard. The background color is only set when the activity is used
     * as a wizard.
     *
     * @param color
     *         The background color, which should be set, as an {@link Integer} value
     * @return True, if the background color has been set, false otherwise
     */
    public final boolean setButtonBarBackgroundColor(@ColorInt final int color) {
        return setButtonBarBackground(new ColorDrawable(color));
    }

    /**
     * Sets the background of the button bar, which contains the buttons, which are shown when the
     * activity is used as a wizard. The background is only set when the activity is used as a
     * wizard.
     *
     * @param drawable
     *         The background, which should be set, as an instance of the class {@link Drawable} or
     *         null, if no background should be set
     * @return True, if the background has been set, false otherwise
     */
    public final boolean setButtonBarBackground(@Nullable final Drawable drawable) {
        if (getButtonBar() != null) {
            ViewUtil.setBackground(getButtonBar(), drawable);
            return true;
        }

        return false;
    }

    @Override
    public final void onItemClick(final AdapterView<?> parent, final View view, final int position,
                                  final long id) {
        showPreferenceScreen(getListAdapter().getItem(position), null, true);
    }

    @Override
    public final void onPreferenceHeaderAdded(@NonNull final PreferenceHeaderAdapter adapter,
                                              @NonNull final PreferenceHeader preferenceHeader,
                                              final int position) {
        if (isSplitScreen()) {
            if (adapter.getCount() == 1) {
                selectPreferenceHeader(0);
            }
        } else {
            updateSavedInstanceState();
        }

        adaptWizardButtons();
    }

    @Override
    public final void onPreferenceHeaderRemoved(@NonNull final PreferenceHeaderAdapter adapter,
                                                @NonNull final PreferenceHeader preferenceHeader,
                                                final int position) {
        if (isSplitScreen()) {
            if (adapter.isEmpty()) {
                removeFragment(preferenceScreenFragment);
                showBreadCrumb(null, null);
                preferenceScreenFragment = null;
                currentHeader = null;
            } else {
                int selectedIndex = getListView().getCheckedItemPosition();

                if (selectedIndex == position) {
                    PreferenceHeader selectedPreferenceHeader;
                    try {
                        selectedPreferenceHeader = getListAdapter().getItem(selectedIndex);
                    } catch (IndexOutOfBoundsException e) {
                        getListView().setItemChecked(selectedIndex - 1, true);
                        selectedPreferenceHeader = getListAdapter().getItem(selectedIndex - 1);
                    }

                    showPreferenceScreen(selectedPreferenceHeader, null, false);
                } else if (selectedIndex > position) {
                    getListView().setItemChecked(selectedIndex - 1, true);
                    showPreferenceScreen(getListAdapter().getItem(selectedIndex - 1), null, false);
                }
            }
        } else {
            if (currentHeader == preferenceHeader) {
                showPreferenceHeaders();
                // TODO hideToolbarNavigationIcon();
                resetTitle();
            }

            updateSavedInstanceState();
        }

        adaptWizardButtons();
    }

    @CallSuper
    @Override
    public void onFragmentCreated(@NonNull final Fragment fragment) {
        getListView().setOnItemClickListener(this);
        getListAdapter().addListener(this);

        if (savedInstanceState == null) {
            onCreatePreferenceHeaders();
            initializeSelectedPreferenceHeader();
            handleInitialFragmentIntent();

        } else {
            ArrayList<PreferenceHeader> preferenceHeaders =
                    savedInstanceState.getParcelableArrayList(PREFERENCE_HEADERS_EXTRA);

            if (preferenceHeaders != null) {
                getListAdapter().addAllItems(preferenceHeaders);
            }

            initializeSelectedPreferenceHeader();
        }
    }


    @CallSuper
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.preference_activity);
        frameLayout = findViewById(R.id.preference_activity_frame_layout);
        preferenceHeaderParentView = findViewById(R.id.preference_header_parent);
        preferenceScreenParentView = findViewById(R.id.preference_screen_parent);
        preferenceScreenContainer = findViewById(R.id.preference_screen_container);
        toolbarShadowView = findViewById(R.id.toolbar_shadow_view);

        if (isSplitScreen()) {
            breadCrumbToolbar = findViewById(R.id.bread_crumb_toolbar);
            breadCrumbShadowView = findViewById(R.id.bread_crumb_shadow_view);
        }

        preferenceHeaderFragment = new PreferenceHeaderFragment();
        preferenceHeaderFragment.addFragmentListener(this);
        initializeToolbar();

        obtainStyledAttributes();
        // TODO overrideNavigationIcon(true);

        if (savedInstanceState != null) {
            restoredPreferenceScreenFragment = getFragmentManager()
                    .getFragment(savedInstanceState, PREFERENCE_SCREEN_FRAGMENT_EXTRA);
        }

        showPreferenceHeaders();
    }

    @CallSuper
    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState.getBundle(CURRENT_BUNDLE_EXTRA);
        CharSequence title = savedInstanceState.getCharSequence(CURRENT_TITLE_EXTRA);
        CharSequence shortTitle = savedInstanceState.getCharSequence(CURRENT_SHORT_TITLE_EXTRA);
        PreferenceHeader currentPreferenceHeader =
                savedInstanceState.getParcelable(CURRENT_PREFERENCE_HEADER_EXTRA);

        if (currentPreferenceHeader != null) {
            preferenceScreenFragment = restoredPreferenceScreenFragment;
            showPreferenceScreen(currentPreferenceHeader, bundle, false);
            showBreadCrumb(title, shortTitle);

            if (isSplitScreen()) {
                int selectedIndex = getListAdapter().indexOf(currentPreferenceHeader);

                if (selectedIndex != -1) {
                    getListView().setItemChecked(selectedIndex, true);
                }
            }
        } else {
            showPreferenceHeaders();
        }
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(CURRENT_BUNDLE_EXTRA, currentBundle);
        outState.putCharSequence(CURRENT_TITLE_EXTRA, currentTitle);
        outState.putCharSequence(CURRENT_SHORT_TITLE_EXTRA, currentShortTitle);
        outState.putParcelable(CURRENT_PREFERENCE_HEADER_EXTRA, currentHeader);
        outState.putParcelableArrayList(PREFERENCE_HEADERS_EXTRA, getListAdapter().getAllItems());

        if (preferenceScreenFragment != null && preferenceScreenFragment.isAdded()) {
            getFragmentManager().putFragment(outState, PREFERENCE_SCREEN_FRAGMENT_EXTRA,
                    preferenceScreenFragment);
        }
    }

    /**
     * The method, which is invoked, when the preference headers should be created. This method may
     * be overridden by implementing subclasses to add the preference headers at the activity's
     * start.
     */
    protected void onCreatePreferenceHeaders() {

    }

}