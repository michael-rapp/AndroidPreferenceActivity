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

        assertEquals("de.mrapp.android.preference.activity.PreferenceFragment",
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