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
package de.mrapp.android.preference.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import static de.mrapp.android.util.Condition.ensureNotEmpty;
import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * A navigation item, which categorizes multiple preferences.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class PreferenceHeader implements Parcelable {

    /**
     * A creator, which allows to create instances of the class {@link PreferenceHeader} from
     * parcels.
     */
    public static final Creator<PreferenceHeader> CREATOR = new Creator<PreferenceHeader>() {

        @Override
        public PreferenceHeader createFromParcel(final Parcel source) {
            return new PreferenceHeader(source);
        }

        @Override
        public PreferenceHeader[] newArray(final int size) {
            return new PreferenceHeader[size];
        }

    };

    /**
     * The title of the navigation item.
     */
    private CharSequence title;

    /**
     * The summary, which describes the preferences, which belong to the navigation item.
     */
    private CharSequence summary;

    /**
     * The text, which is shown as the title in the navigation item's bread crumb.
     */
    private CharSequence breadCrumbTitle;

    /**
     * The text, which is shown as the short title in the navigation item's bread crumb.
     */
    private CharSequence breadCrumbShortTitle;

    /**
     * The navigation item's icon.
     */
    private Drawable icon;

    /**
     * The full qualified class name of the fragment, which is shown, when the navigation item is
     * selected.
     */
    private String fragment;

    /**
     * The intent, which is launched, when the navigation item is selected.
     */
    private Intent intent;

    /**
     * Optional parameters of the intent, which is launched, when the navigation item is selected.
     */
    private Bundle extras;

    /**
     * Creates a new navigation item, which categorizes multiple preferences.
     *
     * @param source
     *         The parcel, the navigation item should be created from, as an instance of the class
     *         {@link Parcel}. The parcel may not be null
     */
    @SuppressWarnings("deprecation")
    private PreferenceHeader(@NonNull final Parcel source) {
        setTitle(TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source));
        setSummary(TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source));
        setBreadCrumbTitle(TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source));
        setBreadCrumbShortTitle(TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source));
        setIcon(new BitmapDrawable((Bitmap) source.readParcelable(Bitmap.class.getClassLoader())));
        setFragment(source.readString());
        setExtras(source.readBundle());

        if (source.readInt() != 0) {
            setIntent(Intent.CREATOR.createFromParcel(source));
        }
    }

    /**
     * Creates a new navigation item, which categorizes multiple preferences.
     *
     * @param title
     *         The title of the navigation item as an instance of the class {@link CharSequence}.
     *         The title may neither be null, nor empty
     */
    public PreferenceHeader(@NonNull final CharSequence title) {
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
    public PreferenceHeader(@NonNull final Context context, @StringRes final int titleId) {
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

    /**
     * Returns the summary, which describes the preferences, which belong to the navigation item.
     *
     * @return The summary, which describes the preferences, which belong to the navigation item, as
     * an instance of the class {@link CharSequence} or null, if no summary has been set
     */
    public final CharSequence getSummary() {
        return summary;
    }

    /**
     * Sets the summary, which describes the preferences, which belong to the navigation item.
     *
     * @param summary
     *         The summary, which should be set, as an instance of the class {@link CharSequence} or
     *         null, if no summary should be set
     */
    public final void setSummary(@Nullable final CharSequence summary) {
        this.summary = summary;
    }

    /**
     * Sets the summary, which describes the preferences, which belong to the navigation item.
     *
     * @param context
     *         The context, which should be used to retrieve the string resource, as an instance of
     *         the class {@link Context}. The context may not be null
     * @param resourceId
     *         The resource id of the summary, which should be set as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setSummary(@NonNull final Context context, @StringRes final int resourceId) {
        setSummary(context.getString(resourceId));
    }

    /**
     * Returns the text, which is shown as the title in the navigation item's bread crumb.
     *
     * @return The text, which is shown as the title in the navigation item's bread crumb, as an
     * instance of the class {@link CharSequence} or null, if no title has been set
     */
    public final CharSequence getBreadCrumbTitle() {
        return breadCrumbTitle;
    }

    /**
     * Sets the text, which should be shown as the title in the navigation item's bread crumb.
     *
     * @param breadCrumbTitle
     *         The title, which should be set, as an instance of the class {@link CharSequence} or
     *         null, if no title should be set
     */
    public final void setBreadCrumbTitle(@Nullable final CharSequence breadCrumbTitle) {
        this.breadCrumbTitle = breadCrumbTitle;
    }

    /**
     * Sets the text, which should be shown as the title in the navigation item's bread crumb.
     *
     * @param context
     *         The context, which should be used to retrieve the string resource, as an instance of
     *         the class {@link Context}. The context may not be null
     * @param resourceId
     *         The resource if of the title, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid string resource
     */
    public final void setBreadCrumbTitle(@NonNull final Context context,
                                         @StringRes final int resourceId) {
        setBreadCrumbTitle(context.getString(resourceId));
    }

    /**
     * Returns the text, which is shown as the short title of the navigation item's bread crumb.
     *
     * @return The text, which is shown as the short title of the navigation item's bread crumb, as
     * an instance of the class {@link CharSequence} or null, if no short title has been set
     */
    public final CharSequence getBreadCrumbShortTitle() {
        return breadCrumbShortTitle;
    }

    /**
     * Sets the text, which should be shown as the short title of the navigation item's bread
     * crumb.
     *
     * @param breadCrumbShortTitle
     *         The short title, which should be set, as an instance of the class {@link
     *         CharSequence} or null, if no short title should be set
     */
    public final void setBreadCrumbShortTitle(@Nullable final CharSequence breadCrumbShortTitle) {
        this.breadCrumbShortTitle = breadCrumbShortTitle;
    }

    /**
     * Sets the text, which should be shown as the short title of the navigation item's bread
     * crumb.
     *
     * @param context
     *         The context, which should be used to retrieve the string resource, as an instance of
     *         the class {@link Context}. The context may not be null
     * @param resourceId
     *         The resource id of the short title, which should be set, as an {@link Integer} value.
     *         The resource id must correspond to a valid string resource
     */
    public final void setBreadCrumbShortTitle(@NonNull final Context context,
                                              @StringRes final int resourceId) {
        setBreadCrumbShortTitle(context.getString(resourceId));
    }

    /**
     * Returns the navigation item's icon.
     *
     * @return The navigation item's icon as an instance of the class {@link Drawable} or null, if
     * no icon has been set
     */
    public final Drawable getIcon() {
        return icon;
    }

    /**
     * Sets the navigation item's icon.
     *
     * @param icon
     *         The icon, which should be set, as an instance of the class {@link Drawable} or null,
     *         if no icon should be set
     */
    public final void setIcon(@Nullable final Drawable icon) {
        this.icon = icon;
    }

    /**
     * Sets the navigation item's icon.
     *
     * @param context
     *         The context, which should be used to retrieve the drawable resource, as an instance
     *         of the class {@link Context}. The context may not be null
     * @param resourceId
     *         The resource id of the icon, which should be set, as an {@link Integer} value. The
     *         resource id must correspond to a valid drawable resource
     */
    public final void setIcon(@NonNull final Context context, @DrawableRes final int resourceId) {
        this.icon = ContextCompat.getDrawable(context, resourceId);
    }

    /**
     * Returns the full qualified class name of the fragment, which is shown, when the navigation
     * item is selected.
     *
     * @return The full qualified class name of the fragment, which is shown, when the navigation
     * item is selected or null, if no fragment has been set
     */
    public final String getFragment() {
        return fragment;
    }

    /**
     * Sets the full qualified class name of the fragment, which should be shown, when the
     * navigation item is selected.
     *
     * @param fragment
     *         The class name, which should be set, as a {@link String} or null, if no fragment
     *         should be shown
     */
    public final void setFragment(@Nullable final String fragment) {
        this.fragment = fragment;
    }

    /**
     * Returns the intent, which is launched, when the navigation item is selected.
     *
     * @return The intent, which is launched, when the navigation item is selected, as an instance
     * of the class {@link Intent} or null, if no intent has been set
     */
    public final Intent getIntent() {
        return intent;
    }

    /**
     * Sets the intent, which should be launched, when the navigation item is selected.
     *
     * @param intent
     *         The intent, which should be set, as an instance of the class {@link Integer} or null,
     *         if no intent should be set
     */
    public final void setIntent(@Nullable final Intent intent) {
        this.intent = intent;
    }

    /**
     * Returns the optional parameters, which are passed to the fragment, when the navigation item
     * is selected.
     *
     * @return The parameters of the intent, which are passed to the fragment, when the navigation
     * item is selected, as an instance of the class {@link Bundle} or null, if no parameters have
     * been set
     */
    public final Bundle getExtras() {
        return extras;
    }

    /**
     * Sets the optional parameters of the intent, which should be passed to the fragment, when the
     * navigation item is selected.
     *
     * @param extras
     *         The parameters, which should be set, as an instance of the class {@link Bundle} or
     *         null, if no parameters should be set
     */
    public final void setExtras(@Nullable final Bundle extras) {
        this.extras = extras;
    }

    @Override
    public final int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(final Parcel dest, final int flags) {
        TextUtils.writeToParcel(getTitle(), dest, flags);
        TextUtils.writeToParcel(getSummary(), dest, flags);
        TextUtils.writeToParcel(getBreadCrumbTitle(), dest, flags);
        TextUtils.writeToParcel(getBreadCrumbShortTitle(), dest, flags);
        Bitmap bitmap = (getIcon() != null && getIcon() instanceof BitmapDrawable) ?
                ((BitmapDrawable) getIcon()).getBitmap() : null;
        dest.writeParcelable(bitmap, flags);
        dest.writeString(getFragment());
        dest.writeBundle(getExtras());

        if (getIntent() != null) {
            dest.writeInt(1);
            getIntent().writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
    }

}