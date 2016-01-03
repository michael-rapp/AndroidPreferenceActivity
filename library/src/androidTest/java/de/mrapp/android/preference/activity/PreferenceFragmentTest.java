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
package de.mrapp.android.preference.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;

import junit.framework.Assert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the functionality of the class {@link PreferenceFragment}.
 *
 * @author Michael Rapp
 */
public class PreferenceFragmentTest extends ActivityUnitTestCase<Activity> {

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        ContextThemeWrapper context =
                new ContextThemeWrapper(getInstrumentation().getTargetContext(),
                        R.style.Theme_AppCompat);
        setActivityContext(context);
        startActivity(new Intent(), null, null);
    }

    /**
     * Initializes and returns an instance of the fragment, which should be tested.
     *
     * @return The fragment, which should be tested, as an instance of the class {@link
     * PreferenceFragment}
     */
    private PreferenceFragment initialize() {
        return initialize(null);
    }

    /**
     * Initializes and returns an instance of the fragment, which should be tested.
     *
     * @param arguments
     *         The arguments, which should be passed to the fragment, as an instance of the class
     *         {@link Bundle} or null, if no arguments should be passed
     * @return The fragment, which should be tested, as an instance of the class {@link
     * PreferenceFragment}
     */
    private PreferenceFragment initialize(final Bundle arguments) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        PreferenceFragment preferenceFragment =
                (PreferenceFragment) fragmentManager.findFragmentByTag("fragment");

        if (preferenceFragment == null || preferenceFragment.getActivity() == null) {
            preferenceFragment = (PreferenceFragment) Fragment
                    .instantiate(getActivity(), PreferenceFragmentImplementation.class.getName(),
                            arguments);
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, preferenceFragment, "fragment").commit();
            fragmentManager.executePendingTransactions();
        }

        return preferenceFragment;
    }

    /**
     * Creates a new test class.
     */
    public PreferenceFragmentTest() {
        super(Activity.class);
    }

    /**
     * Tests the functionality of the onCreate-method.
     */
    public final void testOnCreate() {
        PreferenceFragment preferenceFragment = initialize();
        assertFalse(preferenceFragment.setRestoreDefaultsButtonText("foo"));
        assertFalse(preferenceFragment.setRestoreDefaultsButtonText(android.R.string.cancel));
        assertFalse(preferenceFragment.setButtonBarElevation(1));
        assertFalse(preferenceFragment.setButtonBarBackground(new ColorDrawable(Color.BLACK)));
        assertFalse(preferenceFragment.setButtonBarBackground(R.drawable.selector_dark));
        assertNull(preferenceFragment.getButtonBarBackground());
        assertFalse(preferenceFragment.setButtonBarElevation(1));
        assertEquals(-1, preferenceFragment.getButtonBarElevation());
        assertFalse(preferenceFragment.isRestoreDefaultsButtonShown());
        assertNotNull(preferenceFragment.getFrameLayout());
        assertNull(preferenceFragment.getRestoreDefaultsButton());
        assertNull(preferenceFragment.getRestoreDefaultsButtonText());
        assertNull(preferenceFragment.getButtonBar());
    }

    /**
     * Tests, if the arguments, which may be passed to the fragment, are handled properly.
     */
    public final void testArguments() {
        CharSequence buttonText = "text";
        Bundle arguments = new Bundle();
        arguments.putBoolean(PreferenceFragment.EXTRA_SHOW_RESTORE_DEFAULTS_BUTTON, true);
        arguments
                .putCharSequence(PreferenceFragment.EXTRA_RESTORE_DEFAULTS_BUTTON_TEXT, buttonText);
        PreferenceFragment preferenceFragment = initialize(arguments);
        assertNotNull(preferenceFragment.getRestoreDefaultsButton());
        assertEquals(buttonText, preferenceFragment.getRestoreDefaultsButtonText());
        assertNotNull(preferenceFragment.getButtonBar());
        assertNotNull(preferenceFragment.getButtonBarBackground());
        assertEquals(2, preferenceFragment.getButtonBarElevation());
    }

    /**
     * Tests the functionality of the method, which allows to show the button, which allows to
     * restore the preferences' default values.
     */
    public final void testShowRestoreDefaultsButton() {
        PreferenceFragment preferenceFragment = initialize();
        preferenceFragment.showRestoreDefaultsButton(true);
        assertNotNull(preferenceFragment.getRestoreDefaultsButton());
        assertEquals(getActivity().getText(R.string.restore_defaults_button_label),
                preferenceFragment.getRestoreDefaultsButtonText());
        assertNotNull(preferenceFragment.getButtonBar());
        assertNotNull(preferenceFragment.getButtonBarBackground());
        assertEquals(2, preferenceFragment.getButtonBarElevation());
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the button bar
     * and expects a resource id as a parameter.
     */
    public final void testSetButtonBarBackgroundWithResourceIdParameter() {
        int resourceId = R.drawable.selector_dark;
        PreferenceFragment preferenceFragment = initialize();
        assertFalse(preferenceFragment.setButtonBarBackground(resourceId));
        preferenceFragment.showRestoreDefaultsButton(true);
        assertTrue(preferenceFragment.setButtonBarBackground(resourceId));
        assertNotNull(preferenceFragment.getButtonBarBackground());
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the button bar
     * and expects an instance of the class {@link Drawable} as a parameter.
     */
    public final void testSetButtonBarBackgroundWithDrawableParameter() {
        Drawable drawable = new ColorDrawable(Color.BLACK);
        PreferenceFragment preferenceFragment = initialize();
        assertFalse(preferenceFragment.setButtonBarBackground(drawable));
        assertNotSame(drawable, preferenceFragment.getButtonBarBackground());
        preferenceFragment.showRestoreDefaultsButton(true);
        assertTrue(preferenceFragment.setButtonBarBackground(drawable));
        assertEquals(drawable, preferenceFragment.getButtonBarBackground());
    }

    /**
     * Tests the functionality of the method, which allows to set the background color of the button
     * bar.
     */
    public final void testSetButtonBarBackgroundColor() {
        int color = Color.BLACK;
        PreferenceFragment preferenceFragment = initialize();
        assertFalse(preferenceFragment.setButtonBarBackgroundColor(color));
        preferenceFragment.showRestoreDefaultsButton(true);
        assertTrue(preferenceFragment.setButtonBarBackgroundColor(color));
        assertNotNull(preferenceFragment.getButtonBarBackground());
    }

    /**
     * Tests the functionality of the method, which allows to set the button bar's elevation.
     */
    public final void testSetButtonBarElevation() {
        int elevation = 5;
        PreferenceFragment preferenceFragment = initialize();
        assertFalse(preferenceFragment.setButtonBarElevation(elevation));
        assertEquals(-1, preferenceFragment.getButtonBarElevation());
        preferenceFragment.showRestoreDefaultsButton(true);
        assertEquals(2, preferenceFragment.getButtonBarElevation());
        assertTrue(preferenceFragment.setButtonBarElevation(elevation));
        assertEquals(elevation, preferenceFragment.getButtonBarElevation());
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the button, which
     * allows to restore the preferences' default values, and expects an instance of the class
     * {@link CharSequence} as a parameter.
     */
    public final void testSetRestoreDefaultsButtonTextWithCharSequenceParameter() {
        CharSequence text = "text";
        PreferenceFragment preferenceFragment = initialize();
        preferenceFragment.showRestoreDefaultsButton(true);
        preferenceFragment.setRestoreDefaultsButtonText(text);
        assertEquals(text, preferenceFragment.getRestoreDefaultsButtonText());
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the button, which
     * allows to restore the preferences' default values, and expects a resource id as a parameter.
     */
    public final void testSetRestoreDefaultsButtonTextWithResourceIdParameter() {
        int resourceId = android.R.string.cancel;
        PreferenceFragment preferenceFragment = initialize();
        preferenceFragment.showRestoreDefaultsButton(true);
        preferenceFragment.setRestoreDefaultsButtonText(resourceId);
        assertEquals(getActivity().getText(resourceId),
                preferenceFragment.getRestoreDefaultsButtonText());
    }

    /**
     * Tests the functionality of the method, which allows to add a listener, which should be
     * notified, when the preferences' default values are about to be restored.
     */
    public final void testAddRestoreDefaultsListener() {
        RestoreDefaultsListener listener = mock(RestoreDefaultsListener.class);
        PreferenceFragment preferenceFragment = initialize();
        preferenceFragment.showRestoreDefaultsButton(true);
        preferenceFragment.addRestoreDefaultsListener(listener);
        preferenceFragment.addRestoreDefaultsListener(listener);
        preferenceFragment.getRestoreDefaultsButton().performClick();
        verify(listener, times(1)).onRestoreDefaultValuesRequested(preferenceFragment);
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to add a
     * listener, which should be notified, when the preferences' default values are about to be
     * restored, if the listener is null.
     */
    public final void testAddRestoreDefaultsListenerThrowsException() {
        try {
            PreferenceFragment preferenceFragment = initialize();
            preferenceFragment.addRestoreDefaultsListener(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to remove a listener, which should not be
     * notified, when the preferences' default values are about to be restored, anymore.
     */
    public final void testRemoveRestoreDefaultsListener() {
        RestoreDefaultsListener listener = mock(RestoreDefaultsListener.class);
        PreferenceFragment preferenceFragment = initialize();
        preferenceFragment.showRestoreDefaultsButton(true);
        preferenceFragment.addRestoreDefaultsListener(listener);
        preferenceFragment.removeRestoreDefaultsListener(listener);
        preferenceFragment.removeRestoreDefaultsListener(listener);
        preferenceFragment.getRestoreDefaultsButton().performClick();
        verify(listener, times(0)).onRestoreDefaultValuesRequested(preferenceFragment);
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to remove
     * a listener, which should not be notified, when the preferences' default values are about to
     * be restored, anymore, if the listener is null.
     */
    public final void testRemoveRestoreDefaultsListenerThrowsException() {
        try {
            PreferenceFragment preferenceFragment = initialize();
            preferenceFragment.removeRestoreDefaultsListener(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to restore the default values.
     */
    public final void testRestoreDefaults() {
        RestoreDefaultsListener listener = mock(RestoreDefaultsListener.class);
        PreferenceFragment preferenceFragment = initialize();
        preferenceFragment.addRestoreDefaultsListener(listener);
        SharedPreferences sharedPreferences =
                preferenceFragment.getPreferenceManager().getSharedPreferences();
        sharedPreferences.edit().putBoolean("key", false).apply();
        preferenceFragment.showRestoreDefaultsButton(true);
        preferenceFragment.addPreferencesFromResource(R.xml.preferences);
        Preference preference = preferenceFragment.findPreference("key");
        when(listener.onRestoreDefaultValuesRequested(preferenceFragment)).thenReturn(true);
        when(listener.onRestoreDefaultValueRequested(preferenceFragment, preference, false))
                .thenReturn(true);
        preferenceFragment.getRestoreDefaultsButton().performClick();
        verify(listener, times(1)).onRestoreDefaultValuesRequested(preferenceFragment);
        verify(listener, times(1))
                .onRestoreDefaultValueRequested(preferenceFragment, preference, false);
        verify(listener, times(1))
                .onRestoredDefaultValue(preferenceFragment, preference, false, true);
        assertTrue(sharedPreferences.getBoolean("key", false));
    }

    /**
     * Tests the functionality of the method, which allows to restore the default values, if the
     * listener return false.
     */
    public final void testRestoreDefaultsWhenListenerReturnsFalse() {
        RestoreDefaultsListener listener = mock(RestoreDefaultsListener.class);
        PreferenceFragment preferenceFragment = initialize();
        preferenceFragment.addRestoreDefaultsListener(listener);
        SharedPreferences sharedPreferences =
                preferenceFragment.getPreferenceManager().getSharedPreferences();
        sharedPreferences.edit().putBoolean("key", false).apply();
        preferenceFragment.showRestoreDefaultsButton(true);
        preferenceFragment.addPreferencesFromResource(R.xml.preferences);
        Preference preference = preferenceFragment.findPreference("key");
        when(listener.onRestoreDefaultValuesRequested(preferenceFragment)).thenReturn(false);
        preferenceFragment.getRestoreDefaultsButton().performClick();
        verify(listener, times(1)).onRestoreDefaultValuesRequested(preferenceFragment);
        verify(listener, times(0))
                .onRestoreDefaultValueRequested(preferenceFragment, preference, false);
        verify(listener, times(0))
                .onRestoredDefaultValue(preferenceFragment, preference, false, true);
        assertFalse(sharedPreferences.getBoolean("key", true));
    }

    /**
     * Tests the functionality of the method, which allows to restore the default values, if the
     * listener return false when a single preference's default values should be restored.
     */
    public final void testRestoreDefaultsWhenListenerReturnsFalseOnSinglePreference() {
        RestoreDefaultsListener listener = mock(RestoreDefaultsListener.class);
        PreferenceFragment preferenceFragment = initialize();
        preferenceFragment.addRestoreDefaultsListener(listener);
        SharedPreferences sharedPreferences =
                preferenceFragment.getPreferenceManager().getSharedPreferences();
        sharedPreferences.edit().putBoolean("key", false).apply();
        preferenceFragment.showRestoreDefaultsButton(true);
        preferenceFragment.addPreferencesFromResource(R.xml.preferences);
        Preference preference = preferenceFragment.findPreference("key");
        when(listener.onRestoreDefaultValuesRequested(preferenceFragment)).thenReturn(true);
        when(listener.onRestoreDefaultValueRequested(preferenceFragment, preference, false))
                .thenReturn(false);
        preferenceFragment.getRestoreDefaultsButton().performClick();
        verify(listener, times(1)).onRestoreDefaultValuesRequested(preferenceFragment);
        verify(listener, times(1))
                .onRestoreDefaultValueRequested(preferenceFragment, preference, false);
        verify(listener, times(0))
                .onRestoredDefaultValue(preferenceFragment, preference, false, true);
        assertFalse(sharedPreferences.getBoolean("key", true));
    }

}