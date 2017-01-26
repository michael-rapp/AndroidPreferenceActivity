/*
 * Copyright 2014 - 2017 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
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