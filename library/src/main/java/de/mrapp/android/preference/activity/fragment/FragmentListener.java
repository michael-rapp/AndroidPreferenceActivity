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
package de.mrapp.android.preference.activity.fragment;

import android.app.Fragment;
import android.support.annotation.NonNull;

/**
 * The interface, which must be implemented by all classes, which should be notified on events
 * concerning fragments.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public interface FragmentListener {

    /**
     * The method, which is invoked, when the fragment has been created.
     *
     * @param fragment
     *         The observed fragment as an instance of the class {@link Fragment}
     */
    void onFragmentCreated(@NonNull Fragment fragment);

}