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
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import static de.mrapp.android.util.Condition.ensureNotEmpty;
import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * An abstract base class for all navigation items of a {@link PreferenceActivity}.
 *
 * @author Michael Rapp
 * @since 4.3.0
 */
public abstract class AbstractNavigationItem implements Parcelable {

    /**
     * The title of the navigation item.
     */
    private CharSequence title;

    /**
     * Creates a new navigation item.
     *
     * @param source
     *         The parcel, the navigation item should be created from, as an instance of the class
     *         {@link Parcel}. The parcel may not be null
     */
    protected AbstractNavigationItem(@NonNull final Parcel source) {
        setTitle(TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source));
    }

    /**
     * Creates a new navigation item.
     *
     * @param title
     *         The title of the navigation item as an instance of the class {@link CharSequence}.
     *         The title may neither be null, nor empty
     */
    public AbstractNavigationItem(@NonNull final CharSequence title) {
        setTitle(title);
    }

    /**
     * Creates a new navigation item, which categorizes multiple preferences.
     *
     * @param context
     *         The context, which should be used to retrieve the string resource, as an instance of
     *         the class {@link Context}. The context may not be null
     * @param titleId
     *         The resource id of the title of the navigation item as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public AbstractNavigationItem(@NonNull final Context context, @StringRes final int titleId) {
        setTitle(context, titleId);
    }

    /**
     * Returns the title of the navigation item.
     *
     * @return The title of the navigation item as an instance of the class {@link CharSequence}.
     * The title may neither be null, nor empty
     */
    public final CharSequence getTitle() {
        return title;
    }

    /**
     * Sets the title of the navigation item.
     *
     * @param title
     *         The title, which should be set, as an instance of the class {@link CharSequence}. The
     *         title may neither be null, nor empty
     */
    public final void setTitle(@NonNull final CharSequence title) {
        ensureNotNull(title, "The title may not be null");
        ensureNotEmpty(title, "The title may not be empty");
        this.title = title;
    }

    /**
     * Sets the title of the navigation item.
     *
     * @param context
     *         The context, which should be used to retrieve the string resource, as an instance of
     *         the class {@link Context}. The context may not be null
     * @param resourceId
     *         The resource id of the title, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setTitle(@NonNull final Context context, @StringRes final int resourceId) {
        setTitle(context.getString(resourceId));
    }

    @Override
    public final int describeContents() {
        return 0;
    }

    @CallSuper
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        TextUtils.writeToParcel(getTitle(), dest, flags);
    }

}