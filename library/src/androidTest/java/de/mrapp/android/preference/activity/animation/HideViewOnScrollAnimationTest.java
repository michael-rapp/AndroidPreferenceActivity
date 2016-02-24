/*
 * Copyright 2014 - 2016 Michael Rapp
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

import android.test.AndroidTestCase;
import android.view.View;

import junit.framework.Assert;

import de.mrapp.android.preference.activity.animation.HideViewOnScrollAnimation.Direction;

/**
 * Tests the functionality of the class {@link HideViewOnScrollAnimation}.
 *
 * @author Michael Rapp
 */
public class HideViewOnScrollAnimationTest extends AndroidTestCase {

    /**
     * Tests, if all properties are set correctly by the constructor, which expects a view and a
     * direction as parameters.
     */
    public final void testConstructorWithViewAndDirectionParameters() {
        View view = new View(getContext());
        Direction direction = Direction.DOWN;
        HideViewOnScrollAnimation animation = new HideViewOnScrollAnimation(view, direction);
        assertEquals(view, animation.getView());
        assertEquals(direction, animation.getDirection());
        assertEquals(300L, animation.getAnimationDuration());
    }

    /**
     * Tests, if all properties are set correctly by the constructor, which expects a view, a
     * direction and a duration as parameters.
     */
    public final void testConstructorWithViewDirectionAndDurationParameters() {
        View view = new View(getContext());
        Direction direction = Direction.DOWN;
        long animationDuration = 100L;
        HideViewOnScrollAnimation animation =
                new HideViewOnScrollAnimation(view, direction, animationDuration);
        assertEquals(view, animation.getView());
        assertEquals(direction, animation.getDirection());
        assertEquals(animationDuration, animation.getAnimationDuration());
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the constructor, if the view is
     * null.
     */
    public final void testConstructorThrowsExceptionIfViewIsNull() {
        try {
            new HideViewOnScrollAnimation(null, Direction.UP);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the constructor, if the direction
     * is null.
     */
    public final void testConstructorThrowsExceptionIfDirectionIsNull() {
        try {
            new HideViewOnScrollAnimation(new View(getContext()), null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, if the
     * animation duration is not greater than 0.
     */
    public final void testConstructorThrowsExceptionIfAnimationDurationIsNotGreaterThanZero() {
        try {
            new HideViewOnScrollAnimation(new View(getContext()), Direction.UP, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            return;
        }
    }

}