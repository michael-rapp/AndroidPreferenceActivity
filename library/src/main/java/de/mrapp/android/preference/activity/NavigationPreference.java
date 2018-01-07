/*
 * Copyright 2014 - 2018 Michael Rapp
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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;

import de.mrapp.android.util.view.AbstractSavedState;

/**
 * A preference, which allows to show a fragment within a {@link PreferenceActivity} when
 * clicked.
 *
 * @author Michael Rapp
 * @since 5.0.0
 */
public class NavigationPreference extends Preference {

    /**
     * Defines the callback, a class, which should be notified, when the fragment, which is
     * associated with a navigation preference, should be shown, must implement.
     */
    public interface Callback {

        /**
         * The method, which is invoked, when the fragment, which is associated with a specific
         * navigation preference, should be shown.
         *
         * @param navigationPreference
         *         The navigation preference, whose fragment should be shown, as an instance of the
         *         class {@link NavigationPreference}. The navigation preference may not be null
         */
        void onShowFragment(@NonNull final NavigationPreference navigationPreference);

    }

    /**
     * A data structure, which allows to save the internal state of a {@link NavigationPreference}.
     */
    public static class SavedState extends AbstractSavedState {

        /**
         * A creator, which allows to create instances of the class {@link SavedState} from
         * parcels.
         */
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    @Override
                    public SavedState createFromParcel(final Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(final int size) {
                        return new SavedState[size];
                    }

                };

        /**
         * The saved value of the attribute "breadCrumbTitle".
         */
        public CharSequence breadCrumbTitle;

        /**
         * The saved value of the attribute "fragment".
         */
        public String fragment;

        /**
         * The saved value of the attribute "extras".
         */
        public Bundle extras;

        /**
         * Creates a new data structure, which allows to store the internal state of a {@link
         * NavigationPreference}. This constructor is called by derived classes when saving their
         * states.
         *
         * @param superState
         *         The state of the superclass of this view, as an instance of the type {@link
         *         Parcelable}. The state may not be null
         */
        public SavedState(@NonNull final Parcelable superState) {
            super(superState);
        }

        /**
         * Creates a new data structure, which allows to store the internal state of a {@link
         * NavigationPreference}. This constructor is used when reading from a parcel. It reads the
         * state of the superclass.
         *
         * @param source
         *         The parcel to read read from as a instance of the class {@link Parcel}. The
         *         parcel may not be null
         */
        public SavedState(@NonNull final Parcel source) {
            super(source);
            breadCrumbTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
            fragment = source.readString();
            extras = source.readBundle(NavigationPreference.class.getClassLoader());

        }

        @Override
        public final void writeToParcel(final Parcel destination, final int flags) {
            super.writeToParcel(destination, flags);
            TextUtils.writeToParcel(breadCrumbTitle, destination, flags);
            destination.writeString(fragment);
            destination.writeBundle(extras);
        }

    }

    /**
     * The breadcrumb title, which is shown, when showing the fragment, which is associated with the
     * preference.
     */
    private CharSequence breadCrumbTitle;

    /**
     * The fully classified class name of the fragment, which is associated with the preference.
     */
    private String fragment;

    /**
     * The arguments, which are passed to the fragment, which is associated with the preference.
     */
    private Bundle extras;

    /**
     * The callback, which is notified, when the fragment, which is associated with the preference,
     * should be shown.
     */
    private Callback callback;

    /**
     * Initializes the preference.
     *
     * @param attributeSet
     *         The attribute set, the attributes should be obtained from, as an instance of the type
     *         {@link AttributeSet}
     * @param defaultStyle
     *         The default style to apply to this preference. If 0, no style will be applied (beyond
     *         what is included in the theme). This may either be an attribute resource, whose value
     *         will be retrieved from the current theme, or an explicit style resource
     * @param defaultStyleResource
     *         A resource identifier of a style resource that supplies default values for the
     *         preference, used only if the default style is 0 or can not be found in the theme. Can
     *         be 0 to not look for defaults
     */
    private void initialize(final AttributeSet attributeSet, @AttrRes final int defaultStyle,
                            @StyleRes final int defaultStyleResource) {
        setOnPreferenceClickListener(null);
        obtainStyledAttributes(attributeSet, defaultStyle, defaultStyleResource);
    }

    /**
     * Obtains all attributes from a specific attribute set.
     *
     * @param attributeSet
     *         The attribute set, the attributes should be obtained from, as an instance of the type
     *         {@link AttributeSet} or null, if no attributes should be obtained
     * @param defaultStyle
     *         The default style to apply to this preference. If 0, no style will be applied (beyond
     *         what is included in the theme). This may either be an attribute resource, whose value
     *         will be retrieved from the current theme, or an explicit style resource
     * @param defaultStyleResource
     *         A resource identifier of a style resource that supplies default values for the
     *         preference, used only if the default style is 0 or can not be found in the theme. Can
     *         be 0 to not look for defaults
     */
    private void obtainStyledAttributes(@Nullable final AttributeSet attributeSet,
                                        @AttrRes final int defaultStyle,
                                        @StyleRes final int defaultStyleResource) {
        TypedArray typedArray = getContext()
                .obtainStyledAttributes(attributeSet, R.styleable.NavigationPreference,
                        defaultStyle, defaultStyleResource);

        try {
            obtainBreadCrumbTitle(typedArray);
            obtainFragment(typedArray);
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Obtains the breadcrumb title from a specific typed array.
     *
     * @param typedArray
     *         The typed array, the breadcrumb title should be obtained from, as an instance of the
     *         class {@link TypedArray}. The typed array may not be null
     */
    private void obtainBreadCrumbTitle(@NonNull final TypedArray typedArray) {
        setBreadCrumbTitle(
                typedArray.getText(R.styleable.NavigationPreference_android_breadCrumbTitle));
    }

    /**
     * Obtains the fragment from a specific typed array.
     *
     * @param typedArray
     *         The typed array, the fragment should be obtained from, as an instance of the class
     *         {@link TypedArray}. The typed array may not be null
     */
    private void obtainFragment(@NonNull final TypedArray typedArray) {
        setFragment(typedArray.getString(R.styleable.NavigationPreference_android_fragment));
    }

    /**
     * Creates a click listener, which notifies the callback, when the preference's fragment should
     * be shown and forwards the click event to an encapsulated listener.
     *
     * @param listener
     *         The listener, which should be encapsulated, as an instance of the type {@link
     *         OnPreferenceClickListener} or null, if no listener should be encapsulated
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceClickListener}. The listener may not be null
     */
    @NonNull
    private OnPreferenceClickListener createOnPreferenceClickListenerWrapper(
            @Nullable final OnPreferenceClickListener listener) {
        return new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(final Preference preference) {
                notifyOnShowFragment();

                if (listener != null) {
                    listener.onPreferenceClick(preference);
                }

                return true;
            }

        };
    }

    /**
     * Notifies the callback, that the fragment, which is associated with the preference, should be
     * shown.
     */
    private void notifyOnShowFragment() {
        if (callback != null) {
            callback.onShowFragment(this);
        }
    }

    /**
     * Creates a new preference, which allows to show a preference screen within a {@link
     * PreferenceActivity} when clicked.
     *
     * @param context
     *         The context, which should be used by the preference, as an instance of the class
     *         {@link Context}. The context may not be null
     */
    public NavigationPreference(@NonNull final Context context) {
        this(context, null);
    }

    /**
     * Creates a new preference, which allows to show a preference screen within a {@link
     * PreferenceActivity} when clicked.
     *
     * @param context
     *         The context, which should be used by the preference, as an instance of the class
     *         {@link Context}. The context may not be null
     * @param attributeSet
     *         The attributes of the XML tag that is inflating the preference, as an instance of the
     *         type {@link AttributeSet} or null, if no attributes are available
     */
    public NavigationPreference(@NonNull final Context context,
                                @Nullable final AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(attributeSet, 0, 0);
    }

    /**
     * Creates a new preference, which allows to show a preference screen within a {@link
     * PreferenceActivity} when clicked.
     *
     * @param context
     *         The context, which should be used by the preference, as an instance of the class
     *         {@link Context}. The context may not be null
     * @param attributeSet
     *         The attributes of the XML tag that is inflating the preference, as an instance of the
     *         type {@link AttributeSet} or null, if no attributes are available
     * @param defaultStyle
     *         The default style to apply to this preference. If 0, no style will be applied (beyond
     *         what is included in the theme). This may either be an attribute resource, whose value
     *         will be retrieved from the current theme, or an explicit style resource
     */
    public NavigationPreference(@NonNull final Context context,
                                @Nullable final AttributeSet attributeSet,
                                @AttrRes final int defaultStyle) {
        super(context, attributeSet, defaultStyle);
        initialize(attributeSet, defaultStyle, 0);
    }

    /**
     * Creates a new preference, which allows to show a preference screen within a {@link
     * PreferenceActivity} when clicked.
     *
     * @param context
     *         The context, which should be used by the preference, as an instance of the class
     *         {@link Context}. The context may not be null
     * @param attributeSet
     *         The attributes of the XML tag that is inflating the preference, as an instance of the
     *         type {@link AttributeSet} or null, if no attributes are available
     * @param defaultStyle
     *         The default style to apply to this preference. If 0, no style will be applied (beyond
     *         what is included in the theme). This may either be an attribute resource, whose value
     *         will be retrieved from the current theme, or an explicit style resource
     * @param defaultStyleResource
     *         A resource identifier of a style resource that supplies default values for the
     *         preference, used only if the default style is 0 or can not be found in the theme. Can
     *         be 0 to not look for defaults
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NavigationPreference(@NonNull final Context context,
                                @Nullable final AttributeSet attributeSet,
                                @AttrRes final int defaultStyle,
                                @StyleRes final int defaultStyleResource) {
        super(context, attributeSet, defaultStyle, defaultStyleResource);
        initialize(attributeSet, defaultStyle, defaultStyleResource);
    }

    /**
     * Sets the callback, which should be notified, when the fragment, which is associated with the
     * preference, should be shown.
     *
     * @param callback
     *         The callback, which should be set, as an instance of the type {@link Callback} or
     *         null, if no callback should be notified
     */
    public final void setCallback(@Nullable final Callback callback) {
        this.callback = callback;
    }

    /**
     * Returns the breadcrumb title, which is shown, when showing the fragment, which is
     * associated with the preference.
     *
     * @return The breadcrumb title, which is shown, when showing the fragment, which is associated
     * with the preference, as an instance of the type {@link CharSequence} or null, if no
     * breadcrumb title is available
     */
    @Nullable
    public final CharSequence getBreadCrumbTitle() {
        return breadCrumbTitle;
    }

    /**
     * Sets the breadcrumb title, which should be shown, when showing the fragment, which is
     * associated with the preference.
     *
     * @param resourceId
     *         The resource id of the breadcrumb title, which should be set, as an {@link Integer}
     *         value. The resource id must correspond to a valid string resource
     */
    public final void setBreadCrumbTitle(@StringRes final int resourceId) {
        setBreadCrumbTitle(getContext().getText(resourceId));
    }

    /**
     * Sets the breadcrumb title, which should be shown, when showing the fragment, which is
     * associated with the preference.
     *
     * @param breadCrumbTitle
     *         The breadcrumb title, which should be set, as an instance of the type {@link
     *         CharSequence} or null, if no breadcrumb title should be set
     */
    public final void setBreadCrumbTitle(@Nullable final CharSequence breadCrumbTitle) {
        this.breadCrumbTitle = breadCrumbTitle;
    }

    /**
     * Returns the fully classified class name of the fragment, which is associated with the
     * preference.
     *
     * @return The fully classified class name of the fragment, which is associated with the
     * preference, as a {@link String} or null, if no fragment is associated with the preference
     */
    @Nullable
    public final String getFragment() {
        return fragment;
    }

    /**
     * Sets the fully classified class name of the fragment, which should be associated with the
     * preference.
     *
     * @param fragment
     *         The fully classified class name of the fragment, which should be set, as a {@link
     *         String} or null, if no fragment should be set
     */
    public final void setFragment(@Nullable final String fragment) {
        this.fragment = fragment;
    }

    /**
     * Returns the arguments, which are passed to the fragment, which is associated with the
     * preference.
     *
     * @return The arguments, which are passed to the fragment, which is associated with the
     * preference, as an instance of the class {@link Bundle} or null, if no arguments are passed to
     * the fragment
     */
    @Nullable
    public final Bundle getExtras() {
        return extras;
    }

    /**
     * Sets the argument, which should be passed to the fragment, which is associated with the
     * preference.
     *
     * @param extras
     *         The arguments, which should be set, as an instance of the class {@link Bundle} or
     *         null, if no arguments should be passed to the fragment
     */
    public final void setExtras(@Nullable final Bundle extras) {
        this.extras = extras;
    }

    @Override
    public final void setOnPreferenceClickListener(
            @Nullable final OnPreferenceClickListener listener) {
        super.setOnPreferenceClickListener(createOnPreferenceClickListenerWrapper(listener));
    }

    @Override
    protected final Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        if (!isPersistent()) {
            SavedState savedState = new SavedState(superState);
            savedState.breadCrumbTitle = getBreadCrumbTitle();
            savedState.fragment = getFragment();
            savedState.extras = getExtras();
            return savedState;
        }

        return superState;
    }

    @Override
    protected final void onRestoreInstanceState(final Parcelable state) {
        if (state != null && state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            setBreadCrumbTitle(savedState.breadCrumbTitle);
            setFragment(savedState.fragment);
            setExtras(savedState.extras);
            super.onRestoreInstanceState(savedState.getSuperState());
        } else {
            super.onRestoreInstanceState(state);
        }
    }

}