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

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import de.mrapp.android.util.datastructure.ListenerList;

import static de.mrapp.android.util.Condition.ensureGreater;
import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * A scroll listener, which allows to animate a view to become hidden or shown depending on the
 * observed list view's scrolling direction.
 *
 * @author Michael Rapp
 * @since 2.0.0
 */
public class HideViewOnScrollAnimation extends RecyclerView.OnScrollListener {

    /**
     * Contains all possible directions, which can be used to translate the animated view in order
     * hide it.
     */
    public enum Direction {

        /**
         * If the view should be translated upwards.
         */
        UP,

        /**
         * If the view should be translated downwards.
         */
        DOWN

    }

    /**
     * The default duration of the animation, which is used to show or hide the view, in
     * milliseconds.
     */
    private static final long DEFAULT_ANIMATION_DURATION = 300L;

    /**
     * The view, which is animated by the listener.
     */
    private final View animatedView;

    /**
     * The direction, which is used to translate the view in order to hide it.
     */
    private final Direction direction;

    /**
     * The duration of the animation, which is used to show or hide the view, in milliseconds.
     */
    private final long animationDuration;

    /**
     * True, if the observed recycler view was scrolling up, when the listener was called the last
     * time.
     */
    private Boolean scrollingUp;

    /**
     * True, if the animated view is currently hidden, false otherwise.
     */
    private boolean hidden;

    /**
     * The initial position of the view, which is animated by the listener.
     */
    private float initialPosition = -1.0f;

    /**
     * A set, which contains the listeners, which should be notified about the animation's internal
     * state.
     */
    private ListenerList<HideViewOnScrollAnimationListener> listeners;

    /**
     * Notifies all listeners, which have been registered to be notified about the animation's
     * internal state, when the observed list view is scrolling downwards.
     *
     * @param animatedView
     *         The view, which is animated by the observed animation, as an instance of the class
     *         {@link View}
     * @param scrollPosition
     *         The current scroll position of the list view's first item in pixels as an {@link
     *         Integer} value
     */
    private void notifyOnScrollingDown(@NonNull final View animatedView, final int scrollPosition) {
        for (HideViewOnScrollAnimationListener listener : listeners) {
            listener.onScrollingDown(this, animatedView, scrollPosition);
        }
    }

    /**
     * Notifies all listeners, which have been registered to be notified about the animation's
     * internal state, when the observed list view is scrolling upwards.
     *
     * @param animatedView
     *         The view, which is animated by the observed animation, as an instance of the class
     *         {@link View}
     * @param scrollPosition
     *         The current scroll position of the list view's first item in pixels as an {@link
     *         Integer} value
     */
    private void notifyOnScrollingUp(@NonNull final View animatedView, final int scrollPosition) {
        for (HideViewOnScrollAnimationListener listener : listeners) {
            listener.onScrollingUp(this, animatedView, scrollPosition);
        }
    }

    /**
     * The method, which is invoked, when the observed list view is scrolling upwards.
     */
    private void onScrollingUp() {
        if (hidden) {
            hidden = false;

            if (animatedView.getAnimation() == null) {
                ObjectAnimator animator = createAnimator(false);
                animator.start();
            }
        }
    }

    /**
     * The method, which is invoked, when the observed list view is scrolling downwards.
     */
    private void onScrollingDown() {
        if (!hidden) {
            hidden = true;

            if (animatedView.getAnimation() == null) {
                ObjectAnimator animator = createAnimator(true);
                animator.start();
            }
        }
    }

    /**
     * Creates and returns an animator, which allows to translate the animated view to become shown
     * or hidden.
     *
     * @param hide
     *         True, if the view should become hidden, false otherwise
     * @return The animator, which has been created, as an instance of the class {@link
     * ObjectAnimator}
     */
    private ObjectAnimator createAnimator(final boolean hide) {
        if (initialPosition == -1.0f) {
            initialPosition = animatedView.getY();
        }

        float targetPosition = hide ? initialPosition - animatedView.getHeight() : initialPosition;

        if (direction == Direction.DOWN) {
            targetPosition = hide ? initialPosition + animatedView.getHeight() : initialPosition;
        }

        ObjectAnimator animation =
                ObjectAnimator.ofFloat(animatedView, "y", animatedView.getY(), targetPosition);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(animationDuration);
        return animation;
    }

    /**
     * Creates a new scroll listener, which allows to animate a view to become hidden or shown
     * depending on the observed list view's scrolling direction.
     *
     * @param view
     *         The view, which should be animated by the listener, as an instance of the class
     *         {@link View}. The view may not be null
     * @param direction
     *         The direction, which should be be used to translate the view in order to hide it, as
     *         a value of the enum {@link Direction}. The direction may either be <code>UP</code> or
     *         <code>DOWN</code>
     */
    public HideViewOnScrollAnimation(@NonNull final View view, @NonNull final Direction direction) {
        this(view, direction, DEFAULT_ANIMATION_DURATION);
    }

    /**
     * Creates a new scroll listener, which allows to animate a view to become hidden or shown
     * depending on the observed list view's scrolling direction.
     *
     * @param view
     *         The view, which should be animated by the listener, as an instance of the class
     *         {@link View}. The view may not be null
     * @param direction
     *         The direction, which should be be used to translate the view in order to hide it, as
     *         a value of the enum {@link Direction}. The direction may either be <code>UP</code> or
     *         <code>DOWN</code>
     * @param animationDuration
     *         The duration of the animation, which is used to show or hide the view, in
     *         milliseconds as a {@link Long} value. The duration must be greater than 0
     */
    public HideViewOnScrollAnimation(@NonNull final View view, @NonNull final Direction direction,
                                     final long animationDuration) {
        ensureNotNull(view, "The view may not be null");
        ensureNotNull(direction, "The direction may not be null");
        ensureGreater(animationDuration, 0, "The animation duration must be greater than 0");
        this.animatedView = view;
        this.direction = direction;
        this.animationDuration = animationDuration;
        this.listeners = new ListenerList<>();
    }

    /**
     * Shows the view.
     */
    public final void showView() {
        if (animatedView.getAnimation() != null) {
            animatedView.getAnimation().cancel();
        }

        ObjectAnimator animator = createAnimator(false);
        animator.start();
    }

    /**
     * Hides the view.
     */
    public final void hideView() {
        if (animatedView.getAnimation() != null) {
            animatedView.getAnimation().cancel();
        }

        ObjectAnimator animator = createAnimator(true);
        animator.start();
    }

    /**
     * Returns the view, which is animated by the listener.
     *
     * @return The view, which is animated by the listener as an instance of the class {@link View}
     */
    public final View getView() {
        return animatedView;
    }

    /**
     * Returns the direction, which should be be used to translate the view in order to hide it.
     *
     * @return The direction, which should be be used to translate the view in order to hide it, as
     * a value of the enum {@link Direction}. The direction may either be <code>UP</code> or
     * <code>DOWN</code>
     */
    public final Direction getDirection() {
        return direction;
    }

    /**
     * Returns the duration of the animation, which is used to show or hide the view.
     *
     * @return The duration of the animation, which is used to show or hide the view, in
     * milliseconds as a {@link Long} value
     */
    public final long getAnimationDuration() {
        return animationDuration;
    }

    /**
     * Adds a new listener, which should be notified about the animation's internal state, to the
     * animation.
     *
     * @param listener
     *         The listener, which should be added, as an instance of the type {@link
     *         HideViewOnScrollAnimationListener}. The listener may not be null
     */
    public final void addListener(@NonNull final HideViewOnScrollAnimationListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        listeners.add(listener);
    }

    /**
     * Removes a specific listener, which should not be notified about the animation's internal
     * state, from the animation.
     *
     * @param listener
     *         The listener, which should be removed, as an instance of the tpye {@link
     *         HideViewOnScrollAnimationListener}. The listener may not be null
     */
    public final void removeListener(@NonNull final HideViewOnScrollAnimationListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        listeners.remove(listener);
    }

    @Override
    public final void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
        if (ViewCompat.isLaidOut(animatedView)) {
            boolean isScrollingUp = dy < 0;

            if (this.scrollingUp == null || this.scrollingUp != isScrollingUp) {
                this.scrollingUp = isScrollingUp;

                if (scrollingUp) {
                    onScrollingUp();
                    notifyOnScrollingUp(animatedView, dy);
                } else {
                    onScrollingDown();
                    notifyOnScrollingDown(animatedView, dy);
                }
            }
        }
    }

}