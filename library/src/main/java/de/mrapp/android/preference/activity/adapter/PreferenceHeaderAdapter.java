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
package de.mrapp.android.preference.activity.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.mrapp.android.preference.activity.PreferenceHeader;
import de.mrapp.android.preference.activity.PreferenceHeaderDecorator;
import de.mrapp.android.preference.activity.R;

import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * An adapter, which provides instances of the class {@link PreferenceHeader} for visualization
 * using a list view.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class PreferenceHeaderAdapter extends BaseAdapter {

    /**
     * The view holder, which is used by the adapter.
     */
    public static class ViewHolder {

        /**
         * The text view, which is used to show the preference header's title.
         */
        @VisibleForTesting
        protected TextView titleTextView;

        /**
         * The text view, which is used to show the preference header's summary.
         */
        @VisibleForTesting
        protected TextView summaryTextView;

        /**
         * The image view, which is used to show the preference header's icon.
         */
        @VisibleForTesting
        protected ImageView iconImageView;

    }

    /**
     * The context, which is used by the adapter.
     */
    private final Context context;

    /**
     * A list, which contains the adapter's underlying data.
     */
    private List<PreferenceHeader> preferenceHeaders;

    /**
     * True, if the items of the adapter should be enabled, false otherwise.
     */
    private boolean enabled;

    /**
     * The resource id of the view, which is used to visualize the adapter's items.
     */
    private int viewId;

    /**
     * The resource id of the selector, which is used as the background of the view, which is used
     * to visualize the adapter's items.
     */
    private int selectorId;

    /**
     * A set, which contains the listeners, which have been registered to be notified, when the
     * adapter's underlying data has been changed.
     */
    private Set<AdapterListener> listeners;

    /**
     * A set, which contains all decorators, which should be applied, when an item of the adapter is
     * visualized.
     */
    private Set<PreferenceHeaderDecorator> decorators;

    /**
     * Inflates and returns the view, which is used to visualize a preference header. Furthermore,
     * the view holder is initialized.
     *
     * @param parent
     *         The parent view of the view, which should be inflated, as an instance of the class
     *         {@link ViewGroup} or null, if no parent view is available
     * @return The view, which has been inflated, as an instance of the class {@link View}. The view
     * may not be null
     */
    private View inflateView(@Nullable final ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(getViewId(), parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.titleTextView = (TextView) view.findViewById(android.R.id.title);
        viewHolder.summaryTextView = (TextView) view.findViewById(android.R.id.summary);
        viewHolder.iconImageView = (ImageView) view.findViewById(android.R.id.icon);
        view.setTag(viewHolder);
        return view;
    }

    /**
     * Adapts a view to visualize a specific preference header.
     *
     * @param viewHolder
     *         The view holder, which contains the children of the view, which should be adapted, as
     *         an instance of the class {@link ViewHolder}. The view holder may not be null
     * @param preferenceHeader
     *         The preference header, which should be visualized, as an instance of the class {@link
     *         PreferenceHeader}. The preference header may not be null
     */
    private void visualizePreferenceHeader(@NonNull final ViewHolder viewHolder,
                                           @NonNull final PreferenceHeader preferenceHeader) {
        visualizePreferenceHeaderTitle(viewHolder, preferenceHeader);
        visualizePreferenceHeaderSummary(viewHolder, preferenceHeader);
        visualizePreferenceHeaderIcon(viewHolder, preferenceHeader);
    }

    /**
     * Adapts a view to visualize a specific preference header's title.
     *
     * @param viewHolder
     *         The view holder, which contains the children of the view, which should be adapted, as
     *         an instance of the class {@link ViewHolder}. The view holder may not be null
     * @param preferenceHeader
     *         The preference header, whose title should be visualized, as an instance of the class
     *         {@link PreferenceHeader}. The preference header may not be null
     */
    private void visualizePreferenceHeaderTitle(@NonNull final ViewHolder viewHolder,
                                                @NonNull final PreferenceHeader preferenceHeader) {
        if (viewHolder.titleTextView != null) {
            viewHolder.titleTextView.setText(preferenceHeader.getTitle());
        }
    }

    /**
     * Adapts a view to visualize a specific preference header's summary.
     *
     * @param viewHolder
     *         The view holder, which contains the children of the view, which should be adapted, as
     *         an instance of the class {@link ViewHolder}. The view holder may not be null
     * @param preferenceHeader
     *         The preference header, whose summary should be visualized, as an instance of the
     *         class {@link PreferenceHeader}. The preference header may not be null
     */
    private void visualizePreferenceHeaderSummary(@NonNull final ViewHolder viewHolder,
                                                  @NonNull final PreferenceHeader preferenceHeader) {
        if (viewHolder.summaryTextView != null) {
            if (TextUtils.isEmpty(preferenceHeader.getSummary())) {
                viewHolder.summaryTextView.setVisibility(View.GONE);
            } else {
                viewHolder.summaryTextView.setVisibility(View.VISIBLE);
                viewHolder.summaryTextView.setText(preferenceHeader.getSummary());
            }
        }
    }

    /**
     * Adapts a view to visualize a specific preference header's icon.
     *
     * @param viewHolder
     *         The view holder, which contains the children of the view, which should be adapted, as
     *         an instance of the class {@link ViewHolder}. The view holder may not be null
     * @param preferenceHeader
     *         The preference header, whose icon should be visualized, as an instance of the class
     *         {@link PreferenceHeader}. The preference header may not be null
     */
    private void visualizePreferenceHeaderIcon(@NonNull final ViewHolder viewHolder,
                                               @NonNull final PreferenceHeader preferenceHeader) {
        if (viewHolder.iconImageView != null) {
            viewHolder.iconImageView.setImageDrawable(preferenceHeader.getIcon());
        }
    }

    /**
     * Notifies all registered listeners about a preference header, which has been added to the
     * adapter.
     *
     * @param preferenceHeader
     *         The preference header, which has been added to the adapter, as an instance of the
     *         class {@link PreferenceHeader}. The preference header may not be null
     * @param position
     *         The position of the preference header, which has been added, as an {@link Integer}
     *         value
     */
    private void notifyOnPreferenceHeaderAdded(@NonNull final PreferenceHeader preferenceHeader,
                                               final int position) {
        for (AdapterListener listener : listeners) {
            listener.onPreferenceHeaderAdded(this, preferenceHeader, position);
        }
    }

    /**
     * Notifies all registered listeners about a preference header, which has been removed from the
     * adapter.
     *
     * @param preferenceHeader
     *         The preference header, which has been removed from the adapter, as an instance of the
     *         class {@link PreferenceHeader}. The preference header may not be null
     * @param position
     *         The position of the preference header, which has been removed, as an {@link Integer}
     *         value
     */
    private void notifyOnPreferenceHeaderRemoved(@NonNull final PreferenceHeader preferenceHeader,
                                                 final int position) {
        for (AdapterListener listener : listeners) {
            listener.onPreferenceHeaderRemoved(this, preferenceHeader, position);
        }
    }

    /**
     * Applies all registered decorators to modify the visualization of a specific preference
     * header.
     *
     * @param position
     *         The position of the preference header, which should be visualized, as an {@link
     *         Integer} value
     * @param preferenceHeader
     *         The preference header, which should be visualized, as an instance of the class {@link
     *         PreferenceHeader}. The preference header may not be null
     * @param view
     *         The view, which is used to visualize the preference header, as an instance of the
     *         class {@link View}. The view may not be null
     * @param viewHolder
     *         The view holder, which contains the child views of the view, which is used to
     *         visualize the preference header, as an instance of the class {@link ViewHolder}. The
     *         view holder may not be null
     */
    private void applyDecorators(final int position,
                                 @NonNull final PreferenceHeader preferenceHeader,
                                 @NonNull final View view, @NonNull final ViewHolder viewHolder) {
        for (PreferenceHeaderDecorator decorator : decorators) {
            decorator.onApplyDecorator(position, preferenceHeader, view, viewHolder);
        }
    }

    /**
     * Obtains all relevant attributes from the context's current theme.
     */
    private void obtainStyledAttributes() {
        int theme = obtainTheme();

        if (theme != 0) {
            obtainSelector(theme);
        }

        if (selectorId == 0) {
            selectorId = R.drawable.selector_light;
        }
    }

    /**
     * Obtains the resource id of the context's current theme.
     *
     * @return The resource id of the context's current theme as an {@link Integer} value or 0, if
     * an error occurred while obtaining the theme
     */
    private int obtainTheme() {
        try {
            String packageName = context.getClass().getPackage().getName();
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return packageInfo.applicationInfo.theme;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * Obtains the selector from a specific theme.
     *
     * @param theme
     *         The resource id of the theme, the background should be obtained from, as an {@link
     *         Integer} value
     */
    private void obtainSelector(final int theme) {
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(theme, new int[]{R.attr.preferenceHeaderSelector});
        int resourceId = typedArray.getResourceId(0, 0);

        if (resourceId != 0) {
            this.selectorId = resourceId;
        }
    }

    /**
     * Creates a new adapter, which provides instances of the class {@link PreferenceHeader} to be
     * used for visualization via a list view.
     *
     * @param context
     *         The context, which should be used by the adapter, as an instance of the class {@link
     *         Context}. The context may not be null
     */
    public PreferenceHeaderAdapter(@NonNull final Context context) {
        ensureNotNull(context, "The context may not be null");
        this.context = context;
        this.preferenceHeaders = new LinkedList<>();
        this.enabled = true;
        this.viewId = R.layout.preference_header_item;
        this.listeners = new LinkedHashSet<>();
        this.decorators = new LinkedHashSet<>();
        obtainStyledAttributes();
    }

    /**
     * Returns the resource id of the view, which is used to visualize the adapter's items.
     *
     * @return The resource id of the view, which is used to visualize the adapter's items, as an
     * {@link Integer} value
     */
    public final int getViewId() {
        return viewId;
    }

    /**
     * Sets the resource id of the view, which should be used to visualize the adapter's items.
     *
     * @param viewId
     *         The resource id, which should be set, as an {@link Integer} value. The resource id
     *         must correspond to a valid layout resource
     */
    public final void setViewId(@LayoutRes final int viewId) {
        this.viewId = viewId;
        notifyDataSetChanged();
    }

    /**
     * Returns the resource id of the selector, which is used as the background of the view, which
     * is used to visualize the adapter's items.
     *
     * @return The resource id of the selector, which is used as the background of the view, which
     * is used to visualize the adapter's items, as an {@link Integer} value
     */
    public final int getSelectorId() {
        return selectorId;
    }

    /**
     * Sets the resource id of the selector, which should be used as the background of the view,
     * which is used to visualize the adapter's items.
     *
     * @param selectorId
     *         The resource id, which should be set, as an {@link Integer} value. The resource id
     *         must correspond to a valid drawable resource
     */
    public final void setSelectorId(@DrawableRes final int selectorId) {
        this.selectorId = selectorId;
        notifyDataSetChanged();
    }

    /**
     * Adds a new listener, which should be notified, when the underlying data of the adapter has
     * been changed, to the adapter.
     *
     * @param listener
     *         The listener, which should be added, as an instance of the type {@link
     *         AdapterListener}. The listener may not be null
     */
    public final void addListener(@NonNull final AdapterListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        listeners.add(listener);
    }

    /**
     * Removes a specific listener, which should not be notified anymore, when the underlying data
     * of the adapter has been changed, from the adapter.
     *
     * @param listener
     *         The listener, which should be removed, as an instance of the type {@link
     *         AdapterListener}. The listener may not be null
     */
    public final void removeListener(@NonNull final AdapterListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        listeners.remove(listener);
    }

    /**
     * Adds a new decorator, which should be applied, when an item of the adapter is visualized, to
     * the adapter.
     *
     * @param decorator
     *         The decorator, which should be added, as an instance of the type {@link
     *         PreferenceHeaderDecorator}. The decorator may not be null
     */
    public final void addDecorator(@NonNull final PreferenceHeaderDecorator decorator) {
        ensureNotNull(decorator, "The decorator may not be null");
        decorators.add(decorator);
    }

    /**
     * Removes a specific decorator, which should not be applied anymore, when an item of the
     * adapter is visualized, from the adapter.
     *
     * @param decorator
     *         The decorator, which should be removed, as an instance of the type {@link
     *         PreferenceHeaderDecorator}. The decorator may not be null
     */
    public final void removeDecorator(@NonNull final PreferenceHeaderDecorator decorator) {
        ensureNotNull(decorator, "The decorator may not be null");
        decorators.remove(decorator);
    }

    /**
     * Adds a new preference header to the adapter.
     *
     * @param preferenceHeader
     *         The preference header, which should be added, as an instance of the class {@link
     *         PreferenceHeader}. The preference header may not be null
     */
    public final void addItem(@NonNull final PreferenceHeader preferenceHeader) {
        ensureNotNull(preferenceHeader, "The preference header may not be null");
        preferenceHeaders.add(preferenceHeader);
        int position = preferenceHeaders.indexOf(preferenceHeader);
        notifyOnPreferenceHeaderAdded(preferenceHeader, position);
        notifyDataSetChanged();
    }

    /**
     * Adds all preference headers, which are contained by a specific collection, to the adapter.
     *
     * @param preferenceHeaders
     *         The collection, which contains the preference headers, which should be added, as an
     *         instance of the type {@link Collection} or an empty collection, if no preference
     *         headers should be added
     */
    public final void addAllItems(@NonNull final Collection<PreferenceHeader> preferenceHeaders) {
        ensureNotNull(preferenceHeaders, "The collection may not be null");

        for (PreferenceHeader preferenceHeader : preferenceHeaders) {
            addItem(preferenceHeader);
        }
    }

    /**
     * Removes a specific preference header from the adapter.
     *
     * @param preferenceHeader
     *         The preference header, which should be removed, as an instance of the class {@link
     *         PreferenceHeader}. The preference header may not be null
     * @return True, if the preference item has been removed, false otherwise
     */
    public final boolean removeItem(@NonNull final PreferenceHeader preferenceHeader) {
        ensureNotNull(preferenceHeader, "The preference header may not be null");
        int position = preferenceHeaders.indexOf(preferenceHeader);
        boolean removed = preferenceHeaders.remove(preferenceHeader);

        if (removed) {
            notifyOnPreferenceHeaderRemoved(preferenceHeader, position);
            notifyDataSetChanged();
        }

        return removed;
    }

    /**
     * Removes all preference headers from the adapter.
     */
    public final void clear() {
        for (int i = preferenceHeaders.size() - 1; i >= 0; i--) {
            removeItem(preferenceHeaders.get(i));
        }
    }

    /**
     * Returns a collection, which contains all items of the adapter.
     *
     * @return A collection, which contains all items of the adapter, as an instance of the type
     * {@link Collection} or an empty collection, if the adapter does not contain any items
     */
    public final ArrayList<PreferenceHeader> getAllItems() {
        return new ArrayList<>(preferenceHeaders);
    }

    /**
     * Returns the index, a specific preference header belongs to.
     *
     * @param item
     *         The preference header, whose index should be returned, as an instance of the class
     *         {@link PreferenceHeader}. The preference header may not be null
     * @return The index of the given preference header as an {@link Integer} value or -1 , if the
     * adapter does not contain the preference header
     */
    public final int indexOf(@NonNull final PreferenceHeader item) {
        ensureNotNull(item, "The preference header may not be null");
        return preferenceHeaders.indexOf(item);
    }

    /**
     * Sets, whether the items of the adapter should be enabled, or not.
     *
     * @param enabled
     *         True, if the items of the adapter should be enabled, false otherwise
     */
    public final void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public final boolean isEnabled(final int position) {
        return enabled;
    }

    @Override
    public final int getCount() {
        return preferenceHeaders.size();
    }

    @Override
    public final PreferenceHeader getItem(final int position) {
        return preferenceHeaders.get(position);
    }

    @Override
    public final long getItemId(final int position) {
        return position;
    }

    @Override
    public final View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = inflateView(parent);
        }

        view.setBackgroundResource(getSelectorId());

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        PreferenceHeader preferenceHeader = getItem(position);
        visualizePreferenceHeader(viewHolder, getItem(position));

        applyDecorators(position, preferenceHeader, view, viewHolder);
        return view;
    }

}