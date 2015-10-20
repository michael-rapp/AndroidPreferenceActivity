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