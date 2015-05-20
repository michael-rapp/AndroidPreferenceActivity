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

import static de.mrapp.android.preference.activity.util.Condition.ensureAtLeast;
import static de.mrapp.android.preference.activity.util.Condition.ensureAtMaximum;
import static de.mrapp.android.preference.activity.util.Condition.ensureGreaterThan;
import static de.mrapp.android.preference.activity.util.Condition.ensureNotNull;
import static de.mrapp.android.preference.activity.util.DisplayUtil.convertDpToPixels;
import static de.mrapp.android.preference.activity.util.DisplayUtil.convertPixelsToDp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import de.mrapp.android.preference.activity.adapter.AdapterListener;
import de.mrapp.android.preference.activity.adapter.PreferenceHeaderAdapter;
import de.mrapp.android.preference.activity.fragment.FragmentListener;
import de.mrapp.android.preference.activity.fragment.PreferenceHeaderFragment;
import de.mrapp.android.preference.activity.parser.PreferenceHeaderParser;
import de.mrapp.android.preference.activity.util.VisibleForTesting;
import de.mrapp.android.preference.activity.view.ToolbarLarge;

/**
 * An activity, which provides a navigation for multiple groups of preferences,
 * in which each group is represented by an instance of the class
 * {@link PreferenceHeader}. On devices with small screens, e.g. on smartphones,
 * the navigation is designed to use the whole available space and selecting an
 * item causes the corresponding preferences to be shown full screen as well. On
 * devices with large screens, e.g. on tablets, the navigation and the
 * preferences of the currently selected item are shown split screen.
 * 
 * @author Michael Rapp
 *
 * @since 1.0.0
 */
public abstract class PreferenceActivity extends ActionBarActivity implements
		FragmentListener, OnItemClickListener, AdapterListener {

	/**
	 * When starting this activity, the invoking intent can contain this extra
	 * string to specify which fragment should be initially displayed.
	 */
	public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";

	/**
	 * When starting this activity and using <code>EXTRA_SHOW_FRAGMENT</code>,
	 * this extra can also be specified to supply a bundle of arguments to pass
	 * to that fragment when it is instantiated during the initial creation of
	 * the activity.
	 */
	public static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":android:show_fragment_args";

	/**
	 * When starting this activity and using <code>EXTRA_SHOW_FRAGMENT</code>,
	 * this extra can also be specified to supply the title to be shown for that
	 * fragment.
	 */
	public static final String EXTRA_SHOW_FRAGMENT_TITLE = ":android:show_fragment_title";

	/**
	 * When starting this activity and using <code>EXTRA_SHOW_FRAGMENT</code>,
	 * this extra can also be specified to supply the short title to be shown
	 * for that fragment.
	 */
	public static final String EXTRA_SHOW_FRAGMENT_SHORT_TITLE = ":android:show_fragment_short_title";

	/**
	 * When starting this activity, the invoking intent can contain this extra
	 * boolean that the header list should not be displayed. This is most often
	 * used in conjunction with <code>EXTRA_SHOW_FRAGMENT</code> to launch the
	 * activity to display a specific fragment that the user has navigated to.
	 */
	public static final String EXTRA_NO_HEADERS = ":android:no_headers";

	/**
	 * When starting this activity, the invoking intent can contain this extra
	 * boolean to display back and next buttons in order to use the activity as
	 * a wizard.
	 */
	public static final String EXTRA_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";

	/**
	 * When starting this activity and using <code>EXTRA_SHOW_BUTTON_BAR</code>,
	 * this extra can also be specified to supply a custom text for the next
	 * button.
	 */
	public static final String EXTRA_NEXT_BUTTON_TEXT = "extra_prefs_set_next_text";

	/**
	 * When starting this activity and using <code>EXTRA_SHOW_BUTTON_BAR</code>,
	 * this extra can also be specified to supply a custom text for the back
	 * button.
	 */
	public static final String EXTRA_BACK_BUTTON_TEXT = "extra_prefs_set_back_text";

	/**
	 * When starting this activity and using <code>EXTRA_SHOW_BUTTON_BAR</code>,
	 * this extra can also be specified to supply a custom text for the back
	 * button when the last preference header is shown.
	 */
	public static final String EXTRA_FINISH_BUTTON_TEXT = "extra_prefs_set_finish_text";

	/**
	 * When starting this activity using <code>EXTRA_SHOW_BUTTON_BAR</code>,
	 * this boolean extra can also used to specify, whether the number of the
	 * currently shown wizard step and the number of total steps should be shown
	 * as the bread crumb title.
	 */
	public static final String EXTRA_SHOW_PROGRESS = "extra_prefs_show_progress";

	/**
	 * When starting this activity using <code>EXTRA_SHOW_BUTTON_BAR</code> and
	 * <code>EXTRA_SHOW_PROGRESS</code>, this string extra can also be specified
	 * to supply a custom format for showing the progress. The string must be
	 * formatted according to the following syntax: "*%d*%d*%s*"
	 */
	public static final String EXTRA_PROGRESS_FORMAT = "extra_prefs_progress_format";

	/**
	 * The name of the extra, which is used to save the parameters, which have
	 * been passed when the currently shown fragment has been created, within a
	 * bundle.
	 */
	@VisibleForTesting
	protected static final String CURRENT_BUNDLE_EXTRA = PreferenceActivity.class
			.getSimpleName() + "::CurrentBundle";

	/**
	 * The name of the extra, which is used to save the title, which is
	 * currently used by the bread crumb, within a bundle.
	 */
	@VisibleForTesting
	protected static final String CURRENT_TITLE_EXTRA = PreferenceActivity.class
			.getSimpleName() + "::CurrentTitle";

	/**
	 * The name of the extra, which is used to save the short title, which is
	 * currently used by the bread crumb, within a bundle.
	 */
	@VisibleForTesting
	protected static final String CURRENT_SHORT_TITLE_EXTRA = PreferenceActivity.class
			.getSimpleName() + "::CurrentShortTitle";

	/**
	 * The name of the extra, which is used to save the currently selected
	 * preference header, within a bundle.
	 */
	@VisibleForTesting
	protected static final String CURRENT_PREFERENCE_HEADER_EXTRA = PreferenceActivity.class
			.getSimpleName() + "::CurrentPreferenceHeader";

	/**
	 * The name of the extra, which is used to saved the preference headers
	 * within a bundle.
	 */
	@VisibleForTesting
	protected static final String PREFERENCE_HEADERS_EXTRA = PreferenceActivity.class
			.getSimpleName() + "::PreferenceHeaders";

	/**
	 * The name of the extra, which is used to save the fragment, which is
	 * currently shown as the preference screen, within a bundle.
	 */
	private static final String PREFERENCE_SCREEN_FRAGMENT_EXTRA = PreferenceActivity.class
			.getSimpleName() + "::PreferenceScreenFragment";

	/**
	 * The name which is used to store fragment transitions of the back stack.
	 */
	private static final String FRAGMENT_BACK_STACK = PreferenceActivity.class
			.getSimpleName() + "::FragmentBackStack";

	/**
	 * The default elevation of the navigation in dp.
	 */
	private static final int DEFAULT_NAVIGATION_ELEVATION = 3;

	/**
	 * The default elevation of the button bar, which contains the buttons,
	 * which are shown when the activity is used as a wizard.
	 */
	private static final int DEFAULT_BUTTON_BAR_ELEVATION = 2;

	/**
	 * The default elevation of the bread crumb, which is used to show the title
	 * of the currently selected fragment on devices with a large screen, if the
	 * activity's toolbar is not shown.
	 */
	private static final int DEFAULT_BREAD_CRUMB_ELEVATION = 2;

	/**
	 * The saved instance state, which has been passed to the activity, when it
	 * has been created.
	 */
	private Bundle savedInstanceState;

	/**
	 * The fragment, which has been shown as the preference screen before the
	 * activity has been recreated or null, if no preference header has been
	 * previously selected.
	 */
	private Fragment restoredPreferenceScreenFragment;

	/**
	 * The view, which is used to visualize a large toolbar on devices with a
	 * large screen.
	 */
	private ToolbarLarge toolbarLarge;

	/**
	 * The fragment, which contains the preference headers and provides the
	 * navigation to each header's fragment.
	 */
	private PreferenceHeaderFragment preferenceHeaderFragment;

	/**
	 * The fragment, which is currently shown as the preference screen or null,
	 * if no preference header is currently selected.
	 */
	private Fragment preferenceScreenFragment;

	/**
	 * The parent view of the fragment, which provides the navigation to each
	 * preference header's fragment.
	 */
	private ViewGroup preferenceHeaderParentView;

	/**
	 * The parent view of the fragment, which is used to show the preferences of
	 * the currently selected preference header on devices with a large screen.
	 */
	private ViewGroup preferenceScreenParentView;

	/**
	 * The view group, which contains all views, e.g. the preferences itself and
	 * the bread crumb, which are shown when a preference header is selected on
	 * devices with a large screen.
	 */
	private ViewGroup preferenceScreenContainer;

	/**
	 * The view group, which contains the buttons, which are shown when the
	 * activity is used as a wizard.
	 */
	private ViewGroup buttonBar;

	/**
	 * The back button, which is shown, if the activity is used as a wizard.
	 */
	private Button backButton;

	/**
	 * The next button, which is shown, if the activity is used as a wizard and
	 * the last preference header is currently not selected.
	 */
	private Button nextButton;

	/**
	 * The finish button, which is shown, if the activity is used as a wizard
	 * and the last preference header is currently selected.
	 */
	private Button finishButton;

	/**
	 * The view, which is used to draw a shadow below the bread crumb on devices
	 * with a large screen.
	 */
	private View breadCrumbShadowView;

	/**
	 * The view, which is used to draw a shadow above the button bar when the
	 * activity is used as a wizard.
	 */
	private View buttonBarShadowView;

	/**
	 * The view, which is used to draw a shadow besides the navigation on
	 * devices with a large screen.
	 */
	private View navigationShadowView;

	/**
	 * The preference header, which is currently selected or null, if no
	 * preference header is currently selected.
	 */
	private PreferenceHeader currentHeader;

	/**
	 * The parameters which have been passed to the currently shown fragment or
	 * null, if no parameters have been passed.
	 */
	private Bundle currentBundle;

	/**
	 * The title, which is currently used by the bread crumb or null, if no
	 * bread crumb are currently shown.
	 */
	private CharSequence currentTitle;

	/**
	 * The short title, which is currently used by the bread crumb or null, if
	 * no bread crumb are currently shown.
	 */
	private CharSequence currentShortTitle;

	/**
	 * True, if the navigation icon of the activity's toolbar should be shown by
	 * default, false otherwise.
	 */
	private Boolean displayHomeAsUp;

	/**
	 * The default title of the activity.
	 */
	private CharSequence defaultTitle;

	/**
	 * True, if the behavior of the navigation icon of the activity's toolbar is
	 * overridden in order to return to the navigation when a preference header
	 * is currently selected on devices with a small screen.
	 */
	private boolean overrideNavigationIcon;

	/**
	 * True, if the fragment, which provides navigation to each preference
	 * header's fragment on devices with a large screen, is currently hidden or
	 * not.
	 */
	private boolean navigationHidden;

	/**
	 * The elevation of the navigation in dp.
	 */
	private int navigationElevation;

	/**
	 * The elevation of the button bar in dp.
	 */
	private int buttonBarElevation;

	/**
	 * The elevation of the bread crumb in dp.
	 */
	private int breadCrumbElevation;

	/**
	 * The bread crumb, which is used to show the title of the currently
	 * selected fragment on devices with a large screen, if the activity's
	 * toolbar is not shown.
	 */
	private TextView breadCrumb;

	/**
	 * True, if the progress should be shown as the bread crumb title, false
	 * otherwise.
	 */
	private boolean showProgress;

	/**
	 * The text, which is used to format the progress, which is shown as the
	 * bread crumb title.
	 */
	private String progressFormat;

	/**
	 * A set, which contains the listeners, which have registered to be notified
	 * when the user navigates within the activity, if it used as a wizard.
	 */
	private Set<WizardListener> wizardListeners = new LinkedHashSet<WizardListener>();

	/**
	 * Initializes the action bar's toolbar.
	 */
	private void initializeToolbar() {
		if (getSupportActionBar() == null) {
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

			if (isSplitScreen()) {
				toolbarLarge = (ToolbarLarge) findViewById(R.id.toolbar_large);
				toolbarLarge.setVisibility(View.VISIBLE);
			} else {
				toolbar.setVisibility(View.VISIBLE);
			}

			setSupportActionBar(toolbar);
			setTitle(getTitle());
		}
	}

	/**
	 * Initializes the preference header, which is selected by default on
	 * devices with a large screen.
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
	 * Handles extras of the intent, which has been used to start the activity,
	 * that allow to initially display a specific fragment.
	 */
	private void handleInitialFragmentIntent() {
		String initialFragment = getIntent()
				.getStringExtra(EXTRA_SHOW_FRAGMENT);
		Bundle initialArguments = getIntent().getBundleExtra(
				EXTRA_SHOW_FRAGMENT_ARGUMENTS);
		CharSequence initialTitle = getCharSequenceFromIntent(getIntent(),
				EXTRA_SHOW_FRAGMENT_TITLE);
		CharSequence initialShortTitle = getCharSequenceFromIntent(getIntent(),
				EXTRA_SHOW_FRAGMENT_SHORT_TITLE);

		if (initialFragment != null) {
			for (PreferenceHeader preferenceHeader : getListAdapter()
					.getAllItems()) {
				if (preferenceHeader.getFragment() != null
						&& preferenceHeader.getFragment().equals(
								initialFragment)) {
					selectPreferenceHeader(preferenceHeader, initialArguments);

					if (initialTitle != null) {
						showBreadCrumb(initialTitle, initialShortTitle);
					}
				}
			}
		}
	}

	/**
	 * Handles extras of the intent, which has been used to start the activity,
	 * that allow to show the button bar in order to use the activity as a
	 * wizard.
	 */
	private void handleShowButtonBarIntent() {
		boolean showButtonBar = getIntent().getBooleanExtra(
				EXTRA_SHOW_BUTTON_BAR, false);
		CharSequence nextButtonText = getCharSequenceFromIntent(getIntent(),
				EXTRA_NEXT_BUTTON_TEXT);
		CharSequence backButtonText = getCharSequenceFromIntent(getIntent(),
				EXTRA_BACK_BUTTON_TEXT);
		CharSequence finishButtonText = getCharSequenceFromIntent(getIntent(),
				EXTRA_FINISH_BUTTON_TEXT);
		CharSequence progressFormatString = getCharSequenceFromIntent(
				getIntent(), EXTRA_PROGRESS_FORMAT);

		if (showButtonBar) {
			showButtonBar(true);

			if (nextButtonText != null) {
				setNextButtonText(nextButtonText);
			}

			if (backButtonText != null) {
				setBackButtonText(backButtonText);
			}

			if (finishButtonText != null) {
				setFinishButtonText(finishButtonText);
			}

			showProgress(getIntent()
					.getBooleanExtra(EXTRA_SHOW_PROGRESS, false));

			if (progressFormatString != null) {
				setProgressFormat(progressFormatString.toString());
			}
		}
	}

	/**
	 * Handles the extra of the intent, which has been used to start the
	 * activity, that allows to hide the navigation.
	 */
	private void handleHideNavigationIntent() {
		if (getIntent().getExtras() != null
				&& getIntent().getExtras().containsKey(EXTRA_NO_HEADERS)) {
			hideNavigation(getIntent().getExtras().getBoolean(EXTRA_NO_HEADERS));
		} else {
			hideNavigation(isNavigationHidden());
		}
	}

	/**
	 * Returns the char sequence, which is specified by a specific intent extra.
	 * The char sequence can either be specified as a string or as a resource
	 * id.
	 * 
	 * @param intent
	 *            The intent, which specifies the char sequence, as an instance
	 *            of the class {@link Intent}
	 * @param name
	 *            The name of the intent extra, which specifies the char
	 *            sequence, as a {@link String}
	 * @return The char sequence, which is specified by the given intent, as an
	 *         instance of the class {@link CharSequence} or null, if the intent
	 *         does not specify a char sequence with the given name
	 */
	private CharSequence getCharSequenceFromIntent(final Intent intent,
			final String name) {
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
	 * Returns a listener, which allows to proceed to the next step, when the
	 * activity is used as a wizard.
	 * 
	 * @return The listener, which has been created, as an instance of the type
	 *         {@link OnClickListener}
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
	 * Returns a listener, which allows to resume to the previous step, when the
	 * activity is used as a wizard.
	 * 
	 * @return The listener, which has been created, as an instance of the type
	 *         {@link OnClickListener}
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
	 * Returns a listener, which allows to finish the last step, when the
	 * activity is used as a wizard.
	 * 
	 * @return The listener, which has been created, as an instance of the type
	 *         {@link OnClickListener}
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
	 * Notifies all registered listeners that the user wants to navigate to the
	 * next step of the wizard.
	 * 
	 * @return A bundle, which may contain key-value pairs, which have been
	 *         acquired in the wizard, if navigating to the next step of the
	 *         wizard should be allowed, as an instance of the class
	 *         {@link Bundle}, null otherwise
	 */
	private Bundle notifyOnNextStep() {
		Bundle result = null;

		for (WizardListener listener : wizardListeners) {
			Bundle bundle = listener.onNextStep(
					getListAdapter().indexOf(currentHeader), currentHeader,
					preferenceScreenFragment);

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
	 * Notifies all registered listeners that the user wants to navigate to the
	 * previous step of the wizard.
	 * 
	 * @return A bundle, which may contain key-value pairs, which have been
	 *         acquired in the wizard, if navigating to the previous step of the
	 *         wizard should be allowed, as an instance of the class
	 *         {@link Bundle}, null otherwise
	 */
	private Bundle notifyOnPreviousStep() {
		Bundle result = null;

		for (WizardListener listener : wizardListeners) {
			Bundle bundle = listener.onPreviousStep(
					getListAdapter().indexOf(currentHeader), currentHeader,
					preferenceScreenFragment);

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
	 * Notifies all registered listeners that the user wants to finish the last
	 * step of the wizard.
	 * 
	 * @return A bundle, which may contain key-value pairs, which have been
	 *         acquired in the wizard, if finishing the wizard should be
	 *         allowed, as an instance of the class {@link Bundle}, null
	 *         otherwise
	 */
	private Bundle notifyOnFinish() {
		Bundle result = null;

		for (WizardListener listener : wizardListeners) {
			Bundle bundle = listener.onFinish(
					getListAdapter().indexOf(currentHeader), currentHeader,
					preferenceScreenFragment);

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
	 * Notifies all registered listeners that the user wants to skip the wizard.
	 * 
	 * @return A bundle, which may contain key-value pairs, which have been
	 *         acquired in the wizard, if skipping the wizard should be allowed,
	 *         as an instance of the class {@link Bundle}, null otherwise
	 */
	private Bundle notifyOnSkip() {
		Bundle result = null;

		for (WizardListener listener : wizardListeners) {
			Bundle bundle = listener.onSkip(
					getListAdapter().indexOf(currentHeader), currentHeader,
					preferenceScreenFragment);

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
	 * Shows the fragment, which corresponds to a specific preference header.
	 * 
	 * @param preferenceHeader
	 *            The preference header, the fragment, which should be shown,
	 *            corresponds to, as an instance of the class
	 *            {@link PreferenceHeader}. The preference header may not be
	 *            null
	 * @param parameters
	 *            The parameters, which should be passed to the fragment, as an
	 *            instance of the class {@link Bundle} or null, if the
	 *            preference header's extras should be used instead
	 */
	private void showPreferenceScreen(final PreferenceHeader preferenceHeader,
			final Bundle parameters) {
		if (parameters != null && preferenceHeader.getExtras() != null) {
			parameters.putAll(preferenceHeader.getExtras());
		}

		showPreferenceScreen(preferenceHeader, parameters, true);
	}

	/**
	 * Shows the fragment, which corresponds to a specific preference header.
	 * 
	 * @param preferenceHeader
	 *            The preference header, the fragment, which should be shown,
	 *            corresponds to, as an instance of the class
	 *            {@link PreferenceHeader}. The preference header may not be
	 *            null
	 * @param parameters
	 *            The parameters, which should be passed to the fragment, as an
	 *            instance of the class {@link Bundle} or null, if the
	 *            preference header's extras should be used instead
	 * @param launchIntent
	 *            True, if a preference header's intent should be launched,
	 *            false otherwise
	 */
	private void showPreferenceScreen(final PreferenceHeader preferenceHeader,
			final Bundle parameters, final boolean launchIntent) {
		if (currentHeader == null || !currentHeader.equals(preferenceHeader)) {
			currentHeader = preferenceHeader;
			adaptWizardButtons();

			if (preferenceHeader.getFragment() != null) {
				showBreadCrumb(preferenceHeader);
				currentBundle = (parameters != null) ? parameters
						: preferenceHeader.getExtras();
				showPreferenceScreen(preferenceHeader.getFragment(),
						currentBundle);
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
	 * @param fragmentName
	 *            The full qualified class name of the fragment, which should be
	 *            shown, as a {@link String}
	 * @param params
	 *            The parameters, which should be passed to the fragment, as an
	 *            instance of the class {@link Bundle} or null, if the
	 *            preference header's extras should be used instead
	 */
	private void showPreferenceScreen(final String fragmentName,
			final Bundle params) {
		preferenceScreenFragment = Fragment.instantiate(this, fragmentName,
				params);

		if (isSplitScreen()) {
			replaceFragment(preferenceScreenFragment,
					R.id.preference_screen_parent, 0);
		} else {
			updateSavedInstanceState();
			replaceFragment(preferenceScreenFragment,
					R.id.preference_header_parent,
					FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			showToolbarNavigationIcon();
		}
	}

	/**
	 * Adapts the buttons which are shown, when the activity is used as a
	 * wizard, depending on the currently selected preference header.
	 */
	private void adaptWizardButtons() {
		if (currentHeader != null && isButtonBarShown()) {
			int index = getListAdapter().indexOf(currentHeader);
			getBackButton().setVisibility(
					(index != 0) ? View.VISIBLE : View.GONE);
			getNextButton().setVisibility(
					(index != getListAdapter().getCount() - 1) ? View.VISIBLE
							: View.GONE);
			getFinishButton().setVisibility(
					(index == getListAdapter().getCount() - 1) ? View.VISIBLE
							: View.GONE);
		} else if (isButtonBarShown()) {
			getBackButton().setVisibility(View.GONE);
			getNextButton().setVisibility(View.GONE);
			getFinishButton().setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Adapts the GUI, depending on whether the navigation is currently hidden
	 * or not.
	 * 
	 * @param navigationHidden
	 *            True, if the navigation is currently hidden, false otherwise
	 */
	private void adaptNavigation(final boolean navigationHidden) {
		if (isSplitScreen()) {
			getPreferenceHeaderParentView().setVisibility(
					navigationHidden ? View.GONE : View.VISIBLE);
			navigationShadowView.setVisibility(navigationHidden ? View.GONE
					: View.VISIBLE);

			if (toolbarLarge != null) {
				toolbarLarge.hideNavigation(navigationHidden);
			}
		} else {
			if (isPreferenceHeaderSelected()) {
				if (navigationHidden) {
					hideToolbarNavigationIcon();
				} else {
					showToolbarNavigationIcon();
				}
			} else if (navigationHidden) {
				if (getListAdapter() != null && !getListAdapter().isEmpty()) {
					showPreferenceScreen(getListAdapter().getItem(0), null);
				} else if (getListAdapter() != null) {
					finish();
				}
			}
		}
	}

	/**
	 * Shows the fragment, which provides the navigation to each preference
	 * header's fragment.
	 */
	private void showPreferenceHeaders() {
		int transition = 0;

		if (isPreferenceHeaderSelected()) {
			transition = FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;
			currentHeader = null;
			preferenceScreenFragment = null;
		}

		replaceFragment(preferenceHeaderFragment,
				R.id.preference_header_parent, transition);
	}

	/**
	 * Replaces the fragment, which is currently contained by a specific parent
	 * view, by an other fragment.
	 * 
	 * @param fragment
	 *            The fragment, which should replace the current fragment, as an
	 *            instance of the class {@link Fragment}. The fragment may not
	 *            be null
	 * @param parentViewId
	 *            The id of the parent view, which contains the fragment, that
	 *            should be replaced, as an {@link Integer} value
	 * @param transition
	 *            The transition, which should be shown when replacing the
	 *            fragment, as an {@link Integer} value or 0, if no transition
	 *            should be shown
	 */
	private void replaceFragment(final Fragment fragment,
			final int parentViewId, final int transition) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.setTransition(transition);
		transaction.addToBackStack(FRAGMENT_BACK_STACK);
		transaction.replace(parentViewId, fragment);
		transaction.commit();
	}

	/**
	 * Removes a specific fragment from its parent view.
	 * 
	 * @param fragment
	 *            The fragment, which should be removed, as an instance of the
	 *            class {@link Fragment}. The fragment may not be null
	 */
	private void removeFragment(final Fragment fragment) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.remove(fragment);
		transaction.commit();
	}

	/**
	 * Shows the navigation icon of the activity's toolbar.
	 */
	private void showToolbarNavigationIcon() {
		if (isPreferenceHeaderSelected() && isNavigationIconOverridden()
				&& !isNavigationHidden()
				&& !(!isSplitScreen() && isButtonBarShown())) {
			if (displayHomeAsUp == null) {
				displayHomeAsUp = isDisplayHomeAsUpEnabled();
			}

			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	/**
	 * Hides the navigation icon of the activity's toolbar, respectively sets it
	 * to the previous icon.
	 */
	private void hideToolbarNavigationIcon() {
		if (displayHomeAsUp != null && !displayHomeAsUp) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			getSupportActionBar().setHomeButtonEnabled(false);
		}
	}

	/**
	 * Returns, whether the navigation icon of the activity's toolbar is
	 * currently shown, or not.
	 * 
	 * @return True, if the navigation icon of the activity's toolbar is
	 *         currently shown, false otherwise
	 */
	private boolean isDisplayHomeAsUpEnabled() {
		if (getSupportActionBar() != null) {
			return (getSupportActionBar().getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) == ActionBar.DISPLAY_HOME_AS_UP;
		}

		return false;
	}

	/**
	 * Shows the bread crumb for a specific preference header, depending on
	 * whether the device has a large screen or not. On devices with a large
	 * screen the bread crumb will be shown above the currently shown fragment,
	 * on devices with a small screen the bread crumb will be shown as the
	 * action bar's title instead.
	 * 
	 * @param preferenceHeader
	 *            The preference header, the bread crumb should be shown for, as
	 *            an instance of the class {@link PreferenceHeader}. The
	 *            preference header may not be null
	 */
	private void showBreadCrumb(final PreferenceHeader preferenceHeader) {
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
	 * Shows the bread crumb using a specific title and short title, depending
	 * on whether the device has a large screen or not. On devices with a large
	 * screen the bread crumb will be shown above the currently shown fragment,
	 * on devices with a small screen the bread crumb will be shown as the
	 * action bar's title instead.
	 * 
	 * @param title
	 *            The title, which should be used by the bread crumb, as an
	 *            instance of the class {@link CharSequence} or null, if no
	 *            title should be used
	 * @param shortTitle
	 *            The short title, which should be used by the bread crumb, as
	 *            an instance of the class {@link CharSequence} or null, if no
	 *            short title should be used
	 */
	private void showBreadCrumb(final CharSequence title,
			final CharSequence shortTitle) {
		this.currentTitle = title;
		this.currentShortTitle = shortTitle;
		CharSequence breadCrumbTitle = createBreadCrumbTitle(title);

		if (getBreadCrumb() != null) {
			if (breadCrumbTitle != null) {
				getBreadCrumb().setVisibility(View.VISIBLE);
				breadCrumbShadowView.setVisibility(View.VISIBLE);
			} else {
				getBreadCrumb().setVisibility(View.GONE);
				breadCrumbShadowView.setVisibility(View.GONE);
			}

			getBreadCrumb().setText(breadCrumbTitle);
		} else if (toolbarLarge != null) {
			toolbarLarge.setBreadCrumbTitle(breadCrumbTitle);
		} else if (breadCrumbTitle != null) {
			if (defaultTitle == null) {
				defaultTitle = getTitle();
			}
			setTitle(breadCrumbTitle);
		}
	}

	/**
	 * Creates and returns the title of the bread crumb, depending on whether
	 * the activity is used as a wizard and whether the progress should be
	 * shown, or not.
	 * 
	 * @param title
	 *            The title, which should be used by the bread crumb, as an
	 *            instance of the class {@link CharSequence} or null, if no
	 *            title should be used
	 * @return The title, which has been created, as an instance of the class
	 *         {@link CharSequence} or null, if no title should be used
	 */
	private CharSequence createBreadCrumbTitle(final CharSequence title) {
		if (title != null) {
			if (isProgressShown()) {
				int currentStep = getListAdapter().indexOf(currentHeader) + 1;
				int totalSteps = getListAdapter().getCount();
				return String.format(getProgressFormat(), currentStep,
						totalSteps, title);
			} else {
				return title;
			}
		}

		return null;
	}

	/**
	 * Resets the title of the activity to the default title, if it has been
	 * previously changed.
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
	 * Adds the preference headers, which are currently added to the activity,
	 * to the bundle, which has been passed to the activity, when it has been
	 * created. If no bundle has been passed to the activity, a new bundle will
	 * be created.
	 */
	private void updateSavedInstanceState() {
		if (savedInstanceState == null) {
			savedInstanceState = new Bundle();
		}

		savedInstanceState.putParcelableArrayList(PREFERENCE_HEADERS_EXTRA,
				getListAdapter().getAllItems());
	}

	/**
	 * Obtains all relevant attributes from the activity's current theme.
	 */
	private void obtainStyledAttributes() {
		int theme = obtainTheme();

		if (theme != 0) {
			obtainNavigationBackground(theme);
			obtainPreferenceScreenBackground(theme);
			obtainWizardButtonBarBackground(theme);
			obtainBreadCrumbBackground(theme);
			obtainNavigationWidth(theme);
			obtainOverrideNavigationIcon(theme);
			obtainNavigationElevation(theme);
			obtainWizardButtonBarElevation(theme);
			obtainBreadCrumbElevation(theme);
		}
	}

	/**
	 * Obtains the resource id of the activity's current theme.
	 * 
	 * @return The resource id of the acitivty's current theme as an
	 *         {@link Integer} value or 0, if an error occurred while obtaining
	 *         the theme
	 */
	private int obtainTheme() {
		try {
			String packageName = getClass().getPackage().getName();
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					packageName, PackageManager.GET_META_DATA);
			return packageInfo.applicationInfo.theme;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}

	/**
	 * Obtains the background of the navigation from a specific theme.
	 * 
	 * @param theme
	 *            The resource id of the theme, the background should be
	 *            obtained from, as an {@link Integer} value
	 */
	private void obtainNavigationBackground(final int theme) {
		TypedArray typedArray = getTheme().obtainStyledAttributes(theme,
				new int[] { R.attr.navigationBackground });
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
	 * Obtains the background of the preference screen from a specific theme.
	 * 
	 * @param theme
	 *            The resource id of the theme, the background should be
	 *            obtained from, as an {@link Integer} value
	 */
	private void obtainPreferenceScreenBackground(final int theme) {
		TypedArray typedArray = getTheme().obtainStyledAttributes(theme,
				new int[] { R.attr.preferenceScreenBackground });
		int color = typedArray.getColor(0, 0);

		if (color != 0) {
			setPreferenceScreenBackgroundColor(color);
		} else {
			int resourceId = typedArray.getResourceId(0, 0);

			if (resourceId != 0) {
				setPreferenceScreenBackground(resourceId);
			}
		}
	}

	/**
	 * Obtains the background of the wizard button bar from a specific theme.
	 * 
	 * @param theme
	 *            The resource id of the theme, the background should be
	 *            obtained from, as an {@link Integer} value
	 */
	private void obtainWizardButtonBarBackground(final int theme) {
		View wizardButtonBar = findViewById(R.id.wizard_button_bar);
		TypedArray typedArray = getTheme().obtainStyledAttributes(theme,
				new int[] { R.attr.wizardButtonBarBackground });
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
	 * Obtains the background of the bread crumb from a specific theme.
	 * 
	 * @param theme
	 *            The resource id of the theme, the background should be
	 *            obtained from, as an {@link Integer} value
	 */
	private void obtainBreadCrumbBackground(final int theme) {
		TypedArray typedArray = getTheme().obtainStyledAttributes(theme,
				new int[] { R.attr.breadCrumbBackground });
		int color = typedArray.getColor(0, 0);

		if (color != 0) {
			setBreadCrumbBackgroundColor(color);
		} else {
			int resourceId = typedArray.getResourceId(0, 0);

			if (resourceId != 0) {
				setBreadCrumbBackground(resourceId);
			}
		}
	}

	/**
	 * Obtains the width of the navigation from a specific theme.
	 * 
	 * @param theme
	 *            The resource id of the theme, the navigation width should be
	 *            obtained from, as an {@link Integer} value
	 */
	private void obtainNavigationWidth(final int theme) {
		TypedArray typedArray = getTheme().obtainStyledAttributes(theme,
				new int[] { R.attr.navigationWidth });
		int width = convertPixelsToDp(this,
				typedArray.getDimensionPixelSize(0, 0));

		if (width != 0) {
			setNavigationWidth(width);
		}
	}

	/**
	 * Obtains, whether the behavior of the navigation icon should be
	 * overridden, or not.
	 * 
	 * @param theme
	 *            The resource id of the theme, the navigation width should be
	 *            obtained from, as an {@link Integer} value
	 */
	private void obtainOverrideNavigationIcon(final int theme) {
		TypedArray typedArray = getTheme().obtainStyledAttributes(theme,
				new int[] { R.attr.overrideNavigationIcon });
		overrideNavigationIcon(typedArray.getBoolean(0, true));
	}

	/**
	 * Obtains the elevation of the navigation from a specific theme.
	 * 
	 * @param theme
	 *            The resource id of the theme, the navigation width should be
	 *            obtained from, as an {@link Integer} value
	 */
	private void obtainNavigationElevation(final int theme) {
		TypedArray typedArray = getTheme().obtainStyledAttributes(theme,
				new int[] { R.attr.navigationElevation });
		int elevation = convertPixelsToDp(this,
				typedArray.getDimensionPixelSize(0, 0));

		if (elevation != 0) {
			setNavigationElevation(elevation);
		}
	}

	/**
	 * Obtains the elevation of the button bar from a specific theme.
	 * 
	 * @param theme
	 *            The resource id of the theme, the navigation width should be
	 *            obtained from, as an {@link Integer} value
	 */
	@SuppressWarnings("deprecation")
	private void obtainWizardButtonBarElevation(final int theme) {
		TypedArray typedArray = getTheme().obtainStyledAttributes(theme,
				new int[] { R.attr.wizardButtonBarElevation });
		int elevation = convertPixelsToDp(this,
				typedArray.getDimensionPixelSize(0, 0));

		if (elevation != 0) {
			View shadowView = findViewById(R.id.wizard_button_bar_shadow_view);
			String[] shadowColors = getResources().getStringArray(
					R.array.button_bar_elevation_shadow_colors);
			String[] shadowWidths = getResources().getStringArray(
					R.array.button_bar_elevation_shadow_widths);
			ensureAtLeast(elevation, 1, "The elevation must be at least 1");
			ensureAtMaximum(elevation, shadowWidths.length,
					"The elevation must be at maximum " + shadowWidths.length);

			if (shadowView != null) {
				this.buttonBarElevation = elevation;
				int shadowColor = Color.parseColor(shadowColors[elevation - 1]);
				int shadowWidth = convertDpToPixels(this,
						Integer.valueOf(shadowWidths[elevation - 1]));

				GradientDrawable gradient = new GradientDrawable(
						Orientation.BOTTOM_TOP, new int[] { shadowColor,
								Color.TRANSPARENT });
				shadowView.setBackgroundDrawable(gradient);
				shadowView.getLayoutParams().height = shadowWidth;
				shadowView.requestLayout();
			}
		}
	}

	/**
	 * Obtains the elevation of the bread crumb from a specific theme.
	 * 
	 * @param theme
	 *            The resource id of the theme, the navigation width should be
	 *            obtained from, as an {@link Integer} value
	 */
	private void obtainBreadCrumbElevation(final int theme) {
		TypedArray typedArray = getTheme().obtainStyledAttributes(theme,
				new int[] { R.attr.breadCrumbElevation });
		int elevation = convertPixelsToDp(this,
				typedArray.getDimensionPixelSize(0, 0));

		if (elevation != 0) {
			setBreadCrumbElevation(elevation);
		}
	}

	/**
	 * Adds a new listener, which should be notified, when the user navigates
	 * within the activity, if it is used as a wizard, to the activity.
	 * 
	 * @param listener
	 *            The listener, which should be added, as an instance of the
	 *            type {@link WizardListener}. The listener may not be null
	 */
	public final void addWizardListener(final WizardListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		wizardListeners.add(listener);
	}

	/**
	 * Removes a specific listener, which should not be notified, when the user
	 * navigates within the activity, if it is used as a wizard, from the
	 * activity.
	 * 
	 * @param listener
	 *            The listener, which should be removed, as an instance of the
	 *            type {@link WizardListener}. The listener may not be null
	 */
	public final void removeWizardListener(final WizardListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		wizardListeners.remove(wizardListeners);
	}

	/**
	 * Returns the parent view of the fragment, which provides the navigation to
	 * each preference header's fragment. On devices with a small screen this
	 * parent view is also used to show a preference header's fragment, when a
	 * header is currently selected.
	 * 
	 * @return The parent view of the fragment, which provides the navigation to
	 *         each preference header's fragment, as an instance of the class
	 *         {@link ViewGroup}. The parent view may not be null
	 */
	public final ViewGroup getPreferenceHeaderParentView() {
		return preferenceHeaderParentView;
	}

	/**
	 * Returns the parent view of the fragment, which is used to show the
	 * preferences of the currently selected preference header on devices with a
	 * large screen.
	 * 
	 * @return The parent view of the fragment, which is used to show the
	 *         preferences of the currently selected preference header, as an
	 *         instance of the class {@link ViewGroup} or null, if the device
	 *         has a small screen
	 */
	public final ViewGroup getPreferenceScreenParentView() {
		return preferenceScreenParentView;
	}

	/**
	 * Returns the view group, which contains all views, e.g. the preferences
	 * itself and the bread crumb, which are shown when a preference header is
	 * selected on devices with a large screen.
	 * 
	 * @return The view group, which contains all views, which are shown when a
	 *         preference header is selected, as an instance of the class
	 *         {@link ViewGroup} or null, if the device has a small screen
	 */
	public final ViewGroup getPreferenceScreenContainer() {
		return preferenceScreenContainer;
	}

	/**
	 * Returns the view group, which contains the buttons, which are shown when
	 * the activity is used as a wizard.
	 * 
	 * @return The view group, which contains the buttons, which are shown when
	 *         the activity is used as a wizard, as an instance of the class
	 *         {@link ViewGroup} or null, if the wizard is not used as a wizard
	 */
	public final ViewGroup getButtonBar() {
		return buttonBar;
	}

	/**
	 * Returns the next button, which is shown, when the activity is used as a
	 * wizard and the last preference header is currently not selected.
	 * 
	 * @return The next button as an instance of the class {@link Button} or
	 *         null, if the activity is not used as a wizard
	 */
	public final Button getNextButton() {
		return nextButton;
	}

	/**
	 * Returns the text of the next button, which is shown, when the activity is
	 * used as a wizard.
	 * 
	 * @return The text of the next button as an instance of the class
	 *         {@link CharSequence} or null, if the activity is not used as a
	 *         wizard
	 */
	public final CharSequence getNextButtonText() {
		if (nextButton != null) {
			return nextButton.getText();
		}

		return null;
	}

	/**
	 * Sets the text of the next button, which is shown, when the activity is
	 * used as a wizard. The text is only set, if the activity is used as a
	 * wizard.
	 * 
	 * @param text
	 *            The text, which should be set, as an instance of the class
	 *            {@link CharSequence}. The text may not be null
	 * @return True, if the text has been set, false otherwise
	 */
	public final boolean setNextButtonText(final CharSequence text) {
		ensureNotNull(text, "The text may not be null");

		if (nextButton != null) {
			nextButton.setText(text);
			return true;
		}

		return false;
	}

	/**
	 * Sets the text of the next button, which is shown, when the activity is
	 * used as a wizard. The text is only set, if the activity is used as a
	 * wizard.
	 * 
	 * @param resourceId
	 *            The resource id of the text, which should be set, as an
	 *            {@link Integer} value. The resource id must correspond to a
	 *            valid string resource
	 * @return True, if the text has been set, false otherwise
	 */
	public final boolean setNextButtonText(final int resourceId) {
		return setNextButtonText(getText(resourceId));
	}

	/**
	 * Returns the finish button, which is shown, when the activity is used as a
	 * wizard and the last preference header is currently selected.
	 * 
	 * @return The finish button as an instance of the class {@link Button} or
	 *         null, if the activity is not used as a wizard
	 */
	public final Button getFinishButton() {
		return finishButton;
	}

	/**
	 * Sets the text of the next button, which is shown, when the activity is
	 * used as a wizard and the last preference header is currently selected.
	 * The text is only set, if the activity is used as a wizard.
	 * 
	 * @param text
	 *            The text, which should be set, as an instance of the class
	 *            {@link CharSequence}. The text may not be null
	 * @return True, if the text has been set, false otherwise
	 */
	public final boolean setFinishButtonText(final CharSequence text) {
		ensureNotNull(text, "The text may not be null");

		if (finishButton != null) {
			finishButton.setText(text);
			return true;
		}

		return false;
	}

	/**
	 * Sets the text of the next button, which is shown, when the activity is
	 * used as a wizard and the last preference header is currently selected.
	 * The text is only set, if the activity is used as a wizard.
	 * 
	 * @param resourceId
	 *            The resource id of the text, which should be set, as an
	 *            {@link Integer} value. The resource id must correspond to a
	 *            valid string resource
	 * @return True, if the text has been set, false otherwise
	 */
	public final boolean setFinishButtonText(final int resourceId) {
		return setFinishButtonText(getText(resourceId));
	}

	/**
	 * Returns the text of the finish button, which is shown, when the activity
	 * is used as a wizard and the last preference header is currently selected.
	 * 
	 * @return The text of the finish button as an instance of the class
	 *         {@link CharSequence} or null, if the activity is not used as a
	 *         wizard
	 */
	public final CharSequence getFinishButtonText() {
		if (finishButton != null) {
			return finishButton.getText();
		}

		return null;
	}

	/**
	 * Returns the back button, which is shown, when the activity is used as a
	 * wizard.
	 * 
	 * @return The back button as an instance of the class {@link Button} or
	 *         null, if the activity is not used as a wizard
	 */
	public final Button getBackButton() {
		return backButton;
	}

	/**
	 * Returns the text of the back button, which is shown, when the activity is
	 * used as a wizard.
	 * 
	 * @return The text of the back button as an instance of the class
	 *         {@link CharSequence} or null, if the activity is not used as a
	 *         wizard
	 */
	public final CharSequence getBackButtonText() {
		if (backButton != null) {
			return backButton.getText();
		}

		return null;
	}

	/**
	 * Sets the text of the back button, which is shown, when the activity is
	 * used as a wizard. The text is only set, if the activity is used as a
	 * wizard.
	 * 
	 * @param text
	 *            The text, which should be set, as an instance of the class
	 *            {@link CharSequence}. The text may not be null
	 * @return True, if the text has been set, false otherwise
	 */
	public final boolean setBackButtonText(final CharSequence text) {
		ensureNotNull(text, "The text may not be null");

		if (backButton != null) {
			backButton.setText(text);
			return true;
		}

		return false;
	}

	/**
	 * Sets the text of the back button, which is shown, when the activity is
	 * used as a wizard. The text is only set, if the activity is used as a
	 * wizard.
	 * 
	 * @param resourceId
	 *            The resource id of the text, which should be set, as an
	 *            {@link Integer} value. The resource id must correspond to a
	 *            valid string resource
	 * @return True, if the text has been set, false otherwise
	 */
	public final boolean setBackButtonText(final int resourceId) {
		return setBackButtonText(getText(resourceId));
	}

	/**
	 * Returns, whether the progress is shown, if the activity is used as a
	 * wizard.
	 * 
	 * @return True, if the progress is shown, false otherwise or if the
	 *         activity is not used as a wizard
	 */
	public final boolean isProgressShown() {
		return isButtonBarShown() && showProgress;
	}

	/**
	 * Shows or hides the progress, if the activity is used as a wizard.
	 * 
	 * @param showProgress
	 *            True, if the progress should be shown, false otherwise
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
	 * Returns the string, which is used to format the progress, which may be
	 * shown, if the activity is used as a wizard.
	 * 
	 * @return The string, which is used to format the progress, as a
	 *         {@link String} or null, if the activity is not used as a wizard
	 *         or if no progress is shown
	 */
	public final String getProgressFormat() {
		if (isProgressShown()) {
			return progressFormat != null ? progressFormat
					: getString(R.string.progress_format);
		}

		return null;
	}

	/**
	 * Sets the string, which should be used to format the progress, if the
	 * activity is used as a wizard and the progress is shown.
	 * 
	 * @param progressFormat
	 *            The string, which should be set, as a {@link String}. The
	 *            string may not be null. It must be formatted according to the
	 *            following syntax: "*%d*%d*%s*"
	 * @return True, if the string has been set, false otherwise
	 */
	public final boolean setProgressFormat(final String progressFormat) {
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
	 * Sets the string, which should be used to format the progress, if the
	 * activity is used as a wizard and the progress is shown.
	 * 
	 * @param resourceId
	 *            The resource id of the string, which should be set, as an
	 *            {@link Integer} value. The resource id must correspond to a
	 *            valid string resource. It must be formatted according to the
	 *            following syntax: "*%d*%d*%s*"
	 * @return True, if the string has been set, false otherwise
	 */
	public final boolean setProgressFormat(final int resourceId) {
		return setProgressFormat(getString(resourceId));
	}

	/**
	 * Returns the bread crumb, which is used to show the title of the currently
	 * selected fragment on devices with a large screen, if the activity's
	 * toolbar is not shown.
	 * 
	 * @return The bread crumb, which is used to show the title of the currently
	 *         selected fragment or null, if the device has a small screen or
	 *         the activity's toolbar is shown
	 */
	public final TextView getBreadCrumb() {
		return breadCrumb;
	}

	/**
	 * Returns the list view, which is used to show the preference headers.
	 * 
	 * @return The list view, which is used to show the preference header, as an
	 *         instance of the class {@link ListView}. The list view may not be
	 *         null
	 */
	public final ListView getListView() {
		return preferenceHeaderFragment.getListView();
	}

	/**
	 * Returns the adapter, which provides the preference headers for
	 * visualization using the list view.
	 * 
	 * @return The adapter, which provides the preference headers for
	 *         visualization using the list view, as an instance of the class
	 *         {@link PreferenceHeaderAdapter}. The adapter may not be null
	 */
	public final PreferenceHeaderAdapter getListAdapter() {
		return preferenceHeaderFragment.getListAdapter();
	}

	/**
	 * Adds all preference headers, which are specified by a specific XML
	 * resource, to the activity.
	 * 
	 * @param resourceId
	 *            The resource id of the XML file, which specifies the
	 *            preference headers, as an {@link Integer} value. The resource
	 *            id must correspond to a valid XML resource
	 */
	public final void addPreferenceHeadersFromResource(final int resourceId) {
		getListAdapter().addAllItems(
				PreferenceHeaderParser.fromResource(this, resourceId));
	}

	/**
	 * Adds a new preference header to the activity.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which should be added, as an instance
	 *            of the class {@link PreferenceHeader}. The preference header
	 *            may not be null
	 */
	public final void addPreferenceHeader(
			final PreferenceHeader preferenceHeader) {
		getListAdapter().addItem(preferenceHeader);
	}

	/**
	 * Adds all preference headers, which are contained by a specific
	 * collection, to the activity.
	 * 
	 * @param preferenceHeaders
	 *            The collection, which contains the preference headers, which
	 *            should be added, as an instance of the type {@link Collection}
	 *            or an empty collection, if no preference headers should be
	 *            added
	 */
	public final void addAllPreferenceHeaders(
			final Collection<PreferenceHeader> preferenceHeaders) {
		getListAdapter().addAllItems(preferenceHeaders);
	}

	/**
	 * Removes a specific preference header from the activity.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which should be removed, as an instance
	 *            of the class {@link PreferenceHeader}. The preference header
	 *            may not be null
	 * @return True, if the preference header has been removed, false otherwise
	 */
	public final boolean removePreferenceHeader(
			final PreferenceHeader preferenceHeader) {
		return getListAdapter().removeItem(preferenceHeader);
	}

	/**
	 * Returns a collection, which contains all preference headers, which are
	 * currently added to the activity.
	 * 
	 * @return A collection, which contains all preference headers, as an
	 *         instance of the type {@link Collection} or an empty collection,
	 *         if the activity does not contain any preference headers
	 */
	public final Collection<PreferenceHeader> getAllPreferenceHeaders() {
		return getListAdapter().getAllItems();
	}

	/**
	 * Returns the preference header, which belongs to a specific position.
	 * 
	 * @param position
	 *            The position of the preference header, which should be
	 *            returned, as an {@link Integer} value
	 * @return The preference header, which belongs to the given position, as an
	 *         instance of the class {@link PreferenceHeader}. The preference
	 *         header may not be null
	 */
	public final PreferenceHeader getPreferenceHeader(final int position) {
		return getListAdapter().getItem(position);
	}

	/**
	 * Returns the number of preference headers, which are currently added to
	 * the activity.
	 * 
	 * @return The number of preference header, which are currently added to the
	 *         activity, as an {@link Integer} value
	 */
	public final int getNumberOfPreferenceHeaders() {
		return getListAdapter().getCount();
	}

	/**
	 * Removes all preference headers, which are currently added to the
	 * activity.
	 */
	public final void clearPreferenceHeaders() {
		getListAdapter().clear();
	}

	/**
	 * Selects a specific preference header.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which should be selected, as an
	 *            instance of the class {@link PreferenceHeader}. The preference
	 *            header may not be null. If the preference header does not
	 *            belong to the activity, a {@link NoSuchElementException} will
	 *            be thrown
	 */
	public final void selectPreferenceHeader(
			final PreferenceHeader preferenceHeader) {
		selectPreferenceHeader(preferenceHeader, null);
	}

	/**
	 * Selects a specific preference header.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which should be selected, as an
	 *            instance of the class {@link PreferenceHeader}. The preference
	 *            header may not be null. If the preference header does not
	 *            belong to the activity, a {@link NoSuchElementException} will
	 *            be thrown
	 * @param parameters
	 *            The parameters, which should be passed to the preference
	 *            header's fragment, as an instance of the class {@link Bundle}
	 *            or null, if the preference header's extras should be used
	 *            instead
	 */
	public final void selectPreferenceHeader(
			final PreferenceHeader preferenceHeader, final Bundle parameters) {
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
	 *            The position of the preference header, which should be
	 *            selected, as an {@link Integer} value. If the position is
	 *            invalid, an {@link IndexOutOfBoundsException} will be thrown
	 */
	public final void selectPreferenceHeader(final int position) {
		selectPreferenceHeader(position, null);
	}

	/**
	 * Selects the preference header, which belongs to a specific position.
	 * 
	 * @param position
	 *            The position of the preference header, which should be
	 *            selected, as an {@link Integer} value. If the position is
	 *            invalid, an {@link IndexOutOfBoundsException} will be thrown
	 * @param parameters
	 *            The parameters, which should be passed to the preference
	 *            header's fragment, as an instance of the class {@link Bundle}
	 *            or null, if the preference header's extras should be used
	 *            instead
	 */
	public final void selectPreferenceHeader(final int position,
			final Bundle parameters) {
		getListView().setItemChecked(position, true);
		getListView().smoothScrollToPosition(position);
		showPreferenceScreen(getListAdapter().getItem(position), parameters);
	}

	/**
	 * Returns, whether the preference headers and the corresponding fragments
	 * are shown split screen, or not.
	 * 
	 * @return True, if the preference headers and the corresponding fragments
	 *         are shown split screen, false otherwise
	 */
	public final boolean isSplitScreen() {
		return getPreferenceScreenParentView() != null;
	}

	/**
	 * Returns, whether the fragment, which provides navigation to each
	 * preference header's fragment, is currently hidden or not.
	 * 
	 * @return True, if the fragment, which provides navigation to each
	 *         preference header's fragment is currently hidden, false otherwise
	 */
	public final boolean isNavigationHidden() {
		return navigationHidden;
	}

	/**
	 * Hides or shows the fragment, which provides navigation to each preference
	 * header's fragment. When the activity is used as a wizard on devices with
	 * a small screen, the navigation is always hidden.
	 * 
	 * @param hideNavigation
	 *            True, if the fragment, which provides navigation to each
	 *            preference header's fragment, should be hidden, false
	 *            otherwise
	 */
	public final void hideNavigation(final boolean hideNavigation) {
		this.navigationHidden = hideNavigation;
		adaptNavigation(hideNavigation);
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
	 * Shows or hides the view group, which contains the buttons, which are
	 * shown when the activity is used as a wizard.
	 * 
	 * @param showButtonBar
	 *            True, if the button bar should be shown, false otherwise
	 */
	public final void showButtonBar(final boolean showButtonBar) {
		if (showButtonBar) {
			buttonBar = (ViewGroup) findViewById(R.id.wizard_button_bar);
			buttonBar.setVisibility(View.VISIBLE);
			buttonBarShadowView = findViewById(R.id.wizard_button_bar_shadow_view);
			buttonBarShadowView.setVisibility(View.VISIBLE);

			if (buttonBarElevation == 0) {
				setButtonBarElevation(DEFAULT_BUTTON_BAR_ELEVATION);
			}

			nextButton = (Button) findViewById(R.id.next_button);
			nextButton.setOnClickListener(createNextButtonListener());
			finishButton = (Button) findViewById(R.id.finish_button);
			finishButton.setOnClickListener(createFinishButtonListener());
			backButton = (Button) findViewById(R.id.back_button);
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
	 * @return True, if a preference header is currently selected, false
	 *         otherwise
	 */
	public final boolean isPreferenceHeaderSelected() {
		return currentHeader != null && currentHeader.getFragment() != null;
	}

	/**
	 * Returns the preference header, which is currently selected.
	 * 
	 * @return The preference header, which is currently selected or null, if no
	 *         preference header is currently selected
	 */
	public final PreferenceHeader getSelectedPreferenceHeader() {
		return currentHeader;
	}

	/**
	 * Returns the position of the preference header, which is currently
	 * selected.
	 * 
	 * @return The position of the preference header, which is currently
	 *         selected or -1, if no preference header is currently selected
	 */
	public final int getSelectedPreferenceHeaderPosition() {
		return getListAdapter().indexOf(currentHeader);
	}

	/**
	 * Returns the elevation of the parent view of the fragment, which provides
	 * navigation to each preference header's fragment on devices with a large
	 * screen.
	 * 
	 * @return The elevation of the parent view of the fragment, which provides
	 *         navigation to each preference header's fragment on devices with a
	 *         large screen, in dp as an {@link Integer} value or -1, if the
	 *         device has a small screen
	 */
	public final int getNavigationElevation() {
		if (isSplitScreen()) {
			return navigationElevation;
		} else {
			return -1;
		}
	}

	/**
	 * Sets the elevation of the parent view of the fragment, which provides
	 * navigation to each preference header's fragment on devices with a large
	 * screen. The elevation is only set on devices with a large screen.
	 * 
	 * @param elevation
	 *            The elevation, which should be set, in dp as an
	 *            {@link Integer} value. The elevation must be at least 1 and at
	 *            maximum 5
	 * @return True, if the elevation has been set, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public final boolean setNavigationElevation(final int elevation) {
		String[] shadowColors = getResources().getStringArray(
				R.array.navigation_elevation_shadow_colors);
		String[] shadowWidths = getResources().getStringArray(
				R.array.navigation_elevation_shadow_widths);
		ensureAtLeast(elevation, 1, "The elevation must be at least 1");
		ensureAtMaximum(elevation, shadowWidths.length,
				"The elevation must be at maximum " + shadowWidths.length);

		if (navigationShadowView != null) {
			this.navigationElevation = elevation;
			int shadowColor = Color.parseColor(shadowColors[elevation - 1]);
			int shadowWidth = convertDpToPixels(this,
					Integer.valueOf(shadowWidths[elevation - 1]));

			GradientDrawable gradient = new GradientDrawable(
					Orientation.LEFT_RIGHT, new int[] { shadowColor,
							Color.TRANSPARENT });
			navigationShadowView.setBackgroundDrawable(gradient);
			navigationShadowView.getLayoutParams().width = shadowWidth;
			navigationShadowView.requestLayout();

			if (toolbarLarge != null) {
				toolbarLarge.setNavigationElevation(elevation);
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns the elevation of the button bar, which contains the buttons,
	 * which are shown when the activity is used as a wizard.
	 * 
	 * @return The elevation of the button bar in dp as an {@link Integer} value
	 *         or -1, if the activity is not used as a wizard
	 */
	public final int getButtonBarElevation() {
		if (isButtonBarShown()) {
			return buttonBarElevation;
		} else {
			return -1;
		}
	}

	/**
	 * Sets the elevation of the button bar, which contains the buttons, which
	 * are shown when the activity is used as a wizard. The elevation is only
	 * set when the activity is used as a wizard.
	 * 
	 * @param elevation
	 *            The elevation, which should be set, in dp as an
	 *            {@link Integer} value. The elevation must be at least 1 and at
	 *            maximum 5
	 * @return True, if the elevation has been set, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public final boolean setButtonBarElevation(final int elevation) {
		String[] shadowColors = getResources().getStringArray(
				R.array.button_bar_elevation_shadow_colors);
		String[] shadowWidths = getResources().getStringArray(
				R.array.button_bar_elevation_shadow_widths);
		ensureAtLeast(elevation, 1, "The elevation must be at least 1");
		ensureAtMaximum(elevation, shadowWidths.length,
				"The elevation must be at maximum " + shadowWidths.length);

		if (buttonBarShadowView != null) {
			this.buttonBarElevation = elevation;
			int shadowColor = Color.parseColor(shadowColors[elevation - 1]);
			int shadowWidth = convertDpToPixels(this,
					Integer.valueOf(shadowWidths[elevation - 1]));

			GradientDrawable gradient = new GradientDrawable(
					Orientation.BOTTOM_TOP, new int[] { shadowColor,
							Color.TRANSPARENT });
			buttonBarShadowView.setBackgroundDrawable(gradient);
			buttonBarShadowView.getLayoutParams().height = shadowWidth;
			buttonBarShadowView.requestLayout();
			return true;
		}

		return false;
	}

	/**
	 * Returns the elevation of the bread crumb, which is used to show the title
	 * of the currently selected fragment on devices with a large screen, if the
	 * activity's toolbar is not shown.
	 * 
	 * @return The elevation of the bread crumb in dp as an {@link Integer}
	 *         value or -1, if the device has a small screen or the activity's
	 *         toolbar is shown
	 */
	public final int getBreadCrumbElevation() {
		if (getBreadCrumb() != null) {
			return breadCrumbElevation;
		} else {
			return -1;
		}
	}

	/**
	 * Sets the elevation of the bread crumb, which is used to show the title of
	 * the currently selected fragment on devices with a large screen, if the
	 * activity's toolbar is not shown. The elevation is only set when the bread
	 * crumb is shown.
	 * 
	 * @param elevation
	 *            The elevation, which should be set, in dp as an
	 *            {@link Integer} value. The elevation must be at least 1 and at
	 *            maximum 5
	 * @return True, if the elevation has been set, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public final boolean setBreadCrumbElevation(final int elevation) {
		String[] shadowColors = getResources().getStringArray(
				R.array.bread_crumb_elevation_shadow_colors);
		String[] shadowWidths = getResources().getStringArray(
				R.array.bread_crumb_elevation_shadow_widths);
		ensureAtLeast(elevation, 1, "The elevation must be at least 1");
		ensureAtMaximum(elevation, shadowWidths.length,
				"The elevation must be at maximum " + shadowWidths.length);

		if (breadCrumbShadowView != null) {
			this.breadCrumbElevation = elevation;
			int shadowColor = Color.parseColor(shadowColors[elevation - 1]);
			int shadowWidth = convertDpToPixels(this,
					Integer.valueOf(shadowWidths[elevation - 1]));

			GradientDrawable gradient = new GradientDrawable(
					Orientation.TOP_BOTTOM, new int[] { shadowColor,
							Color.TRANSPARENT });
			breadCrumbShadowView.setBackgroundDrawable(gradient);
			breadCrumbShadowView.getLayoutParams().height = shadowWidth;
			breadCrumbShadowView.requestLayout();
			return true;
		}

		return false;
	}

	/**
	 * Returns the background of the view group, which contains all views, which
	 * are shown when a preference header is selected on devices with a large
	 * screen.
	 * 
	 * @return The background of the view group, which contains all views, which
	 *         are shown when a preference header is selected or null, if no
	 *         background has been set or device has a small screen
	 */
	public final Drawable getPreferenceScreenBackground() {
		if (getPreferenceScreenContainer() != null) {
			return getPreferenceScreenContainer().getBackground();
		} else {
			return null;
		}
	}

	/**
	 * Sets the background of the view group, which contains all views, which
	 * are shown when a preference header is selected. The background is only
	 * set on devices with a large screen.
	 * 
	 * @param resourceId
	 *            The resource id of the background, which should be set, as an
	 *            {@link Integer} value. The resource id must correspond to a
	 *            valid drawable resource
	 * @return True, if the background has been set, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public final boolean setPreferenceScreenBackground(final int resourceId) {
		return setPreferenceScreenBackground(getResources().getDrawable(
				resourceId));
	}

	/**
	 * Sets the background color of the view group, which contains all views,
	 * which are shown when a preference header is selected. The background is
	 * only set on devices with a large screen.
	 * 
	 * @param color
	 *            The background color, which should be set, as an
	 *            {@link Integer} value
	 * @return True, if the background has been set, false otherwise
	 */
	public final boolean setPreferenceScreenBackgroundColor(final int color) {
		return setPreferenceScreenBackground(new ColorDrawable(color));
	}

	/**
	 * Sets the background of the view group, which contains all views, which
	 * are shown when a preference header is selected. The background is only
	 * set on devices with a large screen.
	 * 
	 * @param drawable
	 *            The background, which should be set, as an instance of the
	 *            class {@link Drawable} or null, if no background should be set
	 * @return True, if the background has been set, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public final boolean setPreferenceScreenBackground(final Drawable drawable) {
		if (getPreferenceScreenContainer() != null) {
			getPreferenceScreenContainer().setBackgroundDrawable(drawable);
			return true;
		}

		return false;
	}

	/**
	 * Returns the background of the parent view of the fragment, which provides
	 * navigation to each preference header's fragment.
	 * 
	 * @return The background of the parent view of the fragment, which provides
	 *         navigation to each preference header's fragment, as an instance
	 *         of the class {@link Drawable} or null, if no background has been
	 *         set
	 */
	public final Drawable getNavigationBackground() {
		return getPreferenceHeaderParentView().getBackground();
	}

	/**
	 * Sets the background of the parent view of the fragment, which provides
	 * navigation to each preference header's fragment.
	 * 
	 * @param resourceId
	 *            The resource id of the background, which should be set, as an
	 *            {@link Integer} value. The resource id must correspond to a
	 *            valid drawable resource
	 */
	@SuppressWarnings("deprecation")
	public final void setNavigationBackground(final int resourceId) {
		setNavigationBackground(getResources().getDrawable(resourceId));
	}

	/**
	 * Sets the background color of the parent view of the fragment, which
	 * provides navigation to each preference header's fragment.
	 * 
	 * @param color
	 *            The background color, which should be set, as an
	 *            {@link Integer} value
	 */
	public final void setNavigationBackgroundColor(final int color) {
		setNavigationBackground(new ColorDrawable(color));
	}

	/**
	 * Sets the background of the parent view of the fragment, which provides
	 * navigation to each preference header's fragment.
	 * 
	 * @param drawable
	 *            The background, which should be set, as an instance of the
	 *            class {@link Drawable} or null, if no background should be set
	 */
	@SuppressWarnings("deprecation")
	public final void setNavigationBackground(final Drawable drawable) {
		getPreferenceHeaderParentView().setBackgroundDrawable(drawable);
	}

	/**
	 * Returns the background of the button bar, which contains the buttons,
	 * which are shown when the activity is used as a wizard.
	 * 
	 * @return The background of the button bar, which contains the buttons,
	 *         which are shown when the activity is used as a wizard, as an
	 *         instance of the class {@link Drawable} or null, if no background
	 *         has been set or the activity is not used as a wizard
	 */
	public final Drawable getButtonBarBackground() {
		if (getButtonBar() != null) {
			return getButtonBar().getBackground();
		} else {
			return null;
		}
	}

	/**
	 * Sets the background of the button bar, which contains the buttons, which
	 * are shown when the activity is used as a wizard. The background is only
	 * set when the activity is used as a wizard.
	 * 
	 * @param resourceId
	 *            The resource id of the background, which should be set, as an
	 *            {@link Integer} value. The resource id must correspond to a
	 *            valid drawable resource
	 * @return True, if the background has been set, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public final boolean setButtonBarBackground(final int resourceId) {
		return setButtonBarBackground(getResources().getDrawable(resourceId));
	}

	/**
	 * Sets the background color of the button bar, which contains the buttons,
	 * which are shown when the activity is used as a wizard. The background
	 * color is only set when the activity is used as a wizard.
	 * 
	 * @param color
	 *            The background color, which should be set, as an
	 *            {@link Integer} value
	 * @return True, if the background color has been set, false otherwise
	 */
	public final boolean setButtonBarBackgroundColor(final int color) {
		return setButtonBarBackground(new ColorDrawable(color));
	}

	/**
	 * Sets the background of the button bar, which contains the buttons, which
	 * are shown when the activity is used as a wizard. The background is only
	 * set when the activity is used as a wizard.
	 * 
	 * @param drawable
	 *            The background, which should be set, as an instance of the
	 *            class {@link Drawable} or null, if no background should be set
	 * @return True, if the background has been set, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public final boolean setButtonBarBackground(final Drawable drawable) {
		if (getButtonBar() != null) {
			getButtonBar().setBackgroundDrawable(drawable);
			return true;
		}

		return false;
	}

	/**
	 * Returns the background of the bread crumb, which is used to show the
	 * title of the currently selected fragment on devices with a large screen,
	 * if the activity's toolbar is not shown.
	 * 
	 * @return The background of the bread crumb, which is used to show the
	 *         title of the currently selected fragment on devices with a large
	 *         screen, if the activity's toolbar is not shown, as an instance of
	 *         the class {@link Drawable} or null, if no background is set or
	 *         the bread crumb is not shown
	 */
	public final Drawable getBreadCrumbBackground() {
		if (getBreadCrumb() != null) {
			return getBreadCrumb().getBackground();
		} else {
			return null;
		}
	}

	/**
	 * Sets the background of the bread crumb, which is used to show the title
	 * of the currently selected fragment on devices with a large screen, if the
	 * activity's toolbar is not shown. The background is only set when the
	 * bread crumb is shown.
	 * 
	 * @param resourceId
	 *            The resource id of the background, which should be set, as an
	 *            {@link Integer} value. The resource id must correspond to a
	 *            valid drawable resource
	 * @return True, if the background has been set, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public final boolean setBreadCrumbBackground(final int resourceId) {
		return setBreadCrumbBackground(getResources().getDrawable(resourceId));
	}

	/**
	 * Sets the background color of the bread crumb, which is used to show the
	 * title of the currently selected fragment on devices with a large screen,
	 * if the activity's toolbar is not shown. The background color is only set
	 * when the bread crumb is shown.
	 * 
	 * @param color
	 *            The background color, which should be set, as an
	 *            {@link Integer} value
	 * @return True, if the background color has been set, false otherwise
	 */
	public final boolean setBreadCrumbBackgroundColor(final int color) {
		return setBreadCrumbBackground(new ColorDrawable(color));
	}

	/**
	 * Sets the background of the bread crumb, which is used to show the title
	 * of the currently selected fragment on devices with a large screen, if the
	 * activity's toolbar is not shown. The background is only set when the
	 * bread crumb is shown.
	 * 
	 * @param drawable
	 *            The background, which should be set, as an instance of the
	 *            class {@link Drawable} or null, if no background should be set
	 * @return True, if the background has been set, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public final boolean setBreadCrumbBackground(final Drawable drawable) {
		if (getBreadCrumb() != null) {
			getBreadCrumb().setBackgroundDrawable(drawable);
			return true;
		}

		return false;
	}

	/**
	 * Returns the width of the parent view of the fragment, which provides
	 * navigation to each preference header's fragment on devices with a large
	 * screen.
	 * 
	 * @return The width of the parent view of the fragment, which provides
	 *         navigation to each preference header's fragment, in dp as an
	 *         {@link Integer} value or -1, if the device has a small screen
	 */
	public final int getNavigationWidth() {
		if (isSplitScreen()) {
			return convertPixelsToDp(this, getPreferenceHeaderParentView()
					.getLayoutParams().width);
		} else {
			return -1;
		}
	}

	/**
	 * Sets the width of the parent view of the fragment, which provides
	 * navigation to each preference header's fragment. The width is only set on
	 * devices with a large screen.
	 * 
	 * @param width
	 *            The width, which should be set, in dp as an {@link Integer}
	 *            value. The width must be greater than 0
	 * @return True, if the width has been set, false otherwise
	 */
	public final boolean setNavigationWidth(final int width) {
		ensureGreaterThan(width, 0, "The width must be greater than 0");

		if (isSplitScreen()) {
			getPreferenceHeaderParentView().getLayoutParams().width = convertDpToPixels(
					this, width);
			getPreferenceHeaderParentView().requestLayout();

			if (toolbarLarge != null) {
				toolbarLarge.setNavigationWidth(width);
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns, whether the behavior of the navigation icon of the activity's
	 * toolbar is overridden in order to return to the navigation when a
	 * preference header is currently selected on devices with a small screen,
	 * or not.
	 * 
	 * @return True, if the behavior of the navigation icon is overridden, false
	 *         otherwise
	 */
	public final boolean isNavigationIconOverridden() {
		return overrideNavigationIcon;
	}

	/**
	 * Sets, whether the behavior of the navigation icon of the activity's
	 * toolbar should be overridden in order to return to the navigation when a
	 * preference header is currently selected on devices with a small screen,
	 * or not.
	 * 
	 * @param overrideNavigationIcon
	 *            True, if the behavior of the navigation icon should be
	 *            overridden, false otherwise
	 */
	public final void overrideNavigationIcon(
			final boolean overrideNavigationIcon) {
		this.overrideNavigationIcon = overrideNavigationIcon;

		if (isPreferenceHeaderSelected()) {
			if (overrideNavigationIcon) {
				showToolbarNavigationIcon();
			} else {
				hideToolbarNavigationIcon();
			}
		}
	}

	@Override
	public final void onItemClick(final AdapterView<?> parent, final View view,
			final int position, final long id) {
		showPreferenceScreen(getListAdapter().getItem(position), null);
	}

	@Override
	public final void onPreferenceHeaderAdded(
			final PreferenceHeaderAdapter adapter,
			final PreferenceHeader preferenceHeader, final int position) {
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
	public final void onPreferenceHeaderRemoved(
			final PreferenceHeaderAdapter adapter,
			final PreferenceHeader preferenceHeader, final int position) {
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
						selectedPreferenceHeader = getListAdapter().getItem(
								selectedIndex);
					} catch (IndexOutOfBoundsException e) {
						getListView().setItemChecked(selectedIndex - 1, true);
						selectedPreferenceHeader = getListAdapter().getItem(
								selectedIndex - 1);
					}

					showPreferenceScreen(selectedPreferenceHeader, null);
				} else if (selectedIndex > position) {
					getListView().setItemChecked(selectedIndex - 1, true);
					showPreferenceScreen(
							getListAdapter().getItem(selectedIndex - 1), null);
				}
			}
		} else {
			if (currentHeader == preferenceHeader) {
				showPreferenceHeaders();
				hideToolbarNavigationIcon();
				resetTitle();
			}

			updateSavedInstanceState();
		}

		adaptWizardButtons();
	}

	@Override
	public void onFragmentCreated(final Fragment fragment) {
		getListView().setOnItemClickListener(PreferenceActivity.this);
		getListAdapter().addListener(PreferenceActivity.this);

		if (savedInstanceState == null) {
			onCreatePreferenceHeaders();
			initializeSelectedPreferenceHeader();
			handleInitialFragmentIntent();

		} else {
			ArrayList<PreferenceHeader> preferenceHeaders = savedInstanceState
					.getParcelableArrayList(PREFERENCE_HEADERS_EXTRA);
			getListAdapter().addAllItems(preferenceHeaders);
			initializeSelectedPreferenceHeader();
		}

		handleShowButtonBarIntent();
		handleHideNavigationIntent();
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isSplitScreen() && isPreferenceHeaderSelected()
					&& !isNavigationHidden()
					&& !(!isSplitScreen() && isButtonBarShown())) {
				showPreferenceHeaders();
				hideToolbarNavigationIcon();
				resetTitle();
				return true;
			} else if (isButtonBarShown()) {
				if (notifyOnSkip() != null) {
					return super.onKeyDown(keyCode, event);
				}

				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (!isSplitScreen() && isPreferenceHeaderSelected()
					&& isNavigationIconOverridden() && !isNavigationHidden()
					&& !(!isSplitScreen() && isButtonBarShown())) {
				showPreferenceHeaders();
				hideToolbarNavigationIcon();
				resetTitle();
				return true;
			} else if (isButtonBarShown()) {
				if (notifyOnSkip() != null) {
					return super.onOptionsItemSelected(item);
				}

				return true;
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public final void setTitle(final CharSequence title) {
		super.setTitle(title);
		if (toolbarLarge != null) {
			getSupportActionBar().setTitle(null);
			toolbarLarge.setTitle(title);
		} else {
			getSupportActionBar().setTitle(title);
		}
	}

	@Override
	public final void setTitle(final int resourceId) {
		super.setTitle(resourceId);
		if (toolbarLarge != null) {
			getSupportActionBar().setTitle(null);
			toolbarLarge.setTitle(resourceId);
		} else {
			getSupportActionBar().setTitle(resourceId);
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.savedInstanceState = savedInstanceState;
		setContentView(R.layout.preference_activity);
		preferenceHeaderParentView = (ViewGroup) findViewById(R.id.preference_header_parent);
		preferenceScreenParentView = (ViewGroup) findViewById(R.id.preference_screen_parent);
		preferenceScreenContainer = (ViewGroup) findViewById(R.id.preference_screen_container);
		navigationShadowView = findViewById(R.id.navigation_shadow_view);
		preferenceHeaderFragment = new PreferenceHeaderFragment();
		preferenceHeaderFragment.addFragmentListener(this);
		initializeToolbar();

		if (toolbarLarge == null) {
			breadCrumb = (TextView) findViewById(R.id.bread_crumb_view);
			breadCrumbShadowView = findViewById(R.id.bread_crumb_shadow_view);
		}

		overrideNavigationIcon(true);
		setNavigationElevation(DEFAULT_NAVIGATION_ELEVATION);
		setBreadCrumbElevation(DEFAULT_BREAD_CRUMB_ELEVATION);
		obtainStyledAttributes();

		if (savedInstanceState != null) {
			restoredPreferenceScreenFragment = getFragmentManager()
					.getFragment(savedInstanceState,
							PREFERENCE_SCREEN_FRAGMENT_EXTRA);
		}

		showPreferenceHeaders();
	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		Bundle bundle = savedInstanceState.getBundle(CURRENT_BUNDLE_EXTRA);
		CharSequence title = savedInstanceState
				.getCharSequence(CURRENT_TITLE_EXTRA);
		CharSequence shortTitle = savedInstanceState
				.getCharSequence(CURRENT_SHORT_TITLE_EXTRA);
		PreferenceHeader currentPreferenceHeader = savedInstanceState
				.getParcelable(CURRENT_PREFERENCE_HEADER_EXTRA);

		if (currentPreferenceHeader != null) {
			preferenceScreenFragment = restoredPreferenceScreenFragment;
			showPreferenceScreen(currentPreferenceHeader, bundle, false);
			showBreadCrumb(title, shortTitle);

			if (isSplitScreen()) {
				int selectedIndex = getListAdapter().indexOf(
						currentPreferenceHeader);

				if (selectedIndex != -1) {
					getListView().setItemChecked(selectedIndex, true);
				}
			}
		} else {
			showPreferenceHeaders();
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBundle(CURRENT_BUNDLE_EXTRA, currentBundle);
		outState.putCharSequence(CURRENT_TITLE_EXTRA, currentTitle);
		outState.putCharSequence(CURRENT_SHORT_TITLE_EXTRA, currentShortTitle);
		outState.putParcelable(CURRENT_PREFERENCE_HEADER_EXTRA, currentHeader);
		outState.putParcelableArrayList(PREFERENCE_HEADERS_EXTRA,
				getListAdapter().getAllItems());

		if (preferenceScreenFragment != null) {
			getFragmentManager().putFragment(outState,
					PREFERENCE_SCREEN_FRAGMENT_EXTRA, preferenceScreenFragment);
		}
	}

	/**
	 * The method, which is invoked, when the preference headers should be
	 * created. This method may be overridden by implementing subclasses to add
	 * the preference headers at the activity's start.
	 */
	protected void onCreatePreferenceHeaders() {
		return;
	}

}