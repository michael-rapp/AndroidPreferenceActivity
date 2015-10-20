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

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.view.View;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Tests the functionality of the class {@link PreferenceActivity}.
 *
 * @author Michael Rapp
 */
public class PreferenceActivityTest extends ActivityUnitTestCase<PreferenceActivityImplementation> {

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        ContextThemeWrapper context =
                new ContextThemeWrapper(getInstrumentation().getTargetContext(),
                        R.style.Theme_AppCompat);
        setActivityContext(context);
    }

    /**
     * Initializes the {@link PreferenceActivity} which is tested.
     *
     * @return The preference activity, which has been initialized, as an instance of the class
     * {@link PreferenceActivity}
     */
    private PreferenceActivity initialize() {
        startActivity(new Intent(), null, null);
        PreferenceActivity preferenceActivity = getActivity();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.executePendingTransactions();
        return preferenceActivity;
    }

    /**
     * Initializes the {@link PreferenceActivity} which is tested.
     *
     * @param intent
     *         The intent, which should be used to start the activity, as an instance of the class
     *         {@link Intent}
     * @return The preference activity, which has been initialized, as an instance of the class
     * {@link PreferenceActivity}
     */
    private PreferenceActivity initialize(final Intent intent) {
        startActivity(intent, null, null);
        PreferenceActivity preferenceActivity = getActivity();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.executePendingTransactions();
        return preferenceActivity;
    }

    /**
     * Creates a new test class.
     */
    public PreferenceActivityTest() {
        super(PreferenceActivityImplementation.class);
    }

    /**
     * Tests the functionality of the onCreate-method.
     */
    public final void testOnCreate() {
        PreferenceActivity preferenceActivity = initialize();
        assertNotNull(preferenceActivity.getFrameLayout());
        assertNotNull(preferenceActivity.getPreferenceHeaderParentView());
        assertTrue(preferenceActivity.isNavigationIconOverridden());
        assertFalse(preferenceActivity.isPreferenceHeaderSelected());
        assertFalse(preferenceActivity.isButtonBarShown());
        assertFalse(preferenceActivity.isProgressShown());
        assertNull(preferenceActivity.getProgressFormat());
        assertFalse(preferenceActivity.isNavigationHidden());
        assertNull(preferenceActivity.getButtonBar());
        assertNull(preferenceActivity.getNextButton());
        assertNull(preferenceActivity.getBackButton());
        assertNull(preferenceActivity.getFinishButton());
        assertNull(preferenceActivity.getBackButtonText());
        assertNull(preferenceActivity.getNextButtonText());
        assertNull(preferenceActivity.getFinishButtonText());
        assertNull(preferenceActivity.getButtonBarBackground());
        assertEquals(-1, preferenceActivity.getButtonBarElevation());
        assertNotNull(preferenceActivity.getListAdapter());
        assertNotNull(preferenceActivity.getListView());
        assertTrue(preferenceActivity.getAllPreferenceHeaders().isEmpty());
        assertEquals(0, preferenceActivity.getNumberOfPreferenceHeaders());
        assertNotNull(preferenceActivity.getNavigationBackground());

        if (preferenceActivity.isSplitScreen()) {
            assertNotNull(preferenceActivity.getPreferenceScreenParentView());
            assertNotNull(preferenceActivity.getPreferenceScreenContainer());
            assertNotNull(preferenceActivity.getBreadCrumb());
            assertNotNull(preferenceActivity.getBreadCrumbBackground());
            assertEquals(2, preferenceActivity.getBreadCrumbElevation());
            assertEquals(3, preferenceActivity.getNavigationElevation());
            assertNotNull(preferenceActivity.getPreferenceScreenBackground());
            assertEquals(360, preferenceActivity.getNavigationWidth());
            assertNotNull(preferenceActivity.getNavigationBackground());
        } else {
            assertNull(preferenceActivity.getPreferenceScreenParentView());
            assertNull(preferenceActivity.getPreferenceScreenContainer());
            assertNull(preferenceActivity.getBreadCrumb());
            assertNull(preferenceActivity.getBreadCrumbBackground());
            assertEquals(-1, preferenceActivity.getBreadCrumbElevation());
            assertEquals(-1, preferenceActivity.getNavigationElevation());
            assertNull(preferenceActivity.getPreferenceScreenBackground());
            assertEquals(-1, preferenceActivity.getNavigationWidth());
        }
    }

    /**
     * Tests the functionality of the onCreate-method, if the intent extra, which allows to hide the
     * navigation, is passed to the activity.
     */
    public final void testHideNavigationIntent() {
        Intent intent = new Intent();
        intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        PreferenceActivity preferenceActivity = initialize(intent);
        assertTrue(preferenceActivity.isNavigationHidden());
    }

    /**
     * Tests the functionality of the onCreate-method, if the intent extras, which allow to show the
     * button bar, are passed to the activity and the alternative button texts are passed as
     * resource ids.
     */
    public final void testShowButtonBarIntentWhenButtonTextsArePassedAsResourceIds() {
        Intent intent = new Intent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_BUTTON_BAR, true);
        intent.putExtra(PreferenceActivity.EXTRA_BACK_BUTTON_TEXT, android.R.string.cancel);
        intent.putExtra(PreferenceActivity.EXTRA_NEXT_BUTTON_TEXT, android.R.string.copy);
        intent.putExtra(PreferenceActivity.EXTRA_FINISH_BUTTON_TEXT, android.R.string.copyUrl);
        PreferenceActivity preferenceActivity = initialize(intent);
        assertTrue(preferenceActivity.isButtonBarShown());
        assertEquals(getActivity().getText(android.R.string.cancel),
                preferenceActivity.getBackButtonText());
        assertEquals(getActivity().getText(android.R.string.copy),
                preferenceActivity.getNextButtonText());
        assertEquals(getActivity().getText(android.R.string.copyUrl),
                preferenceActivity.getFinishButtonText());
        assertFalse(preferenceActivity.isProgressShown());
        assertNull(preferenceActivity.getProgressFormat());
    }

    /**
     * Tests the functionality of the onCreate-method, if the intent extras, which allow to show the
     * button bar, are passed to the activity and the alternative button texts are passed as
     * instances of the class {@link CharSequence}.
     */
    public final void testShowButtonBarIntentWhenButtonTextsArePassedAsCharSequences() {
        CharSequence back = "b";
        CharSequence next = "n";
        CharSequence finish = "f";
        Intent intent = new Intent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_BUTTON_BAR, true);
        intent.putExtra(PreferenceActivity.EXTRA_BACK_BUTTON_TEXT, back);
        intent.putExtra(PreferenceActivity.EXTRA_NEXT_BUTTON_TEXT, next);
        intent.putExtra(PreferenceActivity.EXTRA_FINISH_BUTTON_TEXT, finish);
        PreferenceActivity preferenceActivity = initialize(intent);
        assertTrue(preferenceActivity.isButtonBarShown());
        assertEquals(back, preferenceActivity.getBackButtonText());
        assertEquals(next, preferenceActivity.getNextButtonText());
        assertEquals(finish, preferenceActivity.getFinishButtonText());
        assertFalse(preferenceActivity.isProgressShown());
        assertNull(preferenceActivity.getProgressFormat());
    }

    /**
     * Tests the functionality of the onCreate-method, if the intent extras, which allow to show the
     * button bar, are passed to the activity and the progress should be shown.
     */
    public final void testShowButtonBarIntentWhenProgressShouldBeShown() {
        String progressFormat =
                getInstrumentation().getContext().getString(R.string.progress_format);
        Intent intent = new Intent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_BUTTON_BAR, true);
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_PROGRESS, true);
        PreferenceActivity preferenceActivity = initialize(intent);
        assertTrue(preferenceActivity.isButtonBarShown());
        assertTrue(preferenceActivity.isProgressShown());
        assertEquals(progressFormat, preferenceActivity.getProgressFormat());
    }

    /**
     * Tests the functionality of the onCreate-method, if the intent extras, which allow to show the
     * button bar, are passed to the activity, the progress should be shown and an alternative
     * progress format is passed as resource id.
     */
    public final void testShowButtonBarIntentWhenProgressFormatIsPassedAsResourceId() {
        String progressFormat =
                getInstrumentation().getContext().getString(android.R.string.cancel);
        Intent intent = new Intent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_BUTTON_BAR, true);
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_PROGRESS, true);
        intent.putExtra(PreferenceActivity.EXTRA_PROGRESS_FORMAT, android.R.string.cancel);
        PreferenceActivity preferenceActivity = initialize(intent);
        assertTrue(preferenceActivity.isButtonBarShown());
        assertTrue(preferenceActivity.isProgressShown());
        assertEquals(progressFormat, preferenceActivity.getProgressFormat());
    }

    /**
     * Tests the functionality of the onCreate-method, if the intent extras, which allow to show the
     * button bar, are passed to the activity, the progress should be shown and an alternative
     * progress format is passed as an instance of the class {@link CharSequence}.
     */
    public final void testShowButtonBarIntentWhenProgressFormatIsPassedAsCharSequence() {
        CharSequence progressFormat =
                getInstrumentation().getContext().getString(android.R.string.cancel);
        Intent intent = new Intent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_BUTTON_BAR, true);
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_PROGRESS, true);
        intent.putExtra(PreferenceActivity.EXTRA_PROGRESS_FORMAT, progressFormat);
        PreferenceActivity preferenceActivity = initialize(intent);
        assertTrue(preferenceActivity.isButtonBarShown());
        assertTrue(preferenceActivity.isProgressShown());
        assertEquals(progressFormat, preferenceActivity.getProgressFormat());
    }

    /**
     * Tests the functionality of the method, which allows to add a preference header.
     */
    public final void testAddPreferenceHeader() {
        PreferenceActivity preferenceActivity = initialize();
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        preferenceHeader.setFragment("de.mrapp.android.preference.activity.Fragment");
        preferenceActivity.addPreferenceHeader(preferenceHeader);
        assertEquals(1, preferenceActivity.getNumberOfPreferenceHeaders());
        assertEquals(preferenceHeader, preferenceActivity.getPreferenceHeader(0));
        assertEquals(1, preferenceActivity.getAllPreferenceHeaders().size());
        assertEquals(preferenceHeader,
                preferenceActivity.getAllPreferenceHeaders().iterator().next());

        if (preferenceActivity.isSplitScreen()) {
            assertTrue(preferenceActivity.isPreferenceHeaderSelected());
            assertEquals(0, preferenceActivity.getListView().getCheckedItemPosition());
        } else {
            assertFalse(preferenceActivity.isPreferenceHeaderSelected());
        }
    }

    /**
     * Tests the functionality of the method, which allows to remove a preference header.
     */
    public final void testRemovePreferenceHeader() {
        PreferenceActivity preferenceActivity = initialize();
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        preferenceActivity.addPreferenceHeader(preferenceHeader);
        preferenceActivity.removePreferenceHeader(preferenceHeader);
        assertEquals(0, preferenceActivity.getNumberOfPreferenceHeaders());
        assertEquals(0, preferenceActivity.getAllPreferenceHeaders().size());
        assertFalse(preferenceActivity.isPreferenceHeaderSelected());
    }

    /**
     * Tests the functionality of the method, which allows to add all preference headers, which are
     * contained by a specific collection.
     */
    public final void testAddAllPreferenceHeaders() {
        PreferenceActivity preferenceActivity = initialize();
        PreferenceHeader preferenceHeader1 = new PreferenceHeader("foo");
        preferenceHeader1.setFragment("de.mrapp.android.preference.activity.Fragment");
        PreferenceHeader preferenceHeader2 = new PreferenceHeader("bar");
        Collection<PreferenceHeader> collection = new LinkedList<>();
        collection.add(preferenceHeader1);
        collection.add(preferenceHeader2);
        preferenceActivity.addAllPreferenceHeaders(collection);
        assertEquals(2, preferenceActivity.getNumberOfPreferenceHeaders());
        assertEquals(preferenceHeader1, preferenceActivity.getPreferenceHeader(0));
        assertEquals(preferenceHeader2, preferenceActivity.getPreferenceHeader(1));
        assertEquals(2, preferenceActivity.getAllPreferenceHeaders().size());
        Iterator<PreferenceHeader> iterator =
                preferenceActivity.getAllPreferenceHeaders().iterator();
        assertEquals(preferenceHeader1, iterator.next());
        assertEquals(preferenceHeader2, iterator.next());

        if (preferenceActivity.isSplitScreen()) {
            assertTrue(preferenceActivity.isPreferenceHeaderSelected());
            assertEquals(0, preferenceActivity.getListView().getCheckedItemPosition());
        } else {
            assertFalse(preferenceActivity.isPreferenceHeaderSelected());
        }
    }

    /**
     * Tests the functionality of the method, which allows to add the preference headers, which are
     * specified by a XML resource.
     */
    public final void testAddPreferenceHeadersFromResource() {
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.addPreferenceHeadersFromResource(R.xml.preference_headers);
        assertEquals(3, preferenceActivity.getNumberOfPreferenceHeaders());
        assertEquals(3, preferenceActivity.getAllPreferenceHeaders().size());

        if (preferenceActivity.isSplitScreen()) {
            assertTrue(preferenceActivity.isPreferenceHeaderSelected());
            assertEquals(0, preferenceActivity.getListView().getCheckedItemPosition());
        } else {
            assertFalse(preferenceActivity.isPreferenceHeaderSelected());
        }
    }

    /**
     * Tests the functionality of the method, which allows to add all preference headers, which are
     * contained by a specific collection.
     */
    public final void testClearPreferenceHeaders() {
        PreferenceActivity preferenceActivity = initialize();
        PreferenceHeader preferenceHeader1 = new PreferenceHeader("foo");
        PreferenceHeader preferenceHeader2 = new PreferenceHeader("bar");
        preferenceActivity.addPreferenceHeader(preferenceHeader1);
        preferenceActivity.addPreferenceHeader(preferenceHeader2);
        preferenceActivity.clearPreferenceHeaders();
        assertEquals(0, preferenceActivity.getNumberOfPreferenceHeaders());
        assertEquals(0, preferenceActivity.getAllPreferenceHeaders().size());
        assertFalse(preferenceActivity.isPreferenceHeaderSelected());
    }

    /**
     * Tests the functionality of the method, which allows to select a specific preference header.
     */
    public final void testSelectPreferenceHeader() {
        PreferenceHeader preferenceHeader1 = new PreferenceHeader("foo");
        PreferenceHeader preferenceHeader2 = new PreferenceHeader("foo");
        preferenceHeader2.setFragment("de.mrapp.android.preference.activity.Fragment");
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.addPreferenceHeader(preferenceHeader1);
        preferenceActivity.addPreferenceHeader(preferenceHeader2);
        preferenceActivity.selectPreferenceHeader(preferenceHeader2);
        assertTrue(preferenceActivity.isPreferenceHeaderSelected());
        assertEquals(preferenceHeader2, preferenceActivity.getSelectedPreferenceHeader());
        assertEquals(1, preferenceActivity.getSelectedPreferenceHeaderPosition());

        if (preferenceActivity.isSplitScreen()) {
            assertEquals(1, preferenceActivity.getListView().getCheckedItemPosition());
        }
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to select
     * a specific preference header, if the preference header is null.
     */
    public final void testSelectPreferenceHeaderThrowsExceptionWhenPreferenceHeaderIsNull() {
        try {
            PreferenceActivity preferenceActivity = initialize();
            preferenceActivity.selectPreferenceHeader(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Ensures, that a {@link NoSuchElementException} is thrown by the method, which allows to
     * select a specific preference header, if the activity does not contain the preference header.
     */
    public final void testSelectPreferenceHeaderThrowsExceptionWhenActivityDoesNotContainPreferenceHeader() {
        try {
            PreferenceActivity preferenceActivity = initialize();
            preferenceActivity.selectPreferenceHeader(new PreferenceHeader("foo"));
            Assert.fail();
        } catch (NoSuchElementException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to select a specific preference header
     * and expects a bundle as a parameter.
     */
    public final void testSelectPreferenceHeaderWithBundleParameter() {
        PreferenceHeader preferenceHeader1 = new PreferenceHeader("foo");
        PreferenceHeader preferenceHeader2 = new PreferenceHeader("foo");
        preferenceHeader2.setFragment("de.mrapp.android.preference.activity.Fragment");
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.addPreferenceHeader(preferenceHeader1);
        preferenceActivity.addPreferenceHeader(preferenceHeader2);
        preferenceActivity.selectPreferenceHeader(preferenceHeader2, new Bundle());
        assertTrue(preferenceActivity.isPreferenceHeaderSelected());
        assertEquals(preferenceHeader2, preferenceActivity.getSelectedPreferenceHeader());
        assertEquals(1, preferenceActivity.getSelectedPreferenceHeaderPosition());

        if (preferenceActivity.isSplitScreen()) {
            assertEquals(1, preferenceActivity.getListView().getCheckedItemPosition());
        }
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to select
     * a specific preference header and expects a bundle as a parameter, if the preference header is
     * null.
     */
    public final void testSelectPreferenceHeaderWithBundleParameterThrowsExceptionWhenPreferenceHeaderIsNull() {
        try {
            PreferenceActivity preferenceActivity = initialize();
            preferenceActivity.selectPreferenceHeader(null, new Bundle());
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Ensures, that a {@link NoSuchElementException} is thrown by the method, which allows to
     * select a specific preference header and expects a bundle as a parameter, if the activity does
     * not contain the preference header.
     */
    public final void testSelectPreferenceHeaderWithBundleParameterThrowsExceptionWhenActivityDoesNotContainPreferenceHeader() {
        try {
            PreferenceActivity preferenceActivity = initialize();
            preferenceActivity.selectPreferenceHeader(new PreferenceHeader("foo"), new Bundle());
            Assert.fail();
        } catch (NoSuchElementException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to select the preference header, which
     * belongs to a specific position.
     */
    public final void testSelectPreferenceHeaderWithPositionParameter() {
        PreferenceHeader preferenceHeader1 = new PreferenceHeader("foo");
        PreferenceHeader preferenceHeader2 = new PreferenceHeader("foo");
        preferenceHeader2.setFragment("de.mrapp.android.preference.activity.Fragment");
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.addPreferenceHeader(preferenceHeader1);
        preferenceActivity.addPreferenceHeader(preferenceHeader2);
        preferenceActivity.selectPreferenceHeader(1);
        assertTrue(preferenceActivity.isPreferenceHeaderSelected());
        assertEquals(preferenceHeader2, preferenceActivity.getSelectedPreferenceHeader());
        assertEquals(1, preferenceActivity.getSelectedPreferenceHeaderPosition());

        if (preferenceActivity.isSplitScreen()) {
            assertEquals(1, preferenceActivity.getListView().getCheckedItemPosition());
        }
    }

    /**
     * Ensures, that an {@link IndexOutOfBoundsException} is thrown by the method, which allows to
     * select the preference header, which belongs to a specific position, if position is invalid.
     */
    public final void testSelectPreferenceHeaderWithPositionParameterThrowsException() {
        try {
            PreferenceActivity preferenceActivity = initialize();
            preferenceActivity.selectPreferenceHeader(-1);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to select the preference header, which
     * belongs to a specific position and expects a bundle as a parameter.
     */
    public final void testSelectPreferenceHeaderWithPositionAndBundleParameter() {
        PreferenceHeader preferenceHeader1 = new PreferenceHeader("foo");
        PreferenceHeader preferenceHeader2 = new PreferenceHeader("foo");
        preferenceHeader2.setFragment("de.mrapp.android.preference.activity.Fragment");
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.addPreferenceHeader(preferenceHeader1);
        preferenceActivity.addPreferenceHeader(preferenceHeader2);
        preferenceActivity.selectPreferenceHeader(1, new Bundle());
        assertTrue(preferenceActivity.isPreferenceHeaderSelected());
        assertEquals(preferenceHeader2, preferenceActivity.getSelectedPreferenceHeader());
        assertEquals(1, preferenceActivity.getSelectedPreferenceHeaderPosition());

        if (preferenceActivity.isSplitScreen()) {
            assertEquals(1, preferenceActivity.getListView().getCheckedItemPosition());
        }
    }

    /**
     * Ensures, that an {@link IndexOutOfBoundsException} is thrown by the method, which allows to
     * select the preference header, which belongs to a specific position and expects a bundle as a
     * parameter, if position is invalid.
     */
    public final void testSelectPreferenceHeaderWithPositionAndBundleParameterThrowsException() {
        try {
            PreferenceActivity preferenceActivity = initialize();
            preferenceActivity.selectPreferenceHeader(-1, new Bundle());
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to show the button bar.
     */
    public final void testShowButtonBar() {
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(true);
        assertTrue(preferenceActivity.isButtonBarShown());
        assertNotNull(preferenceActivity.getNextButton());
        assertNotNull(preferenceActivity.getBackButton());
        assertNotNull(preferenceActivity.getFinishButton());
        assertEquals(getActivity().getString(R.string.next_button_label),
                preferenceActivity.getNextButtonText());
        assertEquals(getActivity().getString(R.string.back_button_label),
                preferenceActivity.getBackButtonText());
        assertEquals(getActivity().getString(R.string.finish_button_label),
                preferenceActivity.getFinishButtonText());
        assertEquals(View.GONE, preferenceActivity.getBackButton().getVisibility());
        assertEquals(View.GONE, preferenceActivity.getNextButton().getVisibility());
        assertEquals(View.VISIBLE, preferenceActivity.getFinishButton().getVisibility());
    }

    /**
     * Tests the functionality of the method, which allows to show the progress of a wizard.
     */
    public final void testShowProgress() {
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.showProgress(true);
        assertTrue(set);
        assertTrue(preferenceActivity.isProgressShown());
    }

    /**
     * Tests the functionality of the method, which allows to show the progress of a wizard, when
     * the button bar is not shown.
     */
    public final void testShowProgressWhenButtonBarIsNotShown() {
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(false);
        boolean set = preferenceActivity.showProgress(true);
        assertFalse(set);
        assertFalse(preferenceActivity.isProgressShown());
    }

    /**
     * Tests the functionality of the method, which allows to set a custom progress format and
     * expects a string as a parameter.
     */
    public final void testSetProgressFormatWithStringParameter() {
        String progressFormat = "%d%d%s";
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(true);
        preferenceActivity.showProgress(true);
        boolean set = preferenceActivity.setProgressFormat(progressFormat);
        assertTrue(set);
        assertEquals(progressFormat, preferenceActivity.getProgressFormat());
    }

    /**
     * Tests the functionality of the method, which allows to set a custom progress format and
     * expects a string as a parameter, when the button bar is not shown.
     */
    public final void testSetProgressFormatWithStringParameterWhenButtonBarIsNotShown() {
        PreferenceActivity preferenceActivity = initialize();
        boolean set = preferenceActivity.setProgressFormat("%d%d%s");
        assertFalse(set);
        assertNull(preferenceActivity.getProgressFormat());
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to set a
     * custom progress format, if the format is null.
     */
    public final void testSetProgressFormatThrowsException() {
        try {
            PreferenceActivity preferenceActivity = initialize();
            preferenceActivity.setProgressFormat(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to set a custom progress format and
     * expects a resource id as a parameter.
     */
    public final void testSetProgressFormatWithResourceIdParameter() {
        String progressFormat =
                getInstrumentation().getContext().getString(android.R.string.cancel);
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(true);
        preferenceActivity.showProgress(true);
        boolean set = preferenceActivity.setProgressFormat(android.R.string.cancel);
        assertTrue(set);
        assertEquals(progressFormat, preferenceActivity.getProgressFormat());
    }

    /**
     * Tests the functionality of the method, which allows to set a custom progress format and
     * expects a resource id as a parameter, when the button bar is not shown.
     */
    public final void testSetProgressFormatWithResourceIdParameterWhenButtonBarIsNotShown() {
        PreferenceActivity preferenceActivity = initialize();
        boolean set = preferenceActivity.setProgressFormat(android.R.string.cancel);
        assertFalse(set);
        assertNull(preferenceActivity.getProgressFormat());
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the next button and
     * expects an instance of the class {@link CharSequence} as a parameter.
     */
    public final void testSetNextButtonTextWithCharSequenceParameter() {
        CharSequence text = "n";
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.setNextButtonText(text);
        assertTrue(set);
        assertEquals(text, preferenceActivity.getNextButtonText());
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the next button and
     * expects an instance of the class {@link CharSequence} as a parameter, when the button bar is
     * not shown.
     */
    public final void testSetNextButtonTextWithCharSequenceParameterWhenButtonBarIsNotShown() {
        CharSequence text = "n";
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(false);
        boolean set = preferenceActivity.setNextButtonText(text);
        assertFalse(set);
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the next button and
     * expects a resource id as a parameter.
     */
    public final void testSetNextButtonTextWithResourceIdParameter() {
        int resourceId = android.R.string.cancel;
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.setNextButtonText(resourceId);
        assertTrue(set);
        assertEquals(getActivity().getText(resourceId), preferenceActivity.getNextButtonText());
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the next button and
     * expects a resource id as a parameter.
     */
    public final void testSetNextButtonTextWithResourceIdParameterWhenButtonBarIsNotShown() {
        int resourceId = android.R.string.cancel;
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(false);
        boolean set = preferenceActivity.setNextButtonText(resourceId);
        assertFalse(set);
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the back button and
     * expects an instance of the class {@link CharSequence} as a parameter.
     */
    public final void testSetBackButtonTextWithCharSequenceParameter() {
        CharSequence text = "b";
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.setBackButtonText(text);
        assertTrue(set);
        assertEquals(text, preferenceActivity.getBackButtonText());
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the back button and
     * expects an instance of the class {@link CharSequence} as a parameter, when the button bar is
     * not shown.
     */
    public final void testSetBackButtonTextWithCharSequenceParameterWhenButtonBarIsNotShown() {
        CharSequence text = "b";
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(false);
        boolean set = preferenceActivity.setBackButtonText(text);
        assertFalse(set);
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the back button and
     * expects a resource id as a parameter.
     */
    public final void testSetBackButtonTextWithResourceIdParameter() {
        int resourceId = android.R.string.cancel;
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.setBackButtonText(resourceId);
        assertTrue(set);
        assertEquals(getActivity().getText(resourceId), preferenceActivity.getBackButtonText());
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the back button and
     * expects a resource id as a parameter.
     */
    public final void testSetBackButtonTextWithResourceIdParameterWhenButtonBarIsNotShown() {
        int resourceId = android.R.string.cancel;
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(false);
        boolean set = preferenceActivity.setBackButtonText(resourceId);
        assertFalse(set);
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the finish button and
     * expects an instance of the class {@link CharSequence} as a parameter.
     */
    public final void testSetFinishButtonTextWithCharSequenceParameter() {
        CharSequence text = "f";
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.setFinishButtonText(text);
        assertTrue(set);
        assertEquals(text, preferenceActivity.getFinishButtonText());
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the finish button and
     * expects an instance of the class {@link CharSequence} as a parameter, when the button bar is
     * not shown.
     */
    public final void testSetFinishButtonTextWithCharSequenceParameterWhenButtonBarIsNotShown() {
        CharSequence text = "f";
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(false);
        boolean set = preferenceActivity.setFinishButtonText(text);
        assertFalse(set);
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the finish button and
     * expects a resource id as a parameter.
     */
    public final void testSetFinishButtonTextWithResourceIdParameter() {
        int resourceId = android.R.string.cancel;
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.setFinishButtonText(resourceId);
        assertTrue(set);
        assertEquals(getActivity().getText(resourceId), preferenceActivity.getFinishButtonText());
    }

    /**
     * Tests the functionality of the method, which allows to set the text of the finish button and
     * expects a resource id as a parameter.
     */
    public final void testSetFinishButtonTextWithResourceIdParameterWhenButtonBarIsNotShown() {
        int resourceId = android.R.string.cancel;
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.showButtonBar(false);
        boolean set = preferenceActivity.setFinishButtonText(resourceId);
        assertFalse(set);
    }

    /**
     * Tests the functionality of the method, which allows to hide the navigation.
     */
    public final void testHideNavigation() {
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.hideNavigation(true);
        assertTrue(preferenceActivity.isNavigationHidden());
    }

    /**
     * Tests the functionality of the method, which allows to set the width of the navigation.
     */
    public final void testSetNavigationWidth() {
        int width = 2;
        PreferenceActivity preferenceActivity = initialize();

        if (preferenceActivity.isSplitScreen()) {
            boolean set = preferenceActivity.setNavigationWidth(width);
            assertTrue(set);
            assertEquals(width, preferenceActivity.getNavigationWidth());
        } else {
            assertFalse(preferenceActivity.setNavigationWidth(width));
        }
    }

    /**
     * Tests the functionality of the method, which allows to set, whether the action bar's back
     * button should be overridden, or not.
     */
    public final void testOverrideBackButton() {
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.overrideNavigationIcon(true);
        assertTrue(preferenceActivity.isNavigationIconOverridden());
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the preference
     * screen and expects a resource id as a parameter.
     */
    public final void testSetPreferenceScreenBackgroundWithResourceIdParameter() {
        int resourceId = R.drawable.selector_dark;
        PreferenceActivity preferenceActivity = initialize();

        if (preferenceActivity.isSplitScreen()) {
            boolean set = preferenceActivity.setPreferenceScreenBackground(resourceId);
            assertTrue(set);
            assertNotNull(preferenceActivity.getPreferenceScreenBackground());
        } else {
            assertFalse(preferenceActivity.setPreferenceScreenBackground(resourceId));
        }
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the preference
     * screen and expects a color as a parameter.
     */
    public final void testSetPreferenceScreenBackgroundWithColorParameter() {
        int color = Color.BLACK;
        PreferenceActivity preferenceActivity = initialize();

        if (preferenceActivity.isSplitScreen()) {
            boolean set = preferenceActivity.setPreferenceScreenBackgroundColor(color);
            assertTrue(set);
            assertNotNull(preferenceActivity.getPreferenceScreenBackground());
        } else {
            assertFalse(preferenceActivity.setPreferenceScreenBackgroundColor(color));
        }
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the preference
     * screen and expects an instance of the class {@link Drawable} as a parameter.
     */
    public final void testSetPreferenceScreenBackgroundWithDrawableParameter() {
        Drawable drawable = new ColorDrawable(Color.BLACK);
        PreferenceActivity preferenceActivity = initialize();

        if (preferenceActivity.isSplitScreen()) {
            boolean set = preferenceActivity.setPreferenceScreenBackground(drawable);
            assertTrue(set);
            assertEquals(drawable, preferenceActivity.getPreferenceScreenBackground());
        } else {
            assertFalse(preferenceActivity.setPreferenceScreenBackground(drawable));
        }
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the navigation
     * and expects a resource id as a parameter.
     */
    public final void testSetNavigationBackgroundWithResourceIdParameter() {
        int resourceId = R.drawable.selector_dark;
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.setNavigationBackground(resourceId);
        assertNotNull(preferenceActivity.getNavigationBackground());
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the navigation
     * and expects a color as a parameter.
     */
    public final void testSetNavigationBackgroundWithColorParameter() {
        int color = Color.BLACK;
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.setNavigationBackgroundColor(color);
        assertNotNull(preferenceActivity.getNavigationBackground());
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the navigation
     * and expects an instance of the class {@link Drawable} as a parameter.
     */
    public final void testSetNavigationBackgroundWithDrawableParameter() {
        Drawable drawable = new ColorDrawable(Color.BLACK);
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.setNavigationBackground(drawable);
        assertEquals(drawable, preferenceActivity.getNavigationBackground());
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the bread crumb
     * and expects a resource id as a parameter.
     */
    public final void testSetBreadCrumbBackgroundWithResourceIdParameter() {
        int resourceId = R.drawable.selector_dark;
        PreferenceActivity preferenceActivity = initialize();

        if (preferenceActivity.isSplitScreen()) {
            boolean set = preferenceActivity.setBreadCrumbBackground(resourceId);
            assertTrue(set);
            assertNotNull(preferenceActivity.getBreadCrumbBackground());
        } else {
            assertFalse(preferenceActivity.setBreadCrumbBackground(resourceId));
        }
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the bread crumb
     * and expects an instance of the class {@link Drawable} as a parameter.
     */
    public final void testSetBreadCrumbBackgroundWithDrawableParameter() {
        Drawable drawable = new ColorDrawable(Color.BLACK);
        PreferenceActivity preferenceActivity = initialize();

        if (preferenceActivity.isSplitScreen()) {
            boolean set = preferenceActivity.setBreadCrumbBackground(drawable);
            assertTrue(set);
            assertEquals(drawable, preferenceActivity.getBreadCrumbBackground());
        } else {
            assertFalse(preferenceActivity.setBreadCrumbBackground(drawable));
        }
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the bread crumb
     * and expects a color as a parameter.
     */
    public final void testSetBreadCrumbBackgroundWithColorParameter() {
        int color = Color.BLACK;
        PreferenceActivity preferenceActivity = initialize();

        if (preferenceActivity.isSplitScreen()) {
            boolean set = preferenceActivity.setBreadCrumbBackgroundColor(color);
            assertTrue(set);
            assertNotNull(preferenceActivity.getBreadCrumbBackground());
        } else {
            assertFalse(preferenceActivity.setBreadCrumbBackgroundColor(color));
        }
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the button bar
     * and expects a resource id as a parameter.
     */
    public final void testSetButtonBarBackgroundWithResourceIdParameter() {
        int resourceId = R.drawable.selector_dark;
        PreferenceActivity preferenceActivity = initialize();
        assertFalse(preferenceActivity.setButtonBarBackground(resourceId));
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.setButtonBarBackground(resourceId);
        assertTrue(set);
        assertNotNull(preferenceActivity.getButtonBarBackground());
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the button bar
     * and expects an instance of the class {@link Drawable} as a parameter.
     */
    public final void testSetButtonBarBackgroundWithDrawableParameter() {
        Drawable drawable = new ColorDrawable(Color.BLACK);
        PreferenceActivity preferenceActivity = initialize();
        assertFalse(preferenceActivity.setButtonBarBackground(drawable));
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.setButtonBarBackground(drawable);
        assertTrue(set);
        assertEquals(drawable, preferenceActivity.getButtonBarBackground());
    }

    /**
     * Tests the functionality of the method, which allows to set the background of the button bar
     * and expects a color as a parameter.
     */
    public final void testSetButtonBarBackgroundWithColorParameter() {
        int color = Color.BLACK;
        PreferenceActivity preferenceActivity = initialize();
        assertFalse(preferenceActivity.setButtonBarBackgroundColor(color));
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.setButtonBarBackgroundColor(color);
        assertTrue(set);
        assertNotNull(preferenceActivity.getButtonBarBackground());
    }

    /**
     * Tests the functionality of the method, which allows to set the navigation's elevation.
     */
    public final void testSetNavigationElevation() {
        int elevation = 5;
        PreferenceActivity preferenceActivity = initialize();

        if (preferenceActivity.isSplitScreen()) {
            boolean set = preferenceActivity.setNavigationElevation(elevation);
            assertTrue(set);
            assertEquals(elevation, preferenceActivity.getNavigationElevation());
        } else {
            assertFalse(preferenceActivity.setNavigationElevation(elevation));
        }
    }

    /**
     * Tests the functionality of the method, which allows to set the bread crumb's elevation.
     */
    public final void testSetBreadCrumbElevation() {
        int elevation = 5;
        PreferenceActivity preferenceActivity = initialize();

        if (preferenceActivity.isSplitScreen()) {
            boolean set = preferenceActivity.setBreadCrumbElevation(elevation);
            assertTrue(set);
            assertEquals(elevation, preferenceActivity.getBreadCrumbElevation());
        } else {
            assertFalse(preferenceActivity.setBreadCrumbElevation(elevation));
        }
    }

    /**
     * Tests the functionality of the method, which allows to set the button bar's elevation.
     */
    public final void testSetButtonBarElevation() {
        int elevation = 5;
        PreferenceActivity preferenceActivity = initialize();
        assertFalse(preferenceActivity.setButtonBarElevation(elevation));
        preferenceActivity.showButtonBar(true);
        boolean set = preferenceActivity.setButtonBarElevation(elevation);
        assertTrue(set);
        assertEquals(elevation, preferenceActivity.getButtonBarElevation());
    }

    /**
     * Tests the functionality of the onSaveInstanceState-method.
     */
    public final void testOnSaveInstanceState() {
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        preferenceHeader.setFragment("de.mrapp.android.preference.activity.Fragment");
        preferenceHeader.setBreadCrumbTitle("bar");
        preferenceHeader.setBreadCrumbShortTitle("bar");
        Bundle parameters = new Bundle();
        parameters.putInt("key", 1);
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.addPreferenceHeader(new PreferenceHeader("bar"));
        preferenceActivity.addPreferenceHeader(preferenceHeader);
        preferenceActivity.selectPreferenceHeader(preferenceHeader, parameters);
        Bundle outState = new Bundle();
        preferenceActivity.onSaveInstanceState(outState);
        assertEquals(preferenceHeader,
                outState.getParcelable(PreferenceActivity.CURRENT_PREFERENCE_HEADER_EXTRA));
        assertEquals(parameters, outState.getBundle(PreferenceActivity.CURRENT_BUNDLE_EXTRA));
        assertEquals(preferenceHeader.getBreadCrumbTitle(),
                outState.getCharSequence(PreferenceActivity.CURRENT_TITLE_EXTRA));
        assertEquals(preferenceHeader.getBreadCrumbShortTitle(),
                outState.getCharSequence(PreferenceActivity.CURRENT_SHORT_TITLE_EXTRA));
        List<PreferenceHeader> preferenceHeaders =
                outState.getParcelableArrayList(PreferenceActivity.PREFERENCE_HEADERS_EXTRA);
        assertEquals(2, preferenceHeaders.size());
        assertEquals(preferenceHeader, preferenceHeaders.get(1));
    }

    /**
     * Tests the functionality of the onRestoreInstanceState-method.
     */
    public final void testOnRestoreInstanceState() {
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        preferenceHeader.setFragment("de.mrapp.android.preference.activity.Fragment");
        preferenceHeader.setBreadCrumbTitle("bar");
        preferenceHeader.setBreadCrumbShortTitle("bar");
        ArrayList<PreferenceHeader> preferenceHeaders = new ArrayList<>();
        preferenceHeaders.add(preferenceHeader);
        Bundle parameters = new Bundle();
        Bundle savedInstanceState = new Bundle();
        savedInstanceState.putBundle(PreferenceActivity.CURRENT_BUNDLE_EXTRA, parameters);
        savedInstanceState.putCharSequence(PreferenceActivity.CURRENT_TITLE_EXTRA,
                preferenceHeader.getBreadCrumbTitle());
        savedInstanceState.putCharSequence(PreferenceActivity.CURRENT_SHORT_TITLE_EXTRA,
                preferenceHeader.getBreadCrumbShortTitle());
        savedInstanceState.putParcelable(PreferenceActivity.CURRENT_PREFERENCE_HEADER_EXTRA,
                preferenceHeader);
        savedInstanceState.putParcelableArrayList(PreferenceActivity.PREFERENCE_HEADERS_EXTRA,
                preferenceHeaders);
        PreferenceActivity preferenceActivity = initialize();
        preferenceActivity.onRestoreInstanceState(savedInstanceState);
        assertTrue(preferenceActivity.isPreferenceHeaderSelected());
    }

}