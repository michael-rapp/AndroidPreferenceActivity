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
package de.mrapp.android.preference.activity.decorator;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.support.annotation.NonNull;

import de.mrapp.android.preference.activity.R;

import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * A decorator, which allows to apply Material style on specific preferences.
 *
 * @author Michael Rapp
 * @since 2.0.0
 */
public class PreferenceDecorator {

    /**
     * The prefix of the name of a layout resource, which belongs to the Android SDK.
     */
    private static final String ANDROID_LAYOUT_PREFIX = "android:layout";

    /**
     * The context, which is used by the decorator.
     */
    private final Context context;

    /**
     * Returns, whether the default layout is applied to a specific preference, or not.
     *
     * @param preference
     *         The preference, whose layout should be checked, as an instance of the class {@link
     *         Preference}. The preference may not be null
     * @return True, if the default layout is applied to the given preference, false otherwise
     */
    private boolean hasDefaultLayoutResource(@NonNull final Preference preference) {
        return context.getResources().getResourceName(preference.getLayoutResource())
                .startsWith(ANDROID_LAYOUT_PREFIX);
    }

    /**
     * Applies Material style, by setting an appropriate layout resource, on a specific preference.
     *
     * @param preference
     *         The preference, whose layout resource should be set, as an instance of the class
     *         {@link Preference}. The preference may not be null
     */
    private void setLayoutResource(@NonNull final Preference preference) {
        if (preference instanceof PreferenceCategory) {
            preference.setLayoutResource(R.layout.preference_category_no_divider);
        } else {
            preference.setLayoutResource(R.layout.preference);
        }
    }

    /**
     * Creates a new decorator, which allows to apply Material style on specific preferences.
     *
     * @param context
     *         The context, which should be used by the decorator, as an instance of the class
     *         {@link Context}. The context may not be null
     */
    public PreferenceDecorator(@NonNull final Context context) {
        ensureNotNull(context, "The context may not be null");
        this.context = context;
    }

    /**
     * Applies the decorator on a specific preference. The decorator is only applied, if the default
     * layout is applied on the preference.
     *
     * @param preference
     *         The preference, the decorator should be applied on, as an instance of the class
     *         {@link Preference}. The preference may not be null
     */
    public final void applyDecorator(@NonNull final Preference preference) {
        if (hasDefaultLayoutResource(preference)) {
            setLayoutResource(preference);
        }
    }

}