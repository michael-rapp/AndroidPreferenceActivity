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