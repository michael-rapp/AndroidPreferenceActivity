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

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.LayoutInflater;
import android.view.View;

import junit.framework.Assert;

import de.mrapp.android.preference.activity.Activity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests the functionality of the class {@link PreferenceHeaderFragment}.
 *
 * @author Michael Rapp
 */
public class PreferenceHeaderFragmentTest extends ActivityUnitTestCase<Activity> {

    @Override
    protected final void setUp() throws Exception {
        super.setUp();
        startActivity(new Intent(), null, null);
    }

    /**
     * Creates a new test class.
     */
    public PreferenceHeaderFragmentTest() {
        super(Activity.class);
    }

    /**
     * Tests the functionality of the onCreateView-method.
     */
    public final void testOnCreateView() {
        LayoutInflater layoutInflater =
                (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PreferenceHeaderFragment preferenceHeaderFragment = new PreferenceHeaderFragment();
        View view = preferenceHeaderFragment.onCreateView(layoutInflater, null, null);
        assertNotNull(view);
        assertNotNull(preferenceHeaderFragment.getListView());
    }

    /**
     * Tests the functionality of the onActivityCreated-method.
     */
    public final void testOnActivityCreated() {
        FragmentListener fragmentListener = mock(FragmentListener.class);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        PreferenceHeaderFragment preferenceHeaderFragment =
                (PreferenceHeaderFragment) fragmentManager.findFragmentByTag("fragment");

        if (preferenceHeaderFragment == null || preferenceHeaderFragment.getActivity() == null) {
            preferenceHeaderFragment = new PreferenceHeaderFragment();
            preferenceHeaderFragment.addFragmentListener(fragmentListener);
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, preferenceHeaderFragment, "fragment").commit();
            fragmentManager.executePendingTransactions();
        }

        assertNotNull(preferenceHeaderFragment.getListAdapter());
        assertEquals(preferenceHeaderFragment.getListAdapter(),
                preferenceHeaderFragment.getListView().getAdapter());
        verify(fragmentListener, times(1)).onFragmentCreated(preferenceHeaderFragment);
    }

    /**
     * Tests the functionality of the method, which allows to add a listener, which should be
     * notified when the fragment has been created.
     */
    public final void testAddFragmentListener() {
        FragmentListener fragmentListener = mock(FragmentListener.class);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        PreferenceHeaderFragment preferenceHeaderFragment =
                (PreferenceHeaderFragment) fragmentManager.findFragmentByTag("fragment");

        if (preferenceHeaderFragment == null || preferenceHeaderFragment.getActivity() == null) {
            preferenceHeaderFragment = new PreferenceHeaderFragment();
            preferenceHeaderFragment.addFragmentListener(fragmentListener);
            preferenceHeaderFragment.addFragmentListener(fragmentListener);
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, preferenceHeaderFragment, "fragment").commit();
            fragmentManager.executePendingTransactions();
        }

        verify(fragmentListener, times(1)).onFragmentCreated(preferenceHeaderFragment);
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to add a
     * listener, which should be notified when the fragment has been created.
     */
    public final void testAddFragmentListenerThrowsException() {
        try {
            PreferenceHeaderFragment preferenceHeaderFragment = new PreferenceHeaderFragment();
            preferenceHeaderFragment.addFragmentListener(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to remove a listener, which should not be
     * notified when the fragment has been created, anymore.
     */
    public final void testRemoveFragmentListener() {
        FragmentListener fragmentListener = mock(FragmentListener.class);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        PreferenceHeaderFragment preferenceHeaderFragment =
                (PreferenceHeaderFragment) fragmentManager.findFragmentByTag("fragment");

        if (preferenceHeaderFragment == null || preferenceHeaderFragment.getActivity() == null) {
            preferenceHeaderFragment = new PreferenceHeaderFragment();
            preferenceHeaderFragment.addFragmentListener(fragmentListener);
            preferenceHeaderFragment.removeFragmentListener(fragmentListener);
            preferenceHeaderFragment.removeFragmentListener(fragmentListener);
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, preferenceHeaderFragment, "fragment").commit();
            fragmentManager.executePendingTransactions();
        }

        verify(fragmentListener, times(0)).onFragmentCreated(preferenceHeaderFragment);
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to remove
     * a listener, which should not be notified when the fragment has been created, anymore.
     */
    public final void testRemoveFragmentListenerThrowsException() {
        try {
            PreferenceHeaderFragment preferenceHeaderFragment = new PreferenceHeaderFragment();
            preferenceHeaderFragment.removeFragmentListener(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

}