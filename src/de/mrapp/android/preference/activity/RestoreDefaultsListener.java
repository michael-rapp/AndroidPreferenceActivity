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

/**
 * Defines the interface, a class, which should be notified, when the default
 * values of the preferences, which belong to a {@link PreferenceFragment},
 * should be restored.
 * 
 * @author Michael Rapp
 * 
 * @since 1.1.0
 */
public interface RestoreDefaultsListener {

	/**
	 * The method, which is invoked, when the default values of the preferences,
	 * which belong to a specific preference fragment, should be restored.
	 * 
	 * @param fragment
	 *            The fragment, whose preferences' default values should be
	 *            restored, as an instance of the class
	 *            {@link PreferenceFragment}
	 * @return True, if restoring the preferences' default values should be
	 *         proceeded, false otherwise
	 */
	boolean onRestoreDefaultValues(PreferenceFragment fragment);

}