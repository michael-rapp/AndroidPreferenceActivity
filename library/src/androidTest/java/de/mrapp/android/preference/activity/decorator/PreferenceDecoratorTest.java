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