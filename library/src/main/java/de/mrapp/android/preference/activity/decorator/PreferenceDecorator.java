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

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.support.annotation.NonNull;

import de.mrapp.android.preference.activity.R;

import static de.mrapp.android.preference.activity.util.Condition.ensureNotNull;

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
            preference.setLayoutResource(R.layout.preference_category);
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