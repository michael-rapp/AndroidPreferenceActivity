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
package de.mrapp.android.preference;

import java.util.Collection;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import de.mrapp.android.preference.adapter.PreferenceHeaderAdapter;
import de.mrapp.android.preference.fragment.FragmentListener;
import de.mrapp.android.preference.fragment.PreferenceHeaderFragment;
import de.mrapp.android.preference.parser.PreferenceHeaderParser;

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
public abstract class PreferenceActivity extends Activity implements
		FragmentListener, OnItemClickListener {

	/**
	 * The name of the extra, which is used to save the class name of the
	 * fragment, which is currently shown, within a bundle.
	 */
	private static final String CURRENT_FRAGMENT_EXTRA = PreferenceActivity.class
			.getSimpleName() + "::CurrentFragment";

	/**
	 * The fragment, which contains the preference headers and provides the
	 * navigation to each header's fragment.
	 */
	private PreferenceHeaderFragment preferenceHeaderFragment;

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
	 * The full qualified class name of the fragment, which is currently shown
	 * or null, if no preference header is currently selected.
	 */
	private String currentFragment;

	/**
	 * True, if the back button of the action bar should be shown, false
	 * otherwise.
	 */
	private boolean displayHomeAsUp;

	/**
	 * Shows the fragment, which corresponds to a specific preference header.
	 * 
	 * @param preferenceHeader
	 *            The preference header, the fragment, which should be shown,
	 *            corresponds to, as an instance of the class
	 *            {@link PreferenceHeader}. The preference header may not be
	 *            null
	 */
	private void showPreferenceScreen(final PreferenceHeader preferenceHeader) {
		currentFragment = preferenceHeader.getFragment();
		showPreferenceScreen(currentFragment);
	}

	/**
	 * Shows the fragment, which corresponds to a specific class name.
	 * 
	 * @param fragmentName
	 *            The full qualified class name of the fragment, which should be
	 *            shown, as a {@link String}
	 */
	private void showPreferenceScreen(final String fragmentName) {
		Fragment fragment = Fragment.instantiate(this, fragmentName);
		replacePreferenceHeaderFragment(fragment,
				FragmentTransaction.TRANSIT_FRAGMENT_FADE);

		if (!isSplitScreen() && getActionBar() != null) {
			showActionBarBackButton();
		}
	}

	/**
	 * Shows the fragment, which provides the navigation to each preference
	 * header's fragment.
	 */
	private void showPreferenceHeaders() {
		if (currentFragment != null) {
			replacePreferenceHeaderFragment(preferenceHeaderFragment,
					FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
			currentFragment = null;
		} else {
			replacePreferenceHeaderFragment(preferenceHeaderFragment, 0);
		}

	}

	/**
	 * Replaces the fragment, which is currently contained by the parent view of
	 * the fragment, which provides the navigation to each preference header's
	 * fragment, by an other fragment.
	 * 
	 * @param fragment
	 *            The fragment, which should replace the current fragment, as an
	 *            instance of the class {@link Fragment}. The fragment may not
	 *            be null
	 * @param transition
	 *            The transition, which should be shown when replacing the
	 *            fragment, as an {@link Integer} value or 0, if no transition
	 *            should be shown
	 */
	private void replacePreferenceHeaderFragment(final Fragment fragment,
			final int transition) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.setTransition(transition);
		transaction.replace(R.id.preference_header_parent, fragment);
		transaction.commit();
	}

	/**
	 * Shows the back button in the activity's action bar.
	 */
	private void showActionBarBackButton() {
		if (getActionBar() != null) {
			displayHomeAsUp = isDisplayHomeAsUpEnabled();
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	/**
	 * Hides the back button in the activity's action bar, if it was not
	 * previously shown.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void hideActionBarBackButton() {
		if (getActionBar() != null && !displayHomeAsUp) {
			getActionBar().setDisplayHomeAsUpEnabled(false);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				getActionBar().setHomeButtonEnabled(false);
			}
		}
	}

	/**
	 * Returns, whether the back button of the action bar is currently shown, or
	 * not.
	 * 
	 * @return True, if the back button of the action bar is currently shown,
	 *         false otherwise
	 */
	private boolean isDisplayHomeAsUpEnabled() {
		if (getActionBar() != null) {
			return (getActionBar().getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) == ActionBar.DISPLAY_HOME_AS_UP;
		}

		return false;
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
	 * resource.
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
	 * Adds a new preference header.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which should be added, as an instance
	 *            of the class {@link PreferenceHeader}. The preference header
	 *            may not be null
	 * @return
	 */
	public final void addPreferenceHeader(
			final PreferenceHeader preferenceHeader) {
		getListAdapter().addItem(preferenceHeader);
	}

	/**
	 * Adds all preference headers, which are contained by a specific
	 * collection.
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
	 * Removes a specific preference header.
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
	 * Removes all preference headers.
	 */
	public final void clearPreferenceHeaders() {
		getListAdapter().clear();
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

	@Override
	public final void onFragmentCreated(final Fragment fragment) {
		onCreatePreferenceHeaders();
		getListView().setOnItemClickListener(this);

		if (isSplitScreen()) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public final void onItemClick(final AdapterView<?> parent, final View view,
			final int position, final long id) {
		showPreferenceScreen(getListAdapter().getItem(position));
	}

	@Override
	public final boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !isSplitScreen()
				&& currentFragment != null) {
			showPreferenceHeaders();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == android.R.id.home && !isSplitScreen()
				&& currentFragment != null) {
			showPreferenceHeaders();
			hideActionBarBackButton();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preference_activity);
		preferenceHeaderParentView = (ViewGroup) findViewById(R.id.preference_header_parent);
		preferenceScreenParentView = (ViewGroup) findViewById(R.id.preference_screen_parent);
		preferenceHeaderFragment = new PreferenceHeaderFragment();
		preferenceHeaderFragment.addFragmentListener(this);
		showPreferenceHeaders();
	}

	@Override
	protected final void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(CURRENT_FRAGMENT_EXTRA, currentFragment);
	}

	@Override
	protected final void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		currentFragment = savedInstanceState.getString(CURRENT_FRAGMENT_EXTRA);

		if (currentFragment != null) {
			showPreferenceScreen(currentFragment);
		}
	}

	/**
	 * The method, which is invoked, when the preference headers should be
	 * created. This method has to be overridden by implementing subclasses to
	 * add the preference headers.
	 */
	protected abstract void onCreatePreferenceHeaders();

}