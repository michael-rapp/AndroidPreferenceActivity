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
package de.mrapp.android.preference.activity;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * A navigation item, which categorizes multiple preference headers.
 *
 * @author Michael Rapp
 * @since 4.3.0
 */
public class PreferenceHeaderCategory extends AbstractNavigationItem {

    /**
     * A creator, which allows to create instances of the class {@link PreferenceHeaderCategory}
     * from parcels.
     */
    public static final Creator<PreferenceHeaderCategory> CREATOR =
            new Creator<PreferenceHeaderCategory>() {

                @Override
                public PreferenceHeaderCategory createFromParcel(final Parcel source) {
                    return new PreferenceHeaderCategory(source);
                }

                @Override
                public PreferenceHeaderCategory[] newArray(final int size) {
                    return new PreferenceHeaderCategory[size];
                }

            };

    /**
     * Creates a new navigation item, which categorizes multiple preference headers.
     *
     * @param source
     *         The parcel, the navigation item should be created from, as an instance of the class
     *         {@link Parcel}. The parcel may not be null
     */
    private PreferenceHeaderCategory(@NonNull final Parcel source) {
        super(source);
    }

    /**
     * Creates a new navigation item, which categorizes multiple preference headers.
     *
     * @param title
     *         The title of the navigation item as an instance of the class {@link CharSequence}.
     *         The title may neither be null, nor empty
     */
    public PreferenceHeaderCategory(@NonNull final CharSequence title) {
        super(title);
    }

    /**
     * Creates a new navigation item, which categorizes multiple preference headers.
     *
     * @param context
     *         The context, which should be used to retrieve the string resource, as an instance of
     *         the class {@link Context}. The context may not be null
     * @param titleId
     *         The resource id of the title of the navigation item as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public PreferenceHeaderCategory(@NonNull final Context context, @StringRes final int titleId) {
        super(context, titleId);
    }

}