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

import java.util.ArrayList;
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
import de.mrapp.android.preference.PreferenceHeaderDecorator;
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
	public static class ViewHolder {

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

		/**
		 * Returns the text view, which is used to show the preference header's
		 * title.
		 * 
		 * @return The text view, which is used to show the preference header's
		 *         title, as an instance of the class {@link TextView} or null,
		 *         if the text view is not available
		 */
		public final TextView getTitleTextView() {
			return titleTextView;
		}

		/**
		 * Returns the text view, which is used to show the preference header's
		 * summary.
		 * 
		 * @return The text view, which is used to show the preference header's
		 *         summary, as an instance of the class {@link TextView} or
		 *         null, if the text view is not available
		 */
		public final TextView getSummaryTextView() {
			return summaryTextView;
		}

		/**
		 * Returns the image view, which is used to show the preference header's
		 * icon.
		 * 
		 * @return The image view, which is used to show the preference header's
		 *         icon, as an instance of the class {@link ImageView} or null,
		 *         if the image view is not available
		 */
		public final ImageView getIconImageView() {
			return iconImageView;
		}

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
	 * The resource id of the selector, which is used as the background of the
	 * view, which is used to visualize the adapter's items.
	 */
	private int selectorId;

	/**
	 * A set, which contains the listeners, which have been registered to be
	 * notified, when the adapter's underlying data has been changed.
	 */
	private Set<AdapterListener> listeners;

	/**
	 * A set, which contains all decorators, which should be applied, when an
	 * item of the adapter is visualized.
	 */
	private Set<PreferenceHeaderDecorator> decorators;

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
			int iconId = preferenceHeader.getIconId();

			if (iconId != -1) {
				viewHolder.iconImageView.setImageDrawable(context
						.getResources().getDrawable(iconId));
			}
		}
	}

	/**
	 * Notifies all registered listeners about a preference header, which has
	 * been added to the adapter.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which has been added to the adapter, as
	 *            an instance of the class {@link PreferenceHeader}
	 * @param position
	 *            The position of the preference header, which has been added,
	 *            as an {@link Integer} value
	 */
	private void notifyOnPreferenceHeaderAdded(
			final PreferenceHeader preferenceHeader, final int position) {
		for (AdapterListener listener : listeners) {
			listener.onPreferenceHeaderAdded(this, preferenceHeader, position);
		}
	}

	/**
	 * Notifies all registered listeners about a preference header, which has
	 * been removed from the adapter.
	 * 
	 * @param preferenceHeader
	 *            The preference header, which has been removed from the
	 *            adapter, as an instance of the class {@link PreferenceHeader}
	 * @param position
	 *            The position of the preference header, which has been removed,
	 *            as an {@link Integer} value
	 */
	private void notifyOnPreferenceHeaderRemoved(
			final PreferenceHeader preferenceHeader, final int position) {
		for (AdapterListener listener : listeners) {
			listener.onPreferenceHeaderRemoved(this, preferenceHeader, position);
		}
	}

	/**
	 * Applies all registered decorators to modify the visualization of a
	 * specific preference header.
	 * 
	 * @param position
	 *            The position of the preference header, which should be
	 *            visualized, as an {@link Integer} value
	 * @param preferenceHeader
	 *            The preference header, which should be visualized, as an
	 *            instance of the class {@link PreferenceHeader}. The preference
	 *            header may not be null
	 * @param view
	 *            The view, which is used to visualize the preference header, as
	 *            an instance of the class {@link View}. The view may not be
	 *            null
	 * @param viewHolder
	 *            The view holder, which contains the child views of the view,
	 *            which is used to visualize the preference header, as an
	 *            instance of the class {@link ViewHolder}. The view holder may
	 *            not be null
	 */
	private void applyDecorators(final int position,
			final PreferenceHeader preferenceHeader, final View view,
			final ViewHolder viewHolder) {
		for (PreferenceHeaderDecorator decorator : decorators) {
			decorator.onApplyDecorator(position, preferenceHeader, view,
					viewHolder);
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
		this.selectorId = R.drawable.preference_header_selector;
		this.listeners = new LinkedHashSet<>();
		this.decorators = new LinkedHashSet<>();
	}

	/**
	 * Returns the resource id of the view, which is used to visualize the
	 * adapter's items.
	 * 
	 * @return The resource id of the view, which is used to visualize the
	 *         adapter's items, as an {@link Integer} value
	 */
	public final int getViewId() {
		return viewId;
	}

	/**
	 * Sets the resource id of the view, which should be used to visualize the
	 * adapter's items.
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
	 * Returns the resource id of the selector, which is used as the background
	 * of the view, which is used to visualize the adapter's items.
	 * 
	 * @return The resource id of the selector, which is used as the background
	 *         of the view, which is used to visualize the adapter's items, as
	 *         an {@link Integer} value or 0, if no selector is used
	 */
	public final int getSelectorId() {
		return selectorId;
	}

	/**
	 * Sets the resource id of the selector, which should be used as the
	 * background of the view, which is used to visualize the adapter's items.
	 * 
	 * @param selectorId
	 *            The resource id, which should be set, as an {@link Integer}
	 *            value or 0, if no selector should be used
	 */
	public final void setSelectorId(final int selectorId) {
		this.selectorId = selectorId;
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
	 * Adds a new decorator, which should be applied, when an item of the
	 * adapter is visualized, to the adapter.
	 * 
	 * @param decorator
	 *            The decorator, which should be added, as an instance of the
	 *            type {@link PreferenceHeaderDecorator}. The decorator may not
	 *            be null
	 */
	public final void addDecorator(final PreferenceHeaderDecorator decorator) {
		ensureNotNull(decorator, "The decorator may not be null");
		decorators.add(decorator);
	}

	/**
	 * Removes a specific decorator, which should not be applied anymore, when
	 * an item of the adapter is visualized, from the adapter.
	 * 
	 * @param decorator
	 *            The decorator, which should be removed, as an instance of the
	 *            type {@link PreferenceHeaderDecorator}. The decorator may not
	 *            be null
	 */
	public final void removeDecorator(final PreferenceHeaderDecorator decorator) {
		ensureNotNull(decorator, "The decorator may not be null");
		decorators.remove(decorator);
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
		int position = preferenceHeaders.indexOf(preferenceHeader);
		notifyOnPreferenceHeaderAdded(preferenceHeader, position);
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
	 * @return A collection, which contains all items of the adapter, as an
	 *         instance of the type {@link Collection} or an empty collection,
	 *         if the adapter does not contain any items
	 */
	public final ArrayList<PreferenceHeader> getAllItems() {
		return new ArrayList<>(preferenceHeaders);
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

		view.setBackgroundResource(getSelectorId());

		ViewHolder viewHolder = (ViewHolder) view.getTag();
		PreferenceHeader preferenceHeader = getItem(position);
		visualizePreferenceHeader(viewHolder, getItem(position));

		applyDecorators(position, preferenceHeader, view, viewHolder);
		return view;
	}

}