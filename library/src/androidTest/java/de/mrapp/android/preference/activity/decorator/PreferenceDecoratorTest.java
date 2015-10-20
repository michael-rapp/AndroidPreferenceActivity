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
package de.mrapp.android.preference.activity.decorator;

import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.test.AndroidTestCase;

import de.mrapp.android.preference.activity.R;

/**
 * Tests the functionality of the class {@link PreferenceDecorator}.
 *
 * @author Michael Rapp
 */
public class PreferenceDecoratorTest extends AndroidTestCase {

    /**
     * Tests the functionality of the applyDecorator-method, if the given preference is an instance
     * of the class {@link Preference}.
     */
    public final void testApplyDecoratorOnPreference() {
        Preference preference = new Preference(getContext());
        PreferenceDecorator preferenceDecorator = new PreferenceDecorator(getContext());
        preferenceDecorator.applyDecorator(preference);
        assertEquals(R.layout.preference, preference.getLayoutResource());
    }

    /**
     * Tests the functionality of the applyDecorator-method, if the given preference is an instance
     * of the class {@link PreferenceCategory}.
     */
    public final void testApplyDecoratorOnPreferenceCategory() {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        PreferenceDecorator preferenceDecorator = new PreferenceDecorator(getContext());
        preferenceDecorator.applyDecorator(preferenceCategory);
        assertEquals(R.layout.preference_category, preferenceCategory.getLayoutResource());
    }

    /**
     * Tests the functionality of the applyDecorator-method, if the given preference does not have
     * the default layout resource.
     */
    public final void testApplyDecoratorWhenPreferenceDoesNotHaveDefaultLayoutResource() {
        Preference preference = new Preference(getContext());
        preference.setLayoutResource(R.layout.preference);
        PreferenceDecorator preferenceDecorator = new PreferenceDecorator(getContext());
        preferenceDecorator.applyDecorator(preference);
        assertEquals(R.layout.preference, preference.getLayoutResource());
    }

}