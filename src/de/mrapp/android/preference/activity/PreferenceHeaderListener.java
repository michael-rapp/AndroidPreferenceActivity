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

import android.app.Fragment;

/**
 * Defines the interface, a class, which should be notified when a preference
 * header of a {@link PreferenceActivity} is selected, must implement.
 * 
 * @author Michael Rapp
 *
 * @since 2.1.1
 */
public interface PreferenceHeaderListener {

	/**
	 * The method, which is invoked, when a preference header has been selected.
	 * 
	 * @param position
	 *            The position of the preference header, which has been
	 *            selected, as an {@link Integer} value
	 * @param preferenceHeader
	 *            The preference header, which has been selected, as an instance
	 *            of the class {@link PreferenceHeader}
	 * @param fragment
	 *            The fragment, which belongs to the preference header, which
	 *            has been selected, as an instance of the class
	 *            {@link Fragment}
	 */
	void onPreferenceHeaderSelected(int position,
			PreferenceHeader preferenceHeader, Fragment fragment);

}