/*
 * AndroidPreferenceActivity Copyright 2014 - 2016 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package de.mrapp.android.preference.activity.example.dialog;

import de.mrapp.android.preference.activity.PreferenceActivity;

/**
 * Defines the interface, a class, which should be notified, when the user closes a dialog, which
 * allows to remove a specific preference header from a {@link PreferenceActivity}, confirmatively.
 *
 * @author Michael Rapp
 */
public interface RemovePreferenceHeaderDialogListener {

    /**
     * The method, which is invoked when the user closes the dialog confirmatively in order to
     * remove the preference header, which belongs to a specific position.
     *
     * @param position
     *         The position of the preference header, which should be removed, as an {@link Integer}
     *         value
     */
    void onRemovePreferenceHeader(int position);

}