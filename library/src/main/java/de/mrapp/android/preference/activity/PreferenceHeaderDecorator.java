/*
 * AndroidPreferenceActivity Copyright 2014 - 2015 Michael Rapp
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
package de.mrapp.android.preference.activity;

import android.support.annotation.NonNull;
import android.view.View;

import de.mrapp.android.preference.activity.adapter.PreferenceHeaderAdapter.ViewHolder;

/**
 * Defines the interface, a class, which should be able to modify the visualization of preference
 * headers, must implement.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public interface PreferenceHeaderDecorator {

    /**
     * The method, which is invoked, when the decorator is applied to modify the visualization of a
     * specific preference header.
     *
     * @param position
     *         The position of the preference header, which should be visualized, as an {@link
     *         Integer} value
     * @param preferenceHeader
     *         The preference header, which should be visualized, as an instance of the class {@link
     *         PreferenceHeader}. The preference header may not be null
     * @param view
     *         The view, which is used to visualize the preference header, as an instance of the
     *         class {@link View}. The view may not be null
     * @param viewHolder
     *         The view holder, which contains the child views of the view, which is used to
     *         visualize the preference header, as an instance of the class {@link ViewHolder}. The
     *         view holder may not be null
     */
    void onApplyDecorator(int position, @NonNull PreferenceHeader preferenceHeader,
                          @NonNull View view, @NonNull ViewHolder viewHolder);

}