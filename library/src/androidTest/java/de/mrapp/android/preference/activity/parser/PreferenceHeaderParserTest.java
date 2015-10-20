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
package de.mrapp.android.preference.activity.parser;

import android.content.res.Resources.NotFoundException;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.Collection;
import java.util.Iterator;

import de.mrapp.android.preference.activity.PreferenceHeader;
import de.mrapp.android.preference.activity.R;

/**
 * Tests the functionality of the class {@link PreferenceHeaderParser}.
 *
 * @author Michael Rapp
 */
public class PreferenceHeaderParserTest extends AndroidTestCase {

    /**
     * Tests the functionality of the method, which allows to parse the preference headers, which
     * are defined by a specific XML resource.
     */
    public final void testFromResource() {
        Collection<PreferenceHeader> preferenceHeaders =
                PreferenceHeaderParser.fromResource(getContext(), R.xml.preference_headers);
        assertEquals(3, preferenceHeaders.size());
        Iterator<PreferenceHeader> iterator = preferenceHeaders.iterator();
        PreferenceHeader preferenceHeader = iterator.next();

        assertEquals("de.mrapp.android.preference.activity.Fragment",
                preferenceHeader.getFragment());
        assertEquals("breadCrumbTitle", preferenceHeader.getBreadCrumbTitle());
        assertEquals("breadCrumbShortTitle", preferenceHeader.getBreadCrumbShortTitle());
        assertEquals("title", preferenceHeader.getTitle());
        assertEquals("summary", preferenceHeader.getSummary());
        assertNull(preferenceHeader.getIcon());
        assertNull(preferenceHeader.getIntent());
        assertNull(preferenceHeader.getExtras());

        preferenceHeader = iterator.next();
        assertNull(preferenceHeader.getFragment());
        assertNull(preferenceHeader.getBreadCrumbTitle());
        assertNull(preferenceHeader.getBreadCrumbShortTitle());
        assertEquals("title", preferenceHeader.getTitle());
        assertNull(preferenceHeader.getSummary());
        assertNull(preferenceHeader.getIcon());
        assertNotNull(preferenceHeader.getIntent());
        assertNotNull(preferenceHeader.getExtras());
        assertEquals("value", preferenceHeader.getExtras().getString("key"));

        preferenceHeader = iterator.next();
        assertNull(preferenceHeader.getFragment());
        assertEquals(getContext().getString(android.R.string.copy),
                preferenceHeader.getBreadCrumbTitle());
        assertEquals(getContext().getString(android.R.string.cancel),
                preferenceHeader.getBreadCrumbShortTitle());
        assertEquals(getContext().getString(android.R.string.cut), preferenceHeader.getTitle());
        assertEquals(getContext().getString(android.R.string.copyUrl),
                preferenceHeader.getSummary());
        assertNotNull(preferenceHeader.getIcon());
        assertNull(preferenceHeader.getIntent());
        assertNull(preferenceHeader.getExtras());
    }

    /**
     * Ensures, that a {@link NotFoundException} is thrown by the fromResource-method, if the given
     * resource id is invalid.
     */
    public final void testFromResourceThrowsExceptionWhenResourceIdIsInvalid() {
        try {
            PreferenceHeaderParser.fromResource(getContext(), -1);
            Assert.fail();
        } catch (NotFoundException e) {
            return;
        }
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the fromResource-method, if the
     * given context is null.
     */
    public final void testFromResourceThrowsExceptionWhenContextIsNull() {
        try {
            PreferenceHeaderParser.fromResource(null, R.xml.preference_headers);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Ensures, that a {@link RuntimeException} is thrown by the fromResource-method, if the start
     * tag of the given XML resource is invalid.
     */
    public final void testFromResourceThrowsExceptionWhenStartTagIsInvalid() {
        try {
            PreferenceHeaderParser.fromResource(getContext(), R.xml.no_preference_headers);
            Assert.fail();
        } catch (RuntimeException e) {
            return;
        }
    }

    /**
     * Ensures, that a {@link RuntimeException} is thrown by the fromResource-method, if the title
     * of a preference header is missing.
     */
    public final void testFromResourceThrowsExceptionWhenTitleIsMissing() {
        try {
            PreferenceHeaderParser.fromResource(getContext(), R.xml.invalid_preference_headers);
            Assert.fail();
        } catch (RuntimeException e) {
            return;
        }
    }

}