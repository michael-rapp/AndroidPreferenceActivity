/*
 * Copyright 2014 - 2018 Michael Rapp
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
package de.mrapp.android.preference.activity.animation;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Defines the interface, a class, which should be notified about the internal state of a {@link
 * HideViewOnScrollAnimation}, must implement.
 *
 * @author Michael Rapp
 * @since 2.0.3
 */
public interface HideViewOnScrollAnimationListener {

    /**
     * The method, which is invoked, when the list view, which is observed by the animation, is
     * scrolling downwards.
     *
     * @param animation
     *         The observer animation, as an instance of the class {@link
     *         HideViewOnScrollAnimation}
     * @param animatedView
     *         The view, which is animated by the observed animation, as an instance of the class
     *         {@link View}
     * @param scrollPosition
     *         The current scroll position of the list view's first item in pixels as an {@link
     *         Integer} value
     */
    void onScrollingDown(@NonNull HideViewOnScrollAnimation animation, @NonNull View animatedView,
                         int scrollPosition);

    /**
     * The method, which is invoked, when the list view, which is observed by the animation, is
     * scrolling upwards.
     *
     * @param animation
     *         The observer animation, as an instance of the class {@link
     *         HideViewOnScrollAnimation}
     * @param animatedView
     *         The view, which is animated by the observed animation, as an instance of the class
     *         {@link View}
     * @param scrollPosition
     *         The current scroll position of the list view's first item in pixels as an {@link
     *         Integer} value
     */
    void onScrollingUp(@NonNull HideViewOnScrollAnimation animation, @NonNull View animatedView,
                       int scrollPosition);

}