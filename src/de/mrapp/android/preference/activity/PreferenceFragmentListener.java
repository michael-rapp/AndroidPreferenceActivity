/*
 * AndroidPreferenceActivity Copyright 2014 - 2015 Michael Rapp
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

import android.app.Fragment;

/**
 * Defines the interface, a class, which should be notified when the currently
 * shown preference fragment of a {@link PreferenceActivity} has been changed,
 * must implement.
 * 
 * @author Michael Rapp
 *
 * @since 2.1.1
 */
public interface PreferenceFragmentListener {

	/**
	 * The method, which is invoked, when a preference fragment has been shown.
	 * 
	 * @param position
	 *            The position of the preference header, the fragment, which has
	 *            been shown, belongs to, as an {@link Integer} value
	 * @param preferenceHeader
	 *            The preference header, the fragment, which has been shown,
	 *            belongs to, as an instance of the class
	 *            {@link PreferenceHeader}
	 * @param fragment
	 *            The fragment, which has been shown, as an instance of the
	 *            class {@link Fragment}
	 */
	void onPreferenceFragmentShown(int position,
			PreferenceHeader preferenceHeader, Fragment fragment);

	/**
	 * The method, which is invoked, when a previously preference fragment has
	 * been hidden. This method is only invoked, when the preference headers are
	 * shown on a device with a small screen or when all preference headers have
	 * been removed.
	 */
	void onPreferenceFragmentHidden();

}