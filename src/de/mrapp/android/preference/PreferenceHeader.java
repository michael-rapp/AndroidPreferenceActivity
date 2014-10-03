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

import android.content.Intent;
import android.os.Bundle;

/**
 * A navigation item, which categorizes multiple preferences.
 * 
 * @author Michael Rapp
 * 
 * @since 1.0.0
 */
public class PreferenceHeader {

	/**
	 * The title of the navigation item.
	 */
	private CharSequence title;

	/**
	 * The summary, which describes the preferences, which belong to the
	 * navigation item.
	 */
	private CharSequence summary;

	/**
	 * The text, which is shown as the title in the navigation item's bread
	 * crumb.
	 */
	private CharSequence breadCrumbTitle;

	/**
	 * The text, which is shown as the short title in the navigation item's
	 * bread crumb.
	 */
	private CharSequence breadCrumbShortTitle;

	/**
	 * The resource id of the navigation item's icon.
	 */
	private int iconId;

	/**
	 * The full qualified class name of the fragment, which is shown, when the
	 * navigation item is selected.
	 */
	private String fragment;

	/**
	 * The intent, which is launched, when the navigation item is selected.
	 */
	private Intent intent;

	/**
	 * Optional parameters of the intent, which is launched, when the navigation
	 * item is selected.
	 */
	private Bundle extras;

	/**
	 * Creates a new navigation item, which categorizes multiple preferences.
	 * 
	 * @param title
	 *            The title of the navigation item as an instance of the class
	 *            {@link CharSequence}. The title may neither be null, nor empty
	 * @param fragment
	 *            The full qualified class name of the fragment, which should be
	 *            shown, when the navigation item is selected, as a
	 *            {@link String}. The class name may neither be null, nor empty
	 */
	public PreferenceHeader(final CharSequence title, final String fragment) {
		setTitle(title);
		setFragment(fragment);
	}

	/**
	 * Returns the title of the navigation item.
	 * 
	 * @return The title of the navigation item as an instance of the class
	 *         {@link CharSequence}. The title may neither be null, nor empty
	 */
	public final CharSequence getTitle() {
		return title;
	}

	/**
	 * Sets the title of the navigation item.
	 * 
	 * @param title
	 *            The title, which should be set, as an instance of the class
	 *            {@link CharSequence}. The title may neither be null, nor empty
	 */
	public final void setTitle(final CharSequence title) {
		this.title = title;
	}

	/**
	 * Returns the summary, which describes the preferences, which belong to the
	 * navigation item.
	 * 
	 * @return The summary, which describes the preferences, which belong to the
	 *         navigation item, as an instance of the class {@link CharSequence}
	 *         or null, if no summary has been set
	 */
	public final CharSequence getSummary() {
		return summary;
	}

	/**
	 * Sets the summary, which describes the preferences, which belong to the
	 * navigation item.
	 * 
	 * @param summary
	 *            The summary, which should be set, as an instance of the class
	 *            {@link CharSequence} or null, if no summary should be set
	 */
	public final void setSummary(final CharSequence summary) {
		this.summary = summary;
	}

	/**
	 * Returns the text, which is shown as the title in the navigation item's
	 * bread crumb.
	 * 
	 * @return The text, which is shown as the title in the navigation item's
	 *         bread crumb, as an instance of the class {@link CharSequence} or
	 *         null, if no title has been set
	 */
	public final CharSequence getBreadCrumbTitle() {
		return breadCrumbTitle;
	}

	/**
	 * Sets the text, which should be shown as the title in the navigation
	 * item's bread crumb.
	 * 
	 * @param breadCrumbTitle
	 *            The title, which should be set, as an instance of the class
	 *            {@link CharSequence} or null, if no title should be set
	 */
	public final void setBreadCrumbTitle(final CharSequence breadCrumbTitle) {
		this.breadCrumbTitle = breadCrumbTitle;
	}

	/**
	 * Returns the text, which is shown as the short title of the navigation
	 * item's bread crumb.
	 * 
	 * @return The text, which is shown as the short title of the navigation
	 *         item's bread crumb, as an instance of the class
	 *         {@link CharSequence} or null, if no short title has been set
	 */
	public final CharSequence getBreadCrumbShortTitle() {
		return breadCrumbShortTitle;
	}

	/**
	 * Sets the text, which should be shown as the short title of the navigation
	 * item's bread crumb.
	 * 
	 * @param breadCrumbShortTitle
	 *            The short title, which should be set, as an instance of the
	 *            class {@link CharSequence} or null, if no short title should
	 *            be set
	 */
	public final void setBreadCrumbShortTitle(
			final CharSequence breadCrumbShortTitle) {
		this.breadCrumbShortTitle = breadCrumbShortTitle;
	}

	/**
	 * Returns the resource id of the navigation item's icon.
	 * 
	 * @return The resource id of the navigation item's icon as an
	 *         {@link Integer} value or 0, if no resource id has been set
	 */
	public final int getIconId() {
		return iconId;
	}

	/**
	 * Sets the resource id of the navigation item's icon.
	 * 
	 * @param iconId
	 *            The resource id, which should be set, as an {@link Integer}
	 *            value or 0, if no resource id should be set
	 */
	public final void setIconId(final int iconId) {
		this.iconId = iconId;
	}

	/**
	 * Returns the full qualified class name of the fragment, which is shown,
	 * when the navigation item is selected.
	 * 
	 * @return The full qualified class name of the fragment, which is shown,
	 *         when the navigation item is selected. The class name may neither
	 *         be null, nor empty
	 */
	public final String getFragment() {
		return fragment;
	}

	/**
	 * Sets the full qualified class name of the fragment, which should be
	 * shown, when the navigation item is selected.
	 * 
	 * @param fragment
	 *            The class name, which should be set, as a {@link String}. The
	 *            class name may neither be null, nor empty
	 */
	public final void setFragment(final String fragment) {
		this.fragment = fragment;
	}

	/**
	 * Returns the intent, which is launched, when the navigation item is
	 * selected.
	 * 
	 * @return The intent, which is launched, when the navigation item is
	 *         selected, as an instance of the class {@link Intent} or null, if
	 *         no intent has been set
	 */
	public final Intent getIntent() {
		return intent;
	}

	/**
	 * Sets the intent, which should be launched, when the navigation item is
	 * selected.
	 * 
	 * @param intent
	 *            The intent, which should be set, as an instance of the class
	 *            {@link Integer} or null, if no intent should be set
	 */
	public final void setIntent(final Intent intent) {
		this.intent = intent;
	}

	/**
	 * Returns the optional parameters of the intent, which is launched, when
	 * the navigation item is selected.
	 * 
	 * @return The parameters of the intent, which is launched, when the
	 *         navigation item is selected, as an instance of the class
	 *         {@link Bundle} or null, if no parameters have been set
	 */
	public final Bundle getExtras() {
		return extras;
	}

	/**
	 * Sets the optional parameters of the intent, which should be launched,
	 * when the navigation item is selected.
	 * 
	 * @param extras
	 *            The parameters, which should be set, as an instance of the
	 *            class {@link Bundle} or null, if no parameters should be set
	 */
	public final void setExtras(final Bundle extras) {
		this.extras = extras;
	}

}