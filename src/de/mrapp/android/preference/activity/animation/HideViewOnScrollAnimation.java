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
package de.mrapp.android.preference.activity.animation;

import static de.mrapp.android.preference.activity.util.Condition.ensureNotNull;
import static de.mrapp.android.preference.activity.util.Condition.ensureGreaterThan;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * A scroll listener, which allows to animate a view to become hidden or shown
 * depending on the observed list view's scrolling direction.
 * 
 * @author Michael Rapp
 * 
 * @since 2.0.0
 */
public class HideViewOnScrollAnimation extends Animation implements
		OnScrollListener {

	/**
	 * Contains all possible directions, which can be used to translate the
	 * animated view in order hide it.
	 */
	public enum Direction {

		/**
		 * If the view should be translated upwards.
		 */
		UP,

		/**
		 * If the view should be translated downwards.
		 */
		DOWN;

	}

	/**
	 * The default duration of the animation, which is used to show or hide the
	 * view, in milliseconds.
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
	 * The duration of the animation, which is used to show or hide the view, in
	 * milliseconds.
	 */
	private final long animationDuration;

	/**
	 * The observed list view's scroll position, when the listener was called
	 * the last time.
	 */
	private int oldPosition;

	/**
	 * The item, which was visible at top of the observed list view, when the
	 * listener was called the last time.
	 */
	private int oldFirstVisibleItem;

	/**
	 * True, if the observed list view was scrolled down, when the listener was
	 * called the last time.
	 */
	private boolean scrollingDown;

	/**
	 * The initial position of the view, which is animated by the listener.
	 */
	private float initialPosition;

	/**
	 * The method, which is invoked, when the observed list view is scrolling
	 * up.
	 */
	private void onUpScrolling() {
		if (scrollingDown) {
			scrollingDown = false;

			if (animatedView.getAnimation() == null) {
				ObjectAnimator animator = createAnimator();
				animator.start();
			}
		}
	}

	/**
	 * The method, which is invoked, when the observed list view is scrolling
	 * down.
	 */
	private void onDownScrolling() {
		if (!scrollingDown) {
			scrollingDown = true;

			if (animatedView.getAnimation() == null) {
				ObjectAnimator animator = createAnimator();
				animator.start();
			}
		}
	}

	/**
	 * Creates and returns an animator, which allows to translate the animated
	 * view to become shown or hidden.
	 * 
	 * @return The animator, which has been created, as an instance of the class
	 *         {@link ObjectAnimator}
	 */
	private ObjectAnimator createAnimator() {
		if (initialPosition == 0) {
			initialPosition = animatedView.getY();
		}

		float targetPosition = scrollingDown ? initialPosition
				- animatedView.getHeight() : initialPosition;

		if (direction == Direction.DOWN) {
			targetPosition = scrollingDown ? initialPosition
					+ animatedView.getHeight() : initialPosition;
		}

		ObjectAnimator animation = ObjectAnimator.ofFloat(animatedView, "y",
				animatedView.getY(), targetPosition);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setDuration(animationDuration);
		return animation;
	}

	/**
	 * Creates a new scroll listener, which allows to animate a view to become
	 * hidden or shown depending on the observed list view's scrolling
	 * direction.
	 * 
	 * @param view
	 *            The view, which should be animated by the listener, as an
	 *            instance of the class {@link View}. The view may not be null
	 * @param direction
	 *            The direction, which should be be used to translate the view
	 *            in order to hide it, as a value of the enum {@link Direction}.
	 *            The direction may either be <code>UP</code> or
	 *            <code>DOWN</code>
	 */
	public HideViewOnScrollAnimation(final View view, final Direction direction) {
		this(view, direction, DEFAULT_ANIMATION_DURATION);
	}

	/**
	 * Creates a new scroll listener, which allows to animate a view to become
	 * hidden or shown depending on the observed list view's scrolling
	 * direction.
	 * 
	 * @param view
	 *            The view, which should be animated by the listener, as an
	 *            instance of the class {@link View}. The view may not be null
	 * @param direction
	 *            The direction, which should be be used to translate the view
	 *            in order to hide it, as a value of the enum {@link Direction}.
	 *            The direction may either be <code>UP</code> or
	 *            <code>DOWN</code>
	 * @param animationDuration
	 *            The duration of the animation, which is used to show or hide
	 *            the view, in milliseconds as a {@link Long} value. The
	 *            duration must be greater than 0
	 */
	public HideViewOnScrollAnimation(final View view,
			final Direction direction, final long animationDuration) {
		ensureNotNull(view, "The view may not be null");
		ensureNotNull(direction, "The direction may not be null");
		ensureGreaterThan(animationDuration, 0,
				"The animation duration must be greater than 0");
		this.animatedView = view;
		this.direction = direction;
		this.animationDuration = animationDuration;
	}

	/**
	 * Returns the view, which is animated by the listener.
	 * 
	 * @return The view, which is animated by the listener as an instance of the
	 *         class {@link View}
	 */
	public final View getView() {
		return animatedView;
	}

	/**
	 * Returns the direction, which should be be used to translate the view in
	 * order to hide it.
	 * 
	 * @return The direction, which should be be used to translate the view in
	 *         order to hide it, as a value of the enum {@link Direction}. The
	 *         direction may either be <code>UP</code> or <code>DOWN</code>
	 */
	public final Direction getDirection() {
		return direction;
	}

	/**
	 * Returns the duration of the animation, which is used to show or hide the
	 * view.
	 * 
	 * @return The duration of the animation, which is used to show or hide the
	 *         view, in milliseconds as a {@link Long} value
	 */
	public final long getAnimationDuration() {
		return animationDuration;
	}

	@Override
	public final void onScrollStateChanged(final AbsListView listView,
			final int scrollState) {
		return;
	}

	@Override
	public final void onScroll(final AbsListView listView,
			final int firstVisibleItem, final int visibleItemCount,
			final int totalItemCount) {
		View view = listView.getChildAt(0);
		int position = (view == null) ? 0 : view.getTop();

		if (firstVisibleItem == oldFirstVisibleItem) {
			if (position > oldPosition) {
				onUpScrolling();
			} else if (position < oldPosition) {
				onDownScrolling();
			}
		} else {
			if (firstVisibleItem < oldFirstVisibleItem) {
				onUpScrolling();
			} else {
				onDownScrolling();
			}
		}

		oldPosition = position;
		oldFirstVisibleItem = firstVisibleItem;
	}

}