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
package de.mrapp.android.preference.activity.fragment;

import static de.mrapp.android.preference.activity.util.Condition.ensureNotNull;

import java.util.LinkedHashSet;
import java.util.Set;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.mrapp.android.preference.activity.R;
import de.mrapp.android.preference.activity.adapter.PreferenceHeaderAdapter;

/**
 * A fragment, which shows multiple preference headers and provides navigation
 * to each header's fragment.
 * 
 * @author Michael Rapp
 * 
 * @since 1.0.0
 */
public class PreferenceHeaderFragment extends Fragment {

	/**
	 * The list view, which is used to show the preference headers.
	 */
	private ListView listView;

	/**
	 * The adapter, which provides the preference headers for visualization
	 * using the fragment's list view.
	 */
	private PreferenceHeaderAdapter adapter;

	/**
	 * A set, which contains the listeners, which should be notified on events
	 * concerning the fragment.
	 */
	private Set<FragmentListener> listeners = new LinkedHashSet<FragmentListener>();

	/**
	 * Notifies all registered listeners, that the fragment has been created.
	 */
	private void notifyFragmentCreated() {
		for (FragmentListener listener : listeners) {
			listener.onFragmentCreated(this);
		}
	}

	/**
	 * Adds a new listener, which should be notified on events concerning the
	 * fragment.
	 * 
	 * @param listener
	 *            The listener, which should be added, as an instance of the
	 *            type {@link FragmentListener}. The listener may not be null
	 */
	public final void addFragmentListener(final FragmentListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		listeners.add(listener);
	}

	/**
	 * Removes a specific listener, which should not be notified on events
	 * concerning the fragment, anymore.
	 * 
	 * @param listener
	 *            The listener, which should be removed, as an instance of the
	 *            type {@link FragmentListener}. The listener may not be null
	 */
	public final void removeFragmentListener(final FragmentListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		listeners.remove(listener);
	}

	@Override
	public final View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.preference_header_fragment,
				container, false);
		listView = (ListView) view.findViewById(android.R.id.list);
		return view;
	}

	@Override
	public final void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new PreferenceHeaderAdapter(getActivity());
		getListView().setAdapter(adapter);
		notifyFragmentCreated();
	}

	/**
	 * Returns the list view, which is used to show the preference headers.
	 * 
	 * @return The list view, which is used to show the preference headers, as
	 *         an instance of the class {@link ListView}
	 */
	public final ListView getListView() {
		return listView;
	}

	/**
	 * Returns the adapter, which provides the preference headers for
	 * visualization using the fragment's list view.
	 * 
	 * @return The adapter, which provides the preference headers for
	 *         visualization using the fragment's list view, as an instance of
	 *         the class {@link PreferenceHeaderAdapter}
	 */
	public final PreferenceHeaderAdapter getListAdapter() {
		return adapter;
	}

}