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
package de.mrapp.android.preference.activity.adapter;

import de.mrapp.android.preference.activity.PreferenceHeader;

/**
 * Defines the interface, a class, which should be notified, when the underlying
 * data of a {@link PreferenceHeaderAdapter} has been changed, must implement.
 * 
 * @author Michael Rapp
 *
 * @since 1.0.0
 */
public interface AdapterListener {

	/**
	 * The method, which is invoked, when a preference header has been added to
	 * the adapter.
	 * 
	 * @param adapter
	 *            The obtained adapter as an instance of the class
	 *            {@link PreferenceHeaderAdapter}
	 * @param preferenceHeader
	 *            The preference header, which has been added to the adapter, as
	 *            an instance of the class {@link PreferenceHeader}
	 * @param position
	 *            The position of the preference header, which has been added,
	 *            as an {@link Integer} value
	 */
	void onPreferenceHeaderAdded(PreferenceHeaderAdapter adapter,
			PreferenceHeader preferenceHeader, int position);

	/**
	 * The method, which is invoked, when a preference header has been removed
	 * from the adapter.
	 * 
	 * @param adapter
	 *            The obtained adapter as an instance of the class
	 *            {@link PreferenceHeaderAdapter}
	 * @param preferenceHeader
	 *            The preference header, which has been removed from the
	 *            adapter, as an instance of the class {@link PreferenceHeader}
	 * @param position
	 *            The position of the preference header, which has been removed,
	 *            as an {@link Integer} value
	 */
	void onPreferenceHeaderRemoved(PreferenceHeaderAdapter adapter,
			PreferenceHeader preferenceHeader, int position);

}