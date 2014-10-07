/*
 * AndroidPreferenceActivity Copyright 2014 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>. 
 */
package de.mrapp.android.preference.adapter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.mrapp.android.preference.PreferenceHeader;
import de.mrapp.android.preference.R;
import static de.mrapp.android.preference.util.Condition.ensureNotNull;

/**
 * An adapter, which provides instances of the class {@link PreferenceHeader}
 * for visualization using a list view.
 * 
 * @author Michael Rapp
 * 
 * @since 1.0.0
 */
public class PreferenceHeaderAdapter extends BaseAdapter {

	/**
	 * The view holder, which is used by the adapter.
	 */
	private static class ViewHolder {

		/**
		 * The text view, which is used to show the preference header's title.
		 */
		private TextView titleTextView;

		/**
		 * The text view, which is used to show the preference header's summary.
		 */
		private TextView summaryTextView;

		/**
		 * The image view, which is used to show the preference header's icon.
		 */
		private ImageView iconImageView;

	};

	/**
	 * The context, which is used by the adapter.
	 */
	private final Context context;

	/**
	 * A list, which contains the adapter's underlying data.
	 */
	private List<PreferenceHeader> preferenceHeaders;

	/**
	 * The resource id of the view, which is used to visualize the adapter's
	 * items.
	 */
	private int viewId;

	/**
	 * A set, which contains the listeners, which have been registered to be
	 * notified, when the adapter's underlying data has been changed.
	 */
	private Set<AdapterListener> listeners;

	/**
	 * Inflates and returns the view, which is used to visualize a preference
	 * header. Furthermore, the view holder is initialized.
	 * 
	 * @param parent
	 *            The parent view of the view, which should be inflated, as an
	 *            instance of the class {@link ViewGroup} or null, if no parent
	 *            view is available
	 * @return The view, which has been inflated, as an instance of the class
	 *         {@link View}. The view may not be null
	 */
	private View inflateView(final ViewGroup parent) {
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(getViewId(), parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.titleTextView = (TextView) view
				.findViewById(android.R.id.title);
		viewHolder.summaryTextView = (TextView) view
				.findViewById(android.R.id.summary);
		viewHolder.iconImageView = (ImageView) view
				.findViewById(android.R.id.icon);
		view.setTag(viewHolder);
		return view;
	}

	/**
	 * Adapts a view to visualize a specific preference header.
	 * 
	 * @param viewHolder
	 *            The view holder, which contains the children of the view,
	 *            which should be adapted, as an instance of the class
	 *            {@link ViewHolder}. The view holder may not be null
	 * @param preferenceHeader
	 *            The preference header, which should be visualized, as an
	 *            instance of the class {@link PreferenceHeader}. The preference
	 *            header may not be null
	 */
	private void visualizePreferenceHeader(final ViewHolder viewHolder,
			final PreferenceHeader preferenceHeader) {
		visualizePreferenceHeaderTitle(viewHolder, preferenceHeader);
		visualizePreferenceHeaderSummary(viewHolder, preferenceHeader);
		visualizePreferenceHeaderIcon(viewHolder, preferenceHeader);
	}

	/**
	 * Adapts a view to visualize a specific preference header's title.
	 * 
	 * @param viewHolder
	 *            The view holder, which contains the children of the view,
	 *            which should be adapted, as an instance of the class
	 *            {@link ViewHolder}. The view holder may not be null
	 * @param preferenceHeader
	 *            The preference header, whose title should be visualized, as an
	 *            instance of the class {@link PreferenceHeader}. The preference
	 *            header may not be null
	 */
	private void visualizePreferenceHeaderTitle(final ViewHolder viewHolder,
			final PreferenceHeader preferenceHeader) {
		if (viewHolder.titleTextView != null) {
			viewHolder.titleTextView.setText(preferenceHeader.getTitle());
		}
	}

	/**
	 * Adapts a view to visualize a specific preference header's summary.
	 * 
	 * @param viewHolder
	 *            The view holder, which contains the children of the view,
	 *            which should be adapted, as an instance of the class
	 *            {@link ViewHolder}. The view holder may not be null
	 * @param preferenceHeader
	 *            The preference header, whose summary should be visualized, as
	 *            an instance of the class {@link PreferenceHeader}. The
	 *            preference header may not be null
	 */
	private void visualizePreferenceHeaderSummary(final ViewHolder viewHolder,
			final PreferenceHeader preferenceHeader) {
		if (viewHolder.summaryTextView != null) {
			if (TextUtils.isEmpty(preferenceHeader.getSummary())) {
				viewHolder.summaryTextView.setVisibility(View.GONE);
			} else {
				viewHolder.summaryTextView.setVisibility(View.VISIBLE);
				viewHolder.summaryTextView.setText(preferenceHeader
						.getSummary());
			}
		}
	}

	/**
	 * Adapts a view to visualize a specific preference header's icon.
	 * 
	 * @param viewHolder
	 *            The view holder, which contains the children of the view,
	 *            which should be adapted, as an instance of the class
	 *            {@link ViewHolder}. The view holder may not be null
	 * @param preferenceHeader
	 *            The preference header, whose icon should be visualized, as an
	 *            instance of the class {@link PreferenceHeader}. The preference
	 *            header may not be null
	 */
	private void visualizePreferenceHeaderIcon(final ViewHolder viewHolder,
			final PreferenceHeader preferenceHeader) {
		if (viewHolder.iconImageView != null) {
			viewHolder.iconImageView.setImageDrawable(preferenceHeader
					.getIcon());
		}
	}

	/**
	 * Notifies all registered listeners about a preference header, which has
	 * been added to the adapter.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which has been added to the adapter, as
	 *            an instance of the class {@link PreferenceHeader}
	 */
	private void notifyOnPreferenceHeaderAdded(
			final PreferenceHeader preferenceHeader) {
		for (AdapterListener listener : listeners) {
			listener.onPreferenceHeaderAdded(this, preferenceHeader);
		}
	}

	/**
	 * Notifies all registered listeners about a preference header, which has
	 * been removed from the adapter.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which has been removed from the
	 *            adapter, as an instance of the class {@link PreferenceHeader}
	 */
	private void notifyOnPreferenceHeaderRemoved(
			final PreferenceHeader preferenceHeader) {
		for (AdapterListener listener : listeners) {
			listener.onPreferenceHeaderRemoved(this, preferenceHeader);
		}
	}

	/**
	 * Creates a new adapter, which provides instances of the class
	 * {@link PreferenceHeader} to be used for visualization via a list view.
	 * 
	 * @param context
	 *            The context, which should be used by the adapter, as an
	 *            instance of the class {@link Context}. The context may not be
	 *            null
	 */
	public PreferenceHeaderAdapter(final Context context) {
		ensureNotNull(context, "The context may not be null");
		this.context = context;
		this.preferenceHeaders = new LinkedList<>();
		this.viewId = R.layout.preference_header_item;
		this.listeners = new LinkedHashSet<>();
	}

	/**
	 * Returns the resource id, which is used to visualize the adapter's items.
	 * 
	 * @return The resource id, which is used to visualize the adapter's items,
	 *         as an {@link Integer} value
	 */
	public final int getViewId() {
		return viewId;
	}

	/**
	 * Sets the resource id, which should be used to visualize the adapter's
	 * items.
	 * 
	 * @param viewId
	 *            The resource id, which should be set, as an {@link Integer}
	 *            value. The resource id must correspond to a valid layout
	 *            resource
	 */
	public final void setViewId(final int viewId) {
		this.viewId = viewId;
		notifyDataSetChanged();
	}

	/**
	 * Adds a new listener, which should be notified, when the underlying data
	 * of the adapter has been changed, to the adapter.
	 * 
	 * @param listener
	 *            The listener, which should be added, as an instance of the
	 *            type {@link AdapterListener}. The listener may not be null
	 */
	public final void addListener(final AdapterListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		listeners.add(listener);
	}

	/**
	 * Removes a specific listener, which should not be notified anymore, when
	 * the underlying data of the adapter has been changed, from the adapter.
	 * 
	 * @param listener
	 *            The listener, which should be removed, as an instance of the
	 *            type {@link AdapterListener}. The listener may not be null
	 */
	public final void removeListener(final AdapterListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		listeners.remove(listener);
	}

	/**
	 * Adds a new preference header to the adapter.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which should be added, as an instance
	 *            of the class {@link PreferenceHeader}. The preference header
	 *            may not be null
	 */
	public final void addItem(final PreferenceHeader preferenceHeader) {
		ensureNotNull(preferenceHeader, "The preference header may not be null");
		preferenceHeaders.add(preferenceHeader);
		notifyOnPreferenceHeaderAdded(preferenceHeader);
		notifyDataSetChanged();
	}

	/**
	 * Adds all preference headers, which are contained by a specific
	 * collection, to the adapter.
	 * 
	 * @param preferenceHeaders
	 *            The collection, which contains the preference headers, which
	 *            should be added, as an instance of the type {@link Collection}
	 *            or an empty collection, if no preference headers should be
	 *            added
	 */
	public final void addAllItems(
			final Collection<PreferenceHeader> preferenceHeaders) {
		ensureNotNull(preferenceHeaders, "The collection may not be null");

		for (PreferenceHeader preferenceHeader : preferenceHeaders) {
			addItem(preferenceHeader);
		}
	}

	/**
	 * Removes a specific preference header from the adapter.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which should be removed, as an instance
	 *            of the class {@link PreferenceHeader}. The preference header
	 *            may not be null
	 * @return True, if the preference item has been removed, false otherwise
	 */
	public final boolean removeItem(final PreferenceHeader preferenceHeader) {
		ensureNotNull(preferenceHeader, "The preference header may not be null");
		boolean removed = preferenceHeaders.remove(preferenceHeader);

		if (removed) {
			notifyOnPreferenceHeaderRemoved(preferenceHeader);
			notifyDataSetChanged();
		}

		return removed;
	}

	/**
	 * Removes all preference headers from the adapter.
	 */
	public final void clear() {
		for (PreferenceHeader preferenceHeader : preferenceHeaders) {
			removeItem(preferenceHeader);
		}
	}

	/**
	 * Returns a collection, which contains all items of the adapter.
	 * 
	 * @return A collection, which contains all items of the adapter, as an
	 *         instance of the type {@link Collection} or an empty collection,
	 *         if the adapter does not contain any items
	 */
	public final Collection<PreferenceHeader> getAllItems() {
		return new LinkedList<>(preferenceHeaders);
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
	public final View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			view = inflateView(parent);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag();
		visualizePreferenceHeader(viewHolder, getItem(position));

		return view;
	}

}